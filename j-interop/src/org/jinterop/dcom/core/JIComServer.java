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

package org.jinterop.dcom.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIDefaultAuthInfoImpl;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.JIComEndpoint;
import org.jinterop.dcom.transport.JIComTransportFactory;
import org.jinterop.winreg.IJIWinReg;
import org.jinterop.winreg.JIPolicyHandle;
import org.jinterop.winreg.JIWinRegFactory;

import rpc.Endpoint;
import rpc.FaultException;
import rpc.Stub;


/** <p>Represents a <code>COM</code> Server. This is the startup class for using j-Interop to interoperate 
 *  with a <code> COM </code> component.<br>
 *	
 *	Sample Usage:-
 *  <br>
 *  <code>
 *  
 *  JISession session = JISession.createSession("DOMAIN","USERNAME","PASSWORD"); <br>
 *	JIComServer excelServer = new JIComServer(JIProgId.valueOf(session,"Excel.Application"),address,session); <br>
 *  IJIComObject excelServerObject = excelServer.createInstance(); <br>
 *  
 *  //Obtaining the IJIDispatch (if supported) <br>
 *  IJIDispatch dispatch = (IJIDispatch)JIObjectFactory.createCOMInstance(JIObjectFactory.IID_IDispatch,excelServerObject); <br>
 *  
 *  </code>
 *  
 *  <br> Each instance of <code>JIComServer </code> belongs to 1 session only. The session is responsible for all the reference counting and subsequent garbage collection of this instance. 
 *  
 *   
 *  </p>
 *
 * 
 * @since 1.0
 */
public class JIComServer extends Stub {
	
	private static Properties defaults = new Properties();
	static {
		
		defaults.put("rpc.ntlm.lanManagerKey","false");
		defaults.put("rpc.ntlm.sign","false");
		defaults.put("rpc.ntlm.seal","false");
		defaults.put("rpc.ntlm.keyExchange","false");
		defaults.put("rpc.connectionContext","rpc.security.ntlm.NtlmConnectionContext");
		defaults.put("rpc.socketTimeout", new Integer(0).toString());
//		rpc.connectionContext = rpc.security.ntlm.NtlmConnectionContext		
//		rpc.ntlm.sign = false
//		rpc.ntlm.seal = false
//		rpc.ntlm.keyExchange = false

	}
	
	//private String address = null;
	private JIRemActivation remoteActivation = null;
	private JIOxidResolver oxidResolver = null;
	private String clsid = null;
	private String syntax = null;
	private JISession session = null; 
	private boolean serverInstantiated = false;
	private String remunknownIPID = null;
	private final Object mutex = new Object();
	private boolean timeoutModifiedfrom0 = false;
	private JIInterfacePointer interfacePtrCtor = null;
	
	private JIComServer(){}
	
	/**<p> Instantiates a JIComServer represented by the interfacePointer param. There are cases where a COM server may hand down a 
	 * reference to a different COM server(which may or may not be on the same machine) and we would like to hook in between. 
	 * The <code>IJIComObject</code> interface is usable only in the context of the current JIComServer, but when the interfacePointer 
	 * is of a completely different COM server, the JIObjectFactory APIs will not work. The reason is the interface pointer passed to those 
	 * APIs expects to belong only to a single and same COM server (say 'A'). If by any chance, that COM server passes a reference to you 
	 * of another COM server (say 'B') on a different machine, the <code>IJIComObject</code> so returned from <code>JIObjectFactory</code> APIs 
	 * will result in "Method not found" Exceptions (or others) since the pointer returned via that will always place calls to  'A' instead of 'B'. 
	 * Under such scenarios you must use this API. This is not a usual case and for reasons related to nature of DCOM, will be very well documented 
	 * in the Developers guide of your COM server.  
	 * 
	 * <p>The DCOM specs refer to this as the "middleman" case. (Section 3.3.1) </p>
	 * </p>
	 * @param session Please use a new session and not an already bounded one. The <code>JISession.createSession(JISession)</code> can be used to create a new session.
	 * @param interfacePointer reference to a different COM server pointer.
	 * @param ipAddress		  Can be <code>null</code>. Sometimes there are many adapters (virtual as well) on the Target machine to which this interface pointer belongs,
	 * 						  which may get sent as part of the interface pointer and consequently this call will fail since it is a possibility that IP is not reachable via this machine.
	 * 						  The developer can send in the valid IP and if found in the interface pointer list will be used to talk to the target machine, overriding the other IP addresses present in the interface pointer. 
	 * 						  If this IP is not found then the "machine name" binding will be used. If this param is <code>null</code> then the first binding obtained from the interface pointer is used. 
	 */
	JIComServer(JISession session, JIInterfacePointer interfacePointer,String ipAddress) throws JIException
	{
		super();
		
		if (interfacePointer == null || session == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMSTUB_ILLEGAL_ARGUMENTS));
		}
		
		if (session.getStub() != null)
		{
			throw new JIException(JIErrorCodes.JI_SESSION_ALREADY_ESTABLISHED);
		}

		
		super.setTransportFactory(JIComTransportFactory.getSingleTon());
		//now read the session and prepare information for the stub.
		super.setProperties(new Properties(defaults));
		super.getProperties().setProperty("rpc.security.username", session.getUserName());
		super.getProperties().setProperty("rpc.security.password", session.getPassword());
		super.getProperties().setProperty("rpc.ntlm.domain", session.getDomain());
		super.getProperties().setProperty("rpc.socketTimeout", new Integer(session.getGlobalSocketTimeout()).toString());

		JIStringBinding[] addressBindings = interfacePointer.getStringBindings().getStringBindings();

		int i = 0;
		JIStringBinding binding = null;
		JIStringBinding nameBinding = null;
		String targetAddress = ipAddress == null ? "" : ipAddress.trim();
		
