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
 * @see http://msdn.microsoft.com/en-us/library/cc250911(v=PROT.10).aspx
 * @author danny
 */
public class JICIMHeapRef {

    public static JICIMHeapRef readFrom(CIMBuffer bb) {
        JICIMHeapRef heapRef = new JICIMHeapRef();
        heapRef.init(bb);
        return heapRef;
    }
    private Object ref;

    private void init(CIMBuffer bb) {
        int value = bb.getUint32();
        if (value == 0xFFFFFFFF) {
            this.ref = null;
            return;
        }
        if ((value & 0x80000000) != 0) {
            this.ref = new JICIMDictionaryReference(value);
            return;
        }
        this.ref = value; //the reference is an offset to a HeapItem in the Heap.
    }

    @Override
    public String toString() {
        return this.ref.toString();
    }

    public int getOffset() {
        return (Integer) this.ref;
    }
}
