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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ndr.NdrException;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIComVersion;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;

import com.iwombat.foundation.IdentifierFactory;
import com.iwombat.util.GUIDUtil;

final class JIOrpcThis implements Serializable {


	private static final long serialVersionUID = 9148006530957254901L;
	private static ThreadLocal cidForCallback = new ThreadLocal();

	private int flags = 0;
	private JIOrpcExtentArray[] arry = null;
	private JIComVersion version = JISystem.getCOMVersion();
	private String cid = null;

	public JIOrpcThis()
	{
		cid = GUIDUtil.guidStringFromHexString(IdentifierFactory.createUniqueIdentifier().toHexString());
	}

	public JIOrpcThis(UUID casualityIdentifier)
	{
		cid = casualityIdentifier.toString();
	}

	public void setORPCFlags(int flags) {
		this.flags = flags;
	}

	public int getORPCFlags() {
		return flags;
	}

	public void setExtentArray(JIOrpcExtentArray[] arry) {
		this.arry = arry;
	}

	public JIOrpcExtentArray[] getExtentArray() {
		return arry;
	}

	public String getCasualityIdentifier()
	{
		return cid;
	}

	public void encode (NetworkDataRepresentation ndr)
	{
		ndr.writeUnsignedShort(version.getMajorVersion()); //COM Major version
		ndr.writeUnsignedShort(version.getMinorVersion()); //COM minor version
		ndr.writeUnsignedLong(flags); // No Flags
		ndr.writeUnsignedLong(0); // Reserved ...always 0.

		//the order here is important since the cid is always filled from the ctor hence will never be null.
		String cid2 = cidForCallback.get() == null ? cid : (String)cidForCallback.get();
//		System.out.println(cid2);
		rpc.core.UUID uuid = new rpc.core.UUID(cid2);
		try{
			uuid.encode(ndr,ndr.getBuffer());
		}catch(NdrException e)
		{
			JISystem.getLogger().throwing("JIOrpcThis","encode",e);
		}

		int i = 0;
		if (arry != null && arry.length != 0)
		{
			ndr.writeUnsignedLong(arry.length);
			ndr.writeUnsignedLong(0);
			while (i < arry.length)
			{
				JIOrpcExtentArray arryy = arry[i];
				uuid = new rpc.core.UUID(arryy.getGUID());
				try{
					uuid.encode(ndr,ndr.getBuffer());
				}catch(NdrException e)
				{
					JISystem.getLogger().throwing("JIOrpcThis","encode",e);
				}

				ndr.writeUnsignedLong(arryy.getSizeOfData());
				ndr.writeOctetArray(arryy.getData(),0,arryy.getSizeOfData());
				i++;
			}
		}
		else
		{
			ndr.writeUnsignedLong(0);
		}
	}

	static JIOrpcThis decode(NetworkDataRepresentation ndr)
	{
		JIOrpcThis retval = new JIOrpcThis();
		Map map = new HashMap();
		int majorVersion = ((Short)(JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null, JIFlags.FLAG_NULL,map))).intValue();
		int minorVersion = ((Short)(JIMarshalUnMarshalHelper.deSerialize(ndr,Short.class,null, JIFlags.FLAG_NULL,map))).intValue();

		retval.version = new JIComVersion(majorVersion,minorVersion);
		retval.flags =  ((Integer)(JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null, JIFlags.FLAG_NULL,map))).intValue();

		JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null, JIFlags.FLAG_NULL,map);//reserved.

		rpc.core.UUID uuid = new rpc.core.UUID();
		try{
			uuid.decode(ndr,ndr.getBuffer());
			retval.cid = uuid.toString();
		}catch(NdrException e)
		{
			JISystem.getLogger().throwing("JIOrpcThis","decode",e);
		}


		JIStruct orpcextentarray = new JIStruct();
		try {
		//create the orpcextent struct
		/*
		 *  typedef struct tagORPC_EXTENT
    {
        GUID                    id;          // Extension identifier.
        unsigned long           size;        // Extension size.
        [size_is((size+7)&~7)]  byte data[]; // Extension data.
    } ORPC_EXTENT;

		 */

			JIStruct orpcextent = new JIStruct();
			orpcextent.addMember(UUID.class);
			orpcextent.addMember(Integer.class); //length
			orpcextent.addMember(new JIArray(Byte.class,null,1,true));
		//create the orpcextentarray struct
		/*
		 *    typedef struct tagORPC_EXTENT_ARRAY
    {
        unsigned long size;     // Num extents.
        unsigned long reserved; // Must be zero.
        [size_is((size+1)&~1,), unique] ORPC_EXTENT **extent; // extents
    } ORPC_EXTENT_ARRAY;

		 */


			orpcextentarray.addMember(Integer.class);
			orpcextentarray.addMember(Integer.class);
			//this is since the pointer is [unique]
			orpcextentarray.addMember(new JIPointer(new JIArray(new JIPointer(orpcextent),null,1,true)));
		} catch (JIException e1) {
			//this won't fail...i am certain :)...
		}

		List listOfDefferedPointers = new ArrayList();
		JIPointer orpcextentarrayptr = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,new JIPointer(orpcextentarray),listOfDefferedPointers,JIFlags.FLAG_NULL,map);
		int x = 0;

		while (x < listOfDefferedPointers.size())
		{
			ArrayList newList = new ArrayList();
			JIPointer replacement = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,(JIPointer)listOfDefferedPointers.get(x),newList,JIFlags.FLAG_NULL,map);
			((JIPointer)listOfDefferedPointers.get(x)).replaceSelfWithNewPointer(replacement); //this should replace the value in the original place.
			x++;
			listOfDefferedPointers.addAll(x,newList);
		}

		ArrayList extentArrays = new ArrayList();
		//now read whether extend array exists or not
		if (!orpcextentarrayptr.isNull())
		{
			JIPointer[] pointers = (JIPointer[])((JIArray)((JIPointer)((JIStruct)orpcextentarrayptr.getReferent()).getMember(2)).getReferent()).getArrayInstance();
			for (int i = 0;i < pointers.length;i++)
			{
				if (pointers[i].isNull())
					continue;

				JIStruct orpcextent2 = (JIStruct)pointers[i].getReferent();
				Byte[] byteArray = (Byte[])((JIArray)orpcextent2.getMember(2)).getArrayInstance();

				extentArrays.add(new JIOrpcExtentArray(((UUID)orpcextent2.getMember(0)).toString(),byteArray.length,byteArray));
			}

		}

		retval.arry = (JIOrpcExtentArray[])extentArrays.toArray(new JIOrpcExtentArray[extentArrays.size()]);

		//decode can only be executed incase of a request made from the server side in case of a callback. so the thread making this
		//callback will store the cid from the decode operation in the threadlocal variable. In case an encode is performed using the
		//same thread then we know that this is a nested call. Hence will replace the cid with the thread local cid. For the calls being in
		//case of encode this value will not be used if the encode thread is of the client and not of JIComOxidRuntimeHelper.
		cidForCallback.set(retval.cid);
		return retval;
	}

}
