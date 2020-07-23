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
public class JICIMString {

    private byte flag;
    private String value = "";

    public static JICIMString readFrom(CIMBuffer bb) {
        JICIMString str = new JICIMString();
        str.init(bb);
        return str;
    }

    private void readAnsiString(CIMBuffer buff) {
        StringBuilder sb = new StringBuilder();
        while (buff.remaining() > 0) {
            byte b = buff.getByte();
            if (b == 0) {
                this.value = sb.toString();
                return;
            } else {
                sb.append((char) b);
            }
        }
    }

    private void readUnicodeString(CIMBuffer buff) {
        StringBuilder sb = new StringBuilder();
        while (buff.remaining() > 0) {
            char c = buff.getChar();
            if (c == 0) {
                this.value = sb.toString();
                return;

            } else {
                sb.append(c);
            }
        }
    }

    private void init(CIMBuffer bb) {
        this.flag = bb.getByte();
        if (this.flag == 0x01) {
            readUnicodeString(bb);
        } else // should be 0x00
        {
            readAnsiString(bb);
        }

    }

    @Override
    public String toString() {
        return this.value;
    }

    public int getSize() {
        return this.value.length() + 1;
    }
}
