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
class JICIMMethodDescription {

    public static JICIMMethodDescription readFrom(CIMBuffer bb) {
        JICIMMethodDescription method = new JICIMMethodDescription();
        method.init(bb);
        return method;
    }
    private JICIMHeapRef name;
    private byte flags;
    private int origin;    
    private JICIMHeapRef outputSignature;
    private JICIMHeapRef inputSignature;
    private JICIMHeapRef qualifiers;

    private void init(CIMBuffer bb) {
        this.name = JICIMHeapRef.readFrom(bb);
        this.flags = bb.getByte();
        bb.move(3); // method padding
        this.origin = bb.getUint32();
        this.qualifiers = JICIMHeapRef.readFrom(bb);
        this.inputSignature = JICIMHeapRef.readFrom(bb);
        this.outputSignature = JICIMHeapRef.readFrom(bb);;
    }

    /**
     * @return the name
     */
    public JICIMHeapRef getName() {
        return name;
    }

    /**
     * @return the outputSignature
     */
    public JICIMHeapRef getOutputSignature() {
        return outputSignature;
    }

    /**
     * @return the inputSignature
     */
    public JICIMHeapRef getInputSignature() {
        return inputSignature;
    }
}
