/** j-Interop (Pure Java implementation of DCOM protocol)
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
package org.jinterop.dcom.core;

import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIException;

/**
 * Internal Framework class.
 *
 * @exclude
 * @since 1.0
 *
 */
public class JIComObjectImplWrapper implements IJIComObject {

    /**
     *
     */
    private static final long serialVersionUID = 6142976024482507753L;
    protected final IJIComObject comObject;

    protected JIComObjectImplWrapper(IJIComObject comObject) {
        this.comObject = comObject;
    }

    @Override
    public IJIComObject queryInterface(String iid) throws JIException {

        return comObject.queryInterface(iid);
    }

    @Override
    public void addRef() throws JIException {
        comObject.addRef();
    }

    @Override
    public void release() throws JIException {
        comObject.release();
    }

    @Override
    public String getIpid() {
        return comObject.getIpid();
    }

    @Override
    public Object[] call(JICallBuilder obj) throws JIException {
        return comObject.call(obj);
    }

    @Override
    public JIInterfacePointer internal_getInterfacePointer() {
        return comObject.internal_getInterfacePointer();
    }

    @Override
    public JISession getAssociatedSession() {
        return comObject.getAssociatedSession();
    }

    /**
     * Returns the <i>IID</i> of this object
     *
     * @return String representation of 128 bit uuid.
     */
    @Override
    public String getInterfaceIdentifier() {
        return comObject.getInterfaceIdentifier();
    }

//	/**
//	 * @exclude
//	 */
//	public JIComServer getAssociatedComServer()
//	{
//		return comObject.getAssociatedComServer();
//	}
    @Override
    public boolean isDispatchSupported() {
        return comObject.isDispatchSupported();
    }

    @Override
    public String internal_setConnectionInfo(IJIComObject connectionPoint, Integer cookie) {
        return comObject.internal_setConnectionInfo(connectionPoint, cookie);
    }

    @Override
    public Object[] internal_getConnectionInfo(String identifier) {
        return comObject.internal_getConnectionInfo(identifier);
    }

    @Override
    public Object[] internal_removeConnectionInfo(String identifier) {
        return comObject.internal_removeConnectionInfo(identifier);
    }

    @Override
    public IJIUnreferenced getUnreferencedHandler() {
        return comObject.getUnreferencedHandler();
    }

    @Override
    public void registerUnreferencedHandler(IJIUnreferenced unreferenced) {
        comObject.registerUnreferencedHandler(unreferenced);
    }

    @Override
    public void unregisterUnreferencedHandler() {
        comObject.unregisterUnreferencedHandler();
    }

    @Override
    public Object[] call(JICallBuilder obj, int timeout) throws JIException {
        return comObject.call(obj, timeout);
    }

    @Override
    public int getInstanceLevelSocketTimeout() {
        return comObject.getInstanceLevelSocketTimeout();
    }

    @Override
    public void setInstanceLevelSocketTimeout(int timeout) {
        comObject.setInstanceLevelSocketTimeout(timeout);
    }

    @Override
    public void internal_setDeffered(boolean deffered) {
        comObject.internal_setDeffered(deffered);
    }

    @Override
    public boolean isLocalReference() {
        return comObject.isLocalReference();
    }

    @Override
    public String toString() {
        return comObject.toString();
    }
}
