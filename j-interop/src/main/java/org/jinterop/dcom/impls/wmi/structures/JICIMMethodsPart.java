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

import java.util.LinkedList;
import java.util.List;
import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 */
public class JICIMMethodsPart {

    private List<JICIMMethodDescription> methods = null;

    public static JICIMMethodsPart readFrom(CIMBuffer bb) {
        JICIMMethodsPart methods = new JICIMMethodsPart();
        methods.init(bb);
        return methods;
    }
    private JICIMHeap heap;

    private void init(CIMBuffer bb) {
        int encodingLength = bb.getUint32();
        short methodCount = bb.getUint16();
        bb.move(2); // method padding
        methods = new LinkedList<JICIMMethodDescription>();
        for (short i = 0; i < methodCount; i++) {
            JICIMMethodDescription method = JICIMMethodDescription.readFrom(bb);
            this.methods.add(method);
        }
        this.heap = JICIMHeap.readFrom(bb);
    }

    public JICIMMethod getMethod(String name) {
        for (JICIMMethodDescription methodInfo : this.methods) {
            String methodName = this.heap.getString(methodInfo.getName().getOffset()).toString();
            if (name.equals(methodName)) {
                JICIMObjectBlock inParams = this.heap.getMethodSignatureBlock(methodInfo.getInputSignature().getOffset());
                JICIMObjectBlock outParams = this.heap.getMethodSignatureBlock(methodInfo.getOutputSignature().getOffset());
                return new JICIMMethod(methodName, inParams, outParams);
            }
        }
        return null; //method not found
    }
}