//		if (!targetAddress.equals(""))
		{
			//now we choose, otherwise the first one we get.
			while(i < addressBindings.length)
			{
				binding = addressBindings[i];
				if (binding.getTowerId() != 0x07) //this means, even though I asked for TCPIP something else was supplied, noticed this in win2k.				    
				{
				    i++;
				    continue;
				}
				//get the one with IP address
				int index = binding.getNetworkAddress().indexOf(".");
				if (index != -1)
				{
					try{

						if (binding.getNetworkAddress().equalsIgnoreCase(targetAddress))
						{
							nameBinding = null;
							break;	
						}
						
						//now check for the one with port
						index = binding.getNetworkAddress().indexOf("[");//this contains the port
						if (index != -1 && binding.getNetworkAddress().substring(0,index).equalsIgnoreCase(targetAddress))
						{
							nameBinding = null;
							break;	
						}
						
						
					}catch(NumberFormatException e)
					{
					
					}
				}
				else
				{
					//can only come for the name, saving it incase nothing matches the target address
					nameBinding = binding;
				}
				i++;
			}
			
			binding = nameBinding == null ? binding : nameBinding;
		}
//		else
//		{
//			//Just pick up the first one.
//			binding = addressBindings[0];
//		}
		
		
		//will use this last binding .
		//and currently only TCPIP is supported.
		String address = binding.getNetworkAddress();
		if (address.indexOf("[") == -1 )//this does not contain the port
		{
			//use 135
			address = address + "[135]";
		}
		super.setAddress("ncacn_ip_tcp:" + address);
		this.session = session;
		this.session.setTargetServer(getAddress().substring(getAddress().indexOf(":") + 1,getAddress().indexOf("[")));
		oxidResolver = new JIOxidResolver(((JIStdObjRef)interfacePointer.getObjectReference(JIInterfacePointer.OBJREF_STANDARD)).getOxid());
		try {
			
			syntax = "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0";
			attach();
			//first send an AlterContext to the IID of the IOxidResolver
			getEndpoint().getSyntax().setUuid(new rpc.core.UUID("99fcfec4-5260-101b-bbcb-00aa0021347a"));
			getEndpoint().getSyntax().setVersion(0,0);
			((JIComEndpoint)getEndpoint()).rebindEndPoint();
			
			call(Endpoint.IDEMPOTENT,oxidResolver);
		}catch(FaultException e)
		{
			throw new JIException(e.status,e);
		}
		catch (IOException e) {
			throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED,e);
		}catch (JIRuntimeException e1)
		{
			throw new JIException(e1);
		}

		// Now will setup syntax for IRemUnknown and the address.
		//syntax = "00000143-0000-0000-c000-000000000046:0.0";
		syntax = interfacePointer.getIID() + ":0.0";
		
		//now for the new ip and the port.
		
		JIStringBinding[] bindings = oxidResolver.getOxidBindings().getStringBindings();
		
		binding = null;
		nameBinding = null;
		i = 0;
