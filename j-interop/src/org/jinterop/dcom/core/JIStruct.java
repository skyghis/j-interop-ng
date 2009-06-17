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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;

/** This class represents the <code>Struct</code> data type. <br>
 *
 * @since 1.0
 */
//for conformant and conformant+varying arrays the maxcount etc. should come at the begining of the struct.
public final class JIStruct implements Serializable {


	private static final long serialVersionUID = 7708214775854162549L;

	private List listOfMembers = new ArrayList();
	private List listOfMaxCounts = new ArrayList(); //keeps a list of Max counts for each array dimension, arrays
													//following one another are inserted in sequential order.

	private List listOfDimensions = new ArrayList();
	private boolean arrayAdded = false;

	public static final JIStruct MEMBER_IS_EMPTY = new JIStruct();

	/** Adds the object as a member of this structure. This object is appended to the list of members within. <br>
	 *
	 * @param member
	 */
	public void addMember(Object member) throws JIException
	{
		//null has to be allowed for members who would like to send null...NPE should not be thrown
		addMember(listOfMembers.size(),member);
	}

	/** Adds object as member to the index specified. <br>
	 *
	 * @param position Zero based index
	 * @param member
	 */
	public void addMember(int position,Object member) throws JIException
	{
		//null has to be allowed for members who would like to send null...NPE should not be thrown
		member = member == null ? new Integer(0) : member;

		Class memberClass = member.getClass();

		//An array has already been added , now a new member cannot be added
		if (arrayAdded && position == listOfMembers.size() && !memberClass.equals(JIArray.class))
		{
			throw new JIException(JIErrorCodes.JI_STRUCT_ARRAY_AT_END);
		}

		//arrays can only be the last element of this struct.
		if (memberClass.equals(JIArray.class))
		{
			//this condition will also allow that if another nested struct has an array , this new array is added at the
			// very end.
			if (position != listOfMembers.size())
			{
				throw new JIException(JIErrorCodes.JI_STRUCT_ARRAY_ONLY_AT_END);
			}

			arrayAdded = true;

			//Fixed arrays like char[50] are serialzed\deserialized in place itself.
			if (((JIArray)member).isConformant() || ((JIArray)member).isVarying())
			{
				//since there could be two arrays.
				listOfMaxCounts.addAll(((JIArray)member).getConformantMaxCounts());
				listOfDimensions.add(new Integer(((JIArray)member).getDimensions()));
			}
		}

		//struct part of another struct
		if (memberClass.equals(JIStruct.class))
		{
			//if this has an array then , this struct has to be the last member in the struct list.
			if (((JIStruct)member).arrayAdded && arrayAdded && position != (listOfMembers.size() - 1))
			{
				throw new JIException(JIErrorCodes.JI_STRUCT_INCORRECT_NESTED_STRUCT_POS);
			}

			if (arrayAdded && ((JIStruct)member).arrayAdded)
			{
				//means that we have to move the maxcount of the internal struct to this struct.
				arrayAdded = true;
				listOfMaxCounts.addAll(((JIStruct)member).getArrayMaxCounts());
				((JIStruct)member).listOfMaxCounts.clear(); //this is a "move" of max counts to the
															   //outer struct

				listOfDimensions.addAll(((JIStruct)member).listOfDimensions);
				((JIStruct)member).listOfDimensions.clear();

			}
			else
			if (!arrayAdded && ((JIStruct)member).arrayAdded)
			{
				if (position == listOfMembers.size())
				{
					arrayAdded = true;
					listOfMaxCounts.addAll(((JIStruct)member).getArrayMaxCounts());
					((JIStruct)member).listOfMaxCounts.clear(); //this is a "move" of max counts to the
																   //outer struct

					listOfDimensions.addAll(((JIStruct)member).listOfDimensions);
					((JIStruct)member).listOfDimensions.clear();
				}
				else
				{
					throw new JIException(JIErrorCodes.JI_STRUCT_INCORRECT_NESTED_STRUCT_POS2);
				}
			}


		}


		if (memberClass.equals(JIPointer.class) && !((JIPointer)member).isReference())
		{
			//send this to the end and put the place holder of the pointer here
			((JIPointer)member).setDeffered(true);
		}
		else if (memberClass.equals(JIVariant.class))
		{
			((JIVariant)member).setDeffered(true);
		}
		else
		if (memberClass.equals(JIString.class))
		{
			((JIString)member).setDeffered(true);
		}
		else
//		if (memberClass.equals(JIInterfacePointer.class))
//		{
//			((JIInterfacePointer)member).setDeffered(true);
//		}
//		else
//		if (memberClass.equals(JIDispatchImpl.class))
//		{
//			((JIComObjectImplWrapper)member).getInterfacePointer().setDeffered(true);
//		}
//		else
//		if (memberClass.equals(JIComObjectImpl.class))
//		{
//			((IJIComObject)member).getInterfacePointer().setDeffered(true);
//		}
		if (memberClass.equals(IJIComObject.class))
		{
			((IJIComObject)member).internal_setDeffered(true);
		}
		//else the pointer will be serialized "inplace".

		listOfMembers.add(position,member);
	}

