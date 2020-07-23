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

import java.util.Map;
import org.jinterop.dcom.impls.wmi.CIMBuffer;

/**
 *
 * @author danny
 */
public class JICIMClassType extends JICIMEncoding {

    public static JICIMClassType readFrom(CIMBuffer bb, boolean hasMethods) {
        JICIMClassType klass = new JICIMClassType();
        klass.init(bb, hasMethods);
        return klass;
    }
    private JICIMClassPart parentClass;
    private JICIMMethodsPart parentMethods = null;
    private JICIMClassPart currentClass;
    private JICIMMethodsPart currentMethods = null;

    private void init(CIMBuffer bb, boolean hasMethods) {
        this.parentClass = JICIMClassPart.readFrom(bb);
        if (hasMethods) {
            this.parentMethods = JICIMMethodsPart.readFrom(bb);
        }
        this.currentClass = JICIMClassPart.readFrom(bb);
        if (hasMethods) {
            this.currentMethods = JICIMMethodsPart.readFrom(bb);
        }

    }

    /**
     * @return the parentClass
     */
    public JICIMClassPart getParentClass() {
        return parentClass;
    }

    /**
     * @return the currentClass
     */
    public JICIMClassPart getCurrentClass() {
        return currentClass;
    }

    @Override
    public String getName() {
        return this.currentClass.getName();
    }

    @Override
    public Map<String, String> getProperties() {
        return this.currentClass.getProperties();
    }

    public JICIMMethod getMethod(String methodName) {
        JICIMMethod method = null;

        if (this.currentMethods != null) {
            method = this.currentMethods.getMethod(methodName);
            if (method == null) {
                if (this.parentMethods != null) {
                    method = this.parentMethods.getMethod(methodName);
                }
            }
        }
        return method;
    }
}