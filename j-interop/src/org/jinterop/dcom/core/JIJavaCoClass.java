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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;


/**<p>Each instance of this class represents a JAVA COCLASS. It may contain many interfaces, each represented 
 * at instance level by their uniqueIID and an instance. This instance is assigned an IPID when a Q.I call 
 * comes for them. This is provided incase the developer wants to maintain some
 * sort of state in the Java Instance. if <code>clazz</code> constructor has been used, then the library will try
 * to create a instance of the Java class using Class.newInstance. Please make sure that at least in all your classes 
 * visible nullary ctor is present.</p>
 * <p>Stores all IIDs, IPIDs internally in upper case. </p><br>
 *
 * <i>Please refer to <b>MSInternetExplorer</b> example for more details on how to use this class.</i><br>
 * @since 1.0
 *
 */
public final class JIJavaCoClass implements Serializable
{
	
	private static final long serialVersionUID = 5542223845228327383L;
	private static Random randomGen = new Random(Double.doubleToRawLongBits(Math.random()));
	private final int identifier ; 
	private JIInterfacePointer interfacePointer = null;
	private byte[] objectID = null;
	private JIInterfaceDefinition interfaceDefinition = null;
	
	private static final String IID_IDispatch = "00020400-0000-0000-c000-000000000046"; 
	
	private  ArrayList listOfSupportedInterfaces = new ArrayList();
	
	private ArrayList listOfSupportedEventInterfaces = new ArrayList();
	
	private HashMap mapOfIIDsToInterfaceDefinitions = new HashMap();
	
	private JISession session = null;
	
	private boolean realIID = false;
	
	static
	{
		
	}
	
	private Map ipidVsIID = new HashMap();// will use this to identify which IID is being talked about
										  //if it is IDispatch then delegate to it's invoke.

	private Map IIDvsIpid = new HashMap();// will use this to identify which IPID is being talked about
	
	private void init(JIInterfaceDefinition interfaceDefinition,Class clazz,Object instance,boolean realIID)
	{
		listOfSupportedInterfaces.add(IID_IDispatch.toUpperCase()); //IDispatch
		listOfSupportedInterfaces.add("00000131-0000-0000-C000-000000000046"); //IRemUnknown
		this.interfaceDefinition = interfaceDefinition;
		interfaceDefinition .clazz = clazz;
		interfaceDefinition.instance = instance;
		listOfSupportedInterfaces.add(interfaceDefinition.getInterfaceIdentifier().toUpperCase());
		mapOfIIDsToInterfaceDefinitions.put(interfaceDefinition.getInterfaceIdentifier().toUpperCase(),interfaceDefinition);
		this.realIID = realIID;
	}
	
