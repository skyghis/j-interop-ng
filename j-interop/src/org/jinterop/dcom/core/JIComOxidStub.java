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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.JIComTransportFactory;

import rpc.Endpoint;
import rpc.Stub;

/**Class only used for Oxid ping requests between the Java client and the COM server. This is not for 
 * reverse operations i.e COM client and Java server. That is handled at the OxidResolverImpl level in JIComOxidRuntimeHelper,
 * since each of the Oxid Resolver has a separate thread for COM client.  
 * 
 * 
 * @exclude
 * @since 1.0
 *
 */
final class JIComOxidStub extends Stub{

	private static Properties defaults = new Properties();
	
	static {
			
			defaults.put("rpc.ntlm.lanManagerKey","false");
			defaults.put("rpc.ntlm.sign","false");
			defaults.put("rpc.ntlm.seal","false");
			defaults.put("rpc.ntlm.keyExchange","false");
			defaults.put("rpc.connectionContext","rpc.security.ntlm.NtlmConnectionContext");
	
	}
	
	protected String getSyntax() {
		return "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0";
	}
	
	public JIComOxidStub(String address, String domain,String username, String password, 
			boolean useNTLMv2, boolean isSSO)
	{
		super();
		super.setTransportFactory(JIComTransportFactory.getSingleTon());
		super.setProperties(new Properties(defaults));
		
		if (isSSO)
		{
			super.getProperties().setProperty("rpc.ntlm.sso", "true");
		}
		else
		{
			super.getProperties().setProperty("rpc.security.username", username);
			super.getProperties().setProperty("rpc.security.password", password);
			super.getProperties().setProperty("rpc.ntlm.domain", domain);
		}
		
		super.setAddress("ncacn_ip_tcp:" + address + "[135]");
		super.getProperties().setProperty("rpc.ntlm.ntlmv2", Boolean.toString(useNTLMv2));
	}
	
	public byte[] call(boolean isSimplePing,byte[] setId,ArrayList listOfAdds,ArrayList listOfDels, int seqNum)
	{
		PingObject pingObject = new PingObject();
		pingObject.setId = setId;
		pingObject.listOfAdds = listOfAdds;
		pingObject.listOfDels = listOfDels;
		pingObject.seqNum = seqNum;
		
		if (isSimplePing)
		{
			pingObject.opnum = 1;
		}
		else
		{
			pingObject.opnum = 2;
		}
		
		try {
			call(Endpoint.IDEMPOTENT,pingObject);
		} catch (IOException e) {
			JISystem.getLogger().throwing("JIComOxidStub","call",e);  
		}

		//returns setId.
		return pingObject.setId;
	}
	
	public void close()
	{
		try {
			detach();
		} catch (Exception e) 
		{
			//JISystem.getLogger().throwing("JIComOxidStub","close",e);  
		}
	}
	
}

class PingObject extends NdrObject
{
	int opnum = -1;
	
	ArrayList listOfAdds = new ArrayList();
	ArrayList listOfDels = new ArrayList();
	byte[] setId = null;
	int seqNum = 0;
	
	public int getOpnum()
	{
		return opnum; 
	}
	
	//read follows write...please remember
	public void write(NetworkDataRepresentation ndr) 
	{
		switch(opnum)
		{
			case 2: //complex ping
				
				int newlength = 8 + 6 + 8 + listOfAdds.size()*8 + 8 + listOfDels.size()*8 + 16;
				if (newlength > ndr.getBuffer().buf.length)
				{
					ndr.getBuffer().buf = new byte[newlength + 16];
				}
				
				if(setId == null)
				{
					if (JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("Complex Ping going for the first time, will get the setId as response of this call ");
					}
					setId = new byte[]{0,0,0,0,0,0,0,0};
				}
				else
				{
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				   	jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), setId, 0, setId.length);
				   	if (JISystem.getLogger().isLoggable(Level.INFO))
				   	{
				   		JISystem.getLogger().info("Complex Ping going for setId: " + byteArrayOutputStream.toString());
				   	}
				}
				
