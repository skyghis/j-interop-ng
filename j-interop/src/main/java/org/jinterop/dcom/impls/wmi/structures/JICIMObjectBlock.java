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
public class JICIMObjectBlock {

    private JICIMDecoration decoration;
    private JICIMObjectFlags flags;
    private JICIMEncoding encoding;

    public static JICIMObjectBlock readFrom(CIMBuffer bb, JICIMClassPart classPart) {
        JICIMObjectBlock objectBlock = new JICIMObjectBlock();
        objectBlock.init(bb, classPart);
        return objectBlock;
    }

    private void init(CIMBuffer bb, JICIMClassPart classPart) {
        this.flags = JICIMObjectFlags.readFrom(bb);
        if (this.flags.hasDecoration()) {
            this.decoration = JICIMDecoration.readFrom(bb);
        }
        if (this.flags.isClass()) {
            this.encoding = JICIMClassType.readFrom(bb, true);
        } else if (this.flags.isInstance()) {
            this.encoding = JICIMInstanceType.readFrom(bb, classPart);
        }
    }

    public JICIMInstanceType getCIMInstance() {
        if (this.getEncoding() == null) {
            return null;
        }
        if (JICIMInstanceType.class.isAssignableFrom(this.getEncoding().getClass())) {
            return (JICIMInstanceType) this.getEncoding();
        }
        return null;
    }

    public JICIMClassType getCIMClass() {
        if (this.getEncoding() == null) {
            return null;
        }
        if (JICIMClassType.class.isAssignableFrom(this.getEncoding().getClass())) {
            return (JICIMClassType) this.getEncoding();
        }
        return null;
    }

    /**
     * @return the decoration
     */
    public JICIMDecoration getDecoration() {
        return decoration;
    }

    /**
     * @return the encoding
     */
    public JICIMEncoding getEncoding() {
        return encoding;
    }

    public void setEncoding(JICIMInstanceType instance) {
        this.encoding = instance;
    }

  
}