//		if (!targetAddress.equals(""))
		{
			//now we choose, otherwise the first one we get.
			while(i < bindings.length)
			{
				binding = bindings[i];
				if (binding.getTowerId() != 0x07) //this means, even though I asked for TCPIP something else was supplied, noticed this in win2k.                   
                {
                    i++;
                    continue;
                }
				//get the one with IP address
				int index = binding.getNetworkAddress().indexOf(".");
				if (index != -1)
				{
					try{
						
						if (binding.getNetworkAddress().equalsIgnoreCase(targetAddress))
						{
							nameBinding = null;
							break;	
						}
						
						//now check for the one with port
						index = binding.getNetworkAddress().indexOf("[");//this contains the port
						if (index != -1 && binding.getNetworkAddress().substring(0,index).equalsIgnoreCase(targetAddress))
						{
							nameBinding = null;
							break;	
						}
					}catch(NumberFormatException e)
					{
					
					}
				}
				else
				{
					//can only come for the name, saving it incase nothing matches the target address
					nameBinding = binding;
				}
				i++;
			}
			
			binding = nameBinding == null ? binding : nameBinding;
		}
//		else
//		{
//			//Just pick up the first one.
//			binding = bindings[0];
//		}

		
		//now set the NTLMv2 Session Security.
		if (session.isSessionSecurityEnabled())
		{
			super.getProperties().setProperty("rpc.ntlm.seal", "true");
			super.getProperties().setProperty("rpc.ntlm.sign", "true");
			super.getProperties().setProperty("rpc.ntlm.keyExchange", "true");
			super.getProperties().setProperty("rpc.ntlm.keyLength", "128");
			super.getProperties().setProperty("rpc.ntlm.ntlm2", "true");
		}
		
		//and currently only TCPIP is supported.
		setAddress("ncacn_ip_tcp:" + binding.getNetworkAddress());
		remunknownIPID = oxidResolver.getIPID();
		interfacePtrCtor = interfacePointer;
		this.session.setStub(this);
		
	}
	
	
	/**<p>Instantiates this object with JIProgId which is, according to MSDN:- <br> 
	 *  
	 *  <i>
	 *  A ProgID, or programmatic identifier, is a registry entry that can be associated with a JIClsid. 
	 *  The format of a ProgID is <Vendor>.<Component>.<Version>, separated by periods and with no spaces,
	 *  as in Word.Document.6. Like the JIClsid, the ProgID identifies a class, but with less precision. 
	 *  </i>
	 *  
	 *  <br> ProgId is searched for it's JIClsid in the target servers registry. For the very first run, calling
	 *  with ProgId will result in 3 extra calls to the server (for JIClsid resolution). Subsequently the 
	 *  j-Interop Library caches the JIClsid for this progId and uses that. It is also stored in a properties file (progIdVsClsidDB.properties) 
	 *  and reffered from there. We can save on the 3 calls in the subsequent runs also. <br>
	 *  
	 *  This call works on the LOCALHOST.
	 *  </p>
	 *  
	 * @param progId Id like "Excel.Application" , "TestCOMServer.Test123" e.t.c
	 * @param session User's session.
	 * @throws IllegalArgumentException raised when either progId or session is null.
	 * @throws JIException
	 * @throws UnknownHostException 
	 */
	public JIComServer(JIProgId progId,JISession session) throws JIException, UnknownHostException
	{
		this(progId,InetAddress.getLocalHost().getHostAddress(),session);
	}
	
	/** <p>Instantiates this object with JIClsid which is, according to MSDN:- <br> 
	 *  
	 *  <i>
	 *  A universally unique identifier (UUID) that identifies a type of Component Object Model (COM) object.  
	 *  Each type of COM object item has its JIClsid in the registry so that it can be loaded and used by other
	 *  applications. For example, a spreadsheet may create worksheet items, chart items, and macrosheet items.
	 *  Each of these item types has its own JIClsid that uniquely identifies it to the system. 
	 *  </i><br>
	 *  
	 *  This call works on the LOCALHOST.
	 *  </p>
	 *  
	 * @param clsid 128 bit uuid like "00024500-0000-0000-C000-000000000046".
	 * @param session User's session.
	 * @throws IllegalArgumentException raised when either clsid or session is null.
	 * @throws JIException
	 * @throws UnknownHostException 
	 */
	public JIComServer(JIClsid clsid,JISession session) throws IllegalArgumentException,JIException, UnknownHostException
	{
		this(clsid,InetAddress.getLocalHost().getHostAddress(),session);
	}
	
	/**<p> Instantiates this object with JIProgId which is, according to MSDN:- <p> 
	 *  
	 *  <i>
	 *  A ProgID, or programmatic identifier, is a registry entry that can be associated with a JIClsid. 
	 *  The format of a ProgID is <Vendor>.<Component>.<Version>, separated by periods and with no spaces,
	 *  as in Word.Document.6. Like the JIClsid, the ProgID identifies a class, but with less precision. 
	 *  </i>
	 *  
	 *  <br> ProgId is searched for it's JIClsid in the target servers registry. For the very first run, calling
	 *  with ProgId will result in 3 extra calls to the server (for JIClsid resolution). Subsequently the 
	 *  j-Interop Library caches the JIClsid for this progId and uses that. It is also stored in a properties file (progIdVsClsidDB.properties) 
	 *  and reffered from there. We can save on the 3 calls in the subsequent runs also. <br>
	 *  </p>
	 *  
	 * @param progId Id like "Excel.Application" , "TestCOMServer.Test123" e.t.c
	 * @param address address of the server where the <code>COM<code> object resides . This should be in the IEEE IP format (e.g. 192.168.170.6) or HostName.
	 * @param session User's session.
	 * @throws IllegalArgumentException raised when either progId or address or session is null.
	 * @throws JIException
	 * @throws UnknownHostException 
	 */
	public JIComServer(JIProgId progId,String address, JISession session) throws JIException, UnknownHostException
	{
		super();
		
		if (progId == null || address == null || session == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMSTUB_ILLEGAL_ARGUMENTS));
		}
		
		if (session.getStub() != null)
		{
			throw new JIException(JIErrorCodes.JI_SESSION_ALREADY_ESTABLISHED);
		}
		
		address = address.trim();
		address = InetAddress.getByName(address).getHostAddress();
		
		progId.setServer(address);
		address = "ncacn_ip_tcp:"+address+"[135]";
		JIClsid clsid = progId.getCorrespondingCLSID();
		initialise(clsid,address,session);
	}
	
	/** <p>Instantiates this object with JIClsid which is, according to MSDN:- <br> 
	 *  
	 *  <i>
	 *  A universally unique identifier (UUID) that identifies a type of Component Object Model (COM) object.  
	 *  Each type of COM object item has its JIClsid in the registry so that it can be loaded and used by other
	 *  applications. For example, a spreadsheet may create worksheet items, chart items, and macrosheet items.
	 *  Each of these item types has its own JIClsid that uniquely identifies it to the system. An IllegalArgumentException raised when either clsid or address or session is null. 
	 *  </i>
	 *  
	 *  </p>
	 *  
	 * @param clsid 128 bit uuid like "00024500-0000-0000-C000-000000000046".
	 * @param address address of the server where the <code>COM<code> object resides . This should be in the IEEE IP format (e.g. 192.168.170.6) or HostName.
	 * @param session User's session.
	 * @throws JIException
	 * @throws UnknownHostException 
	 */
	public JIComServer(JIClsid clsid,String address, JISession session) throws JIException, UnknownHostException
	{
		super();
		
		if (clsid == null || address == null || session == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMSTUB_ILLEGAL_ARGUMENTS));
		}
		
		if (session.getStub() != null)
		{
			throw new JIException(JIErrorCodes.JI_SESSION_ALREADY_ESTABLISHED);
		}

		address = address.trim();
		//address = address.replace(' ','');
		address = "ncacn_ip_tcp:"+InetAddress.getByName(address).getHostAddress()+"[135]";
		
		initialise(clsid,address,session);
	}

	private void initialise(JIClsid clsid,String address, JISession session) throws JIException
	{
		super.setTransportFactory(JIComTransportFactory.getSingleTon());
		//now read the session and prepare information for the stub.
		super.setProperties(new Properties(defaults));
		super.getProperties().setProperty("rpc.security.username", session.getUserName());
		super.getProperties().setProperty("rpc.security.password", session.getPassword());
		super.getProperties().setProperty("rpc.ntlm.domain", session.getDomain());
		super.getProperties().setProperty("rpc.socketTimeout", new Integer(session.getGlobalSocketTimeout()).toString());
		super.setAddress(address);
//		if (session.isSessionSecurityEnabled())
//		{
//			super.getProperties().setProperty("rpc.ntlm.seal", "true");
//			super.getProperties().setProperty("rpc.ntlm.sign", "true");
//			super.getProperties().setProperty("rpc.ntlm.keyExchange", "true");
//			super.getProperties().setProperty("rpc.ntlm.keyLength", "128");
//			super.getProperties().setProperty("rpc.ntlm.ntlm2", "true");
//		}
		this.clsid = clsid.getCLSID().toUpperCase();
		this.session = session;
		this.session.setTargetServer(address.substring(address.indexOf(":") + 1,address.indexOf("[")));
		try{
			init();
		}catch(JIException e)
		{
			if (e.getErrorCode() == 0x80040154)
			{
				if (JISystem.getLogger().isLoggable(Level.WARNING))
				{
					JISystem.getLogger().warning("Got the class not registered exception , will attempt setting entries based on status flags...");
				}
				//try registering the dll\ocx on our own
				//check for clsid.autoregister flag
				//check for jisystem.autoregister flag. 
				//jisystem takes precedence over clsid.
				
				if (JISystem.isAutoRegistrationSet() || clsid.isAutoRegistrationSet())
				{	
					
					//first create the registry entries.
					try {
						IJIWinReg registry = null;
						registry = JIWinRegFactory.getSingleTon().getWinreg(new JIDefaultAuthInfoImpl(session.getDomain(),session.getUserName(),session.getPassword()),session.getTargetServer(),true);
						JIPolicyHandle hkcr = registry.winreg_OpenHKCR();
						JIPolicyHandle key = registry.winreg_CreateKey(hkcr,"CLSID\\{" + this.clsid + "}",IJIWinReg.REG_OPTION_NON_VOLATILE,IJIWinReg.KEY_ALL_ACCESS );
						registry.winreg_SetValue(key,"AppID",("{" + this.clsid + "}").getBytes(),false,false);
						registry.winreg_CloseKey(key);
						key = registry.winreg_CreateKey(hkcr,"AppID\\{" + this.clsid + "}",IJIWinReg.REG_OPTION_NON_VOLATILE,IJIWinReg.KEY_ALL_ACCESS );
						registry.winreg_SetValue(key,"DllSurrogate","  ".getBytes(),false,false);
						registry.winreg_CloseKey(key);
						registry.winreg_CloseKey(hkcr);
						registry.closeConnection();
					} catch (UnknownHostException e1) {
						//auto registration failed as well...
						JISystem.getLogger().throwing("JIComServer","initialise",e1);
						throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION3,e1);
					}
					//lets retry
					init();
				}
				else
				{
					throw e;
				}
			}
			else
			{
				throw e;
			}
			
		}
		
		this.session.setStub(this);
		
	}
	
	
	private void init () throws JIException
	{
		if (remoteActivation != null && remoteActivation.isActivationSuccessful())
		{
			return;
		}
		
		try {
			
			
			syntax = "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0";
			attach();
			//first send an AlterContext to the IID of the IOxidResolver
			getEndpoint().getSyntax().setUuid(new rpc.core.UUID("99fcfec4-5260-101b-bbcb-00aa0021347a"));
			getEndpoint().getSyntax().setVersion(0,0);
			((JIComEndpoint)getEndpoint()).rebindEndPoint();
			
			//setup syntax for IRemoteActivation
			syntax = "4d9f4ab8-7d1c-11cf-861e-0020af6e7c57:0.0";
			getEndpoint().getSyntax().setUuid(new rpc.core.UUID("4d9f4ab8-7d1c-11cf-861e-0020af6e7c57"));
			getEndpoint().getSyntax().setVersion(0,0);
			((JIComEndpoint)getEndpoint()).rebindEndPoint();

			remoteActivation = new JIRemActivation(clsid);
			call(Endpoint.IDEMPOTENT,remoteActivation);
		}catch(FaultException e)
		{
			remoteActivation = null;
			throw new JIException(e.status,e);
		}
		catch (IOException e) {
			remoteActivation = null;
			throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED,e);
		}catch (JIRuntimeException e1)
		{
			remoteActivation = null;
			throw new JIException(e1);
		}

		// Now will setup syntax for IRemUnknown and the address.
		syntax = "00000143-0000-0000-c000-000000000046:0.0";
		//now for the new ip and the port.
		
		JIStringBinding[] bindings = remoteActivation.getDualStringArrayForOxid().getStringBindings();
		int i = 0;
		JIStringBinding binding = null;
		JIStringBinding nameBinding = null;
		String targetAddress = getAddress();
		targetAddress  = targetAddress.substring(targetAddress.indexOf(':') + 1,targetAddress.indexOf('['));
		while(i < bindings.length)
		{
			binding = bindings[i];
			if (binding.getTowerId() != 0x07) //this means, even though I asked for TCPIP something else was supplied, noticed this in win2k.                    
            {
                i++;
                continue;
            }
			//get the one with IP address
			int index = binding.getNetworkAddress().indexOf(".");
			if (index != -1)
			{
				try{
					//Integer.parseInt(binding.getNetworkAddress().substring(0,index));
					index = binding.getNetworkAddress().indexOf("[");//this contains the port
					if (index != -1 && binding.getNetworkAddress().substring(0,index).equalsIgnoreCase(targetAddress))
					{
						break;	
					}
				}catch(NumberFormatException e)
				{
				
				}
			}
			else
			{
				//can only come for the name, saving it incase nothing matches the target address
				//then we are not sure which is the right IP and which might be virtual, refer to 
				//issue faced by Igor. 
				nameBinding = binding;
				index = binding.getNetworkAddress().indexOf("[");//this contains the port
				if (binding.getNetworkAddress().substring(0,index).equalsIgnoreCase(targetAddress))
				{
					break;	
				}
			}
			i++;
		}

		if (binding == null)
		{
			binding = nameBinding;
		}
		//will use this last binding .
		//and currently only TCPIP is supported.
		//now set the NTLMv2 Session Security.
		if (session.isSessionSecurityEnabled())
		{
			super.getProperties().setProperty("rpc.ntlm.seal", "true");
			super.getProperties().setProperty("rpc.ntlm.sign", "true");
			super.getProperties().setProperty("rpc.ntlm.keyExchange", "true");
			super.getProperties().setProperty("rpc.ntlm.keyLength", "128");
			super.getProperties().setProperty("rpc.ntlm.ntlm2", "true");
		}
		setAddress("ncacn_ip_tcp:" + binding.getNetworkAddress());
		remunknownIPID = remoteActivation.getIPID();
 	}

	
	
	//Will give a call to IRemUnknown for the passed IID.
	IJIComObject getInterface(String iid,String ipidOfTheTargetUnknown) throws JIException 
	{
		IJIComObject retval = null;
		//this is still essentially serial, since all threads will have to wait for mutex before 
		//entering addToSession.
		synchronized (mutex) {
			//now also set the Object ID for IRemUnknown call this will be the IPID of the returned JIRemActivation
			setObject(remunknownIPID);
			//setObject(ipid);
			
			//JIRemUnknown reqUnknown = new JIRemUnknown(unknownIPID,iid,5);
			JIRemUnknown reqUnknown = new JIRemUnknown(ipidOfTheTargetUnknown,iid);
			try {
				call(Endpoint.IDEMPOTENT,reqUnknown);
			}catch(FaultException e)
			{
				throw new JIException(e.status,e);
			}
			catch (IOException e) {
				throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED,e);
			}catch (JIRuntimeException e1)
			{
				//remoteActivation = null;
				throw new JIException(e1);
			}
			
			retval = JISessionHelper.instantiateComObject(session, reqUnknown.getInterfacePointer());
			
			//for querying dispatch we can't send another call
			if (!iid.equalsIgnoreCase("00020400-0000-0000-c000-000000000046"))
			{
				boolean success = true;
				((JIComObjectImpl)retval).isDual = true;
				//now to check whether it supports IDispatch
				//IDispatch 00020400-0000-0000-c000-000000000046
				JIRemUnknown dispatch = new JIRemUnknown(retval.getIpid(),"00020400-0000-0000-c000-000000000046");
				try {
					call(Endpoint.IDEMPOTENT,dispatch);
				}catch(FaultException e)
				{
					throw new JIException(e.status,e);
				}catch (IOException e) {
					throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED,e);
				}catch (JIRuntimeException e1)
				{
					//will eat this exception here. 
					((JIComObjectImpl)retval).isDual = false;
					success = false;
				}
				
				if (success)
				{
					//which means that IDispatch is supported
					session.releaseRef(dispatch.getInterfacePointer().getIPID());
				}
			}
		}
	
		return retval;
		
	}
	
	
	
	
	
	/** Returns a <code>IJIComObject</code> representing the <code>COM</code> Server. Not to be used with <code>JIComServer(JISession,JIInterfacePointer,String)</code> ctor,
	 * Use getInstance() instead.
	 * 
	 * @return
	 * @throws JIException
	 */
	public IJIComObject createInstance() throws JIException
	{
		if (interfacePtrCtor != null)
		{
			throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMSTUB_WRONGCALLCREATEINSTANCE));
		}
		IJIComObject comObject = null;
		
		//This method is still essentially serial, since all threads will have to stop at mutex and then 
		//go to addToSession after it (since there is no condition).
		synchronized (mutex) {
			if (serverInstantiated)
			{
				throw new JIException(JIErrorCodes.JI_OBJECT_ALREADY_INSTANTIATED,(Throwable)null); 
			}
//			JIStdObjRef objRef = (JIStdObjRef)(remoteActivation.getMInterfacePointer().getObjectReference(JIInterfacePointer.OBJREF_STANDARD));
//			comObject = getObject(objRef.getIpid(),IJIUnknown.IID);
			comObject = JISessionHelper.instantiateComObject(session, remoteActivation.getMInterfacePointer());
			if (remoteActivation.isDual)
			{
				//IJIComObject comObject2 = getObject(remoteActivation.dispIpid,"00020400-0000-0000-c000-000000000046");
				//this will get garbage collected and then removed.
				//session.addToSession(comObject2,remoteActivation.dispOid);
				session.releaseRef(remoteActivation.dispIpid);
				remoteActivation.dispIpid = null;
				((JIComObjectImpl)comObject).isDual = true;
			}
			serverInstantiated = true;
		}
		
		return comObject;
	}

	/**Returns a <code>IJIComObject</code> representing the <code>COM</code> Server. To be used only with <code>JIComServer(JISession,JIInterfacePointer,String)</code> ctor,
	 * otherwise use createInstance() instead.
	 * 
	 * @return
	 * @throws JIException
	 */
	IJIComObject getInstance() throws JIException
	{
		if (interfacePtrCtor == null)
		{
			throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMSTUB_WRONGCALLGETINSTANCE));
		}

		IJIComObject comObject = null;
		//This method is still essentially serial, since all threads will have to stop at mutex and then 
		//go to addToSession after it (since there is no condition).
		synchronized (mutex) {
			if (serverInstantiated)
			{
				throw new JIException(JIErrorCodes.JI_OBJECT_ALREADY_INSTANTIATED,(Throwable)null); 
			}
			
//			JIStdObjRef objRef = (JIStdObjRef)(interfacePtrCtor.getObjectReference(JIInterfacePointer.OBJREF_STANDARD));
//			comObject = getObject(objRef.getIpid(),interfacePtrCtor.getIID());
			comObject = JISessionHelper.instantiateComObject(session,interfacePtrCtor);
			serverInstantiated = true;
		}
		
		return comObject;
	}

	
	protected String getSyntax() {
		return syntax;
	}
	
//	/**
//	 * @exclude
//	 * @return
//	 */
//	String getIpid()
//	{
//		if (remoteActivation != null && remoteActivation.isActivationSuccessful())
//		{
//			return remoteActivation.getIPID();
//		}
//		else
//			return null;
//	}
	
	/** Execute a Method on the COM Interface identified by the IID.
	 * 
	 * 
	 * @exclude
	 * @param obj
	 * @param targetIID
	 * @return
	 * @throws JIException
	 */ 
	Object[] call(JICallObject obj,String targetIID) throws JIException
	{
		return call(obj, targetIID, session.getGlobalSocketTimeout());
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
	Object[] call(JICallObject obj,String targetIID, int socketTimeout) throws JIException
	{
		synchronized (mutex) {
			
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
	
	/**
	 * @exclude
	 * @return
	 */
	JIInterfacePointer getServerInterfacePointer()
	{
		//remoteactivation can be null only incase of OxidResolver ctor getting called.
		return remoteActivation == null ? interfacePtrCtor : remoteActivation.getMInterfacePointer();
	}

	void addRef_ReleaseRef(JICallObject obj) throws JIException
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
				call(obj,JIRemUnknown.IID_IUnknown);
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
