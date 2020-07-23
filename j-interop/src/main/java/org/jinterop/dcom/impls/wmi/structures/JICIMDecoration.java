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
public class JICIMDecoration {
    private JICIMString serverName;
    private JICIMString nameSpace;
    
    public static JICIMDecoration readFrom(CIMBuffer bb)
    {
        JICIMDecoration decoration = new JICIMDecoration();
        decoration.init(bb);
        return decoration;
    }

    private void init(CIMBuffer bb) {
        this.serverName = JICIMString.readFrom(bb);        
        this.nameSpace = JICIMString.readFrom(bb);
    }

    public int getSize() {
        return this.serverName.getSize() + this.nameSpace.getSize();
    }
}
