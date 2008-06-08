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

import java.util.logging.Level;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.IJIUnknown;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIComObjectImpl;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIInterfacePointer;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JISessionHelper;


/**<p>Helper class for creating <code>COM</code> interfaces. <br>
 * 
 * Sample Usage:-
 * <br>
 * 
 * <code>
 *  //Assume comStub is the reference to JIComServer, obtained earlier... <br> 
 *	IJIUnknown unknown = comStub.createInstance(); <br>
 *  // This call will result into a <i>QueryInterface</i> for the IDispatch <br>
 *	IJIDispatch dispatch = (IJIDispatch)JIComFactory.<b>createCOMInstance</b>(JIComFactory.IID_IDispatch,unknown); <br>
 * </code>
 * <br>
 * Another example:-
 * <br>
 * <code>
 *  //From MSExcel2 example <br>
 *  int dispId = dispatch.getIDsOfNames("Workbooks"); <br>
 *	JIVariant outVal = dispatch.get(dispId); <br>
 *	JIInterfacePointer ptr = outVal.getObjectAsInterfacePointer(); <br>
 *  // This call wraps the JIInterfacePointer into it's actual type, based on it's IID (ptr.getIID()) <br>
 *	IJIDispatch dispatchOfWorkBooks =(IJIDispatch)JIComFactory.<b>createCOMInstance</b>(unknown,ptr); <br>
 * </code>
 * </p>
 * @since 1.0
 */
public class JIComFactory {

	/**
	 * IID of <code>IDispatch [IJIDispatch]</code>.  
	 */
	public static final String IID_IDispatch = IJIDispatch.IID;
	/**
	 * IID of <code>ITypeInfo [IJITypeInfo]</code>.
	 */
	public static final String IID_ITypeInfo = IJITypeInfo.IID;
	/**
	 * IID of <code>ITypeLib [IJITypeLib]</code>.
	 */
	public static final String IID_ITypeLib = IJITypeLib.IID;
	
	/**
	 * IID of <code>IEnumVARIANT [IJIEnumVARIANT]</code>. 
	 */
	public static final String IID_IEnumVARIANT = IJIEnumVARIANT.IID;
	
	
	
	/**Returns an IJIComObject as reference to the interface defined by <code>IID</code>. Performs a <i>QueryInterface</i> on the <code>iUnknown</code> parameter for this IID.
	 * 
	 * @param IID interface identifier of the requested interface.
	 * @param iUnknown unknown reference to queryInterface on.
	 * @return IJIComObject , null if IID is null
	 * @throws JIException
	 */
	public static IJIComObject createCOMInstance(String IID, IJIComObject iUnknown) throws JIException
	{
		if (IID == null)
		{
			return null;
		}
		
		IJIUnknown retVal = null;
		IJIUnknown unknown = iUnknown.queryInterface(IID);
		if (IID.equalsIgnoreCase(IID_IDispatch))
		{
			retVal = new JIDispatchImpl((IJIComObject)unknown);
		}
		else
		if (IID.equalsIgnoreCase(IID_IEnumVARIANT))
		{
			retVal = new JIEnumVARIANTImpl((IJIComObject)unknown);
		}
		else
		if (IID.equalsIgnoreCase(IID_ITypeInfo))// && iUnknown.isIDispatchSupported())
		{
			retVal = new JITypeInfoImpl((IJIComObject)unknown);
		}
		else
		if (IID.equalsIgnoreCase(IID_ITypeLib))// && iUnknown.isIDispatchSupported())
		{
			retVal = new JITypeLibImpl((IJIComObject)unknown);
		}
		
		return (IJIComObject)retVal;
	}
	
