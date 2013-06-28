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

package org.jinterop.winreg;

/** Policy handle for each key.
 *
 * @since 1.0
 *
 */
public class JIPolicyHandle {
	/**
	 * Handle to the Key
	 */
	public final byte handle[] = new byte[20];
	/**
	 * True, if the key was newly created.
	 */
	public final boolean newlyCreated;
	/**
	 * @exclude
	 * @param newlyCreated
	 */
	public JIPolicyHandle(boolean newlyCreated)
	{
		this.newlyCreated = newlyCreated;
	}
}
