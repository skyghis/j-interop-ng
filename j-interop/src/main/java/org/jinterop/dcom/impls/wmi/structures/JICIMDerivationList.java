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
import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 */
public class JICIMDerivationList {

    private LinkedList<JICIMString> superClasses = new LinkedList<JICIMString>();

    public static JICIMDerivationList readFrom(CIMBuffer bb) {
        JICIMDerivationList derivationList = new JICIMDerivationList();
        derivationList.init(bb);
        return derivationList;
    }

    private void init(CIMBuffer bb) {
        int len = bb.getUint32() - 0x4;
        while (len > 0) {
            JICIMString name = JICIMString.readFrom(bb);
            this.superClasses.add(name);
            int nameSize = bb.getUint32();
            len -= nameSize;
            len -= 4;
        }
    }
}
