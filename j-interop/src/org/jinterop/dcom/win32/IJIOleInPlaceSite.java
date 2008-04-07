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
 * This interface enables communication between a client site of an in-place object and the container. 
 * @since 1.0
 *
 */
public interface IJIOleInPlaceSite extends IJIOleWindow {

	 public static final String IID = "00000119-0000-0000-C000-000000000046";
	 
	 public void canInPlaceActivate() throws JIException; 
     
	 public void deactivateAndUndo() throws JIException; 
	            
	 public void discardUndoState() throws JIException;
	            
	 /**
	  * IJIOleInPlaceFrame ppFrame, IJIOleInPlaceUIWindow ppDoc, JIRect lprcPosRect, 
			 					  JIRect lprcClipRect
	  */
	 public JIStruct getWindowContext(JIStruct lpFrameInfo/*LPOLEINPLACEFRAMEINFO*/) throws JIException; 
	            
	 public void onInPlaceActivate() throws JIException;
	            
	 public void onInPlaceDeactivate() throws JIException;
	            
	 public void onPosRectChange(JIRect lprcPosRect) throws JIException;
	            
	 public void onUIActivate() throws JIException;
	            
	 public void onUIDeactivate(boolean fUndoable) throws JIException;
	            
	 public void scroll(int scrollX, int scrollY) throws JIException;  

}
