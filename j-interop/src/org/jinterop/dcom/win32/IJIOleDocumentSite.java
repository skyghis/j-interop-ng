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
 * This interface implements each client site that hosts a document. 
 * @since 1.0
 *
 */
public interface IJIOleDocumentSite extends IJIUnknown {
	public static final String IID = "b722bcc7-4e68-101b-a2bc-00aa00404770";
	public void activateMe(JIInterfacePointer pViewToActivate) throws JIException ;
}
