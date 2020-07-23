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

package org.jinterop.dcom.impls.wmi.structures;

import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 */
public enum JICIMType {

    CIM_TYPE_SINT8((short) 16),
    CIM_TYPE_UINT8((short) 17),
    CIM_TYPE_SINT16((short) 2),
    CIM_TYPE_UINT16((short) 18),
    CIM_TYPE_SINT32((short) 3),
    CIM_TYPE_UINT32((short) 19),
    CIM_TYPE_SINT64((short) 20),
    CIM_TYPE_UINT64((short) 21),
    CIM_TYPE_REAL32((short) 4),
    CIM_TYPE_REAL64((short) 5),
    CIM_TYPE_BOOLEAN((short) 11),
    CIM_TYPE_STRING((short) 8),
    CIM_TYPE_DATETIME((short) 101),
    CIM_TYPE_REFERENCE((short) 102),
    CIM_TYPE_CHAR16((short) 103),
    CIM_TYPE_OBJECT((short) 13),
    CIM_ARRAY_SINT8((short) 8208),
    CIM_ARRAY_UINT8((short) 8209),
    CIM_ARRAY_SINT16((short) 8194),
    CIM_ARRAY_UINT16((short) 8210),
    CIM_ARRAY_SINT32((short) 8195),
    CIM_ARRAY_UINT32((short) 8201),
    CIM_ARRAY_SINT64((short) 8202),
    CIM_ARRAY_UINT64((short) 8203),
    CIM_ARRAY_REAL32((short) 8196),
    CIM_ARRAY_REAL64((short) 8197),
    CIM_ARRAY_BOOLEAN((short) 8203),
    CIM_ARRAY_STRING((short) 8200),
    CIM_ARRAY_DATETIME((short) 8293),
    CIM_ARRAY_REFERENCE((short) 8294),
    CIM_ARRAY_CHAR16((short) 8295),
    CIM_ARRAY_OBJECT((short) 8205);

    public static JICIMType readFrom(CIMBuffer buff) {
        short val = buff.getInt16();
        buff.move(2);
        return valueFor(val);
    }

    public static JICIMType valueFor(short val) {
        for (JICIMType type : JICIMType.values()) {
            if (type.value == val) {
                return type;
            }
        }
        throw new RuntimeException("Invalid JCIMType " + val);
    }
    private short value;

    private JICIMType(short value) {
        this.value = value;
    }

    public boolean isArray() {
        return ((this.value & (short) 0x2000) != 0);
    }

    public JICIMType baseType() {
        short baseType = (short) (this.value & (short) (~0x2000));
        return valueFor(baseType);
    }
}
