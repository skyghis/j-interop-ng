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

/**<p> j-Interop class corresponding to COM <code>IDispatch</code> Interface. <br>
 *  
 * Sample Usage:-
 * <br>
 * 
 * <code>
 *  //Assume comStub is the reference to JIComServer, obtained earlier... <br> 
 *	IJIUnknown unknown = comStub.createInstance(); <br>
 *  // This call will result into a <i>QueryInterface</i> for the IDispatch <br>
 *	IJIDispatch dispatch = (IJIDispatch)JIObjectFactory.<b>createCOMInstance</b>(JIObjectFactory.IID_IDispatch,unknown); <br>
 * </code>
 * <br>
 * Another example:-
 * <br>
 * <code>
 *  //From MSWord example <br>
 *	JIVariant variant = dispatch.get("Documents"); <br>
 * 	JIInterfacePointer ptr = variant.getObjectAsInterfacePointer(); <br>
 *	IJIDispatch documents = (IJIDispatch)JIObjectFactory.<b>createCOMInstance</b>(unknown,ptr); <br>
 *	JIString filePath = new JIString("c:/temp/test.doc"); <br>
 *	JIVariant variant2[] = documents.callMethodA("open",new Object[]{new JIVariant(filePath,true),JIVariant.OPTIONAL_PARAM 
 *			,JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM, <br>
 *			JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM, <br>
 *			JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM, <br>
 *			JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM, <br>
 *			JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM}); <br>
 *	IJIDispatch document = (IJIDispatch)JIObjectFactory.<b>createCOMInstance</b>(unknown,variant2[0].getObjectAsInterfacePointer()); <br>
 * </code>
 * <br> 
 *  
 *  Please note that the behaviour of all APIs in this class accepting parameters is such that all <code>inparams</code> are converted to <code>JIVariants</code> before being sent to the COM server through the <code>IJIDispatch</code> 
 *  interface. If the <code>inparam</code> is already a <code>JIVariant</code> , it is left as it is.
 *  <br>
 *  for example:- <br>
 *  <code>
 *  //From MSADO example. <br> 	
 *  dispatch = (IJIDispatch)JIObjectFactory.createCOMInstance(JIObjectFactory.IID_IDispatch,unknown); <br>
 *	dispatch.callMethod("Open",<b>new Object[]{new JIString("driver=Microsoft Access Driver (*.mdb);dbq=C:\\temp\\products.mdb"),JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,new Integer(-1)} </b>); <br>
 *	JIVariant variant[] = dispatch.callMethodA("Execute",<b>new Object[]{new JIString("SELECT * FROM Products"),new Integer(-1)}</b>); <br>
 *	if (variant[0].isNull()) <br>
 *	{ <br>
 *		System.out.println("Recordset is empty."); <br>
 *	}<br>
 *	else<br>
 * 	{<br>
 *		//Do something...<br>
 *  }<br>
 *   </code>
 *  <br>
 *  
 *  This implicit conversion to <code>JIVariant</code> is <i>NOT</i> byReference. If the developer wishes to send that then he\she must put in a <code>JIVariant</code> with <code>byRef</code> flag set in the <code>inparam</code> array itself.
 *  
 *  Also, where ever the corresponding COM interface API requires an [optional] parameter, the developer can use <code>JIVariant.OPTIONAL_PARAM</code> , like in the example above.
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
	 * DISPID for property "put" or "putRef".
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
	
	/**From MSDN: <br>
	 * <i>Determines whether there is type information available for the dual interface. </i>
	 * <br>
	 * @return If the object provides type information, this number is 1; otherwise the number is 0. 
	 * @throws JIException
	 */
	public int getTypeInfoCount() throws JIException;
	
	/** Maps a method name to its corresponding <code>DISPID</code>.The result of this call is cached for further usage and no network call is performed again for the same method name. <br>
	 *   
	 * @param apiName Method name.
	 * @return int representing the DISPID.
	 * @throws JIException
	 */	
	public int getIDsOfNames(String apiName) throws JIException;
	
	/** Maps a single method name and an optional set of it's argument names to a corresponding set of <code>DISPIDs</code>. The result of this call is cached for further usage and no network call is performed again for the same method[argument] set. <br>
	 *  
	 * @param apiName String[] with first index depicting method name and the rest depicting parameters.
	 * @return int[] <code>DISPIDs</code> in the same order as the method[argument] set.
	 * @throws JIException
	 */
	public int[] getIDsOfNames(String[] apiName) throws JIException;
	
	/** Returns an j-Interop implementation of COM <code>ITypeInfo</code> interface based on the <code>typeInfo </code>. 
	 * <br>
	 * @param typeInfo
	 * @return
	 * @throws JIException
	 */
	public IJITypeInfo getTypeInfo(int typeInfo) throws JIException;

	/** Performs a <code>propput</code> for the method identified by the dispId. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 */
	public void put(int dispId, JIVariant inparam) throws JIException;
    
	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>put(int,JIVariant)</code>. <br>
	 * 
	 * @param name name of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 */
	public void put(String name, JIVariant inparam) throws JIException;

	/** Performs a <code>propputref</code> for the method identified by the dispId. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 */
	public void putRef(int dispId, JIVariant inparam) throws JIException;

	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>putRef(int,JIVariant)</code>.
	 * 
	 * @param name name of the method to invoke.
	 * @param inparam parameter for that method.
	 * @throws JIException
	 */
	public void putRef(String name, JIVariant inparam) throws JIException;

	/** Performs a <code>propget</code> for the method identified by the dispId. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	public JIVariant get(int dispId) throws JIException;

	/** Performs a <code>propget</code> for the method identified by the <code>dispId</code> parameter. <code>inparams</code> defines the parameters for the <i>get</i> operation.<br>
	 *   
	 * @param dispId dispId of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return array of JIVariants
	 * @throws JIException
	 */
	public JIVariant[] get(int dispId, Object[] inparams) throws JIException;
	
	
	/** Performs a <code>propget</code> for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>get(int,Object[])</code>.<code>inparams</code> defines the parameters for the <i>get</i> operation.
	 * <br>
	 * @param name
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return array of JIVariants
	 * @throws JIException
	 */
	public JIVariant[] get(String name,Object[] inparams) throws JIException;
	
	
	/** Performs a <code>propget</code> for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>get(int)</code>. <br>
	 *  
	 * @param name name of the method to invoke.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	public JIVariant get(String name) throws JIException;
    
	/**Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>callMethod(int)</code>.	 
	 * <br> 
	 * @param name name of the method to invoke.
	 * @throws JIException
	 */
	public void callMethod(String name) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @throws JIException
	 */
	public void callMethod(int dispId) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. <br>
	 * Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <br>
	 * <code>callMethodA(int)</code>.	 
	 * <br>
	 * @param name name of the method to invoke.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	public JIVariant callMethodA(String name) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	public JIVariant callMethodA(int dispId) throws JIException;
	
	/**Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>callMethod(int,Object[])</code>. For the <code>inparams</code> array, sequential dispIds (zero based index) will be used. 
	 * For <code>inparam[0]</code> , dispId will be <code>0</code>, for <code>inparam[1]</code> it will be <code>1</code> and so on. <br>
	 *  
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the 
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @throws JIException
	 */
	//sequential dispIds for params are used 0,1,2,3...
	public void callMethod(String name, Object[] inparams) throws JIException;
	
	//sequential dispIds for params are used 0,1,2,3...
	/**Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 * For the <code>inparams</code> array, sequential dispIds (zero based index) will be used. 
	 * For <code>inparam[0]</code> , dispId will be <code>0</code>, for <code>inparam[1]</code> it will be <code>1</code> 
	 * and so on. <br>
	 *  
	 * @param dispId dispId of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @throws JIException
	 */
	public void callMethod(int dispId, Object[] inparams) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to 
	 * <code>callMethodA(int,Object[])</code>. For the <code>inparams</code> array, sequential dispIds (zero based index) will be used. 
	 * For <code>inparam[0]</code> , dispId will be <code>0</code>, for <code>inparam[1]</code> it will be <code>1</code> 
	 * and so on. <br>
	 * 
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	//sequential dispIds for params are used 0,1,2,3...
	public JIVariant[] callMethodA(String name, Object[] inparams) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. For the <code>inparams</code> array, sequential dispIds (zero based index) will be used. 
	 * For <code>inparam[0]</code> , dispId will be <code>0</code>, for <code>inparam[1]</code> it will be <code>1</code> and so on. <br>
	 *  
	 * @param dispId dispId of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	//sequential dispIds for params are used 0,1,2,3...
	public JIVariant[] callMethodA(int dispId, Object[] inparams) throws JIException;
	

	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to 
	 * <code>callMethod(int,Object[],int[]).</code>. For the <code>inparams</code> array, the corresponding dispIds are present in the <code>dispIds</code> array. The size of both arrays should match. 
	 *  <br>
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds Array of dispIds , matching by index to those in <code>inparams</code> array.
	 * @throws JIException
	 */
	//inparams.length == dispIds.length.
	public void callMethod(String name, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 *  For the <code>inparams</code> array, the corresponding dispIds are present in the <code>dispIds</code> array. 
	 *  The size of both arrays should match. 
	 *  <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds Array of dispIds , matching by index to those in <code>inparams</code> array.
	 * @throws JIException
	 */
	//inparams.length == dispIds.length.
	public void callMethod(int dispId, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to 
	 * <code>callMethodA(int,Object[],int[]).</code>. 
	 *  For the <code>inparams</code> array, the corresponding dispIds are present in the <code>dispId</code> array. 
	 *  The size of both arrays should match. 
	 *  <br>
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds Array of dispIds , matching by index to those in <code>inparams</code> array.
	 * @return JMeoVariant result.
	 * @throws JIException
	 */
	//inparams.length == dispIds.length.
	public JIVariant[] callMethodA(String name, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>dispId</code> parameter. 
	 *  For the <code>inparams</code> array, the corresponding dispIds are present in the <code>dispIds</code> array. 
	 *  The size of both arrays should match. 
	 *  <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param dispIds Array of dispIds , matching by index to those in <code>inparams</code> array.
	 * @return JMeoVariant result.
	 * @throws JIException
	 */
	//inparams.length == dispIds.length.
	public JIVariant[] callMethodA(int dispId, Object[] inparams, int[] dispIds) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a <code>getIdOfNames(String[])</code> by forming <code>name + paramNames []</code>, 
	 *  and then delegates the call to <code>callMethod(int,Object[],int[]).</code>. 
	 *  For the <code>inparams</code> array, the corresponding <i>parameter names</i> are present in the <code>paramNames</code> array. 
	 *  The size of both arrays should match. 
	 *  <br>
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param paramNames Array of parameter names, matching by index to those in <code>inparams</code> array.
	 * @throws JIException
	 */
	//	inparams.length == paramNames.length.
	public void callMethod(String name, Object[] inparams, String[] paramNames) throws JIException;
	
	/** Performs a <code>method</code> call for the method identified by the <code>name</code> parameter. 
	 * 	Internally it will first do a <code>getIdOfNames(String[])</code> by forming <code>name + paramNames []</code>, 
	 *  and then delegates the call to <code>callMethodA(int,Object[],int[]).</code>. 
	 *  For the <code>inparams</code> array, the corresponding <i>parameter names</i> are present in the <code>paramNames</code> array. 
	 *  The size of both arrays should match. 
	 *  <br>
	 * @param name name of the method to invoke.
	 * @param inparams members of this array are implicitly converted to <code>JIVariants</code> before performing the <br>
	 *				   actual call to the COM server, via the <code>IJIDispatch</code> interface.
	 * @param paramNames Array of parameter names, matching by index to those in <code>inparams</code> array.
	 * @return JIVariant result.
	 * @throws JIException
	 */
	//inparams.length == paramNames.length.
	public JIVariant[] callMethodA(String name, Object[] inparams, String[] paramNames) throws JIException;
	
	/** Performs a <code>propput</code> for the method identified by the dispId. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 */
	public void put(int dispId, Object[] params) throws JIException;
    
	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>put(int,Object[])</code>. <br>
	 * 
	 * @param name name of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 */
	public void put(String name, Object[] params) throws JIException;

	/** Performs a <code>propputref</code> for the method identified by the dispId. <br>
	 * 
	 * @param dispId dispId of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 */
	public void putRef(int dispId, Object[] params) throws JIException;

	/** Performs a <code>propput</code> for the method identified by the <code>name</code> parameter. Internally it will first do a <code>getIdOfNames(name)</code> and then delegates the call to <code>putRef(int,Object[])</code>.
	 * 
	 * @param name name of the method to invoke.
	 * @param params parameters for that method.
	 * @throws JIException
	 */
	public void putRef(String name, Object[] params) throws JIException;

	/** Returns the EXCEPINFO structure wrapped as a data object for the 
	 * last operation. Please note this will only be valid if a JIException has been raised.
	 * 
	 * 
	 * @return
	 */
	public JIExcepInfo getLastExcepInfo();
}
