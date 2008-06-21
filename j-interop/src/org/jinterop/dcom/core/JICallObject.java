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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;

/** <p>This class is used to setup all the parameters (in and out) for the call to the 
 * COM server to be successfully executed. 
 * 
 * <br> Sample Usage:-
 * <code>
 *  <br>
 *  JICallObject obj = new JICallObject(handle.getIpid()); <br>
 *  obj.reInit(); <br>
 *	obj.setOpnum(94); //This needs to be obtained via IDL, Other wise use IJIDispatch if COM <i>IDispatch</i> <br> 
 *					  //interface is supported by the underlying COM Object.
 *	<br>
 *	obj.addInParamAsPointer(new JIPointer(new JIString("j-Interop Rocks",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
 *	obj.addInParamAsPointer(new JIPointer(new JIString("Pretty simple ;)",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL); <br>
 *	<br>
 *	//handle is previously obtained IJIComObject <br>
 *	Object[] result = handle.call(obj); 
 * <br>
 * </code>
 * 
 * <br>Please note that if values are expected back from the call, they should be added as <code>outParams</code> before 
 * the <code>call(...)</code> api is used.
 * <br>
 * <code>
 *  obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL); <br>
 *  obj.addOutParamAsObject(new JIPointer(Short.class,true),JIFlags.FLAG_NULL); <br>
 * </code>
 * <br>
 * <b>Note: </b> Something odd that I have noticed, BSTR* and any second level Interface Pointer like IDispatch** , are always returned back to the callee. So when they are being used in inParams, please use them in outParams as well.
 * </p>
 * @since 1.0
 */
public class JICallObject extends NdrObject implements Serializable {

	static final String CURRENTSESSION = "CURRENTSESSION";
	static final String COMOBJECTS = "COMOBJECTS";
	
	private static final long serialVersionUID = -2939657500731135110L;
	private int opnum = -1;
	private Object[] outparams = null;
	private boolean dispatchNotSupported = false;
	private String enclosingParentsIPID = null;
	private ArrayList inparamFlags = new ArrayList();
	private ArrayList outparamFlags  = new ArrayList();
	private ArrayList inParams = new ArrayList();
	private ArrayList outParams = new ArrayList();
	private int hresult = 0;
	private boolean executed = false;
	private Object[] resultsOfException = null;
	private JISession session = null;
	
	/** Creates a JICallObject associated with an Interface Identifier.
	 * 
	 * @param IPIDofParent Please use <code>IJIComObject.getIpid()</code> for this.
	 * @param dispatchNotSupported true if dispatch is not supported by the Interface, please use <code>IJIComObject.isDispatchSupported()</code> <br>
	 * to find out whether dispatch is supported on the parent or not.
	 */
	public JICallObject(String IPIDofParent,boolean dispatchNotSupported)
	{
		this(IPIDofParent);
		this.dispatchNotSupported = dispatchNotSupported;
	}
	
	/**<p> Creates a JICallObject associated with an Interface Identifier. This ctor assumes that Dispatch is 
	 * supported by the Interface.
	 * 
	 * <br> Sample Usage:-
	 * <code>
	 *  <br>
	 *  //handle is an IJIComObject obtained before <br> 
	 *  JICallObject obj = new JICallObject(handle.getIpid()); <br>
	 *  </code>
	 *  </p>
	 * @param IPIDofParent Please use <code>IJIComObject.getIpid()</code> for this.
	 */
	public JICallObject(String IPIDofParent)
	{
		enclosingParentsIPID = IPIDofParent;
	}
	
	/**
	 * Reinitialises all members of this object except the parent's IPID. The Object is ready to be used again on a 
	 * fresh <code>IJIComObject.call(...)</code> after this step. 
	 *
	 */
	//after reinit, except parent, nothing is available.
	public void reInit()
	{
		opnum = -1;
		inParams = new ArrayList();
		inparamFlags = new ArrayList();
		outParams = new ArrayList();
		outparamFlags = new ArrayList();
		hresult = -1;
		outparams = null;
		executed = false;
	}
	
	void setParentIpid(String IPIDofParent)
	{
		enclosingParentsIPID = IPIDofParent;
	}
	
	String getParentIpid()
	{
		return enclosingParentsIPID;
	}

//	/**Add IN parameter as <code>JIInterfacePointer</code> at the end of the Parameter list.
//	 * 
//	 * @param value
//	 * @param FLAGS from JIFlags (if need be)
//	 */
//	public void addInParamAsInterfacePointer(JIInterfacePointer interfacePointer, int FLAGS)
//	{
//		insertInParamAsInterfacePointerAt(inParams.size(),interfacePointer,FLAGS);
//	}

