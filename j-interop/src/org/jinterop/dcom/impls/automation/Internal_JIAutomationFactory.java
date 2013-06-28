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

package org.jinterop.dcom.impls.automation;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;

/** Creates automation related objects. Internal Factory , to be used only by the framework.
 *
 *
 * @exclude
 * @since 1.25
 */
public final class Internal_JIAutomationFactory {


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
	public static final String IID_IEnumVariant = IJIEnumVariant.IID;

	public static IJIComObject narrowObject(final IJIComObject comObject) throws JIException
	{
		IJIComObject retval = comObject;
		final String IID = comObject.getInterfaceIdentifier();
		if (IID.equalsIgnoreCase(IID_IDispatch))// && iUnknown.isIDispatchSupported())
		{
			retval = new JIDispatchImpl((IJIComObject)retval);
		}
		else
		if (IID.equalsIgnoreCase(IID_ITypeInfo))// && iUnknown.isIDispatchSupported())
		{
			retval = new JITypeInfoImpl((IJIComObject)retval);
		}
		else
		if (IID.equalsIgnoreCase(IID_ITypeLib))// && iUnknown.isIDispatchSupported())
		{
			retval = new JITypeLibImpl((IJIComObject)retval);
		}
		else
		if (IID.equalsIgnoreCase(IID_IEnumVariant))// && iUnknown.isIDispatchSupported())
		{
			retval = new JIEnumVARIANTImpl((IJIComObject)retval);
		}

		return retval;
	}




}
