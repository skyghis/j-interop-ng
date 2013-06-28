/**
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v3.
*  
*/

package org.jinterop.dcom.common;

/** Framework Internal class.
 * 
 * @exclude
 * <p>Internally used class from JICallBuilder, since the read(), write() do not throw
 * exceptions. The IJIComObject call or QI or any other APIs will always throw checked JIException
 *</p>
 */
public final class JIRuntimeException extends RuntimeException {

	
	private static final long serialVersionUID = 4972599190342284084L;
	private Object[] parameters = null;
	private int hresult = 0;
	public JIRuntimeException(int hresult)
	{
		//error code
		this.hresult = hresult;
	}
	
	public JIRuntimeException(int hresult, Object[] parameters)
	{
		//error code
		this.hresult = hresult;
		this.parameters = parameters;
	}
	
	public int getHResult()
	{
		return hresult;
	}
	
	public Object[] getParameters()
	{
		return parameters;
	}
	
	public String getMessage() {
        return JISystem.getLocalizedMessage(hresult);
    }
}
