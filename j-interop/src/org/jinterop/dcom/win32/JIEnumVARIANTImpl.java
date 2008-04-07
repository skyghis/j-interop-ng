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
 
package org.jinterop.dcom.win32;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIDefaultComObjectImpl;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIInterfacePointer;
import org.jinterop.dcom.core.JIVariant;

/**
 * @exclude
 * @since 1.0
 *
 */
final class JIEnumVARIANTImpl extends JIDefaultComObjectImpl implements IJIEnumVARIANT {

	//IJIComObject comObject = null;

	JIEnumVARIANTImpl(IJIComObject comObject)
	{
		super(comObject);
	}
	
	public Object[] next(int celt) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(0);
		callObject.addInParamAsInt(celt,JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(new JIArray(JIVariant.class,null,1,true,true),JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		return result;
	}
	
    public void skip(int celt) throws JIException
    {
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(1);
		callObject.addInParamAsInt(celt,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
	}

    public void reset() throws JIException
    {
    	JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(2);
		Object[] result = comObject.call(callObject);
    }

    public IJIEnumVARIANT Clone() throws JIException
    {
    	JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(3);
		callObject.addOutParamAsObject(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		JIInterfacePointer ptr = (JIInterfacePointer)result[0];
		return (IJIEnumVARIANT)JIComFactory.createCOMInstance(comObject,ptr);
    }

	
}
