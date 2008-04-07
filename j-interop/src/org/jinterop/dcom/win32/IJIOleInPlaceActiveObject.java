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
import org.jinterop.dcom.core.JIStruct;

/**
 * This interface is used by the container to manipulate in-place objects when they are active. 
 * @since 1.0
 *
 */
public interface IJIOleInPlaceActiveObject extends IJIOleWindow {

	public static final String IID = "00000117-0000-0000-C000-000000000046";
	
	public void translateAccelerator(JIStruct lpmsg) throws JIException;
        
	public void  onFrameWindowActivate(boolean fActivate) throws JIException;
        
	public void onDocWindowActivate(boolean fActivate) throws JIException;
        
	public void resizeBorder(JIRect prcBorder,IJIOleInPlaceUIWindow pUIWindow,boolean fFrameWindow) throws JIException;
        
	public void enableModeless(boolean fEnable) throws JIException;
	
}
