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
public class JICIMHeap {

    public static JICIMHeap readFrom(CIMBuffer bb) {
        JICIMHeap heap = new JICIMHeap();
        heap.init(bb);
        return heap;
    }
    private int length;
    private CIMBuffer heap;

    private void init(CIMBuffer bb) {
        // HeapLength is a 32-bit value with the most significant bit always set 
        // (using little-endian binary encoding for the 32-bit value), so that 
        // the length is actually only 31 bits.
        this.length = (bb.getUint32() & 0x7FFFFFFF);
        byte[] data = new byte[this.length];
        bb.copy(data);
        this.heap = new CIMBuffer(data);
    }

    public JICIMPropertyInfo getPropertyInfo(int offset) {
        this.heap.setOffset(offset);
        return JICIMPropertyInfo.readFrom(heap);
    }

    public JICIMString getString(int offset) {
        this.heap.setOffset(offset);
        return JICIMString.readFrom(heap);
    }

    public JICIMEncodedArray getEncodedArray(int offset, JICIMType elemType) {
        this.heap.setOffset(offset);
        return JICIMEncodedArray.readFrom(heap, elemType);
    }

    public JICIMObjectBlock getMethodSignatureBlock(int offset) {
        this.heap.setOffset(offset);
        int len = this.heap.getUint32();
        if (len > 0) {
            return JICIMObjectBlock.readFrom(heap, null);
        }
        return null;
    }
}
