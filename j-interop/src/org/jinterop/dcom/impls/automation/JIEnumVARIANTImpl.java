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
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 * @exclude
 * @since 1.0
 *
 */
final class JIEnumVARIANTImpl extends JIComObjectImplWrapper implements IJIEnumVariant {

	//IJIComObject comObject = null;

	/**
	 *
	 */
	private static final long serialVersionUID = -8405188611519724869L;

	JIEnumVARIANTImpl(IJIComObject comObject)
	{
		super(comObject);
	}

	public Object[] next(int celt) throws JIException
	{
		JICallBuilder callObject = new JICallBuilder(true);
		callObject.setOpnum(0);
		callObject.addInParamAsInt(celt,JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(new JIArray(JIVariant.class,null,1,true,true),JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		return result;
	}

    public void skip(int celt) throws JIException
    {
		JICallBuilder callObject = new JICallBuilder(true);
		callObject.setOpnum(1);
		callObject.addInParamAsInt(celt,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
	}

    public void reset() throws JIException
    {
    	JICallBuilder callObject = new JICallBuilder(true);
		callObject.setOpnum(2);
		Object[] result = comObject.call(callObject);
    }

    public IJIEnumVariant Clone() throws JIException
    {
    	JICallBuilder callObject = new JICallBuilder(true);
		callObject.setOpnum(3);
		callObject.addOutParamAsObject(IJIComObject.class,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		return (IJIEnumVariant)JIObjectFactory.narrowObject((IJIComObject)result[0]);
    }


}
