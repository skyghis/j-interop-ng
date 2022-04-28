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

import java.io.Serializable;
import ndr.NetworkDataRepresentation;
import rpc.core.UUID;

final class JIStdObjRef implements Serializable {

    private static final long serialVersionUID = 7714589108476632990L;

    private JIStdObjRef() {
    }

    private int flags = 0x0;
    private int publicRefs = -1;
    private byte[] oxid = null;
    private byte[] oid = null;
    private String ipidOfthisObjectRef = null;
    //private String oidString = null;

    /**
     * Resolver address are taken of localhost
     *
     */
    JIStdObjRef(String ipid, JIOxid oxid, JIObjectId oid) {
        this.ipidOfthisObjectRef = ipid;
        this.oxid = oxid.getOXID();
        this.oid = oid.getOID();
        //this.oidString = oid.toString();
        this.publicRefs = 5;
    }

    static JIStdObjRef decode(NetworkDataRepresentation ndr) {
        JIStdObjRef objRef = new JIStdObjRef();

        objRef.flags = ndr.readUnsignedLong();
        objRef.publicRefs = ndr.readUnsignedLong();

        objRef.oxid = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8);

        objRef.oid = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8);

        // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), objRef.oid, 0, objRef.oid.length);
        // objRef.oidString = byteArrayOutputStream.toString();
        objRef.ipidOfthisObjectRef = new UUID(ndr.getBuffer()).toString();
        //if (LOGGER.isLoggable(Level.WARNING))
        //{
        //  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //  jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), objRef.oid, 0, objRef.oid.length);
        //  LOGGER.warning("Decode of StdObjref Adding references for " + objRef.ipidOfthisObjectRef + " , num references recieved from COM server: " + objRef.publicRefs + " , the OID is " + byteArrayOutputStream.toString());
        //  JISession.debug_addIpids(objRef.ipidOfthisObjectRef, 5);
        //}
        return objRef;
    }

    public int getFlags() {
        return flags;
    }

    public int getPublicRefs() {
        return publicRefs;
    }

    public byte[] getOxid() {
        return oxid;
    }

    public byte[] getObjectId() {
        return oid;
    }

    public String getIpid() {
        return ipidOfthisObjectRef;
    }

    public void encode(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedLong(flags);
        ndr.writeUnsignedLong(publicRefs);
        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oxid);
        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oid);
        new UUID(ipidOfthisObjectRef).encode(ndr.getBuffer());
    }

    @Override
    public String toString() {
        String retVal = "IPID: " + ipidOfthisObjectRef;//+ " , OID: " + oidString;
        return retVal;
    }
}
