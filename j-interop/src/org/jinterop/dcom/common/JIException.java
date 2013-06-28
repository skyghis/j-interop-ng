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


/** Exception class for the framework. Developers are expected to catch or re-throw these exceptions. 
 * and not create one themselves.
 *  
 * @since 1.0
 */

public class JIException extends Exception
{

	
	private static final long serialVersionUID = 8648697261032503931L;
	private String message = null;
	private int errorCode = -1;
	//US English messages sent by server , this is used especially during IDispatch exceptions
	//when the server returns an error.
	/**
	 * @exclude
	 */
	public JIException(int errorCode,String message)
	{
		this(errorCode,message,null);
	}

	/**
	 * @exclude
	 */
	public JIException(int errorCode)
	{
		this(errorCode,(Throwable)null);
	}
	
	/**
	 * @exclude
	 */
	public JIException(int errorCode, Throwable cause)
	{
		this(errorCode,null,cause);
	}

	/**
	 * @exclude
	 */
	public JIException(JIRuntimeException exception)
	{
		this(exception.getHResult(),null,exception);
	}
		
	/**
	 * @exclude
	 */
	public JIException(int errorCode, String message, Throwable cause)
	{
		super.initCause(cause);
		this.errorCode = errorCode;
		this.message = message;
	}	

	/**
	 * Returns the localized error messages.
	 * 
	 * @return
	 */
	public String getMessage() {
        return message == null ? message = initMessageFromBundle() : message;
    }
	
	private String initMessageFromBundle()
	{
		return (message = JISystem.getLocalizedMessage(errorCode));
	}

	/** Returns the error code associated with this exception. Please refer 
	 * <code>JIErrorCodes</code> for a complete list of errors.
	 * 
	 * @return int representing the error code.
	 */
	public int getErrorCode()
	{
		return errorCode;
	}
}
