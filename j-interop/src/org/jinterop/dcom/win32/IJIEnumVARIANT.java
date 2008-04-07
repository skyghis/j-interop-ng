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

/**<p> j-Interop class corresponding to COM <code>IEnumVARIANT</code> Interface. <br>
 *  Sample Usage:- <br>
 *  <code>
 *  //From MSEnumVariant example <br>
 *  JIVariant variant = dispatch.get("_NewEnum"); <br>
 *	IJIComObject object2 = JIComFactory.createCOMInstance(dispatch,variant.getObjectAsInterfacePointer()); <br>
 *	IJIEnumVARIANT enumVARIANT = (IJIEnumVARIANT)JIComFactory.createCOMInstance(IJIEnumVARIANT.IID,object2); <br>
 *	<br>
 *	for (i = 0; i < 10; i++) <br>
 *	{ <br>
 *		Object[] values = enumVARIANT.next(1); <br>
 *		JIArray array = (JIArray)values[0]; <br>
 *		Object[] arrayObj = (Object[])array.getArrayInstance(); <br>
 *		for (int j = 0; j < arrayObj.length; j++) <br>
 *		{ <br>
 *			System.out.println(((JIVariant)arrayObj[j]).getObjectAsInt() + "," + ((Integer)values[1]).intValue()); <br>	
 *		} <br>
 *	} <br>
 *
 *  </code>
 *  
 *  </p>
 * @since 1.0
 *
 */
public interface IJIEnumVARIANT {
	
	/**
	 * IID representing the COM <code>IEnumVARIANT</code>.
	 */
	public static final String IID = "00020404-0000-0000-C000-000000000046";

	/** From MSDN:-
	 * <i>
	 * Attempts to get the next celt items in the enumeration sequence. If fewer than the requested number 
	 * of elements remain in the sequence, Next returns only the remaining elements. <br>
	 * </i>
	 * 
	 * @param celt The number of elements to be returned. 
	 * @return results
	 * @throws JIException
	 */
	public Object[] next(int celt) throws JIException;
	
	/** From MSDN:-
	 * <i>
	 * Attempts to skip over the next celt elements in the enumeration sequence.
	 * </i>
	 * 
	 * @param celt The number of elements to skip. 
	 * @throws JIException
	 */
    public void skip(int celt) throws JIException;

    /**From MSDN:- 
     * <i>Resets the enumeration sequence to the beginning. There is no guarantee that exactly the same set of 
     * variants will be enumerated the second time as was enumerated the first time. Although an exact duplicate 
     * is desirable, the outcome depends on the collection being enumerated. You may find that it is impractical 
     * for some collections to maintain this condition (for example, an enumeration of the files in a directory). <br>
     * </i>
     * @throws JIException
     */
    public void reset() throws JIException;

    /** From MSDN:-
     * <i>
     * Creates a copy of the current state of enumeration. Using this function, a particular point in the enumeration 
     * sequence can be recorded, and then returned to at a later time. The returned enumerator is of the same actual 
     * interface as the one that is being cloned. <p>
     * There is no guarantee that exactly the same set of variants will be enumerated the second time as was 
     * enumerated the first. Although an exact duplicate is desirable, the outcome depends on the collection 
     * being enumerated. You may find that it is impractical for some collections to maintain this condition 
     * (for example, an enumeration of the files in a directory). <br>
     *
     * </i>
     * @return reference to the clone.
     * @throws JIException
     */
    public IJIEnumVARIANT Clone() throws JIException;


}
