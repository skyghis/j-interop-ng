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

import java.util.HashMap;
import java.util.Map;
import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 */
public class JICIMClassPart {

    public static JICIMClassPart readFrom(CIMBuffer bb) {
        JICIMClassPart klass = new JICIMClassPart();
        klass.init(bb);
        return klass;
    }
    private JICIMClassHeader header;
    private JICIMDerivationList derivationList;
    private JICIMQualifierSet classQualifierSet;
    private JICIMPropertyLookupTable propertyLookupTable;
    private JICIMHeap classHeap;
    private JICIMNDTableValueTable tables;

    private void init(CIMBuffer bb) {
        this.header = JICIMClassHeader.readFrom(bb);
        this.derivationList = JICIMDerivationList.readFrom(bb);
        this.classQualifierSet = JICIMQualifierSet.readFrom(bb);
        this.propertyLookupTable = JICIMPropertyLookupTable.readFrom(bb);
        this.tables = JICIMNDTableValueTable.readFrom(bb,
                this.propertyLookupTable.getCount(),
                this.getHeader().getNdTableValueTableLength());
        this.classHeap = JICIMHeap.readFrom(bb);
    }

    /**
     * @return the header
     */
    public JICIMClassHeader getHeader() {
        return header;
    }

    public Map<String, JICIMPropertyInfo> getPropertiesInfo() {
        HashMap<String, JICIMPropertyInfo> map = new HashMap<String, JICIMPropertyInfo>();
        for (JICIMPropertyLookup lookup : this.propertyLookupTable.getProperties()) {
            String propertyName = this.classHeap.getString(lookup.getNameRef().getOffset()).toString();
            JICIMPropertyInfo propertyInfo = this.classHeap.getPropertyInfo(lookup.getInfoRef().getOffset());
            map.put(propertyName, propertyInfo);
        }
        return map;
    }

    public int getPropertiesCount() {
        return this.propertyLookupTable.getCount();
    }

    public String getName() {
        return this.classHeap.getString(this.getHeader().getClassName().getOffset()).toString();
    }

    /**
     * @return the dataTables
     */
    public JICIMNDTableValueTable getTables() {
        return tables;
    }

    public Map<String, String> getProperties() {
        Map<String, String> objectMap = new HashMap<String, String>();
        HashMap<String, JICIMPropertyInfo> map = new HashMap<String, JICIMPropertyInfo>();
        for (JICIMPropertyLookup lookup : this.propertyLookupTable.getProperties()) {
            String propertyName = this.classHeap.getString(lookup.getNameRef().getOffset()).toString();
            JICIMPropertyInfo propertyInfo = this.classHeap.getPropertyInfo(lookup.getInfoRef().getOffset());
            int offset = propertyInfo.getValueTableOffset();
            JICIMType propType = propertyInfo.getType();
            Object value = "N/A";
            if (propType.isArray()) {
                JICIMHeapRef ref = this.tables.getHeapRef(offset);
                if (ref.getOffset() > 0) {
                    JICIMEncodedArray array = this.classHeap.getEncodedArray(ref.getOffset(), propType);
                    value = array;
                }
            } else if (propType == JICIMType.CIM_TYPE_STRING) {
                JICIMHeapRef ref = this.tables.getHeapRef(offset);
                value = this.classHeap.getString(ref.getOffset());
            } else {
                value = this.tables.getValue(propertyInfo.getValueTableOffset(), propType);
            }
            objectMap.put(propertyName, value.toString());
        }
        return objectMap;
    }
}
