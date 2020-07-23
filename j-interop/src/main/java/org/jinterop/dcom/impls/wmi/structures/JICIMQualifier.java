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
 * @see http://msdn.microsoft.com/en-us/library/cc250904(v=PROT.10).aspx
 * @author danny
 */
public class JICIMQualifier {

    public static JICIMQualifier readFrom(CIMBuffer bb) {
        JICIMQualifier qualifier = new JICIMQualifier();
        qualifier.init(bb);
        return qualifier;
    }
    private JICIMHeapRef qualifierName;
    private byte qualifierFlavor;
    private JICIMType qualifierType;
    private JICIMEncodedValue qualifierValue;

    public int encodingLength() {
        return this.qualifierValue.encodingLength() + 9;
    }

    private void init(CIMBuffer bb) {
        this.qualifierName = JICIMHeapRef.readFrom(bb);
        this.qualifierFlavor = bb.getByte();
        this.qualifierType = JICIMType.readFrom(bb);
        this.qualifierValue = JICIMEncodedValue.readFrom(bb, qualifierType);
    }
}
