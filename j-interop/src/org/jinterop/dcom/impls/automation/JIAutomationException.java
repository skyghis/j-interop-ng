/**j-Interop (Pure Java implementation of DCOM protocol) 
 * Copyright (C) 2008  Vikram Roopchand
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

package org.jinterop.dcom.impls.automation;

import org.jinterop.dcom.common.JIException;;

/** Class for signifying Automation related exceptions. 
 * 
 * @since 2.01
 */
public final class JIAutomationException extends JIException {

	public JIAutomationException(JIException e) {
		super(e.getErrorCode(),e.getMessage(),e.getCause());
	}

	private JIExcepInfo excepInfo = null;
	
	void setExcepInfo(JIExcepInfo excepInfo)
	{
		this.excepInfo = excepInfo;
	}
	
	/** Returns the <code>EXCEPINFO</code> structure.
	 * 
	 * @return
	 */
	public JIExcepInfo getExcepInfo()
	{
		return excepInfo;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6969766293190131365L;

}