	/**
	 * 
	 * @param interfaceDefinition
	 * @param clazz
	 */
	public JIJavaCoClass(JIInterfaceDefinition interfaceDefinition,Class clazz, boolean realIID)
	{
		if (interfaceDefinition == null || clazz == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COM_RUNTIME_INVALID_CONTAINER_INFO));
		}
		this.identifier = clazz.hashCode() ^ new Object().hashCode() ^ randomGen.nextInt();
		init(interfaceDefinition,clazz,null,realIID);
	}
	
	/**
	 * 
	 * @param interfaceDefinition
	 * @param clazz
	 */
	public JIJavaCoClass(JIInterfaceDefinition interfaceDefinition,Class clazz)
	{
		if (interfaceDefinition == null || clazz == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COM_RUNTIME_INVALID_CONTAINER_INFO));
		}
		this.identifier = clazz.hashCode() ^ new Object().hashCode() ^ randomGen.nextInt();
		init(interfaceDefinition,clazz,null,false);
	}
	
	/**
	 * 
	 * @param interfaceDefinition
	 * @param instance
	 */
	public JIJavaCoClass(JIInterfaceDefinition interfaceDefinition,Object instance,boolean realIID)
	{
		if (interfaceDefinition == null || instance == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COM_RUNTIME_INVALID_CONTAINER_INFO));
		}
		this.identifier = instance.hashCode() ^ new Object().hashCode() ^ randomGen.nextInt();
		init(interfaceDefinition,null,instance,realIID);	
	}
	
	/**
	 * 
	 * @param interfaceDefinition
	 * @param instance
	 */
	public JIJavaCoClass(JIInterfaceDefinition interfaceDefinition,Object instance)
	{
		if (interfaceDefinition == null || instance == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COM_RUNTIME_INVALID_CONTAINER_INFO));
		}
		this.identifier = instance.hashCode() ^ new Object().hashCode() ^ randomGen.nextInt();
		init(interfaceDefinition,null,instance,false);	
	}
	
	/**Sets the interface identifiers (IIDs) of the event interfaces this class would support.
	 * 
	 * @param listOfIIDs
	 */
	public void setSupportedEventInterfaces(List listOfIIDs)
	{
		if (listOfIIDs != null)
		{
			for (int i = 0;i < listOfIIDs.size(); i++)
			{
				String s = ((String)listOfIIDs.get(i)).toUpperCase();
				listOfSupportedInterfaces.add(s);
				listOfSupportedEventInterfaces.add(s);
				mapOfIIDsToInterfaceDefinitions.put(s,interfaceDefinition);
			}
			
		}
	}
	
	/**Add another interface definition and it's supporting object instance.
	 * 
	 * @param interfaceDefinition
	 * @param instance
	 */
	public void addInterfaceDefinition(JIInterfaceDefinition interfaceDefinition, Object instance )
	{
		if (interfaceDefinition == null || instance == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COM_RUNTIME_INVALID_CONTAINER_INFO));
		}
		interfaceDefinition.instance = instance;
		String s = interfaceDefinition.getInterfaceIdentifier().toUpperCase();
		listOfSupportedInterfaces.add(s);
		listOfSupportedEventInterfaces.add(s);
		mapOfIIDsToInterfaceDefinitions.put(s,interfaceDefinition);
	}

	/**Add another interface definition and it's class. Please make sure that this class has a default constructor, so that instantiation using reflection 
	 * can take place.
	 * 
	 * @param interfaceDefinition
	 * @param clazz
	 */
	public void addInterfaceDefinition(JIInterfaceDefinition interfaceDefinition, Class clazz )
	{
		if (interfaceDefinition == null || clazz == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COM_RUNTIME_INVALID_CONTAINER_INFO));
		}
		interfaceDefinition.clazz = clazz;
		String s = interfaceDefinition.getInterfaceIdentifier().toUpperCase();
		listOfSupportedInterfaces.add(s);
		listOfSupportedEventInterfaces.add(s);
		mapOfIIDsToInterfaceDefinitions.put(s,interfaceDefinition);
	}

	/**
	 * Returns the instance representing the interface definition. <br>
	 * @return
	 */
	public Object getServerInstance()
	{
		return interfaceDefinition.instance;
	}
	
	/**
	 * Returns the actual class representing the interface definition. <br> 
	 * @return
	 */
	public Class getServerClass()
	{
		return interfaceDefinition.clazz;
	}
	
