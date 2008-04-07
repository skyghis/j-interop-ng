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
import org.jinterop.dcom.core.JIStruct;
/**
 * This interface is used by containers for receiving information about views of a document. 
 * @since 1.0
 *
 */
public interface IJIOleDocumentView extends IJIUnknown {

	 public static final String IID = "b722bcc6-4e68-101b-a2bc-00aa00404770";
	
	// public void applyViewState(IJIStream pStm) throws JIException; 
     
	 public void closeView(int dwReserved) throws JIException;
	            
	 public JIInterfacePointer getDocument() throws JIException;  
	            
	 public JIInterfacePointer getInPlaceSite() throws JIException; 
	            
	 public JIStruct getRect() throws JIException; 
	            
	 public JIInterfacePointer invokeClone(JIInterfacePointer pIPSiteNew) throws JIException; 
	            
	 public void open() throws JIException; 
	            
	// public void saveViewState(IJIStream pStm) throws JIException; 
	            
	 public void setInPlaceSite(JIInterfacePointer pIPSite) throws JIException; 
	            
	 public void setRect(JIStruct prcView) throws JIException; 
	            
	 public void setRectComplex(JIStruct prcView, JIStruct prcHScroll, JIStruct prcVScroll, JIStruct prcSizeBox) throws JIException; 
	            
	 public void show(boolean fShow) throws JIException; 
	            
	 public void UIActivate(boolean fUIActivate) throws JIException; 
	 

}
