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
package net.hasor.rsf.center.server.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.domain.RsfCenterConstants;
import net.hasor.rsf.center.server.manager.AuthManager;
import net.hasor.rsf.domain.ProtocolStatus;
/**
 * 注册中心数据接收器安全过滤器，负责验证注册中心的消息是否可靠。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RsfCenterRegisterVerificationFilter implements RsfFilter {
    protected Logger    logger = LoggerFactory.getLogger(getClass());
    private String      centerVersion;
    private RsfContext  rsfContext;
    private AuthManager authManager;
    //
    public RsfCenterRegisterVerificationFilter(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.authManager = rsfContext.getAppContext().getInstance(AuthManager.class);
        this.centerVersion = this.rsfContext.getSettings().getVersion();
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal()) {
            //-如果是对外发送请求，则添加请求头参数用于远程对注册中心发来数据的校验
            InterAddress target = request.getTargetAddress();
            String checkCode = this.authManager.checkAuth(target);
            request.addOption(RsfCenterConstants.RSF_CHECK_CODE, checkCode);
            request.addOption(RsfCenterConstants.RSF_VERSION, this.centerVersion);
            chain.doFilter(request, response);
        } else {
            //-如果是来自远程的请求响应，则校验是否是和注册中心协调好的授权码
            String appCode = request.getOption(RsfCenterConstants.RSF_AUTH_CODE); //RSF_AUTH_CODE 授权码
            String authCode = request.getOption(RsfCenterConstants.RSF_APP_CODE); //RSF_APP_CODE  应用程序编码
            boolean authResult = this.authManager.checkAuth(appCode, authCode);
            if (authResult) {
                chain.doFilter(request, response);
            } else {
                response.sendStatus(ProtocolStatus.Unauthorized, "check auth code failed.");
            }
        }
    }
}