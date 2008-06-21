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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import ndr.NdrBuffer;
import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.IJICOMRuntimeWorker;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.JIComRuntimeEndpoint;
import org.jinterop.dcom.transport.JIComRuntimeTransportFactory;

import rpc.Stub;
import rpc.core.UUID;

import com.iwombat.foundation.IdentifierFactory;
import com.iwombat.util.GUIDUtil;



/**Used to manipulate Oxid details. one instance is created per binding
 * call to the oxid resolver. 
 * 
 * @since 1.0
 *
 */
final class JIComOxidRuntimeHelper extends Stub {

	
	
	JIComOxidRuntimeHelper(Properties properties)
	{
		super.setTransportFactory(JIComRuntimeTransportFactory.getSingleTon());
		super.setProperties(properties);
		super.setAddress("127.0.0.1[135]");//this is never consulted so , putting localhost here.
	}
	
	protected String getSyntax() {
		//return "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0";//IOxidResolver IID
		return UUID.NIL_UUID + ":0.0" ; //returning nothing
	}

	void startOxid(int portNumLocal,int portNumRemote) throws IOException
	{
		Thread oxidResolverThread = new Thread(new Runnable() {
			public void run() {
				try{
					if (JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("started startOxid thread: " + Thread.currentThread().getName());
					}
					attach();
					((JIComRuntimeEndpoint)getEndpoint()).processRequests(new OxidResolverImpl(getProperties()),null);
				}catch(Exception e)
				{
					if (JISystem.getLogger().isLoggable(Level.WARNING))
					{
						JISystem.getLogger().throwing("Oxid Resolver Thread", "run", e);
						JISystem.getLogger().warning("Oxid Resolver Thread: " +  e.getMessage() + " , on thread Id: " + Thread.currentThread().getName());
					}
				}
				finally{
					try {
						((JIComRuntimeEndpoint)getEndpoint()).detach();
					} catch (IOException e) {}
				}
				if (JISystem.getLogger().isLoggable(Level.INFO))
				{
					JISystem.getLogger().info("terminating startOxid thread: " + Thread.currentThread().getName());
				}
			}
		},"jI_OxidResolver_Client[" + portNumLocal + " , " + portNumRemote + "]");
		oxidResolverThread.setDaemon(true);
		oxidResolverThread.start();
	}
	
	//returns the port to which the server is listening.
	Object[] startRemUnknown(final String baseIID, final String ipidOfRemUnknown, final String ipidOfComponent) throws IOException
	{
	    final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
	    final ServerSocket serverSocket = serverSocketChannel.socket();//new ServerSocket(0);
	    serverSocket.setSoTimeout(120*1000); //2 min timeout.
	    serverSocket.bind(null);
        int remUnknownPort = serverSocket.getLocalPort();
		Thread remUnknownThread = new Thread(new Runnable() {
			public void run() {
				try{
					if (JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("started startRemUnknown thread: " + Thread.currentThread().getName());
					}
					Socket socket = serverSocket.accept();
					if (JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("RemUnknown Thread: Got Connection from " + socket.getPort());
					}
					synchronized (JIComOxidRuntime.mutex) {
			    		JISystem.setSocket(socket);
				    	//now create the JIComOxidRuntimeHelper Object and start it.
			    		attach();
			    		//getEndpoint().getSyntax().getUuid().toString();
					}
					((JIComRuntimeEndpoint)getEndpoint()).processRequests(new RemUnknownObject(ipidOfRemUnknown,ipidOfComponent),baseIID);
				}catch(SmbAuthException e)
				{
					JISystem.getLogger().throwing("JIComOxidRuntimeHelper","startRemUnknown",e);
					throw new JIRuntimeException(JIErrorCodes.JI_CALLBACK_AUTH_FAILURE);
				}
				catch(SmbException e)
				{
					//System.out.println(e.getMessage());
					JISystem.getLogger().throwing("JIComOxidRuntimeHelper","startRemUnknown",e);
					throw new JIRuntimeException(JIErrorCodes.JI_CALLBACK_SMB_FAILURE);
				}		
				catch(IOException e)
				{ 
					if(JISystem.getLogger().isLoggable(Level.WARNING))
					{
						JISystem.getLogger().throwing("JIComOxidRuntimeHelper","startRemUnknown",e);
						JISystem.getLogger().warning("RemUnknown Thread: " +  e.getMessage() + " , on thread Id: " + (Thread.currentThread().getName()));
					}
					//e.printStackTrace();
				}catch(Throwable e)
				{
					JISystem.getLogger().throwing("JIComOxidRuntimeHelper","startRemUnknown",e);
				}
				finally{
					try {
						((JIComRuntimeEndpoint)getEndpoint()).detach();
					} catch (IOException e){}
				}
				
				if (JISystem.getLogger().isLoggable(Level.INFO))
				{
					JISystem.getLogger().info("terminating startRemUnknown thread: " + Thread.currentThread().getName());
				}
			}
		},"jI_RemUnknown[" + baseIID + " , " + remUnknownPort + "]");
		
		remUnknownThread.setDaemon(true);
		remUnknownThread.start();
		return new Object[]{new Integer(remUnknownPort),remUnknownThread};
	}
}

