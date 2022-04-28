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

import java.util.ArrayList;
import java.util.HashMap;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JIRuntimeException;
import rpc.core.UUID;

final class JIRemUnknown extends NdrObject {

    public static final String IID_IUNKNOWN = "00000143-0000-0000-c000-000000000046";
    private String ipidOfIUnknown = null;
    private String requestedIID = null;
    private JIInterfacePointer iidPtr = null;

    JIRemUnknown(String ipidOfIUnknown, String requestedIID) {
        this.ipidOfIUnknown = ipidOfIUnknown;
        this.requestedIID = requestedIID;
    }

    @Override
    public int getOpnum() {
        //opnum is 3 as this is a COM interface and 0,1,2 are occupied by IUnknown
        //3,4,5 by IRemUnknown and we are going to call IRemUnknown2.QI so that we get MIPs.
        return 6;
    }

    @Override
    public void write(NetworkDataRepresentation ndr) {

        JIOrpcThis orpcthis = new JIOrpcThis();
        orpcthis.encode(ndr);

        //now write the IPID
        new UUID(ipidOfIUnknown).encode(ndr.buf);
        ndr.writeUnsignedShort(1);//1 interfaces. (requested IID)
        ndr.writeUnsignedShort(0);//byte alignment
        ndr.writeUnsignedLong(1);//length of the array
        new UUID(requestedIID).encode(ndr.buf);
        ndr.writeUnsignedLong(0); //TODO: Index Matching, there seems to be a bug in
        // the jarapac system, it only reads upto (length - 6) bytes and one has to have another
        // call after that or incomplete request will go. in case no param is present just put an unsigned long = 0.
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {
        JIOrpcThat.decode(ndr);
        ndr.readUnsignedLong(); //size will be one
        int hresult1 = ndr.readUnsignedLong();
        if (hresult1 != 0) {
            //something happened.
            throw new JIRuntimeException(hresult1);
        }
        //array length
        ndr.readUnsignedLong();
        //and now the JIInterfacePointer itself.
        iidPtr = JIInterfacePointer.decode(ndr, new ArrayList<>(), JIFlags.FLAG_NULL, new HashMap<>());
        //final hresult
        hresult1 = ndr.readUnsignedLong();
        if (hresult1 != 0) {
            //something happened.
            throw new JIRuntimeException(hresult1);
        }
    }

    public JIInterfacePointer getInterfacePointer() {
        return iidPtr;
    }
}
