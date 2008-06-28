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

import java.util.HashMap;
import java.util.Map;

import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;

import com.iwombat.foundation.IdentifierFactory;
import com.iwombat.util.GUIDUtil;





/** Implementation for IJIComObject. There is a 1 to 1 mapping between this and a <code>COM</code> interface. 
 * 
 * @exclude
 * @since 1.0
 */ 
final class JIComObjectImpl implements IJIComObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1661750453596032089L;
	private boolean isDual = false;
	private boolean dualInfo = false;
	private transient JISession session = null;
	private JIInterfacePointer ptr = null;
	private Map connectionPointInfo = null;
	private int timeout = 0;
	private final boolean isLocal;
	
	JIComObjectImpl(JISession session,JIInterfacePointer ptr)
	{
		this(session,ptr,false);
	}
	
	JIComObjectImpl(JISession session,JIInterfacePointer ptr, boolean isLocal) 
	{
		this.session = session;
		this.ptr = ptr;
		this.isLocal = isLocal;
	}
	
	void replaceMembers(IJIComObject comObject)
	{
		this.session = comObject.getAssociatedSession();
		this.ptr = comObject.internal_getInterfacePointer();
	}
	
	private void checkLocal()
	{
		if (session == null)
		{
			throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_SESSION_NOT_ATTACHED));
		}
		
		if (isLocalReference())
		{
			throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.E_NOTIMPL));
		}
	}
	
	public IJIComObject queryInterface(String iid) throws JIException 
	{
		checkLocal();
		return session.getStub().getInterface(iid,ptr.getIPID());
	}
	
	public void addRef() throws JIException
	{
		checkLocal();
		JICallBuilder obj = new JICallBuilder(true);
		obj.setParentIpid(ptr.getIPID());
		obj.setOpnum(1);//addRef
		
		//length
		obj.addInParamAsShort((short)1,JIFlags.FLAG_NULL);
		//ipid to addfref on
		JIArray array = new JIArray(new rpc.core.UUID[]{new rpc.core.UUID(ptr.getIPID())},true);
		obj.addInParamAsArray(array,JIFlags.FLAG_NULL);
		//TODO requesting 5 for now, will later build caching mechnaism to exhaust 5 refs first before asking for more
		// same with release.
		obj.addInParamAsInt(5,JIFlags.FLAG_NULL);
		obj.addInParamAsInt(0,JIFlags.FLAG_NULL);//private refs = 0
		
		obj.addOutParamAsType(Short.class,JIFlags.FLAG_NULL);//size
		obj.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);//Hresult for size
		session.getStub().addRef_ReleaseRef(obj);
		
		if (obj.getResultAsIntAt(1) != 0)
		{
			throw new JIException(obj.getResultAsIntAt(1),(Throwable)null);
		}
	}
	
	public void release() throws JIException
	{
		checkLocal();
		JICallBuilder obj = new JICallBuilder(true);
		obj.setParentIpid(ptr.getIPID());
		obj.setOpnum(2);//release
		//length
		obj.addInParamAsShort((short)1,JIFlags.FLAG_NULL);
		//ipid to addfref on
		JIArray array = new JIArray(new rpc.core.UUID[]{new rpc.core.UUID(ptr.getIPID())},true);
		obj.addInParamAsArray(array,JIFlags.FLAG_NULL);
		//TODO requesting 5 for now, will later build caching mechnaism to exhaust 5 refs first before asking for more
		// same with release.
		obj.addInParamAsInt(5,JIFlags.FLAG_NULL);
		obj.addInParamAsInt(0,JIFlags.FLAG_NULL);//private refs = 0
		session.getStub().addRef_ReleaseRef(obj);
	}


	public Object[] call(JICallBuilder obj) throws JIException 
	{
		checkLocal();
		return call(obj,timeout);	
	}

	
	
	
	public JIInterfacePointer internal_getInterfacePointer()
	{
		return ptr == null ? session.getStub().getServerInterfacePointer() : ptr;
	}
	
	public String internal_getIpid()
	{
		return ptr.getIPID();
	}

	public boolean equals(Object obj) {
		
		if (!(obj instanceof JIComObjectImpl))
		{
			return false;
		}
		
		return (this.ptr.getIPID().equalsIgnoreCase(((IJIComObject)obj).internal_getIpid()));
	}
	
	public int hashCode()
	{
		return ptr.getIPID().hashCode();
	}
	
	public JISession getAssociatedSession()
	{
		return session;
	}
	
	public String getInterfaceIdentifier()
	{
		return ptr.getIID();
	}
	
//	public JIComServer getAssociatedComServer()
//	{
//		checkLocal();
//		return session.getStub();
//	}
	
	public synchronized boolean isDispatchSupported()
	{
		checkLocal();
		if (!dualInfo)
		{
			//query interface for it and then release it.
			try {
				IJIComObject comObject = queryInterface("00020400-0000-0000-c000-000000000046");
				comObject.release();
				setIsDual(true);
			} catch (JIException e) {
				setIsDual(false);
			}
		}
		return isDual;
	}

	public synchronized String internal_setConnectionInfo(IJIComObject connectionPoint, Integer cookie) {
		checkLocal();
		if (connectionPointInfo == null) //lazy creation, since this is used by event callbacks only.
		{
			connectionPointInfo = new HashMap();
		}
		String uniqueId = GUIDUtil.guidStringFromHexString(IdentifierFactory.createUniqueIdentifier().toHexString());
		connectionPointInfo.put(uniqueId,new Object[]{connectionPoint,cookie});
		return uniqueId;
	}

	public synchronized Object[] internal_getConnectionInfo(String identifier) {
		checkLocal();
		return (Object[])connectionPointInfo.get(identifier);
	}

	public synchronized Object[] internal_removeConnectionInfo(String identifier) {
		checkLocal();
		return (Object[])connectionPointInfo.remove(identifier);
	}

	public IJIUnreferenced getUnreferencedHandler() {
		checkLocal();
		return session.getUnreferencedHandler(internal_getIpid());
	}

	public void registerUnreferencedHandler(IJIUnreferenced unreferenced) {
		checkLocal();
		session.registerUnreferencedHandler(internal_getIpid(), unreferenced);
	}

	public void unregisterUnreferencedHandler() {
		checkLocal();
		session.unregisterUnreferencedHandler(internal_getIpid());
	}

	public Object[] call(JICallBuilder obj, int socketTimeout) throws JIException
	{
		checkLocal();
		obj.attachSession(session);
		obj.setParentIpid(ptr.getIPID());
		//Call is always made on your stub.
		
		if (socketTimeout != 0) //using instance level timeout
		{
			return session.getStub().call(obj,ptr.getIID(),socketTimeout);
		}
		else
		{
			return session.getStub().call(obj,ptr.getIID());
		}
	}

	public int getInstanceLevelSocketTimeout()
	{
		checkLocal();
		return timeout;
	}

	public void setInstanceLevelSocketTimeout(int timeout)
	{
		checkLocal();
		this.timeout = timeout;
	}

	public void internal_setDeffered(boolean deffered) {
		ptr.setDeffered(deffered);
	}

	public boolean isLocalReference() {
		return isLocal;
	}
	
	void setIsDual(boolean isDual)
	{
		this.dualInfo = true;
		this.isDual = isDual;
	}
}
