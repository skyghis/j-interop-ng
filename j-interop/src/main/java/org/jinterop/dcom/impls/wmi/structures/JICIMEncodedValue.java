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
 * @see http://msdn.microsoft.com/en-us/library/cc250915(v=PROT.10).aspx
 */
//todo: If the value type is CimArrayType, the EncodedValue is a HeapRef to the Encoded-Array.
public class JICIMEncodedValue {

    public static JICIMEncodedValue readFrom(CIMBuffer bb, JICIMType type) {
        JICIMEncodedValue value = new JICIMEncodedValue(type);
        value.init(bb);
        return value;

    }
    private Object value = null;
    private final JICIMType type;

    private JICIMEncodedValue(JICIMType type) {
        this.type = type;
    }

    private void init(CIMBuffer bb) {
        switch (this.type) {
            case CIM_TYPE_UINT8:
            case CIM_TYPE_SINT8:
                this.value = bb.getByte();
                break;
            case CIM_TYPE_UINT16:
                this.value = bb.getUint16();
            case CIM_TYPE_SINT16:
                this.value = bb.getInt16();
                break;
            case CIM_TYPE_UINT32:
                this.value = bb.getUint32();
                break;
            case CIM_TYPE_SINT32:
                this.value = bb.getInt32();
                break;
            case CIM_TYPE_REAL32:
                this.value = bb.getFloat();
                break;
            case CIM_TYPE_REAL64:
                this.value = bb.getDouble();
                break;
            case CIM_TYPE_UINT64:
            case CIM_TYPE_SINT64:
                this.value = bb.getUint64();
                break;
            case CIM_TYPE_CHAR16:
                this.value = bb.getChar();
                break;
            case CIM_TYPE_BOOLEAN:
                this.value = JICIMBOOL.readFrom(bb);
                break;
            case CIM_TYPE_DATETIME:
            case CIM_TYPE_STRING:
            case CIM_TYPE_REFERENCE:
                this.value = JICIMHeapRef.readFrom(bb);
                break;
            case CIM_TYPE_OBJECT:
                //If the value type is CIM-TYPE-OBJECT, 
                //the EncodedValue is a HeapRef to the object encoded as 
                //an ObjectEncodingLength (section 2.2.4) followed by an ObjectBlock (section 2.2.5).
                break;
        }
    }

    public int encodingLength() {
        switch (this.type) {
            case CIM_TYPE_UINT8:
            case CIM_TYPE_SINT8:
                return 1;
            case CIM_TYPE_UINT16:
            case CIM_TYPE_SINT16:
            case CIM_TYPE_CHAR16:
            case CIM_TYPE_BOOLEAN:
                return 2;
            case CIM_TYPE_DATETIME:
            case CIM_TYPE_STRING:
            case CIM_TYPE_REFERENCE:
            case CIM_TYPE_UINT32:
            case CIM_TYPE_SINT32:
            case CIM_TYPE_REAL32:
                return 4;
            case CIM_TYPE_REAL64:
            case CIM_TYPE_UINT64:
            case CIM_TYPE_SINT64:
                return 8;
            case CIM_TYPE_OBJECT:
                return 4; //todo:                
        }
        return 4; // todo
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}