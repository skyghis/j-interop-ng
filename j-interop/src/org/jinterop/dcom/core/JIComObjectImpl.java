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
import org.jinterop.dcom.common.JIException;

import com.iwombat.foundation.IdentifierFactory;
import com.iwombat.util.GUIDUtil;





/** Implementation for IJIComObject. There is a 1 to 1 mapping between this and a <code>COM</code> interface. 
 * 
 * @exclude
 * @since 1.0
 */ 
public final class JIComObjectImpl implements IJIComObject {

	private JIComServer objectInstanceStub = null;
	String selfIPID = null;
	private String iid = null;
	boolean isDual = false;
	private JISession session = null;
	private JIInterfacePointer ptr = null;
	private Map connectionPointInfo = null;
	private int timeout = 0;
	
	public JIComObjectImpl(JISession session,JIInterfacePointer ptr) 
	{
		this(session.getStub(),ptr.getIPID(),ptr.getIID(),session);
		this.ptr = ptr;
	}
	
	JIComObjectImpl(JIComServer objectInstanceStub, 
			String selfIPID, String iid,JISession session)
	{
		this.objectInstanceStub = objectInstanceStub;
		this.selfIPID = selfIPID;
		this.iid = iid;
		this.session = session;
		
	}
	
	public IJIUnknown queryInterface(String iid) throws JIException 
	{
		return objectInstanceStub.getInterface(iid,selfIPID);
	}
	
	public void addRef() throws JIException
	{
		JICallObject obj = new JICallObject(selfIPID,true);
		obj.setOpnum(1);//addRef
		
		//length
		obj.addInParamAsShort((short)1,JIFlags.FLAG_NULL);
		//ipid to addfref on
		JIArray array = new JIArray(new rpc.core.UUID[]{new rpc.core.UUID(selfIPID)},true);
		obj.addInParamAsArray(array,JIFlags.FLAG_NULL);
		//TODO requesting 5 for now, will later build caching mechnaism to exhaust 5 refs first before asking for more
		// same with release.
		obj.addInParamAsInt(5,JIFlags.FLAG_NULL);
		obj.addInParamAsInt(0,JIFlags.FLAG_NULL);//private refs = 0
		
		obj.addOutParamAsType(Short.class,JIFlags.FLAG_NULL);//size
		obj.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);//Hresult for size
		objectInstanceStub.addRef_ReleaseRef(obj);
		
		if (obj.getResultAsIntAt(1) != 0)
		{
			throw new JIException(obj.getResultAsIntAt(1),(Throwable)null);
		}
	}
	
	public void release() throws JIException
	{
		JICallObject obj = new JICallObject(selfIPID,true);
		obj.setOpnum(2);//release
		//length
		obj.addInParamAsShort((short)1,JIFlags.FLAG_NULL);
		//ipid to addfref on
		JIArray array = new JIArray(new rpc.core.UUID[]{new rpc.core.UUID(selfIPID)},true);
		obj.addInParamAsArray(array,JIFlags.FLAG_NULL);
		//TODO requesting 5 for now, will later build caching mechnaism to exhaust 5 refs first before asking for more
		// same with release.
		obj.addInParamAsInt(5,JIFlags.FLAG_NULL);
		obj.addInParamAsInt(0,JIFlags.FLAG_NULL);//private refs = 0
		objectInstanceStub.addRef_ReleaseRef(obj);
	}


	public Object[] call(JICallObject obj) throws JIException 
	{
		return call(obj,timeout);	
	}

	
	public IJIComObject getCOMObject()
	{
		return this;
	}
	
	public JIInterfacePointer getInterfacePointer()
	{
		return ptr == null ? objectInstanceStub.getServerInterfacePointer() : ptr;
	}
	
	public String getIpid()
	{
		return selfIPID;
	}

	public boolean equals(Object obj) {
		
		if (!(obj instanceof JIComObjectImpl))
		{
			return false;
		}
		
		return (this.selfIPID.equalsIgnoreCase(((IJIComObject)obj).getIpid()));
	}
	
	public int hashCode()
	{
		return selfIPID.hashCode();
	}
	
	public JISession getAssociatedSession()
	{
		return session;
	}
	
	public String getInterfaceIdentifier()
	{
		return iid;
	}
	
	public JIComServer getAssociatedComServer()
	{
		return objectInstanceStub;
	}
	
	public boolean isDispatchSupported()
	{
		return isDual;
	}

	public synchronized String setConnectionInfo(IJIComObject connectionPoint, Integer cookie) {
		if (connectionPointInfo == null) //lazy creation, since this is used by event callbacks only.
		{
			connectionPointInfo = new HashMap();
		}
		String uniqueId = GUIDUtil.guidStringFromHexString(IdentifierFactory.createUniqueIdentifier().toHexString());
		connectionPointInfo.put(uniqueId,new Object[]{connectionPoint,cookie});
		return uniqueId;
	}

	public synchronized Object[] getConnectionInfo(String identifier) {
		return (Object[])connectionPointInfo.get(identifier);
	}

	public synchronized Object[] removeConnectionInfo(String identifier) {
		return (Object[])connectionPointInfo.remove(identifier);
	}

	public IJIUnreferenced getUnreferencedHandler(JISession session) {
		return session.getUnreferencedHandler(getIpid());
	}

	public void registerUnreferencedHandler(JISession session, IJIUnreferenced unreferenced) {
		session.registerUnreferencedHandler(getIpid(), unreferenced);
	}

	public void unregisterUnreferencedHandler(JISession session) {
		session.unregisterUnreferencedHandler(getIpid());
	}

	public Object[] call(JICallObject obj, int socketTimeout) throws JIException
	{
		obj.setParentIpid(selfIPID);
		//Call is always made on your stub.
		
		if (socketTimeout != 0) //using instance level timeout
		{
			return objectInstanceStub.call(obj,iid,socketTimeout);
		}
		else
		{
			return objectInstanceStub.call(obj,iid);
		}
	}

	public int getInstanceLevelSocketTimeout()
	{
		// TODO Auto-generated method stub
		return timeout;
	}

	public void setInstanceLevelSocketTimeout(int timeout)
	{
		this.timeout = timeout;
	}
}
