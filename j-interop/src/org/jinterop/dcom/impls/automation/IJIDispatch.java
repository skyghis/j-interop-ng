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

package org.jinterop.dcom.impls.automation;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIVariant;




/**<p> Represents the Windows COM <code>IDispatch</code> Interface. 
 *
 * <p>
 * Sample Usage :-
 * <br>
 * 
 * <code>
 *  //Assume comServer is the reference to JIComServer, obtained earlier... <br> 
 *	IJIComObject comObject = comServer.createInstance(); <br>
 *  // This call will result into a <i>QueryInterface</i> for the IDispatch <br>
 *	IJIDispatch dispatch = (IJIDispatch)JIObjectFactory.narrowObject(comObject.queryInterface(IJIDispatch.IID)); <br>
 * </code>
 * <p>
 * Another example :-
 * <br>
 * <code>
 *  int dispId = dispatch.getIDsOfNames("Workbooks");<br>
 *  JIVariant outVal = dispatch.get(dispId);<br>
 *  IJIDispatch dispatchOfWorkBooks =(IJIDispatch)JIObjectFactory.narrowObject(outVal.getObjectAsComObject());<br>
 *  JIVariant[] outVal2 = dispatchOfWorkBooks.callMethodA("Add",new Object[]{JIVariant.OPTIONAL_PARAM()});<br>
 *  dispatchOfWorkBook =(IJIDispatch)JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());<br>
 *  outVal = dispatchOfWorkBook.get("Worksheets");<br>
 *  dispatchOfWorkSheets = (IJIDispatch)JIObjectFactory.narrowObject(outVal.getObjectAsComObject());<br>
 * </code>
 * <p> 
 *  
 *  Please note that all <code>[in]</code> parameters are converted to <code>{@link JIVariant}</code> 
 *  before being sent to the COM server through the <code>IJIDispatch</code> 
 *  interface. If any <code>[in]</code> parameter is already a <code>JIVariant</code> , it is left as it is.
 *  <p>
 *  for example:- <br>
 *  <code>
 *  //From MSADO example. <br> 	
 *  dispatch = (IJIDispatch)JIObjectFactory.narrowObject(comObject.queryInterface(IJIDispatch.IID));<br>
 *	dispatch.callMethod("Open",new Object[]{new JIString("driver=Microsoft Access Driver (*.mdb);dbq=C:\\temp\\products.mdb"), <br>
 *  JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,new Integer(-1)}); <br>
 *	JIVariant variant[] = dispatch.callMethodA("Execute",new Object[]{new JIString("SELECT * FROM Products"),new Integer(-1)}); <br>
 *	if (variant[0].isNull()) <br>
 *	{ <br>
 *		System.out.println("Recordset is empty."); <br>
 *	}<br>
 *	else<br>
 * 	{<br>
 *		//Do something...<br>
 *  }<br>
 *   </code>
 *  <p>
 *  
 *  Where ever the corresponding COM interface API requires an <code>[optional]</code> parameter,
 *  the developer can use <code>JIVariant.OPTIONAL_PARAM()</code> , like in the example above.
 *  </p>
 * @since 1.0
 */
public interface IJIDispatch extends IJIComObject {

	/**
	 * Flag for selecting a <code>method</code>.
	 */
	public final int DISPATCH_METHOD = 0xFFFFFFF1;
	
	/**
	 * Flag for selecting a Property <code>propget</code>.
	 */
	public final int DISPATCH_PROPERTYGET = 0xFFFFFFF2 ; //(0x2 );
	
	/**
	 * Flag for selecting a Property <code>propput</code>.
	 */
	public final int DISPATCH_PROPERTYPUT = 0xFFFFFFF4 ; //& (0x4 );
	
	/**
	 * COM <code>DISPID</code> for property "put" or "putRef".
	 */
	public final int DISPATCH_DISPID_PUTPUTREF = 0xFFFFFFFD ; //(0x4 | 0x8 | DISPATCH_METHOD);
	
	/**
	 * Flag for selecting a Property <code>propputref</code>. 
	 */
	public final int DISPATCH_PROPERTYPUTREF = 0xFFFFFFF8; //0x8  

	/**
	 * IID representing the COM <code>IDispatch</code>.
	 */
	public final String IID = "00020400-0000-0000-c000-000000000046"; 
	
	/** Definition from MSDN:
	 * <i>Determines whether there is type information available for the dual interface. </i>
	 * <br>
	 * @return 1 if the object provides type information, otherwise 0. 
	 * @throws JIException
	 */
	public int getTypeInfoCount() throws JIException;
	
