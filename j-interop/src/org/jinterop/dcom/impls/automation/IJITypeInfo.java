/**j-Interop (Pure Java implementation of DCOM protocol)
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
import org.jinterop.dcom.core.JIString;


/**
 *  Represents the Windows COM <code>ITypeInfo</code> Interface. <p>
 *
 * Definition from MSDN: <i>
 * ITypeInfo, an interface typically used for reading information about objects. For example, an object browser
 * tool can use ITypeInfo to extract information about the characteristics and capabilities of objects from type
 * libraries. Type information interfaces are intended to describe the parts of the application that can be called
 * by outside clients, rather than those that might be used internally to build an application. <p>
 * The ITypeInfo interface provides access to the following:  <UL>
 * <li>The set of function descriptions associated with the type. For interfaces, this contains the set of member
 * functions in the interface.<li> The set of data member descriptions associated with the type. For structures,
 * this contains the set of fields of the type. <li>The general attributes of the type, such as whether it describes
 * a structure, an interface, and so on.
 * </i>
 * <p>
 * Please note that all APIs of <code>ITypeInfo</code> have not been implemented. <br>
 * @since 1.0
 *
 */
//TODO add APIs here
public interface IJITypeInfo extends IJIComObject {

	/**
	 * IID representing the COM <code>ITypeInfo</code>.
	 */
	public final String IID = "00020401-0000-0000-C000-000000000046";

	/**Retrieves the FuncDesc structure that contains information about a specified function.
	 *
	 * @param index index of the function whose description is to be returned. The index should be in the range
	 * of 0 to 1 less than the number of functions in this type.
	 * @return
	 * @throws JIException
	 */
	public FuncDesc getFuncDesc(int index) throws JIException;

	/**Retrieves a TypeAttr structure that contains the attributes of the type description. <br>
	 *
	 * @return
	 * @throws JIException
	 */
	public TypeAttr getTypeAttr() throws JIException;

	/**Retrieves the containing type library and the index of the type description within that type library. <br>
	 * @return Object[0] = IJITypeLib, Object[1] = Integer
	 * @throws JIException
	 */
	public Object[] getContainingTypeLib() throws JIException;

	/**Retrieves the documentation string, the complete Help file name and path, and the context ID for the Help
	 * topic for a specified type description. <br>
	 *
	 * @param memberId ID of the member whose documentation is to be returned.
	 * @return Object[0] = JIString of BSTR type, Object[1]  = JIString of BSTR type, Object[3] = JIString of BSTR type
	 * @throws JIException
	 */
	public Object[] getDocumentation(int memberId) throws JIException;

	/**Retrieves a description or specification of an entry point for a function in a DLL. <br>
	 *
	 * @param memberId ID of the member function whose DLL entry description is to be returned.
	 * @param invKind Specifies the kind of member identified by <code>memberId</code>. This is important for properties,
	 * because one memid can identify up to three separate functions.
	 * @return Object[0] = JIString of BSTR type, Object[1]  = JIString of BSTR type, Object[2] = Short
	 * @throws JIException
	 */
	public Object[] getDllEntry(int memberId, int invKind) throws JIException;

	/**Retrieves a VARDESC structure that describes the specified variable. <br>
	 *
	 * @param index index of the variable whose description is to be returned. The index should be in the range
	 * of 0 to 1 less than the number of variables in this type.
	 * @return
	 * @throws JIException
	 */
	public VarDesc getVarDesc(int index) throws JIException;

	/**Retrieves the variable with the specified member ID (or the name of the property or method and its parameters)
	 * that correspond to the specified function ID. <br>
	 *
	 * @param memberId ID of the member whose name (or names) is to be returned.
	 * @param maxNames Length of the passed-in array.
	 * @return Object[0] = JIString[] of BSTR type, Object[1] = Integer
	 * @throws JIException
	 */
	public Object[] getNames(int memberId, int maxNames) throws JIException;

	/**If a type description describes a COM class, it retrieves the type description of the implemented
	 * interface types. For an interface, getRefTypeOfImplType returns the type information for inherited interfaces,
	 * if any exist. <br>
	 *
	 * @param index index of the implemented type whose handle is returned. The valid range is 0 to the
	 * cImplTypes field in the TypeAttr structure.
	 * @return
	 * @throws JIException
	 */
    public int getRefTypeOfImplType(int index) throws JIException;

    /**Retrieves the IMPLTYPEFLAGS enumeration for one implemented interface or base interface in a type description. <br>
     *
     * @param index index of the implemented interface or base interface for which to get the flags.
     * @return
     * @throws JIException
     */
    public int getImplTypeFlags(int index) throws JIException;

    /** If a type description references other type descriptions, it retrieves the referenced type descriptions. <br>
     *
     * @param hrefType handle to the referenced type description to be returned.
     * @return
     * @throws JIException
     */
    public IJITypeInfo getRefTypeInfo(int hrefType) throws JIException;

    /**Creates a new instance of a type that describes a component object class (coclass). <br>
     *
     * @param riid
     * @return
     * @throws JIException
     */
    public IJIComObject createInstance(String riid) throws JIException;

    /**Retrieves marshaling information.
     *
     * @param memberId member ID that indicates which marshaling information is needed.

     * @return
     * @throws JIException
     */
    public JIString getMops(int memberId) throws JIException;
}