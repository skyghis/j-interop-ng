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
public class JICIMInstanceType extends JICIMEncoding {

    public static JICIMInstanceType readFrom(CIMBuffer bb, JICIMClassPart classPart) {
        JICIMInstanceType instance = new JICIMInstanceType();
        instance.init(bb, classPart);
        return instance;
    }
    private JICIMClassPart currentClass;
    private JICIMHeapRef instanceClassName;
    private JICIMQualifierSet instanceQualifierSet;
    private JICIMQualifierSet instancePropQualifierSet;
    private JICIMHeap instanceHeap;
    private JICIMNDTableValueTable tables;

    public JICIMInstanceType()
    {
        
    }
    
    public JICIMInstanceType(JICIMClassType fromClass) {
        this.currentClass = fromClass.getCurrentClass();        
    }

    private void init(CIMBuffer bb, JICIMClassPart classPart) {
        if (classPart != null) {
            this.currentClass = classPart;
        } else {
            this.currentClass = JICIMClassPart.readFrom(bb);
        }
        int encodingLength = bb.getUint32();
        byte instanceFlags = bb.getByte(); // must be 0
        this.instanceClassName = JICIMHeapRef.readFrom(bb);
        int propertyCount = this.getCurrentClass().getPropertiesCount();
        int tablesLen = this.getCurrentClass().getHeader().getNdTableValueTableLength();
        this.tables = JICIMNDTableValueTable.readFrom(bb, propertyCount, tablesLen);
        this.instanceQualifierSet = JICIMQualifierSet.readFrom(bb);
        byte instPropQualSetFlag = bb.getByte();
        if (instPropQualSetFlag == 0x2) {
            this.instancePropQualifierSet = JICIMQualifierSet.readFrom(bb);
        }
        this.instanceHeap = JICIMHeap.readFrom(bb);
    }

    /**
     * @return the currentClass
     */
    public JICIMClassPart getCurrentClass() {
        return currentClass;
    }

    @Override
    public String getName() {
        return this.instanceHeap.getString(this.instanceClassName.getOffset()).toString();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> objectMap = new HashMap<String, String>();
        Map<String, JICIMPropertyInfo> properties = this.getCurrentClass().getPropertiesInfo();
        for (Map.Entry<String, JICIMPropertyInfo> prop : properties.entrySet()) {
            int offset = prop.getValue().getValueTableOffset();
            JICIMType propType = prop.getValue().getType();
            Object value = "N/A";
            if (propType.isArray()) {
                JICIMHeapRef ref = this.tables.getHeapRef(offset);
                if (ref.getOffset() > 0) {
                    JICIMEncodedArray array = this.instanceHeap.getEncodedArray(ref.getOffset(), propType);
                    value = array;
                }
            } else if (propType == JICIMType.CIM_TYPE_STRING) {
                JICIMHeapRef ref = this.tables.getHeapRef(offset);
                value = this.instanceHeap.getString(ref.getOffset());
            } else {
                value = this.tables.getValue(prop.getValue().getValueTableOffset(), propType);
            }
            objectMap.put(prop.getKey(), value.toString());
        }
        return objectMap;
    }

    public void setProperty(String name, String value) {
        JICIMPropertyInfo propertyInfo = this.currentClass.getPropertiesInfo().get(name);
        
    }
}