//This object should have serialized access only , i.e at a time only 1 read --> write , cycle should happen
// it is not multithreaded safe.
class OxidResolverImpl extends NdrObject implements IJICOMRuntimeWorker
{
	//override read\write\opnum etc. here, use the util apis to decompose this.
	private int opnum = -1;
	private NdrBuffer buffer = null;
	private Properties p = null;
	public OxidResolverImpl(Properties p)
	{
		super();
		this.p = p;
	}
	
	public void setCurrentObjectID(UUID objectId)
	{
		//does nothing.
	}
//	public void setCurrentJavaInstanceFromIID(String iid)
//	{
//		//does nothing.
//	}
	
	public void setOpnum(int opnum)
	{
		this.opnum = opnum;
	}
	
	public int getOpnum() {
		return opnum;
	}
  
	public void write(NetworkDataRepresentation ndr) 
	{
		ndr.setBuffer(buffer); //this buffer is prepared via read.
	}
	
	public void read(NetworkDataRepresentation ndr) 
	{ 
		//will read according to the opnum. The setOpnum should have been called before this
		//call.	
		
		switch(opnum)
		{
			case 1: 
				buffer = SimplePing(ndr);
				break;
			case 2:
				buffer = ComplexPing(ndr);
				break;
			case 3: //ServerAlive
				buffer = ServerAlive(ndr);
				break;
			case 5: //This is ServerAlive2
				buffer = ServerAlive2(ndr);
				break;
			case 4: //This is ResolveOxid2
				buffer = ResolveOxid2(ndr);
				break;
			default: //should not have arrived here.
				if (JISystem.getLogger().isLoggable(Level.WARNING))
				{
					JISystem.getLogger().warning("Oxid Object: DEFAULTED !!!");
				}
				throw new JIRuntimeException(JIErrorCodes.RPC_S_PROCNUM_OUT_OF_RANGE);
		}
		
		
	}
	
	private Random random = new Random(System.currentTimeMillis());
	

