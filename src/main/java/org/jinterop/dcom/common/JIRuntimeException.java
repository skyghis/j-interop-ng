/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
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
package org.jinterop.dcom.common;

/**
 * Framework Internal class.
 *
 * @exclude
 * <p>
 * Internally used class from JICallBuilder, since the read(), write() do not
 * throw exceptions. The IJIComObject call or QI or any other APIs will always
 * throw checked JIException
 * </p>
 */
public final class JIRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4972599190342284084L;
    private Object[] parameters = null;
    private int hresult = 0;

    public JIRuntimeException(int hresult) {
        //error code
        this.hresult = hresult;
    }

    public JIRuntimeException(int hresult, Object[] parameters) {
        //error code
        this.hresult = hresult;
        this.parameters = parameters;
    }

    public int getHResult() {
        return hresult;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String getMessage() {
        return JISystem.getLocalizedMessage(hresult);
    }
}