	/** Removes the member from the specified index. <br>
	 *
	 * @param index
	 */
	public void removeMember(int index)
	{
		Object member = listOfMembers.remove(index);
		if (member instanceof JIArray)
		{
			//we need to remove it's max count values also.
			listOfMaxCounts.removeAll(((JIArray)member).getConformantMaxCounts());

		}
		else
		if (member instanceof JIStruct && ((JIStruct)member).arrayAdded)
		{
			//we need to remove it's max count values also.
			listOfMaxCounts.removeAll(((JIStruct)member).getArrayMaxCounts());
		}

		if (listOfMaxCounts.size() == 0)
		{
			arrayAdded = false;
		}
	}


	/** Returns all members as java.util.List. <br>
	 *
	 * @return
	 */
	public List getMembers()
	{
		return listOfMembers;
	}

	/** Retrieves the member at the specified index from the member list. <br>
	 *
	 * @param position Zero based index.
	 * @return
	 */
	public Object getMember(int position)
	{
		return listOfMembers.get(position);
	}

	/** Returns the total number of members.
	 *
	 * @return
	 */
	public int getSize()
	{
		return listOfMembers.size();
	}

	void encode(NetworkDataRepresentation ndr, List defferedPointers ,int FLAG)
	{
		//first write all Max counts and then the rest of the structs
		for (int i = 0;i < listOfMaxCounts.size();i++)
		{
			JIMarshalUnMarshalHelper.serialize(ndr,Integer.class,(Integer)listOfMaxCounts.get(i),null,FLAG);
		}

		int i = 0;
		while (i < listOfMembers.size())
		{
			Object o = listOfMembers.get(i);
			{
				if (o instanceof JIArray)
				{
					//if this array is conformant then reset it's conformancy , since the length would have been
					//written before.
					((JIArray)o).setConformant(false);
				}
				JIMarshalUnMarshalHelper.serialize(ndr,o.getClass(),o,defferedPointers,FLAG);
				if (o instanceof JIArray)
				{
					//noew reset this, so that next time when the same struct is written everything goes proper.
					((JIArray)o).setConformant(((JIArray)o).isConformant());
				}
			}
			i++;
		}
	}

