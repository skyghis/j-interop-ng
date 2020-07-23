/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2011  Danny Tylman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2011  Danny Tylman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
package org.jinterop.dcom.impls.wmi;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.impls.wmi.structures.JICIMEncoding;
import org.jinterop.dcom.impls.wmi.structures.JICIMObjectBlock;

/**
 *
 * @author danny
 */
public class WMIC {

    private final JIWbemLevel1Login wbemLevel1Login;
    private final JIWbemServices wbemServices;

    public WMIC(String hostName, JISession session, String nameSpace) throws JIException, UnknownHostException {
        this.wbemLevel1Login = new JIWbemLevel1Login(hostName, session);
        this.wbemServices = wbemLevel1Login.ntlmLogin(nameSpace, "");
    }

    public List<JICIMEncoding> execQuery(String wql) throws JIException {
        JIWbemWCOSmartEnum wbemEnum = this.wbemServices.execQuery(wql);
        JIWbemObjectArray objectArray = wbemEnum.next();
        LinkedList<JICIMEncoding> list = new LinkedList<JICIMEncoding>();
        for (JICIMObjectBlock object : objectArray.getCimObjects()) {
            list.add(object.getEncoding());
        }
        return list;
    }

//    public void createProcess(String cmdLine) throws JIException {
//        JIWbemClassObject obj = this.wbemServices.getObject("Win32_Process");
//        //Todo:: spawnInstance before calling execMethod.
//        JIWbemClassObject inParams = this.wbemServices.spawnInstance(obj);
//        this.wbemServices.execMethod("Win32_Process", cmdLine, inParams);
//    }
}
