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

import org.jinterop.dcom.common.JISystem;


/**Stores the oxid details in memory.
 * 
 * @since 1.0
 *
 */
final class JIComOxidDetails {

	private JILocalCoClass referent = null;
	private String ipid = null;
	private String remUnknownIpid = null;
	private JIOxid oxid = null;
	private JIObjectId oid = null;
	private String iid = null;
	private JIComOxidRuntimeHelper comRuntimeHelper = null;
	private int portForRemUnknown = -1;
	private int protectionLevel = 2;
	private ThreadGroup remUnknownThread = null;
	
	JIComOxidDetails(JILocalCoClass javaInstance, JIOxid oxid, JIObjectId oid
					,String iid,String ipid,JIInterfacePointer ptr, JIComOxidRuntimeHelper helper,int protectionLevel)
	{
		referent = javaInstance;
		this.ipid = ipid;
		this.oxid = oxid;
		this.oid = oid;
		this.iid = iid;
		this.protectionLevel = protectionLevel;
		comRuntimeHelper = helper;
	}

	void setPortForRemUnknown(int port)
	{
		portForRemUnknown = port;
	}
	
	int getPortForRemUnknown()
	{
		return portForRemUnknown;
	}
	
	String getIID()
	{
		return iid;
	}

	String getIpid() 
	{
		return ipid;
	}

	String getRemUnknownIpid() 
	{
		return remUnknownIpid;
	}
	
	void setRemUnknownIpid(String ipid)
	{
		this.remUnknownIpid = ipid;
	}
	
	JIObjectId getOid() 
	{
		return oid;
	}

	JIOxid getOxid() 
	{
		return oxid;
	}

	JILocalCoClass getReferent()
	{
		return referent;
	}
	
	
	JIComOxidRuntimeHelper getCOMRuntimeHelper()
	{
		return comRuntimeHelper;
	}
	
	int getProtectionLevel()
	{
		return protectionLevel;
	}
	
	void setRemUnknownThreadGroup(ThreadGroup remUnknown)
	{
	    this.remUnknownThread = remUnknown;
	}
	
	void interruptRemUnknownThreadGroup()
	{
		if (remUnknownThread != null)
		{
			try
			{
				remUnknownThread.interrupt();
//				remUnknownThread.destroy();
			}catch(Exception e)
			{
				JISystem.getLogger().info("JIComOxidDetails interruptRemUnknownThreadGroup " +  e.toString());
			}
		}
	}
}
