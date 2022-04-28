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
import java.util.List;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JIComVersion;
import org.jinterop.dcom.common.JIRuntimeException;
import rpc.core.UUID;

final class JIRemActivation extends NdrObject {

    public static final int RPC_C_IMP_LEVEL_IDENTIFY = 2;
    public static final int RPC_C_IMP_LEVEL_IMPERSONATE = 3;
    private int impersonationLevel = RPC_C_IMP_LEVEL_IMPERSONATE;
    private int mode = 0;
    private String monikerName = null;
    private UUID clsid = null;
    private boolean activationSuccessful = false;
    private JIOrpcThat orpcthat = null;
    private byte[] oxid = null;
    private JIDualStringArray dualStringArrayForOxid = null;
    private String ipid = null;
    private int authenticationHint = -1;
    private JIComVersion comVersion = null;
    private int hresult = -1;
    private JIInterfacePointer mInterfacePointer = null;
    boolean isDual = false;
    String dispIpid = null;
    int dispRefs = 5;
    byte[] dispOid = null;

    JIRemActivation(String clsid) {
        //10000002-0000-0000-0000-000000000001 Inside DCOM
        this.clsid = new UUID(clsid);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setClientImpersonationLevel(int implLevel) {
        impersonationLevel = implLevel;
    }

    public void setfileMonikerAtServer(String name) {
        if (name != null && !name.isEmpty()) {
            monikerName = name;
        }
    }

    @Override
    public int getOpnum() {
        return 0;
    }

    @Override
    public void write(NetworkDataRepresentation ndr) {

        JIOrpcThis orpcThis = new JIOrpcThis();
        orpcThis.encode(ndr);

        //JIClsid of the component being activated.
        new UUID(clsid.toString()).encode(ndr.buf);
        if (monikerName == null) {
            ndr.writeUnsignedLong(0);
        } else {
            ndr.writeCharacterArray(monikerName.toCharArray(), 0, monikerName.length()); // Object Name
        }

        ndr.writeUnsignedLong(0); // Minterface pointer
        ndr.writeUnsignedLong(impersonationLevel); // impersonation level
        ndr.writeUnsignedLong(mode); //mode, when object name , interface pointer are not null , this is passed directly to IPersistFile:Load

        ndr.writeUnsignedLong(2); //No. of IIDs requested.

        ndr.writeUnsignedLong(new Object().hashCode());

        ndr.writeUnsignedLong(2); //Array length

        //IID of IUnknown , this is hard coded here, standard way of COM is to first get a handle to the IUnknown
        new UUID("00000000-0000-0000-c000-000000000046").encode(ndr.buf);
        //checking for IDispatch support
        new UUID("00020400-0000-0000-c000-000000000046").encode(ndr.buf);

        ndr.writeUnsignedLong(1); //Protocol Sequences available
        ndr.writeUnsignedLong(1); //Array length
        ndr.writeUnsignedShort(7); //TCP

        byte[] address = JISession.getLocalhostAddressAsIPbytes();

        ndr.writeUnsignedShort(address[0]);
        ndr.writeUnsignedShort(address[1]);
        ndr.writeUnsignedShort(address[2]);
        ndr.writeUnsignedShort(address[3]);
        ndr.writeUnsignedShort(0);
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {

        //first take out JIOrpcThat
        orpcthat = JIOrpcThat.decode(ndr);

        //now fill the oxid
        oxid = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8);

        int skipdual = ndr.readUnsignedLong();

        if (skipdual != 0) {
            ndr.readUnsignedLong();
            //now fill the dual string array for oxid bindings, the call to IRemUnknown will be
            //directed to this address and the port in that address.
            dualStringArrayForOxid = JIDualStringArray.decode(ndr);
        }
        //get the IPID which will be the "Object" in the call to IRemUknown. This is the IPID of the
        //component which has been specified as the JIClsid. This may differ in multiple invokations of
        //of remote activation as everytime a new object may be created at the server per call. This is all
        //server implementation dependent.
        ipid = (new UUID(ndr.getBuffer()).toString());
        //read the auth hint
        authenticationHint = ndr.readUnsignedLong();

        comVersion = new JIComVersion();
        comVersion.setMajorVersion(ndr.readUnsignedShort());
        comVersion.setMinorVersion(ndr.readUnsignedShort());

        hresult = ndr.readUnsignedLong();

        if (hresult != 0) {
            //System.out.println("EXCEPTION FROM SERVER ! --> " + "0x" + Long.toHexString(hresult).substring(8));
            throw new JIRuntimeException(hresult);
        }

        //int numRet = ndr.readUnsignedLong();//Number of interface pointers returned. Currently only 2.
        JIArray array = new JIArray(JIInterfacePointer.class, null, 1, true);
        List<JIPointer> listOfDefferedPointers = new ArrayList<>();
        array = (JIArray) JIMarshalUnMarshalHelper.deSerialize(ndr, array, listOfDefferedPointers, JIFlags.FLAG_NULL, new HashMap<>());
        int x = 0;
        while (x < listOfDefferedPointers.size()) {
            List<JIPointer> newList = new ArrayList<>();
            JIPointer replacement = (JIPointer) JIMarshalUnMarshalHelper.deSerialize(ndr, listOfDefferedPointers.get(x), newList, JIFlags.FLAG_NULL, null);
            listOfDefferedPointers.get(x).replaceSelfWithNewPointer(replacement); //this should replace the value in the original place.
            x++;
            listOfDefferedPointers.addAll(x, newList);
        }
        JIInterfacePointer[] arrayObjs = (JIInterfacePointer[]) array.getArrayInstance();
        mInterfacePointer = arrayObjs[0];
        if (arrayObjs[1] != null) {
            //dual is supported since the IDispatch was obtained
            isDual = true;
            //eat this keeping only the IPID for cleanup , let the user perform another queryInterface for this.
            JIInterfacePointer ptr = arrayObjs[1];
            dispIpid = ptr.getIPID();
            dispOid = ptr.getOID();
            dispRefs = ((JIStdObjRef) ptr.getObjectReference(JIInterfacePointer.OBJREF_STANDARD)).getPublicRefs();
        }

        array = new JIArray(Integer.class, null, 1, true);
        //ignore the retvals
        JIMarshalUnMarshalHelper.deSerialize(ndr, array, null, JIFlags.FLAG_NULL, null);

        activationSuccessful = true;

    }

    public boolean isActivationSuccessful() {
        return activationSuccessful;
    }

    public JIOrpcThat getORPCThat() {
        return orpcthat;
    }

    public byte[] getOxid() {
        return oxid;
    }

    public JIDualStringArray getDualStringArrayForOxid() {
        return dualStringArrayForOxid;
    }

    public int getAuthenticationHint() {
        return authenticationHint;
    }

    public JIComVersion getComVersion() {
        return comVersion;
    }

    public int getHresult() {
        return hresult;
    }

    public JIInterfacePointer getMInterfacePointer() {
        return mInterfacePointer;
    }

    public String getIPID() {
        return ipid;
    }
}
