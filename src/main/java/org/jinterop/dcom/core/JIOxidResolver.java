/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2007  Vikram Roopchand
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

import java.util.ArrayList;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JIComVersion;
import org.jinterop.dcom.common.JIRuntimeException;
import rpc.core.UUID;

/**
 * Partially implements IOxidResolver interface, used only for ResolveOxid
 * calls.
 *
 *
 * @since 1.23
 *
 */
final class JIOxidResolver extends NdrObject {

    private final byte[] oxid;

    private JIDualStringArray oxidBindings = null;
    private String ipid = null;

    JIOxidResolver(final byte[] oxid) {
        this.oxid = oxid;
    }

    @Override
    public int getOpnum() {
        return 4;
    }

    @Override
    public void write(NetworkDataRepresentation ndr) {
        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oxid);
        JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) 1, new ArrayList<>(), JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr, JIArray.class, new JIArray(new Short[]{(short) 7}, true), new ArrayList<>(), JIFlags.FLAG_REPRESENTATION_ARRAY);
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {
        ndr.readUnsignedLong(); //pointer
        ndr.readUnsignedLong(); //some length component, irrelevant for us right now
        oxidBindings = JIDualStringArray.decode(ndr);
        ipid = (new UUID(ndr.getBuffer()).toString());
        //read the auth hint
        int authenticationHint = ndr.readUnsignedLong();

        JIComVersion comVersion = new JIComVersion();
        comVersion.setMajorVersion(ndr.readUnsignedShort());
        comVersion.setMinorVersion(ndr.readUnsignedShort());

        int hresult = ndr.readUnsignedLong();

        if (hresult != 0) {
            //System.out.println("EXCEPTION FROM SERVER ! --> " + "0x" + Long.toHexString(hresult).substring(8));
            throw new JIRuntimeException(hresult);
        }

    }

    JIDualStringArray getOxidBindings() {
        return oxidBindings;
    }

    String getIPID() {
        return ipid;
    }

}
