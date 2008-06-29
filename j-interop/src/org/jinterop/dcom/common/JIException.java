/**j-Interop (Pure Java implementation of DCOM protocol)  
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jinterop.dcom.common;


/** Exception class for the framework. Developers are expected to catch or re-throw these exceptions. 
 * and not create one themselves.
 *  
 * @since 1.0
 */

public final class JIException extends Exception
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
