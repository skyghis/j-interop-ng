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

import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIException;





/**<p> Represents a Windows COM Object. Instances of this interface can 
 *be retrieved by the following ways only :-
 *<ul>
 *<li>During initial handshake as expressed in the sample below.
 *<li>As references passed from <i>Windows COM runtime</i> such as when using 
 *{@link #queryInterface(String)} or returned as <code>[out]</code> parameters 
 *to calls (directly as <code>IJIComObject</code>(s) or part of <code>JIVariant</code>
 *(s)). </li>
 *<li>From raw bytes using {@link org.jinterop.dcom.impls.JIObjectFactory#buildObject(JISession, byte[])}</li>
 *<li>As references to local Java-COM interfaces (which are then used for 
 *event handling). See {@link org.jinterop.dcom.impls.JIObjectFactory#buildObject(JISession, JILocalCoClass)}
 *for more details.</li>
 *</ul>
 *<br>
 *All references obtained by any mechanism stated above <b>must</b> be <i>narrowed</i> 
 *using {@link org.jinterop.dcom.impls.JIObjectFactory#narrowObject(IJIComObject)}
 *before being casted to the expected type.
 *<br>
 *</p>
 *<p>
 *Sample usage :-
 *<br>
 *<code>
 *JISession session = JISession.createSession("DOMAIN","USERNAME","PASSWORD");
 *<br>
 *JIComServer comserver = new JIComServer(JIProgId.valueOf(session,"Word.Application"),address,session);
 *<br>
 *IJIComObject comObject = comserver.createInstance();
 *<br>
 *</code>
 *<br>
 *Also , 
 *<code>
 *<br>
 *IJIComObject handle = comObject.queryInterface("620012E2-69E3-4DC0-B553-AE252524D2F6");
 *</code>
 *</p>
 *
 *<b>Note</b>: Methods starting with <i>internal_</i> keyword are internal to the framework 
 * and must not be called by the developer.
 * 
 *@since 1.0 
 *
 */
//All IIDs Interfaces will be extending this interface
public interface IJIComObject extends Serializable {

	/**
	 * IID representing the <code>IUnknown</code>.
	 */
	public final String IID = "00000000-0000-0000-c000-000000000046";
	
	/**<p>Retrieve interface references based on <code>iid</code>. Make sure to 
	 * narrow before casting to the expected type.
	 * <p>
	 * For example when expecting an <code>IJIEnumVariant</code> :-
	 * <p>
	 * <code>
	 * IJIComObject object2 = variant.getObjectAsComObject();
	 * <br>
	 * IJIEnumVariant enumVariant = (IJIEnumVariant)JIObjectFactory.narrowObject(object2.queryInterface(IJIEnumVariant.IID));
	 * </code>
	 * <p> 
	 * Throws IllegalStateException if {@link #isLocalReference()} returns <code>true</code>.
	 * 
	 * @param iid string representation of the IID.
	 * @return reference to the requested unknown.
	 * @throws JIException
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 * @see  org.jinterop.dcom.impls.JIObjectFactory#narrowObject(IJIComObject)
	 */
	public IJIComObject queryInterface(String iid) throws JIException;
	
	/** <P>Increases the reference count on the COM server by 5
	 * (currently hard coded). The developer should refrain from calling this API, 
	 * as referencing is maintained internally by the system though he is not 
	 * obligated to do so. If the {@link #release()} is not called in conjunction 
	 * with <code>addRef</code> then the COM Instance will not get garbage collected 
	 * at the server. 
	 * </P>
	 * @throws JIException
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 * */
	public void addRef() throws JIException;
	
	/**<P> Decreases the reference count on the COM server by 5 
	 * (currently hard coded). The developer should refrain from calling this API, 
	 * as referencing is maintained internally by the system though he is not
	 * obligated to do so. If the <code>release</code> is not called in conjunction 
	 * with {@link #addRef()} then the COM Instance will not get garbage collected at 
	 * the server.
	 * </P>
	 * @throws JIException
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference. 
	 */	
	public void release() throws JIException;
	
	/** <i><u>Framework Internal</u></i>
	 * Unique 128 bit uuid representing the interface on the COM server. <br>
	 * 
	 * @exclude
	 * @return string representation of ipid.
	 */
	public String internal_getIpid();
	
	/** <P>Executes a method call on the actual COM object represented by this interface. 
	 * All the data like parameter information, operation number etc. are prepared and 
	 * sent via the <code>JICallBuilder</code>.
	 * <p>
	 * <code>
	 *  JICallBuilder obj = new JICallBuilder(); <br>
	 *  obj.reInit(); <br>
	 *	obj.setOpnum(0); //methods are sequentially indexed from 0 in the IDL
	 *	<br>
	 *	obj.addInParamAsString(new JIString("j-Interop Rocks",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR), JIFlags.FLAG_NULL); <br>
	 *	obj.addInParamAsPointer(new JIPointer(new JIString("Pretty simple ;)",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
	 *	<br>
	 *	Object[] result = comObject.call(obj);
	 * <br>
	 * </code>
	 * <p>
	 * If return values are expected then set up the <i>Out Params</i> also in the 
	 * <code>JICallBuilder</code>. 
	 * <p>
	 * 
	 * The call timeout used here , by default is the instance level timeout. If no 
	 * instance level timeout has been specified(or is 0) then the global timeout set in 
	 * {@link org.jinterop.dcom.core.JISession} will be used. 
	 * 
	 * </P>
	 * @param obj call builder carrying all information necessary to make the call successfully.
	 * @return Object[] array representing the results in the order expected or set in <code>JICallBuilder</code>.
	 * @throws JIException
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 * @see #setInstanceLevelSocketTimeout(int)
	 * @see org.jinterop.dcom.core.JISession#setGlobalSocketTimeout(int)
	 */
	public Object[] call(JICallBuilder obj) throws JIException;
	
