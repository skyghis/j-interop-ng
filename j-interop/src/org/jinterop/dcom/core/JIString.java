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

package org.jinterop.dcom.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JISystem;

/**<p>Class representing a COM string. The Wide Char (<code>LPWSTR</code>) and the <code>BSTR</code> are
 * both encoded by the server in "UTF-16LE". This encoding will be preserved by the library for all to
 * and fro operations.
 * </p>
 * @since 1.0
 */
public final class JIString implements Serializable {

	/**
	 * Represents <code>JIVariant</code> for this object, it is valid only if this object is a <code>BSTR</code>
	 * (<code>JIFlags.FLAG_REPRESENTATION_STRING_BSTR</code>) type.
	 */
	public final JIVariant Variant;
	/**
	 * Represents <code>JIVariant(byRef = true)</code> for this object, it is valid only if this object is a <code>BSTR</code>
	 * (<code>JIFlags.FLAG_REPRESENTATION_STRING_BSTR</code>) type.
	 */
	public final JIVariant VariantByRef;
	private static final long serialVersionUID = -1656299949818101872L;
	private JIPointer member = null;
	private int type = JIFlags.FLAG_NULL;


	/**Creates an object of the specified type. Used while deserialiazing this object.
	 *
	 * @param type JIFlags string flags
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_BSTR
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_LPCTSTR
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_LPWSTR
	 * @throws IllegalArgumentException if <code>type</code> is not a string flag.
	 *
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


	/** Creates a string object of a given <code>type</code>.
	 *
	 * @param str value encapsulated by this object.
	 * @param type JIFlags string flags
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_BSTR
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_LPCTSTR
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_LPWSTR
	 * @throws IllegalArgumentException if <code>type</code> is not a string flag.
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



	/** Creates a object of the <code>BSTR</code> type.
	 *
	 * @param str value encapsulated by this object.
	 */
	public JIString(String str)
	{
		this(str,JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
	}


	/** String encapsulated by this object. The encoding scheme for <code>LPWSTR</code> and <code>BSTR</code> strings is "UTF-16LE".
	 *
	 *
	 * @return
	 */
	public String getString()
	{
		return (String)member.getReferent();
	}

	/** Type representing this object.
	 *
	 * @return JIFlags string flags
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_BSTR
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_LPCTSTR
	 * @see JIFlags#FLAG_REPRESENTATION_STRING_LPWSTR
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