	JIStruct decode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG,Map additionalData)
	{
		JIStruct retVal = new JIStruct();
		ArrayList listOfMaxCounts2 = new ArrayList();
		//first read all Max counts and then the rest of the structs
		for (int i = 0;i < listOfDimensions.size();i++)
		{
			for (int j = 0;j < ((Integer)listOfDimensions.get(i)).intValue();j++)
			{
				listOfMaxCounts2.add(JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,FLAG,additionalData));
			}
		}


		int i = 0;
		int j = 0; //index only for the conformant \ varying arrays
		while (i < listOfMembers.size())
		{
			Object o = listOfMembers.get(i);
			List maxCountTemp= null;
			if (o instanceof JIArray)
			{
				if (((JIArray)o).isConformant() || ((JIArray)o).isVarying())
				{
					//if this array is conformant then reset it's conformancy , since the length would have been
					//read before.
					((JIArray)o).setConformant(false);
					maxCountTemp = ((JIArray)o).getConformantMaxCounts();
					((JIArray)o).setMaxCountAndUpperBounds(listOfMaxCounts2.subList(j,((Integer)listOfDimensions.get(j)).intValue()));
					j++;
				}
			}
			Object o1 = JIMarshalUnMarshalHelper.deSerialize(ndr,o,defferedPointers,FLAG,additionalData);
			if (o instanceof JIArray)
			{
				if (((JIArray)o).isConformant() || ((JIArray)o).isVarying())
				{
					//now reset this, so that next time when the same struct is written everything goes proper.
					((JIArray)o).setConformant(((JIArray)o).isConformant());
					((JIArray)o).setMaxCountAndUpperBounds(maxCountTemp);
				}
			}
			try{
			retVal.addMember(o1);//listOfMembers.add(o);
			}catch(JIException e)
			{
				throw new JIRuntimeException(e.getErrorCode());
			}
			i++;
		}

		//do not copy other members since the addMember above will take care of all the conditions.
		return retVal;
	}

	int getLength()
	{
		int length = 0;
		int i = 0;
		while (i < listOfMembers.size())
		{
			Object o = listOfMembers.get(i);
			if (o instanceof Class)
			{
				length += JIMarshalUnMarshalHelper.getLengthInBytes((Class)o,o,JIFlags.FLAG_NULL);
			}
			else
			{
				length += JIMarshalUnMarshalHelper.getLengthInBytes(o.getClass(),o,JIFlags.FLAG_NULL);
			}
			i++;
		}
		return length;
	}

	List getArrayMaxCounts()
	{
		return listOfMaxCounts;
	}

	int getAlignment()
	{
		int alignment = 0;

		for (int i = 0;i < listOfMembers.size(); i++)
		{
			Class c = listOfMembers.get(i).getClass();
			boolean isClass = false;
			if (c.equals(Class.class))
			{
				isClass = true;
				c = (Class)listOfMembers.get(i);
			}

			if(c.equals(Integer.class) || c.equals(Float.class)
			|| c.equals(String.class) || c.equals(JIString.class)
			|| c.equals(JIPointer.class) || c.equals(JIUnsignedInteger.class)
			|| c.equals(JIVariant.class) )
			{
				//align with 4 bytes
				alignment = alignment <= 4 ? 4 : alignment;
			}
			else if (c.equals(Double.class) || c.equals(Date.class) || c.equals(Long.class))
			{
				//align with 8
				alignment = alignment <= 8 ? 8 : alignment;
			}
			else if (c.equals(Short.class) || c.equals(JIUnsignedShort.class))
			{
				//align with 2
				alignment = alignment <= 2 ? 2 : alignment;
			}
			else
			if (c.equals(JIStruct.class))
			{
				if (!isClass)
				{
					int align = ((JIStruct)listOfMembers.get(i)).getAlignment();
					alignment = alignment <= align ? align : alignment;
				}
				else
				{
					//incorrect entry !!!...
				}
			}
			else
			if (c.equals(JIUnion.class))
			{
				if (!isClass)
				{
					int align = ((JIUnion)listOfMembers.get(i)).getAlignment();
					alignment = alignment <= align ? align : alignment;
				}
				else
				{
					//incorrect entry !!!...
				}
			}
			if (alignment == 8)
				break;
		}

		return alignment;
	}

//	public String toString()
//	{
//		return "[" + listOfMembers + "]";
//	}

}
