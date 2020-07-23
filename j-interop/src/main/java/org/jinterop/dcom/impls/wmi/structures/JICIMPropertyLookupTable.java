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

import java.util.ArrayList;
import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 */
public class JICIMPropertyLookupTable {

    private ArrayList<JICIMPropertyLookup> lookupTable = new ArrayList<JICIMPropertyLookup>();

    public static JICIMPropertyLookupTable readFrom(CIMBuffer bb) {
        JICIMPropertyLookupTable propLookup = new JICIMPropertyLookupTable();
        propLookup.init(bb);
        return propLookup;
    }
    private int count;

    private void init(CIMBuffer bb) {
        this.count = bb.getUint32();
        for (int i = 0; i < this.count; i++) {
            JICIMPropertyLookup lookup = JICIMPropertyLookup.readFrom(bb);
            this.lookupTable.add(lookup);
        }
    }

    public int getCount() {
        return this.count;
    }

    public ArrayList<JICIMPropertyLookup> getProperties() {
        return this.lookupTable;
    }
}