	/**<p>Returns an IJIComObject as reference to the interface defined by <code>ptr.getIID()</code>. This API creates a IJIComObject based on the <code>ptr</code>, the <code>JISession</code> and <code>JIComServer</code> <br>
	 * is taken from the <code>template</code>. No <i>QueryInterface</i> is performed in this case. The new object will belong to the session to which the 
	 * <code>template</code> belongs.
	 * 
	 * <b><p>Imp Note: </b> It could be that the interface pointer "ptr", may be a reference to an new COM server itself (similar to man-in-middle case)
	 * under such scenarios the IJIComObject returned will belong to a new session altogether. Make sure to check for this (JISession.getSessionIdentifier).
	 * The above statement should be watched out for destroying this new session and releasing associated resources when the system is shutting down. 
	 * </p>
	 * </p>
	 * @param template <code>IJIComObject</code> whose basic parameters such as <code>JIComServer</code> will be used while creating the new Instance.
	 * @param ptr reference used for creating the new instance.
	 * @return IJIComObject , if <code>template</code> or <code>ptr</code> is null then an exception will be thrown
	 * @throws JIException
	 * @deprecated
	 */
	//can be used to create objects with IJIComObject as template
	public static IJIComObject createCOMInstance(IJIComObject template, JIInterfacePointer ptr) throws JIException
	{
		if (ptr == null || template == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMFACTORY_ILLEGAL_ARG));
		}
		
		IJIUnknown retval = null;
		JISession session = template.getAssociatedSession();
		if (JIInterfacePointer.isOxidEqual(template.getInterfacePointer(), ptr))
		{
		    retval = new JIComObjectImpl(session,ptr);
		}
		else
		{
		    //new COM server pointer
		    session = JISession.createSession(session);
		    JIComServer comServer = new JIComServer(session,ptr,null);
		    retval = comServer.getInstance();
		}
		
		if (ptr.getIID().equalsIgnoreCase(IID_IDispatch))// && iUnknown.isIDispatchSupported())
		{
			retval = new JIDispatchImpl((IJIComObject)retval);
		}
		else
		if (ptr.getIID().equalsIgnoreCase(IID_ITypeInfo))// && iUnknown.isIDispatchSupported())
		{
			retval = new JITypeInfoImpl((IJIComObject)retval);
		}
		else
		if (ptr.getIID().equalsIgnoreCase(IID_ITypeLib))// && iUnknown.isIDispatchSupported())
		{
			retval = new JITypeLibImpl((IJIComObject)retval);
		}	
		else
		if (ptr.getIID().equalsIgnoreCase(IID_IEnumVARIANT))// && iUnknown.isIDispatchSupported())
		{
			retval = new JIEnumVARIANTImpl((IJIComObject)retval);
		}
		
		
		//add this IPID to session
		session.addToSession((IJIComObject)retval,ptr.getOID());
		return (IJIComObject)retval;
	}
	
	
	/**<p>Returns an IJIComObject as reference to the interface defined by <code>ptr.getIID()</code>. This API creates a IJIComObject based on the <code>ptr</code>.
	 *  No <i>QueryInterface</i> is performed in this case. The new object will belong to the session which is passed as a parameter.
	 * 
	 * <b><p>Imp Note: </b> It could be that the interface pointer "ptr", may be a reference to an new COM server itself (similar to man-in-middle case)
	 * under such scenarios the IJIComObject returned will belong to a new session altogether. This session is "linked" as a child to the session supplied as parameter and will
	 * have all the properties (such as the timeout, security etc. set from the parent session). This child session will also be automatically destroyed when the parent
	 * session is destroyed.  
	 * </p>
	 * If the above behaviour is not desired, then please create a new JISession yourself and use the JIComServer.createServer(...,JIInterfacePointer,...) ctor. When this is done
	 * the sessions formed are independent of any existing sessions.
	 * </p>
	 * @param session <code>JISession</code> whose basic parameters such as <code>JIComServer</code> will be used while creating the new Instance.
	 * @param ptr reference used for creating the new instance.
	 * @return IJIComObject , if <code>template</code> or <code>ptr</code> is null then an exception will be thrown
	 * @throws JIException
	 */
	//can be used to create objects with IJIComObject as template
	public static IJIComObject createCOMInstance(JISession session, JIInterfacePointer ptr) throws JIException
	{
		if (ptr == null || session == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMFACTORY_ILLEGAL_ARG));
		}
		
		IJIUnknown retval = null;
		JIInterfacePointer stubPtr = JISessionHelper.getInterfacePointerOfStub(session);
		if (JIInterfacePointer.isOxidEqual(stubPtr, ptr))
		{
		    retval = new JIComObjectImpl(session,ptr);
		}
		else
		{
		    //first check if a session for this OXID does not already exist and thus its stub
			JISession newsession = null;
			newsession = JISessionHelper.resolveSessionForOXID(ptr);
			if (newsession == null)
			{
				//new COM server pointer
			    newsession = JISession.createSession(session);
			    newsession.setGlobalSocketTimeout(session.getGlobalSocketTimeout());
			    newsession.useSessionSecurity(session.isSessionSecurityEnabled());
			    JIComServer comServer = new JIComServer(newsession,ptr,null);
			    retval = comServer.getInstance();
			    JISessionHelper.link2Sessions(session, newsession);
			}
			else
			{
				retval = new JIComObjectImpl(newsession,ptr);
			}
				
			//this is so that the reference gets added correctly.
			session = newsession;
		}
		
		if (ptr.getIID().equalsIgnoreCase(IID_IDispatch))// && iUnknown.isIDispatchSupported())
		{
			retval = new JIDispatchImpl((IJIComObject)retval);
		}
		else
		if (ptr.getIID().equalsIgnoreCase(IID_ITypeInfo))// && iUnknown.isIDispatchSupported())
		{
			retval = new JITypeInfoImpl((IJIComObject)retval);
		}
		else
		if (ptr.getIID().equalsIgnoreCase(IID_ITypeLib))// && iUnknown.isIDispatchSupported())
		{
			retval = new JITypeLibImpl((IJIComObject)retval);
		}	
		else
		if (ptr.getIID().equalsIgnoreCase(IID_IEnumVARIANT))// && iUnknown.isIDispatchSupported())
		{
			retval = new JIEnumVARIANTImpl((IJIComObject)retval);
		}
		
		
		//add this IPID to session
		session.addToSession((IJIComObject)retval,ptr.getOID());
		return (IJIComObject)retval;
	}
	
	
	
	
	
	
	/**<p> Attaches an Event Handler to this <code>comObject</code> for the source event interface of COM , identified by the 
	 * <code>sourceUUID</code>. The event listener is itself identified by <code>eventListener</code>. It is obtained 
	 * by <code>JIInterfacePointer.getInterfacePointer(JISession,JIJavaCoClass)</code> api. A JIException will get thrown if 
	 * <code>sourceUUID</code> is not supported by the COM Server.
	 *  </p>
	 * @param comObject
	 * @param sourceUUID
	 * @param eventListener
	 * @return String identifier for this connection, please save this for eventual release using <code>detachEventHandler</code>
	 * @throws JIException
	 */
	public static String attachEventHandler(IJIComObject comObject,String sourceUUID,JIInterfacePointer eventListener) throws JIException
	{
		if (eventListener == null || comObject == null || sourceUUID == null || sourceUUID.equalsIgnoreCase(""))
		{
			throw new JIException(JIErrorCodes.JI_CALLBACK_INVALID_PARAMS);
		}
		
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Attaching event handler for  comObject: " + comObject.getInterfaceIdentifier() + " , sourceUUID: " + sourceUUID + " , eventListener: " + eventListener.getIID() + " and eventListner IPID: " + eventListener.getIPID());
		}
		//IID of IConnectionPointContainer :- B196B284-BAB4-101A-B69C-00AA00341D07
		IJIComObject connectionPointContainer = (IJIComObject)comObject.queryInterface("B196B284-BAB4-101A-B69C-00AA00341D07");
		JICallObject object = new JICallObject(connectionPointContainer.getIpid(),true);
		object.setOpnum(1);
		object.addInParamAsUUID(sourceUUID,JIFlags.FLAG_NULL);
		object.addOutParamAsObject(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		Object[] objects = (Object[])connectionPointContainer.call(object); //find connection point
		JIInterfacePointer connectionPtr = (JIInterfacePointer)objects[0];
		IJIComObject connectionPointer = JIComFactory.createCOMInstance(connectionPointContainer,connectionPtr);
		
		object.reInit();
		object.setOpnum(2);
		object.addInParamAsObject(eventListener, JIFlags.FLAG_NULL);
		object.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		Object[] obj = connectionPointer.call(object);
		
		//used to unadvise from the connectionpoint
		Integer dwcookie = ((Integer)obj[0]);

		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Event handler returned cookie " + dwcookie);
		}
		connectionPointContainer.release();
		
		return comObject.setConnectionInfo(connectionPointer,dwcookie);
		
	}
	/**Detaches the event handler identified by <code>identifier</code> and associated with this <code>comObject</code>. 
	 * 
	 * @param comObject
	 * @param identifier
	 * @throws JIException
	 */
	public static void detachEventHandler(IJIComObject comObject, String identifier) throws JIException
	{
		Object[] connectionInfo = comObject.getConnectionInfo(identifier);
		if (connectionInfo == null)
		{
			throw new JIException(JIErrorCodes.JI_CALLBACK_INVALID_ID);
		}
		
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Detaching event handler for  comObject: " + comObject.getInterfaceIdentifier() + " , identifier: " + identifier);
		}
		
		IJIComObject connectionPointer = (IJIComObject)connectionInfo[0];
		
		//first use the cookie to detach.
		JICallObject object = new JICallObject(connectionPointer.getIpid(),true);
		object.setOpnum(3);
		object.addInParamAsInt(((Integer)connectionInfo[1]).intValue(),JIFlags.FLAG_NULL);
		connectionPointer.call(object);
		//now release the connectionPointer.
		connectionPointer.release();
	}
}
