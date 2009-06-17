/**j-Interop (Pure Java implementation of DCOM protocol)  
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

package org.jinterop.dcom.core;

import rpc.core.UUID;

/**<p>Wrapper for class identifier to a COM Object.
 * <p>
 * Definition from MSDN: <i> A universally unique identifier (UUID) that 
 * identifies a type of Component Object Model (COM) object. Each type of 
 * COM object item has its CLSID in the registry so that it can be loaded 
 * and used by other applications. For example, a spreadsheet may create 
 * worksheet items, chart items, and macrosheet items. Each of these item 
 * types has its own CLSID that uniquely identifies it to the system. </i>
 * 
 *<p>
 * For example Microsoft Office Excel Application has clsid of "00024500-0000-0000-C000-000000000046".
 *  </p>
 * @since 1.0
 */
public class JIClsid {
	
	private UUID nestedUUID = new UUID();
	private boolean autoRegister = false;
	
	/** Indicates to the framework, if Windows Registry settings for DLL\OCX
	 * component identified by this object should be modified to add a <code>Surrogate</code> 
	 * automatically. A <code>Surrogate</code> is a process which provides resources
	 * such as memory and cpu for a DLL\OCX to execute.
	 * 
	 * @param autoRegister <code>true</code> if auto registration should be done by the framework.
	 */
	public void setAutoRegistration(boolean autoRegister)
	{
		this.autoRegister = autoRegister; 
	}

	/**Returns the status of the auto registration flag for the component identified by this object.
	 * 
	 * @return <code>true</code> if the auto registration flag is set.
	 */
	public boolean isAutoRegistrationSet()
	{
		return autoRegister;
	}
	
	/** Factory method returning an instance of this class.
	 *
	 * @param uuid - clsid of the form "00000000-0000-0000-0000-000000000000"
	 * @return - instance of JIClsid 
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
	 * 
	 * @return string of the form "00000000-0000-0000-0000-000000000000"
	 */
	public String getCLSID()
	{
		return nestedUUID.toString();
	}

	
	
}