	/**<P> Refer {@link #call(JICallBuilder)} for details on this method.
	 * </P>
	 * @param obj call builder carrying all information necessary to make the call successfully.
	 * @param timeout timeout for this call in milliseconds, overrides the instance level 
	 * timeout. Passing 0 here will use the global socket timeout.
	 * @return Object[] array representing the results in the order expected or set in <code>JICallBuilder</code>.
	 * @throws JIException
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference. 
	 * @see org.jinterop.dcom.core.JISession#setGlobalSocketTimeout(int)
	 */
	public Object[] call(JICallBuilder obj, int timeout) throws JIException;
	
	/**<p>Sets a timeout for all socket level operations done on this
	 * object. Calling this overrides the global socket timeout at the 
	 * <code>JISession</code> level. To unset a previous timeout, pass 0 as a 
	 * parameter.
	 * 
	 * @param timeout timeout for this call in milliseconds
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 * @see org.jinterop.dcom.core.JISession#setGlobalSocketTimeout(int)
	 */
	public void setInstanceLevelSocketTimeout(int timeout);

	/** Returns the socket timeout set at the instance level. This timeout value
	 * is used during all socket level operations such as {@link #call(JICallBuilder)}
	 * , {@link #queryInterface(String)} etc. 
	 * 
	 * @return timeout set on this object in milliseconds.
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 * */
	public int getInstanceLevelSocketTimeout();
	
	/**<i><u>Framework Internal</u></i>
	 * Returns self Interface pointer.
	 */
	public JIInterfacePointer internal_getInterfacePointer();
	
	/** Returns session associated with this object.   
	 * 
	 * @return JISession 
	 */
	public JISession getAssociatedSession();
	
	/** Returns the COM <i>IID</i> of this object
	 * 
	 * @return String representation of 128 bit uuid.
	 */
	public String getInterfaceIdentifier();
	
//	/**
//	 * @exclude
//	 * @return
//	 */
//	public JIComServer getAssociatedComServer();
	
	/**Returns <code>true</code> if <code>IDispatch</code> interface is supported 
	 * by this object.
	 * 
	 * @return <code>true</code> if <code>IDispatch</code> is supported, <code>false</code>
	 * otherwise.
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 * 
	 * @see org.jinterop.dcom.impls.automation.IJIDispatch
	 */
	public boolean isDispatchSupported();
	
	/**Adds a connection point information and it's cookie to the connectionPointMap internally.
	 * To be called only by the framework.
	 * 
	 * @exclude
	 * @param connectionPoint
	 * @param cookie
	 * @return unique identifier for the combination.
	 */
	public String internal_setConnectionInfo(IJIComObject connectionPoint,Integer cookie);
	
	/**<i><u>Framework Internal</u></i> Returns the ConnectionPoint (IJIComObject) and it's Cookie.
	 * 
	 * @exclude
	 * @param identifier
	 * @return
	 */
	public Object[] internal_getConnectionInfo(String identifier);
	
	/**<i><u>Framework Internal</u></i> Returns and Removes the connection info from the internal map. 
	 * 
	 * @exclude
	 * @param identifier
	 * @return
	 */
	public Object[] internal_removeConnectionInfo(String identifier);
	
	/**Adds a <code>IJIUnreferenced</code> handler. The handler will be invoked when this comObject goes 
	 * out of reference and is removed from it's session by the library. Only a single handler can be
	 * added for each object. If a handler for this object already exists , it would be replaced by this
	 * call.
	 * 
	 * @param unreferenced handler to get notification when reference count for this object hits 0 and is
	 * garbage collected by the library's runtime.
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 */
	public void registerUnreferencedHandler(IJIUnreferenced unreferenced);
	
	/** Returns the <code>IJIUnreferenced</code> handler associated with this object.
	 * 
	 * @return null if no handler is associated with this object.
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 */
	public IJIUnreferenced getUnreferencedHandler();
	
	/**Removes the <code>IJIUnreferenced</code> handler associated with this object. No exception will
	 * be thrown if one does not exist for this object.
	 * @throws IllegalStateException if there is no session associated 
	 * with this object or this object represents a local java reference.
	 */
	public void unregisterUnreferencedHandler();
	
	
	/** <i><u>Framework Internal</u></i> 
	 * 
	 * @exclude
	 * @param deffered
	 */
	public void internal_setDeffered(boolean deffered);
	
	/** Returns <code>true</code> if this COM object represents a local Java reference obtained by 
	 * {@link org.jinterop.dcom.impls.JIObjectFactory#buildObject(JISession, JILocalCoClass)}.
	 * <p>
	 * 
	 * @return <code>true</code> if this is a local reference , <code>false</code> otherwise.
	 */
	public boolean isLocalReference();
}
