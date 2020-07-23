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

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;

/**
 *
 * @author danny
 */
public class JIWbemServices {

//    private IJIComObject comObject = null;
    private IJIComObject iface = null;
    private final String IID = "9556dc99-828c-11cf-a37e-00aa003240c7";

    public JIWbemServices(IJIComObject servicesObj) throws JIException {

//        this.comObject = servicesObj;
        this.iface = servicesObj.queryInterface(IID);
    }

    public JIWbemWCOSmartEnum execQuery(String query) throws JIException {
        final int WBEM_FLAG_RETURN_IMMEDIATELY = 0x10;
        final int WBEM_FLAG_FORWARD_ONLY = 0x20;
//        final int WBEM_FLAG_ENSURE_LOCATABLE = 0x100;
        return execQuery("WQL", query, WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY);
    }

    public JIWbemWCOSmartEnum execQuery(String language, String query, int flags) throws JIException {
        final int OPNUM_EXEC_QUERY = 17;
        JICallBuilder cb = new JICallBuilder(true);
        cb.setOpnum(OPNUM_EXEC_QUERY);
        cb.addInParamAsString(language, JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        cb.addInParamAsString(query, JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
        cb.addInParamAsInt(flags, JIFlags.FLAG_NULL);
        cb.addInParamAsPointer(new JIPointer(null), JIFlags.FLAG_NULL);
        cb.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] res = this.iface.call(cb);
        return new JIWbemWCOSmartEnum((IJIComObject) res[0]);
    }

    public JIWbemClassObject getObject(String path) throws JIException {
        final int OPNUM_GET_OBJECT = 3;
        JICallBuilder cb = new JICallBuilder(!this.iface.isDispatchSupported());
        cb.setOpnum(OPNUM_GET_OBJECT);
        cb.addInParamAsString(path, JIFlags.FLAG_REPRESENTATION_STRING_BSTR); // strObjectPath
        cb.addInParamAsInt(0, JIFlags.FLAG_NULL); // lFlags
        cb.addInParamAsInt(0, JIFlags.FLAG_NULL); // ctx
        cb.addInParamAsInt(1, JIFlags.FLAG_NULL); // ppObject
        cb.addInParamAsInt(0, JIFlags.FLAG_NULL); // ppCallResult
        cb.addOutParamAsType(JIWbemClassObject.class, JIFlags.FLAG_NULL);
        //cb.addOutParamAsType(JIIWbemCallResult.class, JIFlags.FLAG_NULL);
        Object[] res = this.iface.call(cb);
        return (JIWbemClassObject) res[0];
    }


//    public void execMethod(String strObjectPath, String strMethodName, JIWbemClassObject inParams) throws JIException {
//        final int OPNUM_EXEC_METHOD = 21;
//        JICallBuilder cb = new JICallBuilder(!this.iface.isDispatchSupported());
//        cb.setOpnum(OPNUM_EXEC_METHOD);
//        cb.addInParamAsString(strObjectPath, JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
//        cb.addInParamAsString(strMethodName, JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
//        cb.addInParamAsInt(0, JIFlags.FLAG_NULL); //flags
//        cb.addInParamAsInt(0, JIFlags.FLAG_NULL); //ctx
//        cb.addInParamAsObject(inParams, JIFlags.FLAG_NULL);
//        cb.addInParamAsInt(0, JIFlags.FLAG_NULL); // no out params
//        cb.addInParamAsInt(0,JIFlags.FLAG_NULL); //no res
//
//        Object[] res = this.iface.call(cb);
//
//
//    }
//
//    JIWbemClassObject spawnInstance(JIWbemClassObject obj) {
//        throw new NotImplementedException();
//    }
}
