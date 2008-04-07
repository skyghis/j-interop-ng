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

import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIException;




/** <p>Represents a <code>COM</code> Object. All interfaces (representing <code>COM</code> objects) 
 * for e.g. IJIDispatch, IJITypeInfo must extend from this interface.</p>
 * <p>
 * Sample usage:- 
 * <code><br>
 * 		JISession session = JISession.createSession("DOMAIN","USERNAME","PASSWORD");<br>
 *		JIComServer comserver = new JIComServer(JIProgId.valueOf(session,"Word.Application"),address,session);<br>
 *<br>
 *		IJIComObject comObject = comserver.createInstance();<br> 
 * </code></p>
 * 	
 * <p>Also , 
 * 	
 * <code><br>
 * 		IJIComObject handle = (IJIComObject)comObject.queryInterface("620012E2-69E3-4DC0-B553-AE252524D2F6");<br>
 *</code><br>
 *</p>
 *@since 1.0 
 */

//All IIDs Interfaces will be extending this interface
public interface IJIComObject extends IJIUnknown {

	/**Unique 128 bit uuid representing the interface on the <code>COM</code> server. <br>
	 * 
	 * @return
	 */
	public String getIpid();
	
	/** <P>Executes a <i>method call</i> on the actual <code>COM</code> object represented by this interface. All the data like parameter information, operation number etc. are 
	 *  prepared and sent via the JICallObject.
	 * <br>
	 * <code>
	 * <br>
	 *  JICallObject obj = new JICallObject(handle.getIpid()); <br>
	 *  obj.reInit(); <br>
	 *	obj.setOpnum(94); //This needs to be obtained via IDL, Other wise use IJIDispatch if COM <i>IDispatch</i> 
	 *					  //interface is supported by the underlying COM Object.
	 *	<br>
	 *	obj.addInParamAsPointer(new JIPointer(new JIString("j-Interop Rocks",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
	 *	obj.addInParamAsPointer(new JIPointer(new JIString("Pretty simple ;)",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
	 *	<br>
	 *	Object[] result = handle.call(obj);
	 * <br>
	 * </code>
	 * <br>
	 * The above example demonstrates a call where no results are expected. When you expect something to be returned back
	 * , set up the Out Params also in the JICallObject (see corresponding JICallObject javadoc for more details). 
	 * <br>
	 * 
	 * The timeout used here , by default is the instance level timeout. If not instance level timeout has been specified(or is 0) then the global timeout in JISession
	 * will be used. 
	 * 
	 * </P>
	 * @param obj Data Object carrying all information neccessary to make the call.
	 * @return Object[] representing the results in the order set in JICallObject.
	 * 
	 * @throws JIException
	 */
	public Object[] call(JICallObject obj) throws JIException;
	
	/** <P>Executes a <i>method call</i> on the actual <code>COM</code> object represented by this interface. All the data like parameter information, operation number etc. are 
	 *  prepared and sent via the JICallObject.
	 * <br>
	 * <code>
	 * <br>
	 *  JICallObject obj = new JICallObject(handle.getIpid()); <br>
	 *  obj.reInit(); <br>
	 *	obj.setOpnum(94); //This needs to be obtained via IDL, Other wise use IJIDispatch if COM <i>IDispatch</i> 
	 *					  //interface is supported by the underlying COM Object.
	 *	<br>
	 *	obj.addInParamAsPointer(new JIPointer(new JIString("j-Interop Rocks",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
	 *	obj.addInParamAsPointer(new JIPointer(new JIString("Pretty simple ;)",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
	 *	<br>
	 *	Object[] result = handle.call(obj);
	 * <br>
	 * </code>
	 * <br>
	 * The above example demonstrates a call where no results are expected. When you expect something to be returned back
	 * , set up the Out Params also in the JICallObject (see corresponding JICallObject javadoc for more details). 
	 * <br>
	 * </P>
	 * @param obj Data Object carrying all information necessary to make the call.
	 * @param timeout timeout for this call in millisecs, overrides the instance level timeout. Passing 0 here will use the global socket timeout (not the class level timeout) .
	 * @return Object[] representing the results in the order set in JICallObject.
	 *
	 * @throws JIException
	 */
	public Object[] call(JICallObject obj, int timeout) throws JIException;
	
	/** Sets a timeout for all socket level operations , overrides the global socket timeout at the JISession level.
	 * To unset a previous timeout, pass 0 as a parameter.
	 * 
	 * @param timeout
	 */
	public void setInstanceLevelSocketTimeout(int timeout);

	/** Returns the timeout set at the instance level. 
	 * 
	 * @return
	 */
	public int getInstanceLevelSocketTimeout();
	
	/**
	 * Returns self Interface pointer
	 */
	public JIInterfacePointer getInterfacePointer();
	
	/** Session associated with this object.   
	 * 
	 * @return JISession 
	 */
	public JISession getAssociatedSession();
	
	/** Returns the <i>IID</i> of this object
	 * 
	 * @return String representation of 128 bit uuid.
	 */
	public String getInterfaceIdentifier();
	
	/**
	 * @exclude
	 * @return
	 */
	public JIComServer getAssociatedComServer();
	
	/**Returns true if <code>IJIDispatch</code> is supported by this interface.
	 * 
	 * @return
	 */
	public boolean isDispatchSupported();
	
	/**Adds a connection point information and it's cookie to the connectionPointMap internally.
	 * 
	 * @exclude
	 * @param connectionPoint
	 * @param cookie
	 * @return unique identifier for the combination.
	 */
	public String setConnectionInfo(IJIComObject connectionPoint,Integer cookie);
	
	/**Returns the ConnectionPoint (IJIComObject) and it's Cookie.
	 * @exclude
	 * @param identifier
	 * @return
	 */
	public Object[] getConnectionInfo(String identifier);
	
	/**Returns and Removes the connection info from the internal map. 
	 * 
	 * @param identifier
	 * @return
	 */
	public Object[] removeConnectionInfo(String identifier);
	
	/**Adds a IJIUnreferenced handler , the handler will be invoked when this comObject goes out of reference
	 * and is removed from the session by the library. 
	 * 
	 * @param session
	 * @param unreferenced
	 */
	public void registerUnreferencedHandler(JISession session, IJIUnreferenced unreferenced);
	
	/**Returns the IJIUnreferenced handler associated with this comObject, null if none is.
	 * 
	 * @param session
	 * @return
	 */
	public IJIUnreferenced getUnreferencedHandler(JISession session);
	
	/**Removes the IJIUnreferenced handler associated with this comObject.
	 * 
	 * @param session
	 */
	public void unregisterUnreferencedHandler(JISession session);
}
