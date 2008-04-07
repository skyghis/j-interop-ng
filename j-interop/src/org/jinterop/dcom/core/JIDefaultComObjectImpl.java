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

/**
 * @exclude 
 * @since 1.0
 **/
public class JIDefaultComObjectImpl implements IJIComObject {

	protected IJIComObject comObject = null;
	protected JIDefaultComObjectImpl(IJIComObject comObject)
	{
		this.comObject = comObject;
	}
	

	public IJIUnknown queryInterface(String iid) throws JIException {
		
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
	
	public Object[] call(JICallObject obj) throws JIException
	{
		return comObject.call(obj);
	}
	
	public JIInterfacePointer getInterfacePointer()
	{
		return comObject.getInterfacePointer();
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
	
	/**
	 * @exclude
	 */
	public JIComServer getAssociatedComServer()
	{
		return comObject.getAssociatedComServer();
	}
	
	public boolean isDispatchSupported()
	{
		return comObject.isDispatchSupported();
	}


	public String setConnectionInfo(IJIComObject connectionPoint, Integer cookie) {
		return comObject.setConnectionInfo(connectionPoint,cookie);
	}


	public Object[] getConnectionInfo(String identifier) {
		return comObject.getConnectionInfo(identifier);
	}


	public Object[] removeConnectionInfo(String identifier) {
		return comObject.removeConnectionInfo(identifier);
	}


	public IJIUnreferenced getUnreferencedHandler(JISession session) {
		return comObject.getUnreferencedHandler(session);
	}


	public void registerUnreferencedHandler(JISession session, IJIUnreferenced unreferenced) {
		comObject.registerUnreferencedHandler(session, unreferenced);
	}


	public void unregisterUnreferencedHandler(JISession session) {
		comObject.unregisterUnreferencedHandler(session);
	}


	public Object[] call(JICallObject obj, int timeout) throws JIException
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
}
