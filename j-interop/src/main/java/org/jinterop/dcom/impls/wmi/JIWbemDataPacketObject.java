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
import org.jinterop.dcom.impls.wmi.structures.JICIMClassPart;
import org.jinterop.dcom.impls.wmi.structures.JICIMObjectBlock;

/**
 *
 * @author danny
 */
public class JIWbemDataPacketObject {

    private static final byte WBEMOBJECT_CLASS = 1;
    private static final byte WBEMOBJECT_INSTANCE = 2;
    private static final byte WBEMOBJECT_INSTANCE_NOCLASS = 3;
    private JICIMObjectBlock object;
    private UUID classID;

    public JIWbemDataPacketObject(CIMBuffer buff, JICIMClassPart classPart) {
        int dwSizeOfHeader = buff.getUint32();
        int dwSizeOfData = buff.getUint32();        
        byte bObjectType = buff.getByte();
        int nextObject = buff.getPosition() + dwSizeOfData;
        if (bObjectType == WBEMOBJECT_CLASS) {
            dwSizeOfHeader = buff.getUint32();
            int dwSizeOfObjectData = buff.getUint32();
            this.object = JICIMObjectBlock.readFrom(buff, null);
        } else if ((bObjectType == WBEMOBJECT_INSTANCE) || (bObjectType == WBEMOBJECT_INSTANCE_NOCLASS)) {
            dwSizeOfHeader = buff.getUint32();
            int dwSizeOfObjectData = buff.getUint32();
            this.classID = buff.getUUID();
            this.object = JICIMObjectBlock.readFrom(buff,classPart);
        }
        buff.move(nextObject - buff.getPosition()); // align to next object
    }
    
    public JICIMObjectBlock cimObject() {
        return this.object;
    }

}
