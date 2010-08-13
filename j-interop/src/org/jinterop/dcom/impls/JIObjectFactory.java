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

package org.jinterop.dcom.impls;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIFrameworkHelper;
import org.jinterop.dcom.core.JILocalCoClass;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.impls.automation.Internal_JIAutomationFactory;



/**<p>Factory class for creating COM objects. <p>
 *
 * Sample Usage:-
 * <br>
 *
 * <code>
 *  //Assume comObject is the reference to IJIComObject, obtained earlier... <br>
 *	newComObject = (IJIComObject)comObject.queryInterface("76A6415B-CB41-11d1-8B02-00600806D9B6");//ISWbemLocator <br>
 *	//This will obtain the dispatch interface <br>
 *	dispatch = (IJIDispatch)JIObjectFactory.narrowObject(newComObject.queryInterface(IJIDispatch.IID)); <br>
 * </code>
 *
 * @since 2.0 (formerly JIComFactory)
 */
public class JIObjectFactory {

	/**<p> Attaches an event handler to <code>comObject</code> for the source event interface of COM , identified by the
	 * <code>sourceUUID</code>. The event listener is itself identified by <code>eventListener</code>. An exception will be raised if
	 * <code>sourceUUID</code> is not supported by the COM Server.
	 *  </p>
	 *
	 * @param comObject object to which the listener will be attached.
	 * @param sourceUUID <code>IID</code> of the call back interface.
	 * @param eventListener <code>IJIComObject</code> obtained using {@link #buildObject(JISession, JILocalCoClass)}
	 * @return string identifier for this connection, please save this for eventual release using {@link #detachEventHandler(IJIComObject, String)}
	 * @throws JIException
	 * @throws IllegalArgumentException if any parameter is <code>null</code> or <code>sourceUUID</code> is empty.
	 */
	public static String attachEventHandler(IJIComObject comObject,String sourceUUID,IJIComObject eventListener) throws JIException
	{
		return JIFrameworkHelper.attachEventHandler(comObject, sourceUUID, eventListener);

	}
	/**Detaches the event handler identified by <code>identifier</code> and associated with this <code>comObject</code>. This method
	 * will raise an exception if the <code>identifier</code> is invalid.
	 *
	 * @param comObject
	 * @param identifier
	 * @throws JIException
	 */
	public static void detachEventHandler(IJIComObject comObject, String identifier) throws JIException
	{
		JIFrameworkHelper.detachEventHandler(comObject, identifier);
	}

	/**<i>Narrows</i> the <code>comObject</code> into its right type based on it's <code>IID</code>. For example, passing a
	 * <code>comObject</code> which is a COM <code>IDispatch</code> reference will return a reference which can be safely casted
	 * to <code>IJIDispatch</code> interface.
	 *
	 * @param comObject
	 * @return
	 * @throws JIException
	 * @throws IllegalArgumentException if <code>comObject</code> is <code>null</code> or a local reference.
	 */
	public static IJIComObject narrowObject(final IJIComObject comObject) throws JIException
	{
		if (comObject == null || comObject.isLocalReference())
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_COMFACTORY_ILLEGAL_ARG));
		}

		//Will later on add another way to dynamically moving to factories.
		IJIComObject retval = Internal_JIAutomationFactory.narrowObject(comObject);

		return retval;
	}

	/** Returns a <b>local</b> COM Object representation for the Java component. <code>IJIComObject.IsLocalReference()</code>
	 * method will return <code>true</code> for all objects built by this method. Another important point to note is that a
	 * <code>javaComponent</code> can only export one reference to itself. Reusing the same <code>javaComponent</code> in another
	 * call to this method will raise an exception.
	 *
	 * @param session session to attach <code>comObject</code> to.
	 * @param javaComponent
	 * @return
	 * @throws JIException
	 */
	public static IJIComObject buildObject(JISession session,JILocalCoClass javaComponent) throws JIException
	{
		return JIFrameworkHelper.instantiateLocalComObject(session, javaComponent);
	}
	
	/** To be called after one is done using the local Java CoClass. Recommended to be called from the <code>finalize()</code> method of the 
	 * Java CoClass.
	 * 
	 * @param session
	 * @param javaComponent
	 * @throws JIException
	 */
	public static void releaseObject(JISession session, JILocalCoClass javaComponent) throws JIException
	{
	    JIFrameworkHelper.releaseLocalComponent(session, javaComponent);
	}

	/** Returns a COM Object from raw bytes. These bytes must conform to the Marshalled Interface Pointer template as per DCOM specifications.
	 *
	 * @param session session to attach <code>comObject</code> to. If required the framework will create a new session
     * for this <code>comObject</code> and link the <code>session</code> to the new one. This new session will be
     * destroyed when the parent <code>session</code> is destroyed.
	 * @param rawBytes bytes representing the interface pointer.
	 * @return
	 * @throws JIException
	 * @throws IllegalArgumentException if <code>rawBytes</code> is an invalid representation.
	 */
	public static IJIComObject buildObject(JISession session, byte[] rawBytes) throws JIException
	{
		return narrowObject(JIFrameworkHelper.instantiateComObject(session, rawBytes,null));
	}

	/** Returns a COM Object from raw bytes. These bytes must conform to the Marshalled Interface Pointer template as per DCOM specifications.
	 *
	 * @param session session to attach <code>comObject</code> to. If required the framework will create a new session
     * for this <code>comObject</code> and link the <code>session</code> to the new one. This new session will be
     * destroyed when the parent <code>session</code> is destroyed.
	 * @param rawBytes bytes representing the interface pointer.
	 * @param ipAddress	can be <code>null</code>. Sometimes there are many adapters (virtual as well) on the Target machine to which this interface pointer belongs,
	 * which may get sent as part of the interface pointer and consequently this call will fail since it is a possibility that IP is not reachable via this machine.
	 * The developer can send in the valid IP and if found in the interface pointer list will be used to talk to the target machine, overriding the other IP addresses
	 * present in the interface pointer. If this IP is not found then the "machine name" binding will be used. If this param is <code>null</code> then the first
	 * binding obtained from the interface pointer is used.
	 * @return
	 * @throws JIException
	 * @throws IllegalArgumentException if <code>rawBytes</code> is an invalid representation.
	 */
	public static IJIComObject buildObject(JISession session, byte[] rawBytes, String ipAddress) throws JIException
	{
		return narrowObject(JIFrameworkHelper.instantiateComObject(session, rawBytes,ipAddress));
	}

	 /** Typically used in the Man-In-The-Middle scenario.
	  * <p> Some possible use-cases :-
	  * <ul>
	  * <li>One j-Interop system interacts with another over the wire.</li>
     *  <li>The <code>IJIComObject</code> is read from a database and is not <i>attached</i> to a session.</li>
     * </ul>
     * @param session session to attach <code>comObject</code> to. If required the framework will create a new session
     * for this <code>comObject</code> and link the <code>session</code> to the new one. This new session will be
     * destroyed when the parent <code>session</code> is destroyed.
     * @param comObject <i>drifting</i> object.
     * @return
     * @throws JIException
     * @throws IllegalArgumentException if <code>comObject</code> is <code>null</code> or a local reference.
     * @see IJIComObject#isLocalReference()
     */
	public static IJIComObject narrowObject(JISession session, IJIComObject comObject) throws JIException
	{
		return narrowObject(JIFrameworkHelper.instantiateComObject(session, comObject));
	}
}
