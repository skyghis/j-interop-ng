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

package org.jinterop.dcom.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;


/** Representation of a Pointer. 
 * 
 * @since 1.0
 */
public final class JIPointer implements Serializable {

	
	private static final long serialVersionUID = -3434037097460692619L;
	private Object referent = null;
	private boolean isReferenceTypePtr = false;
	private boolean isDeffered = false;
	private int referentId = -1;
	private boolean isNull = false;
	private int flags = JIFlags.FLAG_NULL;
	private JIPointer() {}
	
	/** Creates an instance of this Object where the referent is of the type <code>value</code>. 
	 * Used when deserializing this pointer. 
	 * 
	 * @param value null is acceptable
	 * @param isReferenceTypePtr true if a referent Identifier will not precede this ptr.
	 */
	public JIPointer(Class value, boolean isReferenceTypePtr)
	{
		//null pointer.
		if (value == null)
		{
			value = Integer.class;
			isReferenceTypePtr = true;
			isNull = true;
		}

		//Should not defer since the enclosing struct,union,array will defer it by itself
		// this is important since , ptr to a ptr to a ptr (and more) will need to 
		//deserialize completely after the first deferement i.e they are not further deffered.
		
		this.referent = value;
		this.isReferenceTypePtr = isReferenceTypePtr;
	}
	
	/** Creates an instance of this Object where the referent is <code>value</code>. 
	 *  Used when serializing this pointer.
	 * 
	 * @param value null is acceptable
	 * @param isReferenceTypePtr true if a referent Identifier will not precede this ptr.
	 */
	public JIPointer(Object value, boolean isReferenceTypePtr)
	{
		if (value == null)
		{
			//since a null is being sent for a pointer , it has to be shown
			//as 0x0.
			value = new Integer(0);
			isReferenceTypePtr = true;
			isNull = true;
		}
		
//		if (value.getClass().equals(JIArray.class))
//		{
//			if (((JIArray)value).getDimensions() > 1)
//				throw new IllegalArgumentException("Only single dimension arrays accepted");
//		}
		
	//Should not defer since the enclosing struct,union,array will defer it by itself
	// this is important since , ptr to a ptr to a ptr (and more) will need to 
	//deserialize completely after the first deferement i.e they are not further deffered.
		
		this.referent = value;
		this.referentId = new Object().hashCode();
		this.isReferenceTypePtr = isReferenceTypePtr;
	}

	/** Sets the flags associated with the referent.
	 * 
	 * @exclude
	 * @param flags JIFlags only.
	 */
	void setFlags(int flags)
	{
		this.flags = flags; 
	}
	
	/**Creates an instance of this Object where the referent is <code>value</code>. 
	 * Used when serializing this pointer. This pointer is <b>not</b> of reference type.
	 * 
	 * @param value
	 */
	public JIPointer(Object value)
	{
		this(value,false);
	}

	/** Returns the referent encapsulated by this pointer.
	 * 
	 * @return
	 */
	public Object getReferent()
	{
		return isNull ? null:referent;
	}

