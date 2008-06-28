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

	/**<p>Returns an implementation for each of the unsigned type. Only 3 types are supported at present Byte, Short, Integer and 
	 * since Java has no support for unsigned types, please send a Short for a Byte, Integer for a Short and Long for an Integer. This is to 
	 * accomodate the entire spectrum for the unsigned type and prevent the rollover problem. 
	 * </p>
	 * @param value Short, Integer, Long only
	 * @param flag refer JIFlags documentation
	 * @return
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
