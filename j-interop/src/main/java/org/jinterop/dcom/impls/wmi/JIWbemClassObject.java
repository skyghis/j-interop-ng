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

import java.util.UUID;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.impls.wmi.structures.JICIMClassType;
import org.jinterop.dcom.impls.wmi.structures.JICIMMethod;
import org.jinterop.dcom.impls.wmi.structures.JICIMObjectBlock;

/**
 *
 * @author danny
 */
public class JIWbemClassObject {

    private UUID unmarshaler;
    private UUID uuid;
    private JICIMObjectBlock cimObject;
 
    public JIWbemClassObject() {
    }

    public void decode(NetworkDataRepresentation ndr, NdrBuffer buffer) {
        CIMBuffer cimBuffer = new CIMBuffer(buffer.buf);
        cimBuffer.move(56);
        uuid = cimBuffer.getUUID();
        int signature = cimBuffer.getUint32();//0x12345678 little-endian
        int objectEncodingLength = cimBuffer.getInt32();
        cimObject = JICIMObjectBlock.readFrom(cimBuffer, null);
        unmarshaler = cimBuffer.getUUID();
        buffer.index = cimBuffer.getPosition();     
    }

    public JICIMMethod getMethod(String methodName) {
        JICIMClassType cimClass =
                this.cimObject.getCIMClass();
        if (cimClass == null) {
            return null;
        }
        return cimClass.getMethod(methodName);
    }

    public void setProperty(String name, String value) {
        this.cimObject.getCIMInstance().setProperty(name, value);
    }

}
