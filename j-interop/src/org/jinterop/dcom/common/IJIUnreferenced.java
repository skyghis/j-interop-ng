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
 * Implement this interface recieve notifications for IJIComObjects when they get GCed. This also means that the 
 * interface pointer (represented by IJIComObject in j-Interop) on the COM server side would have 0 reference count 
 * and will get Gced itself by COM runtime.
 * </p>
 * <br>
 * <p> 
 * One note of caution, the IJIComObject is uniquely identified across the client-server relationship by it's IPID. The IPID should be used
 * as a key to store a relevant "action" object when <code>unReferenced</code> method of this interface is invoked.
 * If the IJIComObject is stored at a place solely for the purpose of this housekeeping than it will NEVER get Gced as j-Interop end, since
 * the logic of GC is based on a Weak references. And that stored housekeeping reference is a strong one.
 * <br>
 * 
 *  <code>
 *  
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
 *<br><i>Please refer to <b>MSWMI</b> example for more details on how to use this class.</i><br>
 *  
 * @since 1.21
 */
public interface IJIUnreferenced {
	
	/**
	 * Called when the IJIComObject associated with this interface is Gced by j-Interop. 
	 *
	 */
	public void unReferenced();

}
