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

package org.jinterop.dcom.core;

import org.jinterop.dcom.common.JIException;

/** <P>Base interface for all <code>COM</code> components. <br>
 *  An Example:- <br>
 *  <code><br>
 * 		JISession session = JISession.createSession("DOMAIN","USERNAME","PASSWORD");<br>
 *		JIComServer stub = new JIComServer(JIProgId.valueOf(session,"TestCOM123.TestServer2"),address,session);<br>
 * <br>
 *		IJIComObject comObject = stub.createInstance();<br> 
 * 		IJIUnknown handle = comObject.queryInterface("620012E2-69E3-4DC0-B553-AE252524D2F6");<br>
 * </code><br>
 * </P>
 *	@since 1.0 
 */
//Interface for COM objects. All objects which wrap COM Objects should be extending this.
public interface IJIUnknown {
	
	/**
	 * IID representing the <code>IUnknown</code>
	 */
	public final String IID = "00000000-0000-0000-c000-000000000046";
	
	/** Used to retrieve interface pointers based on <code>iid</code>. <br>
	 * 
	 * @param iid String representation of the IID (clsid).
	 * @return reference to the requested unknown.
	 * @throws JIException
	 */
	public IJIUnknown queryInterface(String iid) throws JIException;
	
	/** <P>Increases the reference count on the actual <code>COM</code> server by 5 (currently hardcoded). 
	 * The developer should refrain from calling this API, as referencing is maintained internally by the 
	 * system. If the <code>release</code> is not called in conjunction with <code>addRef</code> then the 
	 * COM Instance will not get garbage collected at the server. <br>
	 * </P>
	 * @throws JIException
	 */
	public void addRef() throws JIException;
	
	/**<P> Decreases the reference count on the actual <code>COM</code> server by 5 (currently hardcoded).  
	 * The developer should refrain from calling this API, as referencing is maintained internally by the 
	 * system. If the <code>release</code> is not called in conjunction with <code>addRef</code> then the 
	 * COM Instance will not get garbage collected at the server. <br>
	 * </P>
	 * @throws JIException
	 */	
	public void release() throws JIException;

}
