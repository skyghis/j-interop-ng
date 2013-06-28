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

package org.jinterop.dcom.core;

interface JIIServerActivation {

	public static final int RPC_C_IMP_LEVEL_IDENTIFY = 2;
	public static final int RPC_C_IMP_LEVEL_IMPERSONATE = 3;

	public abstract boolean isActivationSuccessful();

	public abstract JIDualStringArray getDualStringArrayForOxid();

	public abstract JIInterfacePointer getMInterfacePointer();

	public abstract String getIPID();
	
	public abstract boolean isDual();
	
	public abstract String getDispIpid();
	
	public abstract int getDispRefs();
	
	public abstract void setDispIpid(String dispIpid);

}