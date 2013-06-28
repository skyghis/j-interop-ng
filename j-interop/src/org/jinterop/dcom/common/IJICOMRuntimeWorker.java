/**
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v3.
*  
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
