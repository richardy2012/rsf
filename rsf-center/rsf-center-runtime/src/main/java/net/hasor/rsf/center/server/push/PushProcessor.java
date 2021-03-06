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
package net.hasor.rsf.center.server.push;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.RsfCenterListener;
import net.hasor.rsf.center.domain.CenterEventBody;
import net.hasor.rsf.domain.provider.InstanceAddressProvider;
import net.hasor.rsf.rpc.caller.RsfServiceWrapper;
/**
 * 执行处理器，该类的作用是将事件推送到指定的客户端中去。
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class PushProcessor {
    protected Logger                       logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfContext                     rsfContext;
    private ThreadLocal<RsfCenterListener> rsfClientListener;
    //
    @Init
    public void init() {
        this.rsfClientListener = new ThreadLocal<RsfCenterListener>() {
            protected RsfCenterListener initialValue() {
                return rsfContext.getRsfClient().wrapper(RsfCenterListener.class);
            }
        };
    }
    //
    public final void doProcessor(PushEvent event) {
        if (event == null) {
            return;
        }
        if (event.getTarget() == null || event.getTarget().isEmpty()) {
            logger.error("target is empty event ->{}", event);
            return;
            //
        } else {
            for (String target : event.getTarget()) {
                try {
                    InterAddress rsfAddress = new InterAddress(target);
                    this.doProcessor(rsfAddress, event);
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
            //
        }
    }
    //
    public void doProcessor(InterAddress rsfAddress, PushEvent event) throws Throwable {
        CenterEventBody eventBody = new CenterEventBody();
        eventBody.setEventType(event.getPushEventType().forCenterEvent().getEventType());
        eventBody.setServiceID(event.getServiceID());
        eventBody.setSnapshotInfo(event.getSnapshotInfo());
        eventBody.setEventBody(event.getEventBody());
        boolean result = false;
        //
        result = sendEvent(rsfAddress, eventBody);
        if (result == false) {
            result = sendEvent(rsfAddress, eventBody);
            if (result == false) {
                result = sendEvent(rsfAddress, eventBody);
            }
        }
    }
    protected boolean sendEvent(InterAddress rsfAddress, CenterEventBody eventBody) {
        try {
            RsfCenterListener listener = this.rsfClientListener.get();
            ((RsfServiceWrapper) listener).setTarget(new InstanceAddressProvider(rsfAddress));
            return listener.onEvent(eventBody.getEventType(), eventBody);
        } catch (Throwable e) {
            logger.error("send event to target error:" + e.getMessage(), e);
            return false;
        }
    }
}