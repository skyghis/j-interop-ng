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
package org.jinterop.dcom.win32;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIUnknown;
import org.jinterop.dcom.core.JIInterfacePointer;

/**
 * This interface enables communications between an object and its container. 
 * @since 1.0
 *
 */
public interface IJIOleClientSite extends IJIUnknown {

	public static final String IID = "00000118-0000-0000-C000-000000000046";
	
    public void saveObject() throws JIException;
    //public IJIMoniker getMoniker(int dwAssign, int dwWhichMoniker) throws JIException;
    public JIInterfacePointer getContainer() throws JIException;
    public void showObject() throws JIException;
    public void onShowWindow(boolean fShow) throws JIException;
    public void requestNewObjectLayout() throws JIException;
}
