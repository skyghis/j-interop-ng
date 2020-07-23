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
public class JICIMNDTableValueTable {

    public static JICIMNDTableValueTable readFrom(CIMBuffer buff, int propertyCount, int dataLen) {
        JICIMNDTableValueTable ndTableValueTable = new JICIMNDTableValueTable();
        ndTableValueTable.init(buff, propertyCount, dataLen);
        return ndTableValueTable;
    }
    private CIMBuffer valueTable;
    private CIMBuffer ndTable;

    private void init(CIMBuffer buff, int propertyCount, int dataLen) {
        if (dataLen > 0) {
            int ndLen = (propertyCount - 1) / 4 + 1;
            byte[] ndTable = new byte[ndLen];
            buff.copy(ndTable);
            byte[] valueTable = new byte[dataLen - ndLen];
            buff.copy(valueTable);
            this.ndTable = new CIMBuffer(ndTable);
            this.valueTable = new CIMBuffer(valueTable);
        }
    }

    public JICIMEncodedValue getValue(int offset, JICIMType type) {
        this.valueTable.setOffset(offset);
        return JICIMEncodedValue.readFrom(valueTable, type);
    }

    public JICIMHeapRef getHeapRef(int offset) {
        this.valueTable.setOffset(offset);
        return JICIMHeapRef.readFrom(valueTable);
    }
}
