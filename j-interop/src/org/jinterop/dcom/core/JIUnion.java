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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;


/**<p> This class represents the <code>Union</code> data type. Its usage is dictated by the discriminant
 * which acts as a "switch" to select the correct member to be serialized\deserialzed. <br>
 * 
 * Sample Usage :-
 * 
 * <br>
 * <code>
 * 	JIUnion forTypeDesc = new JIUnion(Short.class); <br>
 *	JIPointer ptrToTypeDesc = new JIPointer(typeDesc); <br>
 *	JIPointer ptrToArrayDesc = new JIPointer(arrayDesc); <br>
 *	forTypeDesc.addMember(TypeDesc.VT_PTR,ptrToTypeDesc); <br>
 *	forTypeDesc.addMember(TypeDesc.VT_SAFEARRAY,ptrToTypeDesc); <br>
 *	forTypeDesc.addMember(TypeDesc.VT_CARRAY,ptrToArrayDesc); <br>
 *	forTypeDesc.addMember(TypeDesc.VT_USERDEFINED,Integer.class); <br>
 * </code>
 *
 *	The TypeDesc.VT_PTR is an Integer and is used as a Discriminant to select ptrTypeDesc, TypeDesc.VT_CARRAY 
 *  chooses ptrArrayDesc. <br>
 *  </p>
 *  	
 * 
 * @since 1.0
 */
public final class JIUnion implements Serializable {

	
	private static final long serialVersionUID = -3353313619137076876L;
	private HashMap dsVsMember = new HashMap();
	private Class discriminantClass = null;
	//private int length = 0;
	//private int lengthOfDisc = 0;
	//private Union clone = null;
	private JIUnion() {}
	
	/** Creates the JIUnion object with discriminant type specified. Used only during deserializing 
	 *  the union. Can only be of the type Integer,Short,Boolean or Character. <br>
	 * 
	 * @param discriminantClass
	 */
	public JIUnion(Class discriminantClass)
	{
		//the discriminant can only be a int, boolean or char
		
		if (!discriminantClass.equals(Integer.class) && !discriminantClass.equals(Short.class) 
		&& !discriminantClass.equals(Boolean.class) && !discriminantClass.equals(Character.class))
		{
			//has to be from one of these. Rule from IDL.
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNION_INCORRECT_DISC));
		}
		
		this.discriminantClass = discriminantClass;
		
	}
	
	/** Adds a member to this Union. The member is distinguished using the discriminant. <br>
	 * 
	 * @param discriminant
	 * @param member
	 * @throws JIException
	 */
	public void addMember(Object discriminant, Object member) throws JIException
	{
		if (discriminant == null || member == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNION_NULL_DISCRMINANT));
		}
		
		if (!discriminant.getClass().equals(discriminantClass))
		{
			throw new JIException(JIErrorCodes.JI_UNION_DISCRMINANT_MISMATCH);
		}
		
		if (member.getClass().equals(JIPointer.class) && !((JIPointer)member).isReference())
		{
			((JIPointer)member).setDeffered(true);
		}
		else
		if (member.getClass().equals(JIString.class))
		{
			((JIPointer)member).setDeffered(true);
		}
		
		dsVsMember.put(discriminant,member);
	}
	
	/** Adds a member to this Union. The member is distinguished using the discriminant. <br>
	 * 
	 * @param discriminant
	 * @param member
	 * @throws JIException
	 */
	//used both for reading and writing
	public void addMember(Object discriminant, JIStruct member) throws JIException
	{
		if (discriminant == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNION_NULL_DISCRMINANT));
		}
		
		if (!discriminant.getClass().equals(discriminantClass))
		{
			throw new JIException(JIErrorCodes.JI_UNION_DISCRMINANT_MISMATCH);
		}
		
		if (member == null)
		{
			member = JIStruct.MEMBER_IS_EMPTY;
		}
		
		dsVsMember.put(discriminant,member);
		//do not need a seperate list of pointers like the struct , since based on the discriminant only 1 pointer
		//(if present) can be deserialized\serialized.
 	}

	/**Removes the entry , identified by it's discriminant from the Parameter list of the union. <br>
	 * 
	 * @param discriminant
	 */
	public void removeMember(Object discriminant)
	{
		dsVsMember.remove(discriminant);
	}
	
	/** Returns the discriminant Vs there members Map. <br> 
	 * 
	 * @return
	 */
	public Map getMembers()
	{
		return dsVsMember;
	}
	
	void encode(NetworkDataRepresentation ndr, List listOfDefferedPointers,int FLAGS)
	{
		if (dsVsMember.size() == 0 || dsVsMember.size() > 1)
		{
			throw new JIRuntimeException(JIErrorCodes.JI_UNION_DISCRMINANT_SERIALIZATION_ERROR);
		}
		
		//first write the discriminant and then the member
		Iterator keys = dsVsMember.keySet().iterator();
		JIUtil.serialize(ndr,discriminantClass,keys.next(),listOfDefferedPointers,FLAGS);
		
		keys = dsVsMember.values().iterator();
		Object value = keys.next();
		
		//will not write empty union members
		if (!value.equals(JIStruct.MEMBER_IS_EMPTY))
		{
			JIUtil.serialize(ndr,value.getClass(),value,listOfDefferedPointers,FLAGS);
		}
		
	}
	
	JIUnion decode(NetworkDataRepresentation ndr, List listOfDefferedPointers,int FLAGS,Map additionalData)
	{
		//first read discriminant, and then call the appropriate deserializer of the member
		if (dsVsMember.size() == 0)
		{
			throw new JIRuntimeException(JIErrorCodes.JI_UNION_DISCRMINANT_DESERIALIZATION_ERROR);
		}
		
		//shallowClone();
		//first write the discriminant and then the member
		JIUnion retVal = new JIUnion();
		retVal.discriminantClass = discriminantClass;
		
		Object key = JIUtil.deSerialize(ndr,discriminantClass,listOfDefferedPointers,FLAGS,additionalData);
		
		//next thing to be deserialized is the member
		Object value = dsVsMember.get(key);
		
		//should allow null since this could be a "default"
		if (value == null)
		{
			value = JIStruct.MEMBER_IS_EMPTY;
		}
		
		//will not write empty union members
		if (!value.equals(JIStruct.MEMBER_IS_EMPTY))
		{
			retVal.dsVsMember.put(key,JIUtil.deSerialize(ndr,value,listOfDefferedPointers,FLAGS,additionalData));
		}
		else
		{
			retVal.dsVsMember.put(key,value);
		}
		
		return retVal;
	}
	
	int getLength()
	{
		int length = 0;
		Iterator itr = dsVsMember.keySet().iterator();
		while (itr.hasNext())
		{
			Object o = itr.next();
			int temp = JIUtil.getLengthInBytes(o.getClass(),o,JIFlags.FLAG_NULL);
			length = length > temp ? length : temp; //length of the largest member
		}
				
		return length + JIUtil.getLengthInBytes(discriminantClass,null,JIFlags.FLAG_NULL);
	}
	
	int getAlignment() 
	{	
		int alignment = 0;
	    
		if(discriminantClass.equals(Integer.class))
		{
			//align with 4 bytes
			alignment =  4 ; 
		}else if (discriminantClass.equals(Short.class))
		{
			//align with 2
			alignment = 2 ;
		}
		
		return alignment;
	}

//	public String toString()
//	{
//		return  "[" + dsVsMember +  "]";
//	}
}
