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

import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIException;

/** Internal Framework class.
 * 
 * @exclude 
 * @since 1.0
 **/
public class JIComObjectImplWrapper implements IJIComObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6142976024482507753L;
	protected final IJIComObject comObject;
	
	protected JIComObjectImplWrapper(IJIComObject comObject)
	{
		this.comObject = comObject;
	}
	

	public IJIComObject queryInterface(String iid) throws JIException {
		
		return comObject.queryInterface(iid);
	}

	public void addRef() throws JIException
	{
		comObject.addRef();
	}
	
	public void release() throws JIException
	{
		comObject.release();
	}
	
	public String getIpid()
	{
		return comObject.getIpid();
	}
	
	public Object[] call(JICallBuilder obj) throws JIException
	{
		return comObject.call(obj);
	}
	
	public JIInterfacePointer internal_getInterfacePointer()
	{
		return comObject.internal_getInterfacePointer();
	}
	
	public JISession getAssociatedSession()
	{
		return comObject.getAssociatedSession();
	}
	
	/** Returns the <i>IID</i> of this object
	 * 
	 * @return String representation of 128 bit uuid.
	 */
	public String getInterfaceIdentifier()
	{
		return comObject.getInterfaceIdentifier();
	}
	
//	/**
//	 * @exclude
//	 */
//	public JIComServer getAssociatedComServer()
//	{
//		return comObject.getAssociatedComServer();
//	}
	
	public boolean isDispatchSupported()
	{
		return comObject.isDispatchSupported();
	}


	public String internal_setConnectionInfo(IJIComObject connectionPoint, Integer cookie) {
		return comObject.internal_setConnectionInfo(connectionPoint,cookie);
	}


	public Object[] internal_getConnectionInfo(String identifier) {
		return comObject.internal_getConnectionInfo(identifier);
	}


	public Object[] internal_removeConnectionInfo(String identifier) {
		return comObject.internal_removeConnectionInfo(identifier);
	}


	public IJIUnreferenced getUnreferencedHandler() {
		return comObject.getUnreferencedHandler();
	}


	public void registerUnreferencedHandler(IJIUnreferenced unreferenced) {
		comObject.registerUnreferencedHandler(unreferenced);
	}


	public void unregisterUnreferencedHandler() {
		comObject.unregisterUnreferencedHandler();
	}


	public Object[] call(JICallBuilder obj, int timeout) throws JIException
	{
		return comObject.call(obj, timeout);
	}


	public int getInstanceLevelSocketTimeout()
	{
		return comObject.getInstanceLevelSocketTimeout();
	}


	public void setInstanceLevelSocketTimeout(int timeout)
	{
		comObject.setInstanceLevelSocketTimeout(timeout);
	}


	public void internal_setDeffered(boolean deffered) {
		comObject.internal_setDeffered(deffered);
	}


	public boolean isLocalReference() {
		return comObject.isLocalReference();
	}
	
	public String toString()
	{
		return comObject.toString();
	}


	public JIComCustomMarshallerUnMarshaller getCustomObject() {
		return comObject.getCustomObject();
	}


	public int getLengthOfInterfacePointer() {
		return comObject.getLengthOfInterfacePointer();
	}
}
