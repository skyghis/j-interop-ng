/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
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
package org.jinterop.dcom.core;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.JIComTransportFactory;
import rpc.Endpoint;
import rpc.Stub;

/**
 * Class only used for Oxid ping requests between the Java client and the COM
 * server. This is not for reverse operations i.e COM client and Java server.
 * That is handled at the OxidResolverImpl level in JIComOxidRuntimeHelper,
 * since each of the Oxid Resolver has a separate thread for COM client.
 *
 *
 * @exclude
 * @since 1.0
 *
 */
final class JIComOxidStub extends Stub {

    private static final Properties DEFAULTS = new Properties();

    static {
        DEFAULTS.put("rpc.ntlm.lanManagerKey", "false");
        DEFAULTS.put("rpc.ntlm.sign", "false");
        DEFAULTS.put("rpc.ntlm.seal", "false");
        DEFAULTS.put("rpc.ntlm.keyExchange", "false");
        DEFAULTS.put("rpc.connectionContext", "rpc.security.ntlm.NtlmConnectionContext");
    }

    JIComOxidStub(String address, String domain, String username, String password) {
        super();
        super.setTransportFactory(JIComTransportFactory.getSingleTon());
        super.setProperties(new Properties(DEFAULTS));
        super.getProperties().setProperty("rpc.security.username", username);
        super.getProperties().setProperty("rpc.security.password", password);
        super.getProperties().setProperty("rpc.ntlm.domain", domain);
        super.setAddress("ncacn_ip_tcp:" + address + "[135]");
    }

    public byte[] call(boolean isSimplePing, byte[] setId, List<JIObjectId> listOfAdds, List<JIObjectId> listOfDels, int seqNum) {
        PingObject pingObject = new PingObject();
        pingObject.setId = setId;
        pingObject.listOfAdds = listOfAdds;
        pingObject.listOfDels = listOfDels;
        pingObject.seqNum = seqNum;

        if (isSimplePing) {
            pingObject.opnum = 1;
        } else {
            pingObject.opnum = 2;
        }

        try {
            call(Endpoint.IDEMPOTENT, pingObject);
        } catch (IOException e) {
            JISystem.getLogger().throwing("JIComOxidStub", "call", e);
        }

        //returns setId.
        return pingObject.setId;
    }

    public void close() {
        try {
            detach();
        } catch (IOException e) {
        }
    }

    @Override
    protected String getSyntax() {
        return "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0";
    }
}
