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
public class JICIMClassHeader {

    public static JICIMClassHeader readFrom(CIMBuffer bb) {
        JICIMClassHeader header = new JICIMClassHeader();
        header.init(bb);
        return header;

    }
    private int encodingLength;
    private JICIMHeapRef className;
    private int ndTableValueTableLength;

    private void init(CIMBuffer bb) {
        this.encodingLength = bb.getUint32();
        if (bb.getByte() != 0x00) {
            throw new RuntimeException("this must be zero");
        }
        this.className = JICIMHeapRef.readFrom(bb);
        this.ndTableValueTableLength = bb.getUint32();
    }

    public int getNdTableValueTableLength() {
        return this.ndTableValueTableLength;
    }

    /**
     * @return the className
     */
    public JICIMHeapRef getClassName() {
        return className;
    }
}
