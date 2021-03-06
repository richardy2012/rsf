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
package net.hasor.rsf.console.commands;
import org.more.json.JSON;
import org.more.util.StringUtils;
import net.hasor.core.Singleton;
import net.hasor.rsf.console.RsfCmd;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
/**
 * 
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCmd({ "set", "get" })
public class GetSetRsfCommand implements RsfCommand {
    @Override
    public String helpInfo() {
        return "set/get environment variables of console .\r\n"//
                + " - rsf>get variableName                (returns variable Value.)\r\n"// 
                + " - rsf>set variableName variableValue  (set new values to variable.)";//
    }
    @Override
    public boolean inputMultiLine() {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        request.setAttr(RsfCommandRequest.WITHOUT_AFTER_CLOSE_SESSION, true);//不关闭Session
        String[] args = request.getRequestArgs();
        String argsJoin = StringUtils.join(args);
        argsJoin = argsJoin.replace("\\s+", " ");
        args = argsJoin.split("=");
        //
        if (args != null && args.length > 0) {
            String cmd = request.getCommandString();
            String varName = args[0].trim();
            //
            if ("set".equalsIgnoreCase(cmd)) {
                if (args != null && args.length > 1) {
                    String varValue = args[1].trim();
                    request.setSessionAttr(varName, varValue);
                    return "set the new value, ok.";
                } else {
                    return "args count error.";
                }
            }
            if ("get".equalsIgnoreCase(cmd)) {
                Object obj = request.getSessionAttr(varName);
                if (obj == null) {
                    return "";
                } else {
                    return JSON.toString(obj);
                }
            }
            //
            return "does not support command '" + request.getCommandString() + "'.";
        } else {
            return "args count error.";
        }
    }
}