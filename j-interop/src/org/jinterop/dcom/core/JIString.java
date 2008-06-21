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
import java.util.List;
import java.util.Map;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JISystem;

/**<p> Class representing a String. The Wide Char (LPWSTR) and the BSTRs are both encoded by the server in "UTF-16LE".
 * This encoding will be preserved by the library for all to and fro operations.
 * </p>
 * @since 1.0
 */
public final class JIString implements Serializable {

	/**
	 * Represents JIVariant for this JIString, it is valid only if the JIString is a BSTR(JIFlags.FLAG_REPRESENTATION_STRING_BSTR) type.
	 */
	public final JIVariant Variant;
	/**
	 * Represents JIVariant(byRef = true) for this JIString, it is valid only if the JIString is a BSTR(JIFlags.FLAG_REPRESENTATION_STRING_BSTR) type.
	 */
	public final JIVariant VariantByRef;
	private static final long serialVersionUID = -1656299949818101872L;
	private JIPointer member = null;
	private int type = JIFlags.FLAG_NULL;
	

	/**Creates a JIString Object of the specified type. To be used while deserialiazing this Object.
	 * 
	 * @param type JIFlags , String flags only.
	 */
	public JIString(int type)
	{
		this.type = type;
		if (type == JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR || type == JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)
		{
			member = new JIPointer(String.class,true);
		}
		else if (type == JIFlags.FLAG_REPRESENTATION_STRING_BSTR)
		{
			member = new JIPointer(String.class,false);
		}
		else
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_FLAG_ERROR));
		}
		Variant = null;
		VariantByRef = null;
		member.setFlags(type | JIFlags.FLAG_REPRESENTATION_VALID_STRING);
	}

	
	/** Creates a JIString Object. To be used while serialiazing this Object.
	 * 
	 * @param str
	 * @param type JIFlags , String flags only.
	 */
	public JIString(String str, int type)
	{
		str = (str == null) ? "" : str;
		this.type = type;
		if (type == JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR || type == JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)
		{
			member = new JIPointer(str,true);
			Variant = null;
			VariantByRef = null;
		}
		else if (type == JIFlags.FLAG_REPRESENTATION_STRING_BSTR)
		{
			member = new JIPointer(str,false);
			member.setReferent(0x72657355);//"User" in LEndian.
			Variant = new JIVariant(this);
			VariantByRef = new JIVariant(this,true);
		}
		else
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_FLAG_ERROR));
		}
		
		member.setFlags(type | JIFlags.FLAG_REPRESENTATION_VALID_STRING);
		
	}

	
	
	/** Creates a JIString Object of the BSTR type. To be used while serialiazing this Object.
	 * 
	 * @param str
	 */
	public JIString(String str)
	{
		this(str,JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
	}
	
	
	/** String encapsulated by this Object. The encoding scheme for LPWSTR and BSTR strings is "UTF-16LE".
	 * 
	 * 
	 * @return
	 */
	public String getString()
	{
		return (String)member.getReferent();
	}
	
	/** Type representing the String  
	 * 
	 * @return JIFlag's String types.
	 */
	public int getType()
	{
		return type;
	}

	
	void encode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG)
	{
		JIMarshalUnMarshalHelper.serialize(ndr,member.getClass(),member,defferedPointers,type | FLAG);
	}
	
	
	JIString decode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG, Map additionalData)
	{
		JIString newString = new JIString(type);
		newString.member = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,member,defferedPointers,type | FLAG,additionalData);
		return newString;
	}
	
	void setDeffered(boolean deffered)
	{
		/*
		//this condition is required so that only BSTRs are deffered and also since this member could be deffered and
		//setting it to true would spoil the logic
		 * this is incorrect logic in the bug sent by Kevin , the ONEVENTSTRUCT consists of LPWSTRs which are deffered
		*/
		if (member != null && !member.isReference()) 
		{
			((JIPointer)member).setDeffered(true);
		}
	}
	
	public String toString()
	{
		return member == null ? "[null]" : "[Type: " + type + " , "+ member.toString() + "]";
	}
}