	private NdrBuffer SimplePing(NetworkDataRepresentation ndr)
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Oxid Object: SimplePing");
		}
		byte b[] = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8);//setid
		JIComOxidRuntime.addUpdateSets(new JISetId(b),new ArrayList(),new ArrayList());
		buffer = new NdrBuffer(new byte[16],0);
		buffer.enc_ndr_long(0);
		buffer.enc_ndr_long(0);
		buffer.enc_ndr_long(0);
		buffer.enc_ndr_long(0);
		return buffer;
	}
	
	private NdrBuffer ComplexPing(NetworkDataRepresentation ndr)
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Oxid Object: ComplexPing");
		}
		byte b[] = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8);//setid
		JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null,JIFlags.FLAG_NULL,null);//seqId.
		Short lengthAdds = (Short)JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null,JIFlags.FLAG_NULL,null);//
		Short lengthDels = (Short)JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null,JIFlags.FLAG_NULL,null);//
		JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL,null);//
		
		JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL,null);//length
		ArrayList listOfAdds = new ArrayList();
		for (int i = 0; i < lengthAdds.intValue(); i++ )
		{
			listOfAdds.add(new JIObjectId(JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8)));
		}
		
		JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL,null);//length
		ArrayList listOfDels = new ArrayList();
		for (int i = 0; i < lengthDels.intValue(); i++ )
		{
			listOfDels.add(new JIObjectId(JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8)));
		}
		
		if (Arrays.equals(b,new byte[]{0,0,0,0,0,0,0,0}))
		{
			random.nextBytes(b);
		}
		
		JIComOxidRuntime.addUpdateSets(new JISetId(b),listOfAdds,listOfDels);
		
		buffer = new NdrBuffer(new byte[32],0);
		NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
		ndr2.setBuffer(buffer);
		
		JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr2,b);
		JIMarshalUnMarshalHelper.serialize(ndr2,Short.class,new Short((short)0),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class,new Integer(0),null,JIFlags.FLAG_NULL);//hresult
		return buffer;
	}
	
	private NdrBuffer ServerAlive(NetworkDataRepresentation ndr)
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Oxid Object: ServerAlive");
		}
		byte[] buffer = new byte[32]; //16 + 16=just in case
		NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
		ndrBuffer.enc_ndr_long(0);
		ndrBuffer.enc_ndr_long(0);
		ndrBuffer.enc_ndr_long(0);
		ndrBuffer.enc_ndr_long(0);
		return ndrBuffer;
	}
	private NdrBuffer ServerAlive2(NetworkDataRepresentation ndr)
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Oxid Object: ServerAlive2");
		}
		//there is no in params for this.
		//only out params
		
		//want no port information associated with this.
//		byte[] buffer = new byte[120];
//		FileInputStream inputStream;
//		try {
//			inputStream = new FileInputStream("c:/serveralive2");
//			inputStream.read(buffer,0,120);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
		
		JIDualStringArray dualStringArray = new JIDualStringArray(-1);
		
		byte[] buffer = new byte[dualStringArray.getLength() + 4 /*COMVERSION*/ + 16 /*2 unknown 8 bytes*/ + 16/*just in case*/];
		NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
		
		
		NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
		ndr2.setBuffer(ndrBuffer);
		
		//serialize COMVERSION
		JIMarshalUnMarshalHelper.serialize(ndr2,Short.class, new Short((short)JISystem.getCOMVersion().getMajorVersion()),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Short.class, new Short((short)JISystem.getCOMVersion().getMinorVersion()),null,JIFlags.FLAG_NULL);
		
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer(0),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer(dualStringArray.getLength()),null,JIFlags.FLAG_NULL);
		dualStringArray.encode(ndr2);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer(0),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer(0),null,JIFlags.FLAG_NULL);
		return ndrBuffer;
	}
	//will prepare a NdrBuffer for reply to this call 
	private NdrBuffer ResolveOxid2(NetworkDataRepresentation ndr)
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Oxid Object: ResolveOxid2");
		}
		//System.err.println("VIKRAM: resolve oxid thread Id = " + Thread.currentThread().getId());
		//first read the OXID, then consult the oxid master about it's details.
		JIOxid oxid = new JIOxid(JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8));
		
		//now get the RequestedProtoSeq length.
		int length = ((Short)JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null,JIFlags.FLAG_NULL,null)).intValue(); 
		
		//now for the array.
		JIArray array = (JIArray)JIMarshalUnMarshalHelper.deSerialize(ndr,new JIArray(Short.class,null,1,true),null,JIFlags.FLAG_REPRESENTATION_ARRAY,null);
		
		//now query the Resolver master for this data.
		JIComOxidDetails details = JIComOxidRuntime.getOxidDetails(oxid);
		
		if (details == null)
		{
			//not found, now throw an JIRuntimeException , so that a FaultPdu could be sent.
			throw new JIRuntimeException(JIErrorCodes.RPC_E_INVALID_OXID);
		}
		
