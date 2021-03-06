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
package net.hasor.rsf.center.domain;
/**
 * 各种常量
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfCenterConstants {
    public static final String RSF_APP_CODE   = "RSF_APP_CODE";  //应用程序编码
    public static final String RSF_AUTH_CODE  = "RSF_AUTH_CODE"; //授权码
    public static final String RSF_VERSION    = "RSF_VERSION";   //客户端版本
    //
    public static final String RSF_CHECK_CODE = "RSF_CHECK_CODE";//注册中心客户端检查码：RSF_APP_CODE 和 RSF_AUTH_CODE 的MD5串
}