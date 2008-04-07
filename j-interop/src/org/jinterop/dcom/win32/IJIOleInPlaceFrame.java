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
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;

/**
 * This interface is used by applications for management of modeless dialog boxes, composite menus, and context-sensitive help. 
 * @since 1.0
 */
public interface IJIOleInPlaceFrame extends IJIOleInPlaceUIWindow {
	
	public static final String IID = "00000116-0000-0000-C000-000000000046";

	public void enableModeless(boolean fEnable) throws JIException; 
    
	/**
	 * 	typedef struct tagOleMenuGroupWidths 
	 	{ 
	 		LONG width[6]; 
        } OLEMENUGROUPWIDTHS, * LPOLEMENUGROUPWIDTHS; 
	
	 */
	public JIStruct insertMenus(int hmenuShared, JIStruct lpMenuWidths) throws JIException; 
	            
	public void removeMenus(int hmenuShared) throws JIException; 
	            
	public void setMenu(int hmenuShared, int holemenu, int hwndActiveObject) throws JIException; 
	           
	public void setStatusText(JIString pszStatusText) throws JIException; 
	   
	/**
	 * 	typedef struct tagMSG {     // msg 
	 	HWND   hwnd;     
	 	UINT   message; 
	 	WPARAM wParam; 
	 	LPARAM lParam; 
	 	DWORD  time; 
	 	POINT  pt; 
	 	} MSG; 
 	 */
	public void translateAccelerator(JIStruct lpmsg, int wID) throws JIException;
	 

	
}