//	public boolean isDispatchSupported()
//	{
//		return isDispatchSupported;
//	}
	
	//called from com runtime.
	/**
	 * @exclude
	 */
	void setObjectId(byte[] objectId)
	{
		this.objectID = objectId;
	}
	
	/**
	 * @exclude
	 */
	 void setAssociatedInterfacePointer(JIInterfacePointer interfacePointer)
	{
		this.interfacePointer = interfacePointer;
		String ipid = interfacePointer.getIPID().toUpperCase();
		String iid = interfacePointer.getIID().toUpperCase();
		IIDvsIpid.put(iid,ipid);
		ipidVsIID.put(ipid,iid);
	}
	
	/**
	 * @exclude
	 */
	 JIInterfacePointer getAssociatedInterfacePointer()
	{
		return this.interfacePointer;
	}
	
	
	/**
	 * @exclude
	 */
	 byte[] getObjectId()
	{
		return objectID;
	}
	
	/**
	 * @exclude
	 * @param iid
	 * @return
	 */
	 boolean isPresent(String iid)
	{
		iid = iid.toUpperCase();
		return listOfSupportedInterfaces.contains(iid);
	}
	
	/**
	 * @exclude
	 * @param uniqueIID
	 * @param IPID
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	//advances the index...it cannot be reversed.
	 synchronized boolean exportInstance(String uniqueIID,String IPID) throws InstantiationException, IllegalAccessException
	{
		//Object retval = null;
		IPID = IPID.toUpperCase();

		if (!isPresent(uniqueIID))//not supported IID.
		{
			return false;
		}
		
		IIDvsIpid.put(uniqueIID.toUpperCase(), IPID);
		ipidVsIID.put(IPID,uniqueIID.toUpperCase());
		return true;
	}
	
	/**
	 * Returns the interface identifier of this COCLASS. <br>
	 * @return
	 */
	public String getComponentID()
	{
		return interfaceDefinition.getInterfaceIdentifier();
	}
	
	/**
	 * @exclude
	 * @param IPID
	 * @param Opnum
	 * @param inparams
	 * @return
	 * @throws JIException
	 */
	//This will invoke the API via reflection and return the results of the call back to the
	//actual COM object. This API is to be invoked via the RemUnknown Object 
	Object[] invokeMethod(String IPID,int Opnum ,NetworkDataRepresentation ndr) throws JIException
	{
		IPID = IPID.toUpperCase();
		//somehow identify the method from the Opnum
		//this will come from the IDL.
		
		Object retVal = null;//will be an array.
		
		String iid = (String)ipidVsIID.get(IPID);
		if (iid == null)
		{
			throw new JIException(JIErrorCodes.RPC_E_INVALID_OBJECT);
		}
	
		JIInterfaceDefinition interfaceDefinitionOfClass = (JIInterfaceDefinition)mapOfIIDsToInterfaceDefinitions.get(iid) ;
		interfaceDefinitionOfClass = interfaceDefinitionOfClass == null ? interfaceDefinition : interfaceDefinitionOfClass;
		
		JIMethodDescriptor methodDescriptor = null;
		boolean execute = false;
		Object[] params = null;
	
		//that means the calls will come as IUnknown + IDispatch op numbers...0,1,2 & 3,4,5,6
		//from 7th (inclusive) onwards are the actual COM servers calls
		//now check for dispinterface and take a call...
		//if dispinterface is supported then all calls will come with base of 6 {0,1,2 & 3,4,5,6}
		//i.e 6th will be invoke and 7th(inclusive) onwards will be standard api calls.
		//if not supported than it will be base 2 {0,1,2} i.e real method calls will start from 3(inclusive) onwards.
		boolean isStandardCall = true;
		if (interfaceDefinition.isDispInterface())
		{
			isStandardCall = false;
			switch(Opnum)
			{
				case 3: //getTypeInfoCount
					//not supported
					retVal = new Object[1];
					((Object[])retVal)[0] = new Integer(0); //not supported
					break;
				case 4: //getTypeInfo
					throw new JIException(JIErrorCodes.E_NOTIMPL);
				case 5: //getIDOfNames
					
					JIParameterObject paramObject = new JIParameterObject();
					
					paramObject.addInParamAsType(UUID.class,JIFlags.FLAG_NULL);
					paramObject.addInParamAsObject(new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),null,1,true),JIFlags.FLAG_NULL);
					paramObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
					paramObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
	
					//now read and then send the result back.
					JIArray array = (JIArray)paramObject.read(ndr)[1];
					
					Object[] arrayObj = (Object[])array.getArrayInstance();
					Integer[] dispIds = new Integer[arrayObj.length];
					//get the first member of the Array , which is the APINAME and send the retVal with it's dispId
					JIString apiName = (JIString)arrayObj[0];
					JIMethodDescriptor info = interfaceDefinitionOfClass.getMethodDescriptor(apiName.getString());
					if (info == null)
					{
						dispIds[0] = new Integer(JIErrorCodes.DISP_E_UNKNOWNNAME);
					}
					else
					{
						dispIds[0] = new Integer(info.getMethodNum());
					}
					
					//rest are all 0,1,2...parameters
					for (int i = 1;i < arrayObj.length;i++ )
					{
						dispIds[i] = new Integer(i - 1);
					}
					
					JIArray results = new JIArray(dispIds);
					
					retVal = new Object[1];
					((Object[])retVal)[0] = results;
					
					break;
				case 6: //invoke of IDispatch
	
					paramObject = new JIParameterObject();
					paramObject.setSession(session);
					paramObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
					paramObject.addInParamAsType(UUID.class,JIFlags.FLAG_NULL);
					paramObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
					paramObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
					
					JIStruct dispParams = new JIStruct();
					dispParams.addMember(new JIPointer(new JIArray(JIVariant.class,null,1,true)));
					dispParams.addMember(new JIPointer(new JIArray(Integer.class,null,1,true)));
					dispParams.addMember(Integer.class);
					dispParams.addMember(Integer.class);
					
					paramObject.addInParamAsObject(dispParams,JIFlags.FLAG_REPRESENTATION_IDISPATCH_INVOKE);
					paramObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
					paramObject.addInParamAsObject(new JIArray(Integer.class,null,1,true),JIFlags.FLAG_NULL);
					paramObject.addInParamAsObject(new JIArray(JIVariant.class,null,1,true),JIFlags.FLAG_NULL);
					
					Object[] retresults = paramObject.read(ndr);
					//named params not supported
					int dispId = ((Integer)retresults[0]).intValue();
					
					info = interfaceDefinitionOfClass.getMethodDescriptorForDispId(dispId);
					if (info == null)
					{
                        if(JISystem.getLogger().isLoggable(Level.SEVERE))
                        {
                        	JISystem.getLogger().severe("MethodDescriptor not found for DispId :- " + dispId);
                        }
                        
						throw new JIException(JIErrorCodes.DISP_E_MEMBERNOTFOUND);
					}
					
					dispParams = (JIStruct)retresults[4];
					JIPointer ptrToParamsArray = (JIPointer)dispParams.getMember(0);
					
					params = new Object[0];
					if (!ptrToParamsArray.isNull())
					{
						//form the real array 
						array = (JIArray)ptrToParamsArray.getReferent();
						Object[] variants = (Object[])array.getArrayInstance();
						params = new Object[variants.length]; 
						for (int i = 0;i < variants.length;i++)
						{
							params[i] = ((JIVariant)variants[i]).getObject();
						}
					}
					
					if (((Integer)retresults[5]).intValue() != 0)
					{
						//now replace the params at index from the index array.
						array = (JIArray)retresults[6];
						Integer[] indexs = (Integer[])array.getArrayInstance();
						array = (JIArray)retresults[7];
						JIVariant[] variants = (JIVariant[])array.getArrayInstance();
						for (int i = 0;i < indexs.length; i++)
						{
							params[indexs[i].intValue()] = variants[i]; 
						}
						
						
					}
					
					//now to reverse this array of params.
					int halflength = params.length/2; 
					for (int i = 0;i < halflength; i++)
					{
						Object t = params[i];
						params[i] = params[params.length - 1 - i];
						params[params.length - 1 - i] = t;
					}
					
					
					
					methodDescriptor = info;
					execute = true;
					break;
				default: //others are normal API calls ...Opnum - 6 is there real Opnum. 0,1,2 and 3,4,5,6
					isStandardCall = true;
					Opnum = Opnum - 4; //adjust for only IDispatch(3,4,5,6) , IUnknown(0,1,2) will get adjusted below.
					if(JISystem.getLogger().isLoggable(Level.INFO))
					{
						JISystem.getLogger().info("Standard call came: Opnum is " + Opnum);
					}
					
			}
		}
		
		if (isStandardCall)
		{
			methodDescriptor = interfaceDefinitionOfClass.getMethodDescriptor(Opnum - 3); //adjust for IUnknown
			if (methodDescriptor == null)
			{
				throw new JIException(JIErrorCodes.RPC_S_PROCNUM_OUT_OF_RANGE);
			}
			methodDescriptor.getParameterObject().setSession(session);
			params = methodDescriptor.getParameterObject().read(ndr);	
			execute = true;
		}
		
		if (execute)
		{
			//JIInterfaceDefinition interfaceDefinitionOfCall = interfaceDefinition;
			Class calleeClazz = interfaceDefinitionOfClass.instance == null ? interfaceDefinitionOfClass.clazz : interfaceDefinitionOfClass.instance.getClass();
			Method method = null;
			try {
				if (JISystem.getLogger().isLoggable(Level.INFO))
				{
					JISystem.getLogger().info("methodDescriptor: " + methodDescriptor.getMethodName());
				}
				method = calleeClazz.getDeclaredMethod(methodDescriptor.getMethodName(),methodDescriptor.getInparametersAsClass());
				Object calleeInstance = interfaceDefinitionOfClass.instance == null ? calleeClazz.newInstance() : interfaceDefinitionOfClass.instance;
				if (JISystem.getLogger().isLoggable(Level.INFO))
				{
					JISystem.getLogger().info("Call Back Method to be executed: " + method + " , to be executed on " + calleeInstance);
				}
				Object result = method.invoke(calleeInstance,params);
				
				if (result == null)
				{
					retVal = null;
				}
				else
				if (!(result instanceof Object[]))
				{
					retVal = new Object[1];
					((Object[])retVal)[0] = result;
				}
				else
				{
					retVal = result;
				}
				
				
			} catch (IllegalArgumentException e) {
				JISystem.getLogger().throwing("JIJavaCoClass","invokeMethod",e);
				throw new JIException(JIErrorCodes.E_INVALIDARG,e);	
			} catch (IllegalAccessException e) {
				JISystem.getLogger().throwing("JIJavaCoClass","invokeMethod",e);
				throw new JIException(JIErrorCodes.ERROR_ACCESS_DENIED,e);	
			} catch (InvocationTargetException e) {
				JISystem.getLogger().throwing("JIJavaCoClass","invokeMethod",e);
				throw new JIException(JIErrorCodes.E_UNEXPECTED,e);
			} catch (SecurityException e) {
				JISystem.getLogger().throwing("JIJavaCoClass","invokeMethod",e);
				throw new JIException(JIErrorCodes.ERROR_ACCESS_DENIED,e);			
			} catch (NoSuchMethodException e) {
				JISystem.getLogger().throwing("JIJavaCoClass","invokeMethod",e);
				throw new JIException(JIErrorCodes.RPC_S_PROCNUM_OUT_OF_RANGE,e);
			} catch (InstantiationException e) {
				JISystem.getLogger().throwing("JIJavaCoClass","invokeMethod",e);
				throw new JIException(JIErrorCodes.E_UNEXPECTED,e);
			}

		}
		
		return (Object[])retVal;
	}

	
	/**Returns the primary(i.e the one added first time during construction of this instance) interfaceDefinition. <br>
	 * 
	 * @return
	 */
	public JIInterfaceDefinition getInterfaceDefinition()
	{
		return interfaceDefinition;
	}
	
	/**
	 * @exclude
	 */
	public boolean equals(Object target)
	{
		if (target == null || !(target instanceof JIJavaCoClass))
		{
			return false;
		}
	
		return identifier == ((JIJavaCoClass)target).identifier;
	}
	/**
	 * @exclude
	 */
	public int hashCode()
	{
		return identifier;
	}
	
	/**Returns the interfaceDefinition based on the IID of the interface.
	 * 
	 * @return
	 */
	public JIInterfaceDefinition getInterfaceDefinition(String IID)
	{
		return (JIInterfaceDefinition)mapOfIIDsToInterfaceDefinitions.get(IID.toUpperCase());
	}
	
	/** 
	 * @exclude
	 * @param IPID
	 * @return
	 */
	 JIInterfaceDefinition getInterfaceDefinitionFromIPID(String IPID)
	{
		return (JIInterfaceDefinition)mapOfIIDsToInterfaceDefinitions.get((String)ipidVsIID.get(IPID.toUpperCase()));
	}
	/**
	 * @exclude
	 */
	 String getIpidFromIID(String uniqueIID)
	{
		return (String)IIDvsIpid.get(uniqueIID.toUpperCase());
	}
	
	/**
	 * 
	 * @param uniqueIID
	 * @return
	 */		
	 String getIIDFromIpid(String ipid)
	{
		return (String)ipidVsIID.get(ipid.toUpperCase());
	}
	
	/** <p> Returns true if the primary interface definition represents a real IID. The bind-auth3 and all are then all done as per this IID and
	 * not IUnknown. </p>
	 * 
	 * @return
	 */
	public boolean isCoClassUnderRealIID()
	{
		return realIID;
	}

	/**
	 * Associate the Session with this CoClass. Called by the framework.
	 * @exclude
	 * @param session
	 */
	void setSession(JISession session) {
		this.session = session;
	}

	/**
	 * Returns the session associated with this CoClass.
	 * @return
	 */
	JISession getSession() {
		return session;
	}
}
