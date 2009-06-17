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