	void encode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG)
	{
		
		FLAG = FLAG | flags;
		if (isNull)
		{
			JIUtil.serialize(ndr,Integer.class,new Integer(0),defferedPointers,FLAG);
			return;
		}
		//it is deffered or part of an array, this logic will not get called twice since the
		//deffered list will come in withb FLAG_NULL
		if (!isNull && (isDeffered || (FLAG & JIFlags.FLAG_REPRESENTATION_ARRAY) == JIFlags.FLAG_REPRESENTATION_ARRAY /*|| 
					(FLAG & JIFlags.FLAG_REPRESENTATION_NESTED_POINTER ) == JIFlags.FLAG_REPRESENTATION_NESTED_POINTER*/))
		{
			int referentIdToPut = referentId == -1 ? referent.hashCode() : referentId;
			JIUtil.serialize(ndr,Integer.class,new Integer(referentIdToPut),defferedPointers,FLAG);
			isDeffered = false;
			isReferenceTypePtr = true;
//			try{
			defferedPointers.add(this);
//			}catch(NullPointerException e)
//			{
//				int ni = 0;
//			}
			return;
		}
		
		if (!isNull && !isReferenceTypePtr)
		{
			int referentIdToPut = referentId == -1 ? referent.hashCode() : referentId;
			JIUtil.serialize(ndr,Integer.class,new Integer(referentIdToPut),defferedPointers,FLAG);
		}
		
		try {
			if (!isNull && referent.getClass().equals(JIVariant.class) && ((JIVariant)referent).isArray())
			{
				//write the length first before all elements
				//ndr.writeUnsignedLong(((Object[])(((JIVariant)referent).getObject())).length);
				JIUtil.serialize(ndr,Integer.class,new Integer(((Object[])(((JIVariant)referent).getObject())).length),defferedPointers,FLAG);
			}
		} catch (JIException e) {
			throw new JIRuntimeException(e.getErrorCode());
		}
		
		
		
		JIUtil.serialize(ndr,referent.getClass(),referent,defferedPointers,FLAG);
		
		
	}
	
	//class of type being decoded. If the type being expected is an array , the varType
	//should be the actual array type and not JIArray.
	JIPointer decode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG,Map additionalData)
	{
		//shallowClone();
		FLAG = FLAG | flags;
		
		JIPointer retVal = new JIPointer();
		retVal.setFlags(flags);
		retVal.isNull = isNull; 
		//retVal.isDeffered = isDeffered;
		if (isDeffered || (FLAG & JIFlags.FLAG_REPRESENTATION_ARRAY) == JIFlags.FLAG_REPRESENTATION_ARRAY
				/*|| (FLAG & JIFlags.FLAG_REPRESENTATION_NESTED_POINTER ) == JIFlags.FLAG_REPRESENTATION_NESTED_POINTER */)
		{
			retVal.referentId = ((Integer)JIUtil.deSerialize(ndr,Integer.class,defferedPointers,FLAG,additionalData)).intValue();
			retVal.referent = referent; //will only be the class or object
			if (retVal.referentId ==  0)
			{
				//null pointer
				// just return
				retVal.isNull = true;
				retVal.isDeffered = false;
				return retVal;
			}
			
			retVal.isDeffered = false;
			retVal.isReferenceTypePtr = true;
			defferedPointers.add(retVal);
			return retVal;
		}

		if (!isReferenceTypePtr)
		{
			//referentId = ndr.readUnsignedLong();
			retVal.referentId = ((Integer)JIUtil.deSerialize(ndr,Integer.class,defferedPointers,FLAG,additionalData)).intValue();
			retVal.referent = referent; //will only be the class or object
			if (retVal.referentId ==  0)
			{
				//null pointer
				// just return
				retVal.isNull = true;
				return retVal;
			}
		}
		
		
		retVal.referent = JIUtil.deSerialize(ndr,referent,defferedPointers,FLAG,additionalData);
		return retVal;
	}

	void setDeffered(boolean deffered)
	{
		isDeffered = deffered;
	}
	
	boolean getDeffered()
	{
		return isDeffered;
	}
	
	void setReferent(int referent)
	{
		this.referentId = referent;
	}
	
	/** Returns status whether this is a reference type pointer or not.
	 * 
	 * @return
	 */
	public boolean isReference()
	{
		return isReferenceTypePtr;
	}
	
	/** Returns the referent identifier.
	 * 
	 * @return
	 */
	public Integer getReferentIdentifier()
	{
		return new Integer(referentId);
	}
	
	/**
	 * @exclude
	 * @return
	 */
	public int getLength()
	{
		if (isNull)
		{
			return 4;
		}
		//4 for pointer
		if (referent instanceof Class)
		{
			return 4 + JIUtil.getLengthInBytes((Class)referent,referent,JIFlags.FLAG_NULL);	
		}
		return 4 + JIUtil.getLengthInBytes(referent.getClass(),referent,JIFlags.FLAG_NULL);
	}
	


	void replaceSelfWithNewPointer(JIPointer replacement)
	{
		this.isDeffered = replacement.isDeffered;
		this.isNull = replacement.isNull;
		this.isReferenceTypePtr = replacement.isReferenceTypePtr;
		this.referent = replacement.referent;
	}

	public boolean isNull() {
		return isNull;
	}
	
	void setValue(Object value)
	{
		referent = value;
	}
	
	public String toString()
	{
		return referent == null ? "[null]" : "[" + referent.toString() + "]";
	}
}
