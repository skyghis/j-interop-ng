/** j-Interop (Pure Java implementation of DCOM protocol)
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
package org.jinterop.dcom.impls.wmi;

import java.util.UUID;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIFlags;

/**
 *
 * @author danny
 */
public class JIWbemWCOSmartEnum {

    private static final int WBEM_NO_WAIT = 0;
    private static final int WBEM_INFINITE = 0xFFFFFFFF;
    private static final String IID = "1C1C45EE-4395-11d2-B60B-00104B703EFD";
    private final IJIComObject iface;
    private UUID proxyGUID = UUID.randomUUID();

    public JIWbemWCOSmartEnum(IJIComObject comObject) throws JIException {
        IJIComObject smart = comObject.queryInterface(IID);
        JICallBuilder cb = new JICallBuilder(!smart.isDispatchSupported());
        cb.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
        cb.setOpnum(0);
        Object[] res = smart.call(cb);
        this.iface = (IJIComObject) res[0];
    }

    public JIWbemObjectArray next() throws JIException {
        JICallBuilder cb = new JICallBuilder(!this.iface.isDispatchSupported());
        cb.setOpnum(0); //Next
        cb.addInParamAsUUID(this.proxyGUID.toString(), JIFlags.FLAG_NULL);
        cb.addInParamAsInt(WBEM_INFINITE, JIFlags.FLAG_NULL);
        cb.addInParamAsInt(Short.MAX_VALUE, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); // count requested

        cb.addOutParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); // returned
        cb.addOutParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); // buffer size
        cb.addOutParamAsType(JIWbemObjectArray.class, JIFlags.FLAG_NULL);
        Object[] res = this.iface.call(cb);
        int ret = (Integer) res[0];
        if (ret > 0) {
            return (JIWbemObjectArray) res[2];
        } else {
            return null;
        }
    }
}
