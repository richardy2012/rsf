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
package net.hasor.rsf;
import java.io.IOException;
import net.hasor.core.Settings;
import net.hasor.rsf.address.InterAddress;
/**
 * RSF 配置。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfSettings extends Settings {
    /**获取当发起请求的时候所使用的RSF协议版本。*/
    public byte getProtocolVersion();
    /**获取RSF框架版本。*/
    public String getVersion(); //
    /**获取默认超时时间。*/
    public int getDefaultTimeout();
    /**获取默认超时时间。*/
    public String getDefaultGroup();
    /**获取默认超时时间。*/
    public String getDefaultVersion();
    /**获取默认超时时间。*/
    public String getDefaultSerializeType();
    //
    /**获取配置的服务器端选项*/
    public RsfOptionSet getServerOption();
    /**获取配置的客户端选项*/
    public RsfOptionSet getClientOption();
    //
    /**处理网络IO数据包的线程数*/
    public int getNetworkWorker();
    /**处理网络监听请求的线程数*/
    public int getNetworkListener();
    //
    /**处理任务队列的最大大小，作为服务端当队列满了之后所有新进来的请求都会被回应 ChooseOther*/
    public int getQueueMaxSize();
    /**the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set.*/
    public int getQueueMinPoolSize();
    /**the maximum number of threads to allow in the pool.*/
    public int getQueueMaxPoolSize();
    /**(SECONDS),when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.*/
    public long getQueueKeepAliveTime();
    //
    /**客户端请求超时时间*/
    public int getRequestTimeout();
    /**最大并发请求数*/
    public int getMaximumRequest();
    /** 并发调用请求限制策略，当并发调用达到限制值后的策略（Reject 抛出异常，WaitSecond 等待1秒重试）*/
    public SendLimitPolicy getSendLimitPolicy();
    /**客户端发起一个连接请求所允许的最大耗时（单位毫秒）*/
    public int getConnectTimeout();
    //
    /**获取本地服务绑定地址*/
    public String getBindAddress();
    /**获取本地服务绑定端口*/
    public int getBindPort();
    //
    /**获取注册中心服务地址*/
    public InterAddress[] getCenterServerSet();
    /**与注册中心之间接口调用所使用的超时时间（一般不需要配置，除非发生注册中心接口调用超时异常）*/
    public int getCenterRsfTimeout();
    /**与注册中心保持状态所用的心跳时间间隔*/
    public int getCenterHeartbeatTime();
    //
    /**获取本机所属单元*/
    public String getUnitName();
    /**获取地址失效之后，等待重新尝试连接的时间(毫秒)。默认60秒。*/
    public int getInvalidWaitTime();
    /**自动刷新地址本缓存的时间，默认6分钟。*/
    public long getRefreshCacheTime();
    /**启用磁盘地址本缓存，在refreshCacheTime期间每隔1小时自动写入一次。（被回收的服务不享受此待遇）*/
    public boolean islocalDiskCache();
    /**是否启用注册中心功能。*/
    public boolean isEnableCenter();
    /**应用自动上线*/
    public boolean isAutomaticOnline();
    //
    /**RSF管理控制台监听的端口号（Telnet）*/
    public int getConsolePort();
    /**准许的ip地址列表。*/
    public String[] getConsoleInBoundAddress();
    //
    /**重新加载Rsf配置*/
    public void refreshRsfConfig() throws IOException;
}