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

/**<p>
 * Implement this interface receive notifications for <code>IJIComObject</code>s when 
 * they get garbage collected. This also means that the actual interface reference on 
 * the COM server have a reference count of 0 and will get garbage collected itself by 
 * COM runtime.
 * </p>
 * <p> 
 * One note of caution, the <code>IJIComObject</code> is uniquely identified across the 
 * client-server relationship by it's <code>IPID</code>. The <code>IPID</code> should be used
 * as a key to store a relevant "action" object when <code>unReferenced</code> method of this
 * interface is invoked. If the <code>IJIComObject</code> is stored at a place solely for the
 * purpose of this housekeeping than it will <b>NEVER</b> get garbage collected by the framework as 
 * the logic of collection is based on weak references. 
 * 
 * <p>
 *  <code>
 *    <br>
 *    comObject.registerUnreferencedHandler(session, new IJIUnreferenced(){<br>
 *			public void unReferenced()<br>
 *			{<br>
 *				//do something here<br>
 *			}<br>
 *		});<br>
 *		<br>
 *      
 *  </code>
 * 
 *</p>
 *
 *<br><i>Please refer to MSWMI example for more details on how to use this class.</i><br>
 *  
 * @since 1.21
 */
public interface IJIUnreferenced {
	
	/**
	 * Called when the <code>IJIComObject</code> associated with this interface is garbage collected by the framework. 
	 *
	 */
	public void unReferenced();

}
