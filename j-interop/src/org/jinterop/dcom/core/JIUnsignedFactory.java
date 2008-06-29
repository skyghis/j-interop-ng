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


import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JISystem;

/** Representation of C++ "Unsigned Types".
 * 
 * @since 1.15
 */
public final class JIUnsignedFactory {

	/**<p>Returns an implementation for each of the <code>unsigned</code> type. Only 3 types are supported at present 
	 * <code>Byte</code>, <code>Short</code>, <code>Integer</code>.
	 * 
	 * <p>Since Java has no support for unsigned types, use a <code>Short</code> for a <code>Byte</code>, <code>Integer</code> 
	 * for a <code>Short</code> and <code>Long</code> for an <code>Integer</code>. This is to accomodate the entire 
	 * spectrum for the <code>unsigned</code> type and prevent the rollover problem. 
	 * 
	 * @param value <code>Short</code>, <code>Integer</code>, <code>Long</code> only
	 * @param flag  JIFlags unsigned flags
	 * @return
	 * @throws IllegalArgumentException if the <code>value</code> is not an instance of the supported types or an incorrect 
	 * <code>flag</code> has been provided.
	 * @see JIFlags#FLAG_REPRESENTATION_UNSIGNED_BYTE
	 * @see JIFlags#FLAG_REPRESENTATION_UNSIGNED_SHORT
	 * @see JIFlags#FLAG_REPRESENTATION_UNSIGNED_INT
	 */
	public static IJIUnsigned getUnsigned(Number value, int flag)
	{
		IJIUnsigned retVal = null;
		
		if (!(value instanceof Short) && !(value instanceof Long) && !(value instanceof Integer))
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNSIGNED_INCORRECT_TYPE));
		}
		
		switch(flag)
		{
			case JIFlags.FLAG_REPRESENTATION_UNSIGNED_BYTE:
				retVal = new JIUnsignedByte((Short)value);
				break;
			
			case JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT:
				retVal = new JIUnsignedShort((Integer)value);
				break;
				
			case JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT:
				retVal = new JIUnsignedInteger((Long)value);
				break;
			default:
				throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNSIGNED_INCORRECT_TYPE));
		}
		
		return retVal;
	}
	

//	/** Returns template to be used during [out] params.
//	 * 
//	 * @param flag
//	 * @return
//	 */
//	public static IJIUnsigned getUnsigned(int flag)
//	{
//		IJIUnsigned retVal = null;
//		switch(flag)
//		{
//			case JIFlags.FLAG_REPRESENTATION_UNSIGNED_BYTE:
//				retVal = new JIUnsignedByte();
//				break;
//			
//			case JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT:
//				retVal = new JIUnsignedShort();
//				break;
//				
//			case JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT:
//				retVal = new JIUnsignedInteger();
//				break;
//			default:
//				throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNSIGNED_INCORRECT_TYPE));
//		}
//		
//		return retVal;
//		
//	}
	
	
}
