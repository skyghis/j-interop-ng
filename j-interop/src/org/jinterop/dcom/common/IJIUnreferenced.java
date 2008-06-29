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