	/** Maps a method name to its corresponding <code>DISPID</code>.The result of this call is cached 
	 * for further usage and no network call is performed again for the same method name. <br>
	 *   
	 * @param apiName Method name.
	 * @return <code>DISPID</code> of the method.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>apiName</code> is <code>null</code> or empty.
	 */	
	public int getIDsOfNames(String apiName) throws JIException;
	
	/** Maps a single method name and an optional set of it's argument names to a corresponding set of <code>DISPIDs</code>. 
	 * The result of this call is cached for further usage and no network call is performed again for the same method[argument] set.
	 *  
	 * @param apiName String[] with first index depicting method name and the rest depicting parameters.
	 * @return int[] <code>DISPIDs</code> in the same order as the method[argument] set.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>apiName</code> is <code>null</code> or empty.
	 */
	public int[] getIDsOfNames(String[] apiName) throws JIException;
	
	/** Returns an implementation of COM <code>ITypeInfo</code> interface based on the <code>typeInfo</code>. 
	 * <br>
	 * @param typeInfo the type information to return. Pass 0 to retrieve type information for the <code>IDispatch</code> implementation.
	 * @return
	 * @throws JIException
	 */
	public IJITypeInfo getTypeInfo(int typeInfo) throws JIException;

	/** Performs a <code>propput</code> for the method identified by the <code>dispId</code>. <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 */
	public void put(int dispId, JIVariant inparam) throws JIException;
    
	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #put(int, JIVariant)}.
	 * 
	 * @param name name of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty.
	 */
	public void put(String name, JIVariant inparam) throws JIException;

	/** Performs a <code>propputref</code> for the method identified by the <code>dispId</code>. <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 */
	public void putRef(int dispId, JIVariant inparam) throws JIException;

	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #putRef(int, JIVariant)}.
	 * 
	 * @param name name of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty.
	 */
	public void putRef(String name, JIVariant inparam) throws JIException;

	/** Performs a <code>propget</code> for the method identified by the <code>dispId</code>. <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @return JIVariant result of the call
	 * @throws JIException
	 */
	public JIVariant get(int dispId) throws JIException;