	/**Add IN parameter as <code>IJIComObject</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsComObject(IJIComObject comObject, int FLAGS)
	{
		insertInParamAsComObjectAt(inParams.size(),comObject,FLAGS);
	}
	
	
	/**Add IN parameter as <code>int</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsInt(int value, int FLAGS)
	{
		insertInParamAsIntAt(inParams.size(),value,FLAGS);
	}

	/**Add IN parameter as <code>IJIUnsigned</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsUnsigned(IJIUnsigned value, int FLAGS)
	{
		insertInParamAsUnsignedAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>float</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsFloat(float value, int FLAGS)
	{
		insertInParamAsFloatAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>boolean</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsBoolean(boolean value, int FLAGS)
	{
		insertInParamAsBooleanAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>short</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsShort(short value, int FLAGS)
	{
		insertInParamAsShortAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>double</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsDouble(double value, int FLAGS)
	{
		insertInParamAsDoubleAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>char</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be)
	 */
	public void addInParamAsCharacter(char value, int FLAGS)
	{
		insertInParamAsCharacterAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>String</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (These <i>HAVE</i> to be the <b>String</b> Flags).
	 */
	//flags have to be String flags
	public void addInParamAsString(String value, int FLAGS)
	{
		insertInParamAsStringAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>JIVariant</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void addInParamAsVariant(JIVariant value, int FLAGS)
	{
		insertInParamAsVariantAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>Object</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void addInParamAsObject(Object value, int FLAGS)
	{
		insertInParamAsObjectAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>String representation of UUID</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void addInParamAsUUID(String value, int FLAGS)
	{
		insertInParamAsUUIDAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>JIPointer</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void addInParamAsPointer(JIPointer value, int FLAGS)
	{
		insertInParamAsPointerAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>JIStruct</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void addInParamAsStruct(JIStruct value, int FLAGS)
	{
		insertInParamAsStructAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>JIArray</code> at the end of the Parameter list.
	 * 
	 * @param value
	 * @param FLAGS from JIFlags (if need be).
	 */
	public void addInParamAsArray(JIArray value, int FLAGS)
	{
		insertInParamAsArrayAt(inParams.size(),value,FLAGS);
	}
	
	/**Add IN parameter as <code>Object[]</code> at the end of the Parameter list.The array is iterated and
	 * all members appended to the list.
	 * 
	 * @param values
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void setInParams(Object[] values, int FLAGS)
	{
		for (int i = 0;i < values.length;i++)
		{
			inParams.add(values[i]);	
			inparamFlags.add(new Integer(FLAGS)); //quite useless but do not want to change logic elsewhere
		}
		
	}
	
	/**Add IN parameter as <code>IJIComObject</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsComObjectAt(int index, IJIComObject value, int FLAGS)
	{
		inParams.add(index,value);
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>int</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsIntAt(int index, int value, int FLAGS)
	{
		inParams.add(index,new Integer(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}

	/**Add IN parameter as <code>IJIUnsigned</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsUnsignedAt(int index, IJIUnsigned value, int FLAGS)
	{
		inParams.add(index,value);
		inparamFlags.add(index,new Integer(FLAGS));
	}

	
	/**Add IN parameter as <code>float</code> at the specified index in the Parameter list.
	 *
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsFloatAt(int index, float value, int FLAGS)
	{
		inParams.add(index,new Float(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>boolean</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsBooleanAt(int index, boolean value, int FLAGS)
	{
		inParams.add(index,Boolean.valueOf(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>short</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsShortAt(int index, short value, int FLAGS)
	{
		inParams.add(index, new Short(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>double</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsDoubleAt(int index, double value, int FLAGS)
	{
		inParams.add(index, new Double(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>char</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsCharacterAt(int index, char value, int FLAGS)
	{
		inParams.add(index, new Character(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>String</code>  at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (These <i>HAVE</i> to be the <b>String</b> Flags).
	 */
	//flags have to be String flags
	public void insertInParamAsStringAt(int index, String value, int FLAGS)
	{
		inParams.add(index, new JIString(value,FLAGS));
		inparamFlags.add(index,new Integer(JIFlags.FLAG_NULL));
	}
	
	/**Add IN parameter as <code>JIVariant</code> at the specified index in the Parameter list.
	 *
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsVariantAt(int index, JIVariant value, int FLAGS)
	{
		inParams.add(index, value);
		inparamFlags.add(index,new Integer(JIFlags.FLAG_NULL));
	}
	
	/**Add IN parameter as <code>Object</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	//this is for dispatch, etc...more or less will never be used.
	public void insertInParamAsObjectAt(int index, Object value, int FLAGS)
	{
		inParams.add(index, value);
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>String representation of UUID</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsUUIDAt(int index, String value, int FLAGS)
	{
		inParams.add(index, new rpc.core.UUID(value));
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>JIPointer</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsPointerAt(int index, JIPointer value, int FLAGS)
	{
		inParams.add(index, value);
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>JIStruct</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsStructAt(int index, JIStruct value, int FLAGS)
	{
		inParams.add(index, value);
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Add IN parameter as <code>JIArray</code> at the specified index in the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param value
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void insertInParamAsArrayAt(int index, JIArray value, int FLAGS)
	{
		inParams.add(index, value);
		inparamFlags.add(index,new Integer(FLAGS));
	}
	
	/**Removes IN parameter at the specified index from the Parameter list.
	 * 
	 * @param index 0 based index
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void removeInParamAt(int index, int FLAGS)
	{
		Object value = inParams.remove(index);
		inparamFlags.remove(index);
	}

	/**Returns IN parameter at the specified index from the Parameter list.
	 * 
	 * @param index 0 based index 
	 * @return Primitives are returned as there Derieved types. 
	 */
	//Will just provide 1 getter, for outParams there would be overloads like inParam setters.
	public Object getInParamAt(int index)
	{
		return inParams.get(index);
	}
	
	/** Add OUT parameter of the type <code>clazz</code> at the end of the out parameter list.
	 * 
	 * @param clazz
	 * @param FLAGS
	 */
	public void addOutParamAsType(Class clazz, int FLAGS)
	{
		insertOutParamAt(outParams.size(),clazz,FLAGS);
	}
	
	/** Add OUT parameter at the end of the out parameter list. Typically callers are <br> 
	 * composite in nature JIStruct, JIUnions, JIPointer and JIString . 
	 * 
	 * @param outparam
	 * @param FLAGS
	 */
	public void addOutParamAsObject(Object outparam, int FLAGS)
	{
		insertOutParamAt(outParams.size(),outparam,FLAGS);
	}
	
	/** insert an OUT parameter at the specified index in the out parameter list. 
	 * 
	 * @param index 0 based index
	 * @param classOrInstance can be either a Class or an Object
	 * @param FLAGS
	 */
	public void insertOutParamAt(int index, Object classOrInstance,int FLAGS)
	{
		outParams.add(index, classOrInstance);
		outparamFlags.add(index,new Integer(FLAGS));
	}
	
	/** Retrieves the OUT param at the index in the out parameters list.
	 * 
	 * @param index 0 based index
	 * @return 
	 */
	public Object getOutParamAt(int index)
	{
		return outParams.get(index);
	}
	
	/**Removes OUT parameter at the specified index from the out parameters list.
	 * 
	 * @param index 0 based index
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void removeOutParamAt(int index, int FLAGS)
	{
		outParams.remove(index);
		outparamFlags.remove(index);
	}
	
	/**Add OUT parameter as <code>Object[]</code> at the end of the Parameter list. The array is iterated and
	 * all members appended to the list. 
	 * 
	 * @param values
	 * @param FLAGS from JIFlags (if need be). 
	 */
	public void setOutParams(Object[] values, int FLAGS)
	{
		for (int i = 0;i < values.length;i++)
		{
			outParams.add(values[i]);	
			outparamFlags.add(new Integer(FLAGS));
		}
		
	}
	
	//now for the results
	
	/**
	 * Returns the results as an <code>Object[]</code>. This array has to be iterated over to get the individual values.
	 */
	//	only valid before the interpretation of read, after that has actual values
	public Object[] getResults()
	{
		//checkIfCalled();
		return outparams;
	}
	
	/** Returns the value as <code>int</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public int getResultAsIntAt(int index)
	{
		checkIfCalled();
		return ((Integer)outparams[index]).intValue();
	}
	
	/** Returns the value as <code>float</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public float getResultAsFloatAt(int index)
	{
		checkIfCalled();
		return ((Float)outparams[index]).floatValue();
	}
	
	/** Returns the value as <code>boolean</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public boolean getResultAsBooleanAt(int index)
	{
		checkIfCalled();
		return ((Boolean)outparams[index]).booleanValue();
	}
	
	/** Returns the value as <code>short</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public short getResultAsShortAt(int index)
	{
		checkIfCalled();
		return ((Short)outparams[index]).shortValue();
	}
	
	/** Returns the value as <code>double</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public double getResultAsDoubleAt(int index)
	{
		checkIfCalled();
		return ((Double)outparams[index]).doubleValue();
	}
	
	/** Returns the value as <code>char</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public char getResultAsCharacterAt(int index)
	{
		checkIfCalled();
		return ((Character)outparams[index]).charValue();
	}
	
	/** Returns the value as <code>JIString</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public JIString getResultAsStringAt(int index)
	{
		checkIfCalled();
		return ((JIString)outparams[index]);
	}
	
	/** Returns the value as <code>JIVariant</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public JIVariant getResultAsVariantAt(int index)
	{
		checkIfCalled();
		return ((JIVariant)outparams[index]);
	}
	
	/** Returns the value as <code>String representation of the UUID</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public String getResultAsUUIDStrAt(int index)
	{
		checkIfCalled();
		return ((UUID)outparams[index]).toString();
	}
	
	/** Returns the value as <code>JIPointer</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public JIPointer getResultAsPointerAt(int index)
	{
		checkIfCalled();
		return ((JIPointer)outparams[index]);
	}
	
	/** Returns the value as <code>JIStruct</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public JIStruct getResultAsStructAt(int index)
	{
		checkIfCalled();
		return ((JIStruct)outparams[index]);
	}
	
	/** Returns the value as <code>JIArray</code> at the index from the result list. 
	 * 
	 * @param index 0 based index
	 * @return
	 */
	public JIArray getResultAsArrayAt(int index)
	{
		checkIfCalled();
		return ((JIArray)outparams[index]);
	}
	
	/** Returns the results incase an exception occured. 
	 * 
	 * @return
	 */
	public Object[] getResultsInCaseOfException()
	{
		//checkIfCalled();
		return resultsOfException;
	}
	
	/** Returns the <code>HRESULT</code> of this operation. This should be zero for successful calls and
	 * non-zero for failures.
	 * 
	 * @return
	 */
	public int getHRESULT()
	{
		return hresult;
	}
	
	private void checkIfCalled()
	{
		if (!executed)
		{
			throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_API_INCORRECTLY_CALLED));
		}
	}
	
	/** Returns the entire IN parameters list. 
	 * 
	 * @return
	 */
	public Object[] getInParams()
	{
		return inParams.toArray();
	}
	
	/** Returns the entire OUT parameters list. 
	 * 
	 * @return
	 */
	public Object[] getOutParams()
	{
		return outParams.toArray();
	}
	
	/** Returns the In Param flag.
	 * 
	 * @return
	 */
	public Integer[] getInparamFlags()
	{
		return (Integer[])inparamFlags.toArray(new Integer[0]);
	}
	
	/** Returns the Out Param flag.
	 * 
	 * @return
	 */
	public Integer[] getOutparamFlags()
	{
		return (Integer[])outparamFlags.toArray(new Integer[0]);
	}
	
	/** Returns the opnum of the API which will be invoked at the <code>COM</code> server. 
	 * 
	 */
	public int getOpnum() {
		//opnum is 3 as this is a COM interface and 0,1,2 are occupied by IUnknown
		//TODO remember this for extending com components also.
		return opnum;
	}
	
	//All Methods are 0 index based
	/**
	 *  Sets the opnum of the API which will be invoked at the <code>COM</code> server. This is a 0 based index.
	 *  Refer to the IDL of the <code>COM server</code> for this, all APIs are listed in a sequential order starting from 0. Please ignore the 
	 *  <code>"Id"</code> they might be having and count the index of the API being called here from the beginning of the interface starting from 
	 *  0 as the first index. Also note that if this interface derieves from anything other than <code>IUnknown</code> or <code>IDispatch</code>, your start
	 *  index will change from 0 to the cumulative(if that interface is also a derieved one) count of the super interface. For e.g if A(3 apis) derieves from B
	 *  (10 apis), then first API of A is at Opnum of 3, second at 4 and so on.
	 *  
	 *   
	 *   
	 *   Alternatively, you can use the IJIDispatch interface, if the object supports it.
	 */
	public void setOpnum(int num)
	{
		int dispatch = 0;
		if (!dispatchNotSupported)
		{
			dispatch = 4; //4 apis.
		}
		opnum = dispatch + num + 3 ; //0,1,2, Q.I
	}
	
	
	void write2(NetworkDataRepresentation ndr)
	{
		//reset buffer size here...
		//calculate rough length required length + 16 for the last bytes
		//plus adding 30 more for the verifier etc. 
		ndr.getBuffer().buf = new byte[bufferLength() + 16 + 30];
		JIOrpcThat.encode(ndr);
		writePacket(ndr);
	}
	
	/**
	 * @exclude
	 */
	public void write(NetworkDataRepresentation ndr) 
	{
		
		//reset buffer size here...
		//calculate rough length required length + 16 for the last bytes
		//plus adding 30 more for the verifier etc. 
		ndr.getBuffer().buf = new byte[bufferLength() + 16];
		
		JIOrpcThis orpcthis = new JIOrpcThis();
		orpcthis.encode(ndr);
		
		writePacket(ndr);
		
		//when it ends add 16 zeros.
		ndr.writeUnsignedLong(0);
		ndr.writeUnsignedLong(0);
		ndr.writeUnsignedLong(0);
		ndr.writeUnsignedLong(0);
		
	}
	
	private void writePacket(NetworkDataRepresentation ndr)
	{
		if (session == null)
		{
			throw new IllegalStateException("Programming Error ! Session not attached with this call ! ... Please rectify ! ");
		}
		
		Object[] inparams = inParams.toArray(); 
		
		int index = 0;
		if(inparams != null)
		{
//			if (JISystem.getLogger().isLoggable(Level.FINEST))
//			{
//				String str = "";
//				for (int i = 0;i < inparams.length;i++)
//				{
//					str = str + "In Param:[" + i + "] " + inparams[i] + "\n";
//				}
//				JISystem.getLogger().finest(str);
//			}
			while(index < inparams.length)
			{
				List listOfDefferedPointers = new ArrayList();
				if (inparams[index] == null)
				{
					JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,new Integer(0),listOfDefferedPointers,JIFlags.FLAG_NULL);
				}
				else
				{
					JIMarshalUnMarshalHelper.serialize(ndr,inparams[index].getClass(),inparams[index],listOfDefferedPointers,((Integer)inparamFlags.get(index)).intValue());
				}
				
				int x = 0;
				
				while (x < listOfDefferedPointers.size())
				{
//					thought of this today morning...change the logic here...the defeered pointers need to be 
//					completely serialized here. If they are also having nested deffered pointers then  those pointers
//					should be "inserted" just after the current pointer itself.
//					change the logic below to send out a new list and insert that list after the current x.
//					consider the case when there is a Struct having a nested pointer to another struct and this struct
//					itself having a pointer.
//					
//					Inparams order:- for 2 params.
//					int f,Struct{int i;			 
//								 Struct *ptr;
//								 Struct *ptr2;
//								 int j;
//								}
//					
//					while serializing this struct the pointer 1 will get deffered and so will pointer 2. Now while writing
//					the deffered pointers , we will find that the pointer 1 is pointing to a struct which has another deffered pointer (pointer to another struct maybe)
//					in such case, the current logic will add the deffered pointer to the end of the listOfDefferedPointers list, effectively serializing it
//					after the pointer 2 referent. But that is what is against the rules of DCERPC, in this case the referent of pointer 1 (struct with the pointer to another struct)
//					should be serialized in place (following th rules of the struct serialization ofcourse) and should not go to the end of the list.
					
					//JIMarshalUnMarshalHelper.serialize(ndr,JIPointer.class,(JIPointer)listOfDefferedPointers.get(x),listOfDefferedPointers,inparamFlags);
					ArrayList newList = new ArrayList();
					JIMarshalUnMarshalHelper.serialize(ndr,JIPointer.class,(JIPointer)listOfDefferedPointers.get(x),newList,((Integer)inparamFlags.get(index)).intValue());
					x++; //incrementing index
					listOfDefferedPointers.addAll(x,newList);
				}
				index++;
			}
			
		
		}
	}
	
	/**
	 * @exclude
	 */
	public void read(NetworkDataRepresentation ndr)
	{
//		if (opnum == 10) FOR TESTING ONLY
//		{
//			byte[] buffer = new byte[360];
//			FileInputStream inputStream;
//			try {
//				inputStream = new FileInputStream("c:/temp/ONEEVENTSTRUCT");
//				inputStream.read(buffer,0,360);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
//			ndr.setBuffer(ndrBuffer);
//			NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
//			ndr2.setBuffer(ndrBuffer);
//			read2(ndr2);
//		}
		//interpret based on the out params flags
		JIOrpcThat orpcThat = JIOrpcThat.decode(ndr);
		readPacket(ndr);
		readResult(ndr);
	}
	
	/** 
	 * called by only COMRuntime and NO ONE ELSE.
	 * 
	 * @exclude 
	 * 
	 * @param ndr
	 */
	void read2(NetworkDataRepresentation ndr)
	{
		JIOrpcThis.decode(ndr);
		readPacket(ndr);
		//readResult(ndr);
		//hresult = 0;
	}
	
	private void readPacket(NetworkDataRepresentation ndr)
	{
		
		if (session == null)
		{
			throw new IllegalStateException("Programming Error ! Session not attached with this call ! ... Please rectify ! ");
		}
		
		int index = 0;
		
		outparams = outParams.toArray();
		
		if (JISystem.getLogger().isLoggable(Level.FINEST))
		{
			String str = "";
			for (int i = 0;i < outparams.length;i++)
			{
				str = str + "Out Param:[" + i + "]" + outparams[i] + "\n";
			}
			
			JISystem.getLogger().finest(str);
		}
		
		ArrayList comObjects = new ArrayList();
		Map additionalData = new HashMap();
		additionalData.put(CURRENTSESSION, session);
		additionalData.put(COMOBJECTS, comObjects);
		ArrayList results = new ArrayList();
		//user has nothing to return.
		if (outparams != null && outparams.length > 0)
		{
			while(index < outparams.length)
			{
				List listOfDefferedPointers = new ArrayList();
				results.add(JIMarshalUnMarshalHelper.deSerialize(ndr,outparams[index],listOfDefferedPointers,((Integer)outparamFlags.get(index)).intValue(),additionalData));
				int x = 0;

				while (x < listOfDefferedPointers.size())
				{
					
					ArrayList newList = new ArrayList();
					JIPointer replacement = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,(JIPointer)listOfDefferedPointers.get(x),newList,((Integer)outparamFlags.get(index)).intValue(),additionalData);
					((JIPointer)listOfDefferedPointers.get(x)).replaceSelfWithNewPointer(replacement); //this should replace the value in the original place.	
					x++;
					listOfDefferedPointers.addAll(x,newList);
				}
				index++;
			}	
			
			
			//now create the right COM Objects, it is required here only and no place else. 
			for (int i = 0; i < comObjects.size(); i++)
			{
				JIComObjectImpl comObjectImpl = (JIComObjectImpl)comObjects.get(i);
				try {
					comObjectImpl.replaceMembers(JISessionHelper.instantiateComObject2(session, comObjectImpl.getInterfacePointer()));
					JISessionHelper.addComObjectToSession(comObjectImpl.getAssociatedSession(), comObjectImpl);
				} catch (JIException e) {
					JISystem.getLogger().throwing("JICallObject", "readPacket", e);
					throw new JIRuntimeException(e.getErrorCode());
				}
				//replace the members of the original com objects by the completed ones.
			}
			
			comObjects.clear();
		}
		
		outparams = results.toArray();
		executed = true;
	}
	
	private void readResult(NetworkDataRepresentation ndr)
	{
		//last has to be the result.
		hresult = ndr.readUnsignedLong();
		
		if (hresult != 0)
		{
			//something exception occured at server, set up results
			resultsOfException = outparams;
			outparams = null;
			throw new JIRuntimeException(hresult);
		}
	}
	
	private int bufferLength()
	{
		int length = 0;
		Object[] inparams = inParams.toArray();
		for (int i = 0; i < inparams.length;i++)
		{
			if (inparams[i] == null)
			{
				length = length + 4;
				continue;
			}
			int length2 = JIMarshalUnMarshalHelper.getLengthInBytes(inparams[i].getClass(),inparams[i],JIFlags.FLAG_NULL);
			length = length + length2;
		}
		
		return length + 2048; //2K extra for alignments, if any.
	}
	
	/**Returns true incase the Call resulted in an exception, use getHRESULT to get the error code.
	 * 
	 * @return
	 */
	public boolean isError()
	{
		checkIfCalled();
		return hresult != 0;
	}
	
	void attachSession(JISession session)
	{
		this.session = session;
	}
	
	JISession getSession()
	{
		return session;
	}
}
