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

package org.jinterop.dcom.impls.automation;

import org.jinterop.dcom.common.JIException;

/** Class for signifying Automation related exceptions.
 *
 * @since 2.01
 */
public final class JIAutomationException extends JIException {

	public JIAutomationException(JIException e) {
		super(e.getErrorCode(),e.getMessage(),e.getCause());
	}

	private JIExcepInfo excepInfo = new JIExcepInfo();

	void setExcepInfo(JIExcepInfo excepInfo)
	{
		this.excepInfo.errorCode = excepInfo.errorCode;
		this.excepInfo.excepDesc = excepInfo.excepDesc;
		this.excepInfo.excepHelpfile = excepInfo.excepHelpfile;
		this.excepInfo.excepSource = excepInfo.excepSource;
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