				if (JISystem.getLogger().isLoggable(Level.INFO))
				{
					JISystem.getLogger().info("Complex ping going : listOfAdds -> Size : " + listOfAdds.size() + " , " + listOfAdds);
					JISystem.getLogger().info("listOfDels -> Size : " + listOfDels.size() + " , " + listOfDels);
				}
				
				JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr,setId);
				
				JIMarshalUnMarshalHelper.serialize(ndr,Short.class,new Short((short)seqNum),null,JIFlags.FLAG_NULL);//seq
				JIMarshalUnMarshalHelper.serialize(ndr,Short.class,new Short((short)listOfAdds.size()),null,JIFlags.FLAG_NULL);//add
				JIMarshalUnMarshalHelper.serialize(ndr,Short.class,new Short((short)listOfDels.size()),null,JIFlags.FLAG_NULL);//del
				
				if (listOfAdds.size() > 0)
				{
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(new Object().hashCode()),null,JIFlags.FLAG_NULL);//pointer
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(listOfAdds.size()),null,JIFlags.FLAG_NULL);
					
					
					for (int i = 0;i < listOfAdds.size();i++)
					{
						JIObjectId oid = (JIObjectId)listOfAdds.get(i);
						JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr,oid.getOID());
						//JISystem.getLogger().info("[" + oid.toString() + "]");
					}
				}
				else
				{
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);//null pointer
				}
				
				if (listOfDels.size() > 0)
				{
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(new Object().hashCode()),null,JIFlags.FLAG_NULL);//pointer
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(listOfDels.size()),null,JIFlags.FLAG_NULL);
					
					//now align for array
					double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
					long k = (k = Math.round(index%8.0)) == 0 ? 0 : 8 - k ;
					ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
					
					for (int i = 0;i < listOfDels.size();i++)
					{
						JIObjectId oid = (JIObjectId)listOfDels.get(i);
						JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr,oid.getOID());
						//JISystem.getLogger().info("[" + oid + "]");
					}
				}
				else
				{
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);//null pointer
				}
				
				JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);
				JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);
				JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);
				JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);
				break;
				
			case 1:// simple ping
				
				if(setId != null)
				{
					JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr,setId);//setid
				  	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				   	jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), setId, 0, setId.length);
				   	if (JISystem.getLogger().isLoggable(Level.INFO))
				   	{
				   		JISystem.getLogger().info("Simple Ping going for setId: " + byteArrayOutputStream.toString());
				   	}
				}				
				else
				{
					if (JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("Some error ! Simple ping requested , but has no setID ");
					}
				}
				break;
				
			default:
				//nothing.
		}
	}
	
	public void read(NetworkDataRepresentation ndr) 
	{
		//read response and fill DSs accordingly
		switch(opnum)
		{
			case 2: //complex ping
				
				setId = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8);
				//ping factor
				JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null,JIFlags.FLAG_NULL,null);
				
				//hresult
				int hresult = ((Integer)(JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL,null))).intValue();
				
				if (hresult != 0)
				{
					if (JISystem.getLogger().isLoggable(Level.SEVERE))
					{
						JISystem.getLogger().severe("Some error ! Complex ping failed , hresult: " + hresult);
					}
				}
				else
				{
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				   	jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), setId, 0, setId.length);
				   	if (JISystem.getLogger().isLoggable(Level.INFO))
				   	{
				   		JISystem.getLogger().info("Complex Ping Succeeded,  setId is : " + byteArrayOutputStream.toString());
				   	}
				}
				
				break;
			case 1:// simple ping
				
				//hresult
				hresult = ((Integer)(JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL,null))).intValue();
				
				if (hresult != 0)
				{
					if (JISystem.getLogger().isLoggable(Level.SEVERE))
					{
						JISystem.getLogger().severe("Some error ! Simple ping failed , hresult: " + hresult);
					}
				}
				else
				{
					if (JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("Simple Ping Succeeded");
					}
				}
				break;
				
			default:
				//nothing.
		}
	}
}