	/** Performs a <code>propget</code> for the method identified by the <code>dispId</code> parameter. 
	 * <code>inparams</code> defines the parameters for the <code>get</code> operation.<br>
	 *   
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return array of JIVariants
	 * @throws JIException
	 */
	public JIVariant[] get(int dispId, Object[] inparams) throws JIException;
	
	
	/** Performs a <code>propget</code> for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #get(int, Object[])}.
	 * 
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s 
	 * before performing the actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return array of JIVariants
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty.
	 */
	public JIVariant[] get(String name,Object[] inparams) throws JIException;
	
	
	/** Performs a <code>propget</code> for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #get(int)}
	 *  
	 * @param name name of the method to invoke.
	 * @return JIVariant result of the call.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	public JIVariant get(String name) throws JIException;
    
	/**Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #callMethod(int)}.	 
	 * <br> 
	 * @param name name of the method to invoke.
	 * @throws JIException
	 *  @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty.
	 */
	public void callMethod(String name) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @throws JIException
	 */
	public void callMethod(int dispId) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #callMethodA(int)}.	 
	 * <br>
	 * @param name name of the method to invoke.
	 * @return JIVariant result.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty.
	 */
	public JIVariant callMethodA(String name) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	public JIVariant callMethodA(int dispId) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to 
	 * {@link #callMethod(int, Object[])}. For the <code>inparams</code> array, sequential <code>DISPID</code>s
	 * (zero based index) will be used. For <code>inparam[0]</code> , <code>DISPID</code> will be <code>0</code>, 
	 * for <code>inparam[1]</code> it will be <code>1</code> and so on. <br>
	 *  
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	//sequential dispIds for params are used 0,1,2,3...
	public void callMethod(String name, Object[] inparams) throws JIException;
	
	//sequential dispIds for params are used 0,1,2,3...
	/**Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 * For the <code>inparams</code> array, sequential <code>DISPID</code>s (zero based index) will be used. 
	 * For <code>inparam[0]</code> , <code>DISPID</code> will be <code>0</code>, for <code>inparam[1]</code> 
	 * it will be <code>1</code> and so on. <br>
	 *  
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @throws JIException
	 */
	public void callMethod(int dispId, Object[] inparams) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to 
	 *  {@link #callMethodA(int, Object[])}. For the <code>inparams</code> array, sequential <code>DISPID</code>s 
	 *  (zero based index) will be used. For <code>inparam[0]</code> , <code>DISPID</code> will be <code>0</code>, 
	 *  for <code>inparam[1]</code> it will be <code>1</code> and so on. <br>
	 * 
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return JIVariant[] result.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	//sequential dispIds for params are used 0,1,2,3...
	public JIVariant[] callMethodA(String name, Object[] inparams) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 * For the <code>inparams</code> array, sequential <code>DISPID</code>s (zero based index) will be used. 
	 * For <code>inparam[0]</code> , <code>DISPID</code> will be <code>0</code>, for <code>inparam[1]</code> 
	 * it will be <code>1</code> and so on. 
	 *  
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return JIVariant[] result.
	 * @throws JIException
	 */
	//sequential dispIds for params are used 0,1,2,3...
	public JIVariant[] callMethodA(int dispId, Object[] inparams) throws JIException;
	

	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to 
	 * {@link #callMethod(int, Object[], int[])}. For the <code>inparams</code> array, the corresponding 
	 * <code>DISPID</code>s are present in the <code>dispIds</code> array. The size of both arrays should match. 
	 *  <br>
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds array of <code>DISPID</code>s , matching by index to those in <code>inparams</code> array.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty.
	 */
	//inparams.length == dispIds.length.
	public void callMethod(String name, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 *  For the <code>inparams</code> array, the corresponding <code>DISPID</code>s are present in 
	 *  the <code>dispIds</code> array. The size of both arrays should match. 
	 *  <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds array of <code>DISPID</code>s , matching by index to those in <code>inparams</code> array.
	 * @throws JIException
	 */
	//inparams.length == dispIds.length.
	public void callMethod(int dispId, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to 
	 *  {@link #callMethodA(int, Object[], int[])}.For the <code>inparams</code> array, the corresponding 
	 *  <code>DISPID</code>s are present in the <code>dispId</code> array. The size of both arrays should match. 
	 *  <br>
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds array of <code>DISPID</code>s , matching by index to those in <code>inparams</code> array.
	 * @return JIVariant[] result.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	//inparams.length == dispIds.length.
	public JIVariant[] callMethodA(String name, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 *  For the <code>inparams</code> array, the corresponding <code>DISPID</code>s are present in the
	 *  <code>dispIds</code> array. The size of both arrays should match. 
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds array of <code>DISPID</code>s , matching by index to those in <code>inparams</code> array.
	 * @return JIVariant[] result.
	 * @throws JIException
	 */
	//inparams.length == dispIds.length.
	public JIVariant[] callMethodA(int dispId, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a  {@link #getIDsOfNames(String[])} by forming <code>name + paramNames []</code>, 
	 *  and then delegates the call to {@link #callMethod(int, Object[], int[])}. For the <code>inparams</code> array, 
	 *  the corresponding parameter names are present in the <code>paramNames</code> array. The size of both 
	 *  arrays should match. 
	 *  
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param paramNames Array of parameter names, matching by index to those in <code>inparams</code> array.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	//	inparams.length == paramNames.length.
	public void callMethod(String name, Object[] inparams, String[] paramNames) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a {@link #getIDsOfNames(String[])} by forming <code>name + paramNames []</code>, 
	 *  and then delegates the call to {@link #callMethodA(int, Object[], int[])}. For the <code>inparams</code> array, 
	 *  the corresponding parameter names are present in the <code>paramNames</code> array. The size of both 
	 *  arrays should match. 
	 *  
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariant</code>s before performing the 
	 * actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param paramNames Array of parameter names, matching by index to those in <code>inparams</code> array.
	 * @return JIVariant result.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	//inparams.length == paramNames.length.
	public JIVariant[] callMethodA(String name, Object[] inparams, String[] paramNames) throws JIException;
	
	/** Performs a <code>propput</code> for the method identified by the <code>dispId</code> <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 */
	public void put(int dispId, Object[] params) throws JIException;
    
	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a  {@link #getIDsOfNames(String)} and then delegates the call to {@link #put(int, Object[])}.
	 * 
	 * @param name name of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	public void put(String name, Object[] params) throws JIException;

	/** Performs a <code>propputref</code> for the method identified by the <code>dispId</code>. <br>
	 * 
	 * @param dispId <code>DISPID</code> of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 */
	public void putRef(int dispId, Object[] params) throws JIException;

	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. 
	 * Internally it will first do a {@link #getIDsOfNames(String)} and then delegates the call to {@link #putRef(int, Object[])}.
	 * 
	 * @param name name of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or empty. 
	 */
	public void putRef(String name, Object[] params) throws JIException;

	/** Returns the COM <code>EXCEPINFO</code> structure wrapped as a data object for the 
	 * <b>last</b> operation. Note this will only be valid if a {@link JIException} has been raised
	 * in the last call.
	 * 
	 * @return
	 */
	public JIExcepInfo getLastExcepInfo();
}
