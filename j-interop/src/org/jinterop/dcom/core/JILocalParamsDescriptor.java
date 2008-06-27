/** j-Interop (Pure Java implementation of DCOM protocol)
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

import ndr.NetworkDataRepresentation;


/**<p>Provides a way to express parameters for a particular method. These are only <i>In</i> Params the 
 * <i>Out</i> params are decided at the implementation level. If the IDL method being described by this 
 * class is returning multiple objects then use the return type of the implementation as an <code>Object[]</code> 
 * <br>
 * For example:- <br>
 * 
 * IDL from Microsoft Internet Explorer is:- <br>
 * 
 * [id(0x000000fb), helpstring("A new, hidden, non-navigated WebBrowser window is needed.")] <br>
 *    void NewWindow2(   [in, out] IDispatch** ppDisp, 
 *                       [in, out] VARIANT_BOOL* Cancel); <br>
 *
 * <br> Corresponding <code>JILocalParamsDescriptor</code> would be :- <br>
 * 
 * <code>
 * 		JILocalParamsDescriptor paramObject = new JILocalParamsDescriptor(); <br>
 * 		paramObject.addInParamAsObject(new JIPointer(JIInterfacePointer.class,false), JIFlags.FLAG_NULL); <br>
 * 		paramObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);<br>
 * </code>
 * 
 * and the Java implementation must return an <code>Object[]</code> in this case, for returning the 2 params back. 
 * <br>
 * </p>
 * <br><i>Please refer to <b>MSInternetExplorer</b> example for more details on how to use this class.</i><br>
 * @since 1.0
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
	
	
	/** Add IN parameter of the type <code>clazz</code> at the end of the out parameter list.
	 * 
	 * @param clazz
	 * @param FLAGS
	 */
	public void addInParamAsType(Class clazz, int FLAGS)
	{
		callObject.addOutParamAsType(clazz,FLAGS);
	}
	
	/** Add IN parameter at the end of the out parameter list. Typically callers are  
	 * composite in nature JIStruct, JIUnions, JIPointer and JIString . 
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
	
	/**Removes IN parameter at the specified index from the Parameter list.
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