//		byte[] buffer = new byte[424];
//		FileInputStream inputStream;
//		try {
//			inputStream = new FileInputStream("c:/resolveoxid2");
//			inputStream.read(buffer,0,424);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			details.getCOMRuntimeHelper().startRemUnknown();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
//		
		
		//randomly create IPID and send, this is the ipid of the remunknown, we store it with remunknown object
        UUID uuid = new UUID(GUIDUtil.guidStringFromHexString(IdentifierFactory.createUniqueIdentifier().toHexString()));
        
		//create the bindings for this Java Object.
		//this port will go in the new bindings sent to the COM client.
		int port = -1;
		try {
			//this is so that repeated calls for Oxid resolution return the same rem unknwon.
			port = details.getPortForRemUnknown();
			if (port == -1)
			{
			    Object[] portandthread = details.getCOMRuntimeHelper().startRemUnknown(details.getIID(),uuid.toString(),details.getIpid());
			    port = ((Integer)portandthread[0]).intValue();
			    details.setRemUnknownThread((Thread)portandthread[1]);
			}
			details.setPortForRemUnknown(port);
		} catch (IOException e) {
			
			throw new JIRuntimeException(JIErrorCodes.E_UNEXPECTED);
		}

		//can support only TCP connections
		//JIDualStringArray.test = true;
		JIDualStringArray dualStringArray = new JIDualStringArray(port);
		
		
		Integer authnHint = new Integer(details.getProtectionLevel());
		
		
		byte[] buffer = new byte[4 + 4 + dualStringArray.getLength() + 16 + 4 + 2 + 2 + 4 + 16];
		
		//have all data now prepare the response
		//the response expected here is defines the byte array size.
		NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
		
		NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
		ndr2.setBuffer(ndrBuffer);

		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer(new Object().hashCode()),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer((dualStringArray.getLength() - 4)/2),null,JIFlags.FLAG_NULL);
		dualStringArray.encode(ndr2);
		
		JIMarshalUnMarshalHelper.serialize(ndr2,UUID.class, uuid,null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, authnHint,null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Short.class, new Short((short)JISystem.getCOMVersion().getMajorVersion()),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Short.class, new Short((short)JISystem.getCOMVersion().getMinorVersion()),null,JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class, new Integer(0),null,JIFlags.FLAG_NULL); //hresult
		
		
		return ndrBuffer;
	}
	
	public List getQIedIIDs()
	{
		return null;
	}
	
	public UUID getCurrentObjectID()
	{
		return null;
	}
	
    public boolean isResolver()
    {
    	return true;
    }

	public void setCurrentIID(String iid) {
		//does nothing
	}

    public boolean workerOver()
    {
        //oxid resolver gets over when the client connected to it releases socket.
        return false;
    }
}

//This object should have serialized access only , i.e at a time only 1 read --> write , cycle should happen
//it is not multithreaded safe.
class RemUnknownObject extends NdrObject implements IJICOMRuntimeWorker
{
	//override read\write\opnum etc. here, use the util apis to decompose this.
	private int opnum = -1;
	private NdrBuffer buffer = null;
	
	//component tells you the JIJavaCoClass to act on , sent via the AlterContext calls
	//for all Altercontexts with IRemUnknown , this will be null.
	private JIJavaCoClass component = null; //will hold the current instance to act on.
	/* the component and object id duo work together. 1 component could export many ipids.
	 * 
	 */
	//ObjectID tells you the IPID to act on, sent via the Request calls
	private UUID objectId = null;
	
	//this would be the ipid of this RemUnknownObject
	private final String selfIPID;
	
	private String currentIID = null;
	
	private List listOfIIDsQIed = new ArrayList();
	
	RemUnknownObject(String ipidOfme, String ipidOfComponent)
	{
	    selfIPID = ipidOfme;
	    mapOfIpidsVsRef.put(ipidOfComponent.toUpperCase(),new Integer(5));
	}
	
	//this list will get cleared after this call.
	public List getQIedIIDs()
	{
		return listOfIIDsQIed;
	}
	
	 public boolean isResolver()
	 {
	    return false;
	 }
	
