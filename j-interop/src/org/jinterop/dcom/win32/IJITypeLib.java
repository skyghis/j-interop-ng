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
import org.jinterop.dcom.core.JIString;

/**From MSDN:-
 * <i>
 * The ITypeLib interface provides methods for accessing a library of type descriptions. This interface supports the following:  
 * Generalized containment for type information. ITypeLib allows iteration over the type descriptions contained 
 * in the library. <p>
 * Global functions and data. A type library can contain descriptions of a set of modules, each of which is the 
 * equivalent of a C or C++ source file that exports data and functions. The type library supports compiling references 
 * to the exported data and functions. 
 * <p>General information, including a user-readable name for the library and help for the library as a whole. 
 * </i>
 * <br> 
 * @since 1.0
 *
 */
public interface IJITypeLib extends IJIComObject {
	
	/**
	 * IID representing the COM <code>ITypeLib</code>.
	 */
	public static final String IID = "00020402-0000-0000-C000-000000000046";
	
	/**Returns the number of type descriptions in the type library.
	 * 
	 * @return
	 * @throws JIException
	 */
	public int getTypeInfoCount() throws JIException; 

	/**Retrieves the specified type description in the library.
	 * 
	 * @param index Index of the ITypeInfo interface to be returned. 
	 * @return
	 * @throws JIException
	 */
	public IJITypeInfo getTypeInfo(int index) throws JIException;
	
	/**Retrieves the type of a type description.
	 * 
	 * @param index The index of the type description within the type library. 
	 * @return
	 * @throws JIException
	 */
	public int getTypeInfoType(int index) throws JIException;

	/**Retrieves the type description that corresponds to the specified GUID.
	 * 
	 * @param uuid GUID of the type description. 
	 * @return
	 * @throws JIException
	 */
	public IJITypeInfo getTypeInfoOfGuid(String uuid) throws JIException;
	/** Retrieves the structure that contains the library's attributes.
	 * 
	 * @throws JIException
	 */
	public void getLibAttr() throws JIException;
	
	/**Retrieves the library's documentation string, the complete Help file name and path, and the context 
	 * identifier for the library Help topic in the Help file. <br>
	 * 
	 * @param memberId
	 * @return
	 * @throws JIException
	 */
	public Object[] getDocumentation(int memberId) throws JIException;
	
	/**Finds occurrences of a type description in a type library. This may be used to quickly verify that a 
	 * name exists in a type library. <br>
	 * 
	 * @param nameBuf
	 * @param hashValue
	 * @param found
	 * @return
	 * @throws JIException
	 */
	public Object[] findName(JIString nameBuf,int hashValue,short found) throws JIException;
}
