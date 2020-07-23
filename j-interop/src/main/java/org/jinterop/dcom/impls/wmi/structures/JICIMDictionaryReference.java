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

/**
 *
 * @author danny
 */
public class JICIMDictionaryReference {

    private String value = "";

    public JICIMDictionaryReference(int ref) {
        switch (ref) {
            case 0x80000000:
                this.value = "'";
                break;
            case 0x80000001:
                this.value = "key";
                break;
            case 0x80000002:
                this.value = "";
                break;
            case 0x80000003:
                this.value = "read";
                break;
            case 0x80000004:
                this.value = "write";
                break;
            case 0x80000005:
                this.value = "volatile";
                break;
            case 0x80000006:
                this.value = "provider";
                break;
            case 0x80000007:
                this.value = "dynamic";
                break;
            case 0x80000008:
                this.value = "cvimwin32";
                break;
            case 0x80000009:
                this.value = "DWORD";
                break;
            case 0x80000010:
                this.value = "CIMTYPE";
                break;
        }
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
