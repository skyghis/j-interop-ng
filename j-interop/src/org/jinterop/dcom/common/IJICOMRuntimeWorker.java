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

import java.util.List;

import rpc.core.UUID;

/** Framework Internal.
 * 
 * @exclude
 * @since 1.0
 */
public interface IJICOMRuntimeWorker {
	public void setOpnum(int opnum);
	public void setCurrentIID(String iid);
	public void setCurrentObjectID(UUID objectId);
	public UUID getCurrentObjectID();
	public List getQIedIIDs();
	public boolean isResolver();
	public boolean workerOver();
}
