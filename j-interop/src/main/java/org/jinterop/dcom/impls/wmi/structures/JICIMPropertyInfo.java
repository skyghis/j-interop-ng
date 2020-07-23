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
public class JICIMPropertyInfo {

    public static JICIMPropertyInfo readFrom(CIMBuffer cimBuffer) {
        JICIMPropertyInfo propertyInfo = new JICIMPropertyInfo();
        propertyInfo.init(cimBuffer);
        return propertyInfo;
    }
    private boolean inherited;
    private JICIMType type;
    private short order;
    private int valueTableOffset;
    private int classOfOrigin;
    private JICIMQualifierSet propertyQulifierSet;

    private void init(CIMBuffer cimBuffer) {
        short propertyType = cimBuffer.getUint16();
        this.inherited = ((propertyType & (short) 0x4000) != 0);
        propertyType = (short) (propertyType & (short) (~(short) 0x4000));
        this.type = JICIMType.valueFor(propertyType);
        cimBuffer.move(2);
        this.order = cimBuffer.getUint16();
        this.valueTableOffset = cimBuffer.getUint32();
        this.classOfOrigin = cimBuffer.getUint32();
        this.propertyQulifierSet = JICIMQualifierSet.readFrom(cimBuffer);
    }

    /**
     * @return the inherited
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * @return the type
     */
    public JICIMType getType() {
        return type;
    }

    /**
     * @return the order
     */
    public short getOrder() {
        return order;
    }

    /**
     * @return the valueTableOffset
     */
    public int getValueTableOffset() {
        return valueTableOffset;
    }

    /**
     * @return the classOfOrigin
     */
    public int getClassOfOrigin() {
        return classOfOrigin;
    }

    /**
     * @return the propertyQulifierSet
     */
    public JICIMQualifierSet getPropertyQulifierSet() {
        return propertyQulifierSet;
    }
}
