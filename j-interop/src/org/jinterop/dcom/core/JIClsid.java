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

import rpc.core.UUID;

/**<p> Wrapper for class identifier for a COM Object. <br>
 * 
 * Definition from MSDN: <i> A universally unique identifier (UUID) that identifies a type of 
 * Component Object Model (COM) object. Each type of COM object item has its JIClsid in the registry 
 * so that it can be loaded and used by other applications. For example, a spreadsheet may create 
 * worksheet items, chart items, and macrosheet items. Each of these item types has its own JIClsid that 
 * uniquely identifies it to the system. </i>
 *<br>
 * For e.g. Excel application has clsid of "00024500-0000-0000-C000-000000000046".
 *  </p>
 * @since 1.0
 */
public class JIClsid {
	
	private UUID nestedUUID = new UUID();
	private boolean autoRegister = false;
	
	/** Pass true if, this is an OCX\DLL component and you want the library to do auto registration.
	 * 
	 * @param autoRegister
	 */
	public void setAutoRegistration(boolean autoRegister)
	{
		this.autoRegister = autoRegister; 
	}

	/**Returns true is auto registration is enabled.
	 * 
	 * @return
	 */
	public boolean isAutoRegistrationSet()
	{
		return autoRegister;
	}
	
	/** String representing the class identifier for a particular <code>COM</code> object.
	 * <br>
	 * @param uuid - clsid of the form "00000000-0000-0000-0000-000000000000"
	 * @return - Instance of JIClsid 
	 */
	public static JIClsid valueOf(String uuid)
	{
		if (uuid == null)
		{
			return null;
		}
		return new JIClsid(uuid);
	}
	
	private JIClsid(String uuid)
	{
		this.nestedUUID.parse(uuid);
	}
	
	/** String representation of the wrapped class identifier.
	 * <br>
	 * @return uuid of the form "00000000-0000-0000-0000-000000000000"
	 */
	public String getCLSID()
	{
		return nestedUUID.toString();
	}

	
	
}
