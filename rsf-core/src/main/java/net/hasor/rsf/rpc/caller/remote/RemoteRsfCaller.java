/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.rpc.caller.remote;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.more.future.BasicFuture;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import net.hasor.rsf.utils.ExecutesManager;
/**
 * 扩展{@link RsfCaller}，用来支持远程机器发来的调用请求。
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RemoteRsfCaller extends RsfCaller {
    private final ExecutesManager      executesManager;
    private final RemoteSenderListener senderListener;
    // 
    public RemoteRsfCaller(RsfContext rsfContext, RsfBeanContainer rsfBeanContainer, RemoteSenderListener senderListener) {
        super(rsfContext, rsfBeanContainer, senderListener);
        //
        this.senderListener = senderListener;
        RsfSettings rsfSettings = rsfContext.getSettings();
        int queueSize = rsfSettings.getQueueMaxSize();
        int minCorePoolSize = rsfSettings.getQueueMinPoolSize();
        int maxCorePoolSize = rsfSettings.getQueueMaxPoolSize();
        long keepAliveTime = rsfSettings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
    }
    /**销毁。*/
    public void shutdown() {
        logger.info("rsfCaller -> shutdown.");
        this.executesManager.shutdown();
    }
    /**
     * 收到Request请求直接进行调用，并等待调用结果返回。
     * @param info 请求消息。
     */
    public ResponseInfo doRequest(InterAddress target, RequestInfo info) {
        long requestID = info.getRequestID();
        ResponseInfo resp = null;
        try {
            final BasicFuture<ResponseInfo> future = new BasicFuture<ResponseInfo>();
            new InvokerProcessing(target, this, info) {
                protected void sendResponse(ResponseInfo info) {
                    future.completed(info);
                }
            }.run();
            resp = future.get(info.getClientTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            String errorInfo = "do request(" + requestID + ") failed -> waiting for response.";
            logger.error(errorInfo);
            resp = ProtocolUtils.buildStatus(RsfConstants.RSF_Response, requestID, ProtocolStatus.Timeout, errorInfo);
        } catch (InterruptedException e) {
            String errorInfo = "do request(" + requestID + ") failed -> InterruptedException.";
            logger.error(errorInfo);
            resp = ProtocolUtils.buildStatus(RsfConstants.RSF_Response, requestID, ProtocolStatus.InvokeError, errorInfo);
        } catch (ExecutionException e) {
            Throwable ex = e.getCause();
            String errorInfo = "do request(" + requestID + ") failed -> " + ex.getMessage();
            logger.error(errorInfo);
            resp = ProtocolUtils.buildStatus(RsfConstants.RSF_Response, requestID, ProtocolStatus.InvokeError, errorInfo);
        }
        return resp;
    }
    /**
     * 收到Request请求，并将该请求安排进队列，由队列安排方法调用。
     * @param target 目标调用地址。
     * @param info 请求消息。
     */
    public void onRequest(InterAddress target, RequestInfo info) {
        try {
            logger.debug("received request({}) full = {}", info.getRequestID());
            String serviceUniqueName = "[" + info.getServiceGroup() + "]" + info.getServiceName() + "-" + info.getServiceVersion();
            Executor executor = executesManager.getExecute(serviceUniqueName);
            executor.execute(new RemoteRsfCallerProcessing(target, this, info));//放入业务线程准备执行
        } catch (RejectedExecutionException e) {
            String msgLog = "rejected request, queue is full." + e.getMessage();
            logger.warn(msgLog, e);
            ResponseInfo resp = ProtocolUtils.buildStatus(RsfConstants.RSF_Response, info.getRequestID(), ProtocolStatus.QueueFull, msgLog);
            this.senderListener.sendResponse(target, resp);
        }
    }
    //
    //
    //
    /**获取消息监听器。*/
    RemoteSenderListener getSenderListener() {
        return this.senderListener;
    }
}