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
public class JICIMQualifierSet {

    private LinkedList<JICIMQualifier> qualifiers = new LinkedList<JICIMQualifier>();

    public static JICIMQualifierSet readFrom(CIMBuffer bb) {
        JICIMQualifierSet qualifierSet = new JICIMQualifierSet();
        qualifierSet.init(bb);
        return qualifierSet;
    }

    private void init(CIMBuffer bb) {
        int length = bb.getUint32() - 4;
        while (length > 0) {
            JICIMQualifier qualifier = JICIMQualifier.readFrom(bb);
            this.qualifiers.add(qualifier);
            length -= qualifier.encodingLength();
        }
    }
}