	public void setOpnum(int opnum)
	{
		this.opnum = opnum;
	}
	
	public int getOpnum() {
		return opnum;
	}

	public void write(NetworkDataRepresentation ndr) 
	{
		ndr.setBuffer(buffer); //this buffer is prepared via read.
	}
	
	private static final JIStruct remInterfaceRef = new JIStruct();
	static
	{
	    try
        {
            remInterfaceRef.addMember(UUID.class);
            remInterfaceRef.addMember(Integer.class);
            remInterfaceRef.addMember(Integer.class);
        }
        catch (JIException shouldnothappen)
        {
            JISystem.getLogger().throwing("RemUnknownObject", "Static Initialiser", shouldnothappen);
        }
	}
	private static final JIArray remInterfaceRefArray = new JIArray(remInterfaceRef,null,1,true);
    
	private Map mapOfIpidsVsRef = new HashMap();
	private boolean workerOver = false;
	
	public void read(NetworkDataRepresentation ndr) 
	{ 
		//will read according to the opnum. The setOpnum should have been called before this
		//call.	
		String ipid = objectId.toString();
		
//		if (!mapOfIpidsVsRef.containsKey(ipid.toUpperCase()))
//		{
//		    System.out.println(Thread.currentThread() + " -->> " + ipid.toUpperCase());
//		    //we always give 5 references
//		    mapOfIpidsVsRef.put(ipid.toUpperCase(),new Integer(5));
//		}
		
		//this means the call came for IRemUnknown apis, since selfIpid is null or matches the objectID
		//if (selfIPID == null || selfIPID.equalsIgnoreCase(ipid))
//		if ("00000131-0000-0000-C000-000000000046".equalsIgnoreCase(currentIID))
		if (selfIPID.equalsIgnoreCase(ipid))
		{
			switch(opnum)
			{
				case 3: //IRemUnknown QI.
					buffer = QueryInterface(ndr);
					break;
				case 4: //addref
						JIOrpcThis.decode(ndr);
						int length = ndr.readUnsignedShort();
					
						int[] retvals = new int[length];
						JIArray array = (JIArray)JIMarshalUnMarshalHelper.deSerialize(ndr, remInterfaceRefArray, new ArrayList(), JIFlags.FLAG_REPRESENTATION_ARRAY, new HashMap());
						//saving the ipids with there references. considering public + private references together for now.
						JIStruct[] structs = (JIStruct[])array.getArrayInstance();
						for (int i = 0;i<length;i++)
                        {
						    String ipidref = ((UUID)structs[i].getMember(0)).toString().toUpperCase();
						    int publicRefs = ((Integer)structs[i].getMember(1)).intValue();
						    int privateRefs = ((Integer)structs[i].getMember(2)).intValue();
                            
						    if (!mapOfIpidsVsRef.containsKey(ipidref))
	                        {
						        //this would be strange, since all the ipids we give should be part of the map already.
						        //have to set 0x80000003 (INVALID ARG here)
						        retvals[i] = 0x80000003;
						        continue;
	                        }
						    
						    int total = ((Integer)mapOfIpidsVsRef.get(ipidref)).intValue() + publicRefs + privateRefs;
						    mapOfIpidsVsRef.put(ipidref, new Integer(total));
						}
						
						
						//preparing the response
						buffer = new NdrBuffer(new byte[length*4 + 16],0);
						NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
						ndr2.setBuffer(buffer);
						JIOrpcThat.encode(ndr2);
						for (int i = 0;i<length;i++)
						{
							buffer.enc_ndr_long(retvals[i]);
						}
						
						buffer.enc_ndr_long(0);
						buffer.enc_ndr_long(0);
						
					break;
				case 5: //release
				    
				    
				    JIOrpcThis.decode(ndr);
                    length = ndr.readUnsignedShort();
                    array = (JIArray)JIMarshalUnMarshalHelper.deSerialize(ndr, remInterfaceRefArray, new ArrayList(), JIFlags.FLAG_REPRESENTATION_ARRAY, new HashMap());
                    //saving the ipids with there references. considering public + private references together for now.
                    structs = (JIStruct[])array.getArrayInstance();
                    for (int i = 0;i<length;i++)
                    {
                        String ipidref = ((UUID)structs[i].getMember(0)).toString().toUpperCase();
                        int publicRefs = ((Integer)structs[i].getMember(1)).intValue();
                        int privateRefs = ((Integer)structs[i].getMember(2)).intValue();
                        if (!mapOfIpidsVsRef.containsKey(ipidref))
                        {
                            continue;
                        }
                        
                        int total = ((Integer)mapOfIpidsVsRef.get(ipidref)).intValue() - publicRefs - privateRefs;
                        if (total == 0)
                        {
                            mapOfIpidsVsRef.remove(ipidref);
                        }
                        else
                        {
                            mapOfIpidsVsRef.put(ipidref, new Integer(total));
                        }
                    }
                    
                    //all references to all IPIDs exported are over, this is now done.
                    if (mapOfIpidsVsRef.isEmpty())
                    {
                        workerOver = true;
                    }
				    
					//I have 1 OID == 1 IPID == 1 java instance.
					buffer = new NdrBuffer(new byte[32],0);
					ndr2 = new NetworkDataRepresentation();
					ndr2.setBuffer(buffer);
					JIOrpcThat.encode(ndr2);
					buffer.enc_ndr_long(0);
					buffer.enc_ndr_long(0);
					break;
				default:
					throw new JIRuntimeException(JIErrorCodes.RPC_S_PROCNUM_OUT_OF_RANGE);
			}
		}
		else
		{
			//now use the objectId , just set in before this call to read. That objectId is the IPID on which the
			//call is being made , and was previously exported during Q.I. The component value was filled during an
			//alter context or bind, again made some calls before.
			if (component == null)
			{
			    int i = 0;
			}
			byte b[] = null;
			Object result = null;
			NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
			int hresult = 0;
			Object[] retArray = null;
			try {
				result = component.invokeMethod(ipid,opnum,ndr);
			} catch (JIException e) {
				hresult = e.getErrorCode();
				JISystem.getLogger().severe("Exception occured: " + e.getErrorCode());
		        JISystem.getLogger().throwing("RemUnknownObject","read",e);
			}
			
			
			//now if opnum was 6 then this is a dispatch call , so response has to be dispatch response
			//not the normal one.
			if (component.getInterfaceDefinitionFromIPID(ipid).isDispInterface() && opnum == 6)
			{
				Object result2 = result;
				//orpcthat
				//[out] VARIANT * pVarResult,
				//[out] EXCEPINFO * pExcepInfo,
				//[out] UINT * pArgErr,
				//[in, out, size_is(cVarRef)] VARIANTARG * rgVarRef
				result = new Object[4]; //orpcthat gets filled outside
				JIStruct excepInfo = new JIStruct();
				try {
					excepInfo.addMember(new Short((short)0));
					excepInfo.addMember(new Short((short)0));
					excepInfo.addMember(new JIString(""));
					excepInfo.addMember(new JIString(""));
					excepInfo.addMember(new JIString(""));
					excepInfo.addMember(new Integer(0));
					excepInfo.addMember(new JIPointer(null,true));
					excepInfo.addMember(new JIPointer(null,true));
					excepInfo.addMember(new Integer(0));
				} catch (JIException e) {//not expecting any here
					e.printStackTrace();
				}
				
				if (result2 == null)
				{
					((Object[])result)[0] = JIVariant.EMPTY();
				}
				else
				{
					//now check whether the variant is by ref or not.
					JIVariant variant = (JIVariant)((Object[])result2)[0];
					
					try {
						if (variant.isByRefFlagSet())
						{
							//add empty inplace of this.
							((Object[])result)[0] = JIVariant.EMPTY();
							//now update the array at the end.
							((Object[])result)[3] = new JIArray(new JIVariant[]{variant},true);
								
						}
						else
						{
							((Object[])result)[0] = ((Object[])result2)[0]; //will have only a single index.
							((Object[])result)[3] = new Integer(0); //Array
						}
					} catch (JIException e) {
						throw new JIRuntimeException(e.getErrorCode());
					}
				}
				
				((Object[])result)[1] = excepInfo;
				
				((Object[])result)[2] = new Integer(0); //argErr is null, for now.
				
				
				retArray = (Object[]) result;
				
			}
			
			
			buffer = new NdrBuffer(b,0);
			ndr2.setBuffer(buffer);
			
			//JIOrpcThat.encode(ndr2);
			//have to create a call Object, since these return types could be structs , unions etc. having deffered pointers 
			JICallObject callObject = new JICallObject(null);
			callObject.attachSession(component.getSession());
			if (result != null)
			{
				
				if (retArray != null)
				{
					//serialize all members sequentially.
					for (int i = 0;i < retArray.length;i++)
					{
						callObject.addInParamAsObject(retArray[i],JIFlags.FLAG_NULL);
					}
				}
				else
				{
					//serialize all members sequentially.
					for (int i = 0;i < ((Object[])result).length;i++)
					{
						callObject.addInParamAsObject(((Object[])result)[i],JIFlags.FLAG_NULL);
					}

				}
				
				
				
			}
			callObject.write2(ndr2);
			JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class,new Integer(hresult),null,JIFlags.FLAG_NULL);

			
			
			
		}
		
			
	}
		
	
	private NdrBuffer QueryInterface(NetworkDataRepresentation ndr)
	{
		//now to decompose all
		
		if (JISystem.getLogger().isLoggable(Level.FINEST))
        {
			JISystem.getLogger().finest("Within RemUnknownObject: QueryInterface");
			JISystem.getLogger().finest("RemUnknownObject: [QI] Before call terminated listOfIIDsQIed are: " +  listOfIIDsQIed);
        }
		JIOrpcThis.decode(ndr);
		
		//now get the IPID and export the component with a new IPID and IID. 
		UUID ipid = new rpc.core.UUID();
		try{
			ipid.decode(ndr,ndr.getBuffer());
		}catch(NdrException e)
		{
			JISystem.getLogger().throwing("JIComOxidRuntimeHelper","QueryInterface",e);  
		}
		
		if (JISystem.getLogger().isLoggable(Level.FINEST))
        {
			JISystem.getLogger().finest("RemUnknownObject: [QI] IPID is " + ipid);
        }
		//set the JIJavaCoClass., the ipid should not be null in this call.
		JIComOxidDetails details = JIComOxidRuntime.getComponentFromIPID(ipid.toString());
		
		if (details == null)
		{
			//not found, now throw an JIRuntimeException , so that a FaultPdu could be sent.
			throw new JIRuntimeException(JIErrorCodes.RPC_E_INVALID_OXID);
		}
		
		JIJavaCoClass component = details.getReferent();
		
		if (JISystem.getLogger().isLoggable(Level.FINEST))
        {
			JISystem.getLogger().finest("RemUnknownObject: [QI] JIJavcCoClass is " + component.getComponentID());
        }
		
		((Integer)(JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null, JIFlags.FLAG_NULL,null))).intValue();//refs , don't really care about this.
		
		int length = ((Short)(JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null, JIFlags.FLAG_NULL,null))).intValue();//length of the requested Interfaces
	
		JIArray array = (JIArray)JIMarshalUnMarshalHelper.deSerialize(ndr,new JIArray(UUID.class,null,1,true),null,JIFlags.FLAG_REPRESENTATION_ARRAY,null);
	
		//now to build the buffer and export the IIDs with new IPIDs
		byte[] b = new byte[8 + 4 + 4 + length * (4 + 4 + 40) + 16];
		NdrBuffer buffer = new NdrBuffer(b,0);
		
		//start with response
		NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
		ndr2.setBuffer(buffer);
		
		JIOrpcThat.encode(ndr2);
		
		//pointer
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class,new Integer(new Object().hashCode()),null,JIFlags.FLAG_NULL);
		//length of array
		JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class,new Integer(length),null,JIFlags.FLAG_NULL);
		
		Object[] arrayOfUUIDs = (Object[])array.getArrayInstance();
		
		for (int i = 0; i < arrayOfUUIDs.length; i++ )
		{
			UUID iid = (UUID)arrayOfUUIDs[i];
			if (JISystem.getLogger().isLoggable(Level.FINEST))
            {
				JISystem.getLogger().finest("RemUnknownObject: [QI] Array iid[" + i +  "] is " +  iid);
            }
			//now for each QueryResult
			try {
				int hresult = 0;
				String ipid2 = GUIDUtil.guidStringFromHexString(IdentifierFactory.createUniqueIdentifier().toHexString());;
				if (!component.isPresent(iid.toString()))
				{
					hresult = JIErrorCodes.E_NOINTERFACE;
				}
				else
				{
					String tmpIpid = null;
					try{
						tmpIpid = component.getIpidFromIID(iid.toString());	
					}catch(Exception e)
					{
						JISystem.getLogger().throwing("JIComOxidRuntimeHelper", "QueryInterface", e);
					}
					
					if (tmpIpid == null)
					{
						if (JISystem.getLogger().isLoggable(Level.FINEST))
                        {
							JISystem.getLogger().finest("RemUnknownObject: [QI] tmpIpid is null for iid " + iid);
                        }
						component.exportInstance(iid.toString(), ipid2);
					}
					else
					{
						if (JISystem.getLogger().isLoggable(Level.FINEST))
                        {
							JISystem.getLogger().finest("RemUnknownObject: [QI] tmpIpid is NOT null for iid " + iid + " and ipid sent back is " + ipid2);
                        }
						ipid2 = tmpIpid;
					}
				}	
				//hresult
				JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class,new Integer(hresult),null,JIFlags.FLAG_NULL);
				JIMarshalUnMarshalHelper.serialize(ndr2,Integer.class,new Integer(0xCCCCCCCC),null,JIFlags.FLAG_NULL);
				
				//now generate the IPID and export a java instance with this.
				JIStdObjRef objRef = new JIStdObjRef(ipid2,details.getOxid(),details.getOid());
				objRef.encode(ndr2);
				
				//add it to the exported Ipids map
				mapOfIpidsVsRef.put(ipid2.toUpperCase(), new Integer(objRef.getPublicRefs()));
				 
				if (JISystem.getLogger().isLoggable(Level.FINEST))
                {
					JISystem.getLogger().finest("RemUnknownObject: [QI] for which the stdObjRef is " +  objRef);
                }
			} catch (IllegalAccessException e) {
				JISystem.getLogger().throwing("JIComOxidRuntimeHelper","QueryInterface",e);  
			} catch (InstantiationException e) {
				JISystem.getLogger().throwing("JIComOxidRuntimeHelper","QueryInterface",e);  
			}
			
			String iidtemp = iid.toString().toUpperCase() + ":0.0";
			if (!listOfIIDsQIed.contains(iidtemp))
			{
				listOfIIDsQIed.add(iidtemp);
			}
		}
		
		if (JISystem.getLogger().isLoggable(Level.FINEST))
        {
			JISystem.getLogger().finest("RemUnknownObject: [QI] After call terminated listOfIIDsQIed are: " +  listOfIIDsQIed);
        }
		
		return buffer;
	}
	
	
	//for all remunknown methods and calls component is null, alter context for IRemUnknown will make this
	//null.
//	public void setCurrentJavaInstanceFromIID(String  iid)
//	{
//		int i = iid.indexOf(":");
//		if (i != -1)
//		{
//			iid = iid.substring(0,i);
//		}
//		this.component = JIComOxidRuntime.getJavaComponentForIID(iid);
//		if (component == null)
//		{
//			objectId = null;
//		}
//	}
	
	public void setCurrentObjectID(UUID objectId)
	{
		this.objectId = objectId;
		component = JIComOxidRuntime.getJavaComponentFromIPID(objectId.toString());
	}
	
	public UUID getCurrentObjectID()
	{
		return objectId;
	}

	public void setCurrentIID(String iid) {
		this.currentIID = iid;
		
	}

    public boolean workerOver()
    {
        return workerOver;
    }
}