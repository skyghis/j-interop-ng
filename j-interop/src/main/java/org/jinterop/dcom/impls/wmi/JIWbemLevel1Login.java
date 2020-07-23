/**j-Interop (Pure Java implementation of DCOM protocol)
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
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;

/**
 *
 * @author danny
 */
public class JIWbemLevel1Login {

    private final String JIWbemLevel1Login_IID = "F309AD18-D86A-11d0-A075-00C04FB68820";
    private static final String CLSID_IWbemLevel1Login = "8BC3F05E-D86B-11d0-A075-00C04FB68820";
    private IJIComObject iface = null;

    public JIWbemLevel1Login(String hostName, JISession session) throws JIException, UnknownHostException {
        JIComServer comServer = new JIComServer(JIClsid.valueOf(CLSID_IWbemLevel1Login), hostName, session);
        IJIComObject comObject = comServer.createInstance();
        this.iface = comObject.queryInterface(JIWbemLevel1Login_IID);
    }

    @Deprecated
    public static JIWbemServices ntlmLogin(String hostName, JISession session, String namespace) throws JIException, UnknownHostException {
        JIWbemLevel1Login wbemLevel1Login = new JIWbemLevel1Login(hostName, session);
        return wbemLevel1Login.ntlmLogin(namespace, "");
    }

    public JIWbemServices ntlmLogin(String networkResource, String locale) throws JIException {
        final int OPNUM_NTLM_LOGIN = 3;
        JICallBuilder cb = new JICallBuilder(true);
        cb.setOpnum(OPNUM_NTLM_LOGIN);
        cb.addInParamAsPointer(new JIPointer(new JIString(networkResource, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);
        cb.addInParamAsPointer(new JIPointer(new JIString(locale, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);
        cb.addInParamAsInt(0, JIFlags.FLAG_NULL);
        cb.addInParamAsPointer(new JIPointer(null, true), JIFlags.FLAG_NULL);
        cb.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] res = this.iface.call(cb);
        return new JIWbemServices((IJIComObject) res[0]);
    }
}
