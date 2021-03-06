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
package net.hasor.rsf.center.server.utils;
import org.more.json.JSON;
import org.more.json.JSONEnumConvertor;
import net.hasor.rsf.center.server.push.RsfCenterEventEnum;
/**
 * 
 * @version : 2016年3月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class JsonUtils {
    private static final Object LOCK = new Object();
    private static JSON         json;
    public static String toJsonString(Object object) {
        if (json == null) {
            synchronized (LOCK) {
                if (json == null) {
                    json = new JSON();
                    json.addConvertor(RsfCenterEventEnum.class, new JSONEnumConvertor(false));
                }
            }
        }
        return json.toJSON(object);
    }
}