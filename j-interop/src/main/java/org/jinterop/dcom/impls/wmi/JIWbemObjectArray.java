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

import java.util.LinkedList;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.impls.wmi.structures.JICIMClassPart;
import org.jinterop.dcom.impls.wmi.structures.JICIMClassType;
import org.jinterop.dcom.impls.wmi.structures.JICIMInstanceType;
import org.jinterop.dcom.impls.wmi.structures.JICIMObjectBlock;

/**
 *
 * @author danny
 */
public class JIWbemObjectArray {

    private LinkedList<JICIMObjectBlock> cimObjects = new LinkedList<JICIMObjectBlock>();

    public void decode(NetworkDataRepresentation ndr, NdrBuffer ndrBuff) {
        CIMBuffer cimBuff = new CIMBuffer(ndrBuff.buf);
        cimBuff.move(8);
        int puReturned = cimBuff.getUint32();
        int pdwBuffSize = cimBuff.getUint32();
        cimBuff.move(8);
        int dwByteOrdering = cimBuff.getUint32();
        String abSignature = new String(cimBuff.getBytes(8));
        int dwSizeOfHeader1 = cimBuff.getUint32();
        int dwDataSize1 = cimBuff.getUint32();
        int dwFlags = cimBuff.getUint32();
        byte bVersion = cimBuff.getByte();
        byte bPacketType = cimBuff.getByte();
        int dwSizeOfHeader2 = cimBuff.getUint32();
        int dwDataSize2 = cimBuff.getUint32();
        int dwSizeOfHeader3 = cimBuff.getUint32();
        int dwDataSize3 = cimBuff.getUint32();
        int dwNumObjects = cimBuff.getUint32();
        JIWbemDataPacketObject object = null;
        for (int i = 0; i < dwNumObjects; i++) {
            JICIMClassPart currentClassPart = null;
            if (object != null) {
                JICIMClassType classType = object.cimObject().getCIMClass();
                if (classType != null) {
                    currentClassPart = classType.getCurrentClass();
                }
                JICIMInstanceType instanceType = object.cimObject().getCIMInstance();
                if (instanceType != null) {
                    currentClassPart = instanceType.getCurrentClass();
                }
            }
            object = new JIWbemDataPacketObject(cimBuff, currentClassPart);
            this.getCimObjects().add(object.cimObject());;
        }
        ndrBuff.advance(pdwBuffSize);
    }

    /**
     * @return the cimObjects
     */
    public LinkedList<JICIMObjectBlock> getCimObjects() {
        return cimObjects;
    }
}
