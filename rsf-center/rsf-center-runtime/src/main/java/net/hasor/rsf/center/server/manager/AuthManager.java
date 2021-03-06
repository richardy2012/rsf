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
package net.hasor.rsf.center.server.manager;
import org.more.util.CommonCodeUtils.MD5;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
/**
 * 
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class AuthManager {
    @Inject
    private RsfCenterCfg rsfCenterCfg;
    public boolean checkAuth(String appCode, String authCode) {
        // TODO Auto-generated method stub
        System.out.println("checkAuth -> appCode=" + appCode + " ,authCode=" + authCode);
        return true;
    }
    public String checkAuth(InterAddress targetAddress) {
        try {
            String appCode = "anonymous";
            String authCode = "";
            return MD5.getMD5(appCode + authCode);
        } catch (Exception e) {
            return "";
        }
    }
}