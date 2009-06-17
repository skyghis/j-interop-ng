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

import java.io.Serializable;

import ndr.NetworkDataRepresentation;


/**<p>Provides a way to express parameters for a particular method. These are only <code>[in]</code>
 * parameters, the <code>[out]</code> parameters are decided at the implementation level. If the <code>IDL</code>
 * method being described by this class is returning multiple objects then use the return type of the implementation
 * as an <code>Object[]</code>
 *
 * <p>
 * For example:- <br>
 *
 * IDL from Microsoft Internet Explorer is:- <br>
 * <code>
 * [id(0x000000fb), helpstring("A new, hidden, non-navigated WebBrowser window is needed.")] <br>
 *    void NewWindow2(   [in, out] IDispatch** ppDisp,
 *                       [in, out] VARIANT_BOOL* Cancel); <p>
 * </code>
 * Corresponding <code>JILocalParamsDescriptor</code> would be :- <br>
 * <code>
 * 		JILocalParamsDescriptor paramObject = new JILocalParamsDescriptor(); <br>
 * 		paramObject.addInParamAsObject(new JIPointer(IJIComObject.class,false), JIFlags.FLAG_NULL); <br>
 * 		paramObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);<br>
 * </code>
 * and the Java implementation must return an <code>Object[]</code> in this case, for returning the 2 parameters back.
 * <p><i>Please refer to MSInternetExplorer, Test_ITestServer2_Impl, SampleTestServer
 * and MSShell examples for more details on how to use this class.</i><br>
 *
 * @since 2.0 (formerly JIParameterObject)
 *
 */
public final class JILocalParamsDescriptor implements Serializable
{

	private JICallBuilder callObject = new JICallBuilder();
	private static final long serialVersionUID = -4274963180104543505L;

	/**
	 * @exclude
	 * @param ndr
	 * @return
	 */
	Object[] read(NetworkDataRepresentation ndr)
	{
		callObject.read2(ndr);
		return callObject.getResults();
	}


	/** Add <code>[in]</code> parameter of the type <code>clazz</code> at the end of the out parameter list.
	 *
	 * @param clazz
	 * @param FLAGS
	 */
	public void addInParamAsType(Class clazz, int FLAGS)
	{
		callObject.addOutParamAsType(clazz,FLAGS);
	}

	/** Add <code>[in]</code> parameter at the end of the out parameter list. Typically callers are
	 * composite in nature <code>JIStruct</code> , <code>JIUnions</code> , <code>JIPointer</code>
	 * and <code>JIString</code> .
	 *
	 * @param param
	 * @param FLAGS
	 */
	public void addInParamAsObject(Object param, int FLAGS)
	{
		callObject.addOutParamAsObject(param,FLAGS);
	}

	/**
	 * @exclude
	 * @param params
	 * @param FLAGS
	 */
	void setInParams(Object[] params, int FLAGS)
	{
		callObject.setOutParams(params,FLAGS);
	}

	/**Removes <code>[in]</code> parameter at the specified index from the parameter list.
	 *
	 * @param index 0 based index
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void removeInParamAt(int index, int FLAGS)
	{
		callObject.removeOutParamAt(index,FLAGS);
	}

	/**
	 * @exclude
	 * @return
	 */
	Object[] getInParams()
	{
		return callObject.getOutParams();
	}

	void setSession(JISession session)
	{
		callObject.attachSession(session);
	}

}
