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
public class JICIMObjectFlags {

    public static byte OBJECT_IS_CLASS = 0x01;
    public static byte OBJECT_IS_CIM_INSTANCE = 0x02;
    public static byte HAS_DECORATION_BLOCK = 0x04;
    public static byte IS_PROPTOTYPE_OF_QUERY = 0x10;
    public static byte KEY_NOT_PRESENT = 0x40;

    public static JICIMObjectFlags readFrom(CIMBuffer bb) {
        JICIMObjectFlags flags = new JICIMObjectFlags();
        flags.init(bb);
        return flags;
    }
    private byte value;

    private void init(CIMBuffer bb) {
        this.value = bb.getByte();
    }

    public boolean hasDecoration() {
        return (this.value & HAS_DECORATION_BLOCK) > 0;
    }

    public boolean isClass() {
        return (this.value & OBJECT_IS_CLASS) > 0;
    }

    public boolean isInstance() {
        return (this.value & OBJECT_IS_CIM_INSTANCE) > 0;
    }
    
    public int getSize()
    {
        return 1;
    }
}
