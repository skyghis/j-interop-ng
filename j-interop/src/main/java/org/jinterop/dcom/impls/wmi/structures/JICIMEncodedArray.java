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

import java.util.ArrayList;
import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 * @see http://msdn.microsoft.com/en-us/library/cc250915(v=PROT.10).aspx
 */
public class JICIMEncodedArray {

    public static JICIMEncodedArray readFrom(CIMBuffer bb, JICIMType elemType) {
        JICIMEncodedArray encArray = new JICIMEncodedArray(elemType);
        encArray.init(bb);
        return encArray;
    }
    private ArrayList<JICIMEncodedValue> items;
    private final JICIMType elemType;

    private JICIMEncodedArray(JICIMType elemType) {
        this.elemType = elemType.baseType();
    }

    private void init(CIMBuffer bb) {
        int arrayCount = bb.getUint32();
        this.items = new ArrayList<JICIMEncodedValue>(arrayCount);
        for (int i = 0; i < arrayCount; i++) {
            this.items.add(JICIMEncodedValue.readFrom(bb, elemType));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (JICIMEncodedValue value : this.items) {
            sb.append(value);
            sb.append(",");
        }
        return sb.toString();
    }
}
