/**j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

package org.jinterop.dcom.core;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.JIComEndpoint;
import org.jinterop.dcom.transport.JIComTransportFactory;

import rpc.Endpoint;
import rpc.FaultException;
import rpc.Stub;



 final class JIRemUnknownServer extends Stub {

	private static Properties defaults = new Properties();
	static {

		defaults.put("rpc.ntlm.lanManagerKey","false");
		defaults.put("rpc.ntlm.sign","false");
		defaults.put("rpc.ntlm.seal","false");
		defaults.put("rpc.ntlm.keyExchange","false");
		defaults.put("rpc.connectionContext","rpc.security.ntlm.NtlmConnectionContext");
		defaults.put("rpc.socketTimeout", new Integer(0).toString());
	}

	private JISession session = null;
	private String syntax = null;
	private String remunknownIPID = null;
	private final Object mutex = new Object();
	private boolean timeoutModifiedfrom0 = false;
	
	/** Interface pointer to the initialized COM server , must be called immediately after the JIComServer has been 
	 * initialized. And closeStub must be called where we call closeStub of JIComServer.
	 * 
	 * @param session
	 * @param interfacePointer
	 * @param address in the "ncacn_ip_tcp:host[port]" format
	 * @throws JIException
	 */
	JIRemUnknownServer(JISession session, String remUnknownIpid, String address) throws JIException
	{
		super();

		this.session = session;
		super.setTransportFactory(JIComTransportFactory.getSingleTon());
		super.setProperties(new Properties(defaults));
		super.getProperties().setProperty("rpc.security.username", session.getUserName());
		super.getProperties().setProperty("rpc.security.password", session.getPassword());
		super.getProperties().setProperty("rpc.ntlm.domain", session.getDomain());
		super.getProperties().setProperty("rpc.socketTimeout", new Integer(session.getGlobalSocketTimeout()).toString());
		if (session.isNTLMv2Enabled())
		{
			super.getProperties().setProperty("rpc.ntlm.ntlmv2", "true");
		}

		//now set the NTLMv2 Session Security.
		if (session.isSessionSecurityEnabled())
		{
			super.getProperties().setProperty("rpc.ntlm.seal", "true");
			super.getProperties().setProperty("rpc.ntlm.sign", "true");
			super.getProperties().setProperty("rpc.ntlm.keyExchange", "true");
			super.getProperties().setProperty("rpc.ntlm.keyLength", "128");
			super.getProperties().setProperty("rpc.ntlm.ntlm2", "true");
		}

		
		// Now will setup syntax for IRemUnknown and the address. 
		syntax = "00000143-0000-0000-c000-000000000046:0.0";
		//and currently only TCPIP is supported.
		setAddress(address);
		this.remunknownIPID = remUnknownIpid; 
		this.session.setStub2(this);
	}

	protected String getSyntax() {
		return syntax;
	}

	/** Execute a Method on the COM Interface identified by the IID
	 *
	 *
	 * @exclude
	 * @param obj
	 * @param targetIID
	 * @return
	 * @throws JIException
	 */
	Object[] call(JICallBuilder obj,String targetIID, int socketTimeout) throws JIException
	{
		synchronized (mutex) {

			if (session.isSessionInDestroy() && !obj.fromDestroySession)
			{
				throw new JIException(JIErrorCodes.JI_SESSION_DESTROYED);
			}

			if (socketTimeout != 0)
			{
				setSocketTimeOut(socketTimeout);
			}
			else //for cases where it was something earlier, but is now being set to 0.
			{
				if (timeoutModifiedfrom0)
				{
					setSocketTimeOut(socketTimeout);
				}
			}

			try {

				attach();
				if (!getEndpoint().getSyntax().getUuid().toString().equalsIgnoreCase(targetIID))
				{
					//first send an AlterContext to the IID of the interface
					getEndpoint().getSyntax().setUuid(new rpc.core.UUID(targetIID));
					getEndpoint().getSyntax().setVersion(0,0);
					((JIComEndpoint)getEndpoint()).rebindEndPoint();
				}

				setObject(obj.getParentIpid());
				call(Endpoint.IDEMPOTENT,obj);

			}catch(FaultException e)
			{
				throw new JIException(e.status,e);
			}catch (IOException e) {
				throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED,e);
			}catch (JIRuntimeException e1)
			{
				throw new JIException(e1);
			}

			return obj.getResults();
		}

	}

	void addRef_ReleaseRef(JICallBuilder obj) throws JIException
	{
		synchronized (mutex) {

			if (remunknownIPID == null)
			{
				return;
			}
			//now also set the Object ID for IRemUnknown call this will be the IPID of the returned JIRemActivation or IOxidResolver
			obj.setParentIpid(remunknownIPID);
			obj.attachSession(session);
			try {
				call(obj,JIRemUnknown.IID_IUnknown, session.getGlobalSocketTimeout());
			} catch (JIRuntimeException e1)
			{
				throw new JIException(e1);
			}

		}
	}

	void closeStub()
	{
		try {
			detach();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void setSocketTimeOut(int timeout)
	{
		if (timeout == 0)
		{
			timeoutModifiedfrom0 = false;
		}
		else
		{
			timeoutModifiedfrom0 = true;
		}

		getProperties().setProperty("rpc.socketTimeout", new Integer(timeout).toString());
	}

}
