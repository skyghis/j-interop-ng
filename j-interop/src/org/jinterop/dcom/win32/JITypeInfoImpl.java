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

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIUnion;
import org.jinterop.dcom.core.JIVariant;

import rpc.core.UUID;


/**
 * @exclude
 * @since 1.0
 *
 */
final class JITypeInfoImpl extends JIComObjectImplWrapper implements IJITypeInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 693590689068822035L;

	//IJIComObject comObject = null;
	//JIRemUnknown unknown = null;
	JITypeInfoImpl(IJIComObject comObject/*, JIRemUnknown unknown*/)
	{
		super(comObject);
		//this.comObject = comObject;
	}
	
	public FuncDesc getFuncDesc(int index) throws JIException
	{
		
		
		//prepare the GO here
		
		JICallObject obj = new JICallObject(comObject.getIpid(),true);
		obj.setOpnum(2);
		obj.addInParamAsInt(index,JIFlags.FLAG_NULL);
		
		//now to prepare out params
		JIStruct funcDescStruct = new JIStruct();
		funcDescStruct.addMember(Integer.class);
		funcDescStruct.addMember(new JIPointer(new JIArray(Integer.class,null,1,true)));
		//first read the pointer representation. Do not want to use funcdesc but only describe 
		//it. This should show the flexibility of the API.
		//TODO have to make a Pointer type which only reads the representation.
		obj.addOutParamAsObject(new JIPointer(funcDescStruct),JIFlags.FLAG_NULL);
		
		//CLEANLOCALSTORAGE --> this is wrong, since CLEANLOCALSTORAGE is a struct, but it has always
		//come null and even if something comes, I don't know which pointer PVOID stands for. 
		JIStruct cleanlocalstorage = new JIStruct();
		cleanlocalstorage.addMember(Integer.class);
		cleanlocalstorage.addMember(Integer.class);
		cleanlocalstorage.addMember(Integer.class);
		obj.addOutParamAsObject(new JIPointer(cleanlocalstorage),JIFlags.FLAG_NULL);
		
		
		
		
		//now for member id
		//obj.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		
		//now for lprgscode, Pointer to Conformant array of SCODEs (int)
		//obj.addOutParamAsObject(new Pointer(new JIArray(Integer.class,null,1,true)), JIFlags.FLAG_NULL);
		
		//now for lprgelemdescParam, Pointer to Conformant array of ELEMDESC (struct)
		//define the struct
		JIStruct elemDesc = new JIStruct();
		
		//SAFEARRAYBOUNDS
		JIStruct safeArrayBounds = new JIStruct();
		safeArrayBounds.addMember(Integer.class);
		safeArrayBounds.addMember(Integer.class);
		
		//arraydesc
		JIStruct arrayDesc = new JIStruct();
		//typedesc
		JIStruct typeDesc = new JIStruct();
		
		arrayDesc.addMember(typeDesc);
		arrayDesc.addMember(Short.class);
		arrayDesc.addMember(new JIArray(safeArrayBounds,new int[]{1},1,true));
		
		JIUnion forTypeDesc = new JIUnion(Short.class);
		JIPointer ptrToTypeDesc = new JIPointer(typeDesc);
		JIPointer ptrToArrayDesc = new JIPointer(arrayDesc);
		
		forTypeDesc.addMember(TypeDesc.VT_PTR,ptrToTypeDesc);
		forTypeDesc.addMember(TypeDesc.VT_SAFEARRAY,ptrToTypeDesc);
		forTypeDesc.addMember(TypeDesc.VT_CARRAY,ptrToArrayDesc);
		forTypeDesc.addMember(TypeDesc.VT_USERDEFINED,Integer.class);
		typeDesc.addMember(forTypeDesc);
		typeDesc.addMember(Short.class);//VARTYPE
		
		//PARAMDESC
		JIStruct paramDesc2 = new JIStruct();
		paramDesc2.addMember(Integer.class);
		paramDesc2.addMember(JIVariant.class);
		JIStruct paramDesc = new JIStruct();
		paramDesc.addMember(new JIPointer(paramDesc2,false));
		paramDesc.addMember(Short.class);
		
		elemDesc.addMember(typeDesc);
		elemDesc.addMember(paramDesc);
		
		funcDescStruct.addMember(new JIPointer(new JIArray(elemDesc,null,1,true)));
		//obj.addOutParamAsObject(new Pointer(new JIArray(elemDesc,null,1,true)), JIFlags.FLAG_NULL);
		
//		obj.addOutParamAsObject(Integer.class,JIFlags.FLAG_NULL);
//		obj.addOutParamAsObject(Integer.class,JIFlags.FLAG_NULL);
//		obj.addOutParamAsObject(Integer.class,JIFlags.FLAG_NULL);
//		
//		obj.addOutParamAsObject(Short.class,JIFlags.FLAG_NULL);
//		obj.addOutParamAsObject(Short.class,JIFlags.FLAG_NULL);
//		
//		obj.addOutParamAsObject(Short.class,JIFlags.FLAG_NULL);
//		obj.addOutParamAsObject(Short.class,JIFlags.FLAG_NULL);
//		
//		obj.addOutParamAsObject(elemDesc,JIFlags.FLAG_NULL);
//		obj.addOutParamAsObject(Short.class,JIFlags.FLAG_NULL);
		
		funcDescStruct.addMember(Integer.class);
		funcDescStruct.addMember(Integer.class);
		funcDescStruct.addMember(Integer.class);
		
		funcDescStruct.addMember(Short.class);
		funcDescStruct.addMember(Short.class);
		
		funcDescStruct.addMember(Short.class);
		funcDescStruct.addMember(Short.class);
		
		funcDescStruct.addMember(elemDesc);
		funcDescStruct.addMember(Short.class);
		
		
		Object[] result = comObject.call(obj);
		FuncDesc funcDesc = new FuncDesc((JIPointer)result[0]);
		return funcDesc;
	}
	
	public TypeAttr getTypeAttr() throws JIException
	{
		JICallObject obj = new JICallObject(comObject.getIpid(),true);
		obj.setOpnum(0);
		
		
		
		JIStruct typeAttr = new JIStruct();
		JIPointer mainPtr = new JIPointer(typeAttr);
		obj.addOutParamAsObject(mainPtr,JIFlags.FLAG_NULL);
		
		//CLEANLOCALSTORAGE --> this is wrong, since CLEANLOCALSTORAGE is a struct, but it has always
		//come null and even if something comes, I don't know which pointer PVOID stands for. 
		obj.addOutParamAsObject(new JIPointer(Integer.class),JIFlags.FLAG_NULL);
		
		typeAttr.addMember(UUID.class);
		typeAttr.addMember(Integer.class);
		typeAttr.addMember(Integer.class);
		
		typeAttr.addMember(Integer.class);
		typeAttr.addMember(Integer.class);
		
		typeAttr.addMember(new JIPointer(new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)));
		
		typeAttr.addMember(Integer.class);
		
		typeAttr.addMember(Integer.class);
		
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		typeAttr.addMember(Short.class);
		
		JIStruct typeDesc = new JIStruct();
		JIStruct arrayDesc = new JIStruct();
		JIStruct safeArrayBounds = new JIStruct();
		
		safeArrayBounds.addMember(Integer.class);
		safeArrayBounds.addMember(Integer.class);
		
		arrayDesc.addMember(typeDesc);
		arrayDesc.addMember(Short.class);
		arrayDesc.addMember(new JIArray(safeArrayBounds,new int[]{1},1,true));
		
		JIUnion forTypeDesc = new JIUnion(Short.class);
		JIPointer ptrToTypeDesc = new JIPointer(typeDesc);
		JIPointer ptrToArrayDesc = new JIPointer(arrayDesc);
		
		forTypeDesc.addMember(TypeDesc.VT_PTR,ptrToTypeDesc);
		forTypeDesc.addMember(TypeDesc.VT_SAFEARRAY,ptrToTypeDesc);
		forTypeDesc.addMember(TypeDesc.VT_CARRAY,ptrToArrayDesc);
		forTypeDesc.addMember(TypeDesc.VT_USERDEFINED,Integer.class);
		typeDesc.addMember(forTypeDesc);
		typeDesc.addMember(Short.class);//VARTYPE
		
		typeAttr.addMember(typeDesc);
		
		
		JIStruct paramDesc = new JIStruct();
		paramDesc.addMember(new JIPointer(JIVariant.class,false));
		paramDesc.addMember(Short.class);
		
		typeAttr.addMember(paramDesc);
		
		Object[] result = comObject.call(obj);
		TypeAttr attr = new TypeAttr((JIPointer)result[0]);
		return attr;
	}
	
	public Object[] getContainingTypeLib() throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.addOutParamAsObject(IJIComObject.class,JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(Integer.class,JIFlags.FLAG_NULL);
		callObject.setOpnum(15);
		Object[] result = comObject.call(callObject);
		Object[] retVal = new Object[2];
		retVal[0] = (IJITypeLib) JIComFactory.narrowInstance((IJIComObject)result[0]);
		retVal[1] = result[1];
		return retVal;
	}
	
//	HRESULT GetDllEntry( 
//			  MEMBERID  memid,                
//			  INVOKEKIND  invKind,            
//			  BSTR FAR*  pBstrDllName,        
//			  BSTR FAR*  pBstrName,           
//			  unsigned short FAR*  pwOrdinal  
//			);
	public Object[] getDllEntry(int memberId, int invKind) throws JIException
	{
		if (invKind != INVOKEKIND.INVOKE_FUNC.intValue() && invKind != INVOKEKIND.INVOKE_PROPERTYGET.intValue() 
				&& invKind != INVOKEKIND.INVOKE_PROPERTYPUTREF.intValue() && invKind != INVOKEKIND.INVOKE_PROPERTYPUT.intValue())
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.E_INVALIDARG));
		}
		
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.addInParamAsInt(memberId,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(invKind,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(1,JIFlags.FLAG_NULL);//refPtrFlags , as per the oaidl.idl...
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(Short.class,JIFlags.FLAG_NULL);
		callObject.setOpnum(10);
		return comObject.call(callObject);
	}
			 
//	HRESULT GetDocumentation( 
//			  MEMBERID  memid,                     
//			  BSTR FAR*  pBstrName,                
//			  BSTR FAR*  pBstrDocString,           
//			  unsigned long FAR*  pdwHelpContext,  
//			  BSTR FAR*  pBstrHelpFile             
//			);
	public Object[] getDocumentation(int memberId) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.addInParamAsInt(memberId,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(0xb,JIFlags.FLAG_NULL);//refPtrFlags , as per the oaidl.idl...
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(Integer.class,JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		callObject.setOpnum(9);
		return comObject.call(callObject);
	}
	
	public VarDesc getVarDesc(int index) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(3);
		callObject.addInParamAsInt(index,JIFlags.FLAG_NULL);
		
		//now build the vardesc
		JIStruct vardesc = new JIStruct();
		callObject.addOutParamAsObject(new JIPointer(vardesc),JIFlags.FLAG_NULL);
		//CLEANLOCALSTORAGE --> this is wrong, since CLEANLOCALSTORAGE is a struct, but it has always
		//come null and even if something comes, I don't know which pointer PVOID stands for. 
		JIStruct cleanlocalstorage = new JIStruct();
		cleanlocalstorage.addMember(Integer.class);
		cleanlocalstorage.addMember(Integer.class);
		cleanlocalstorage.addMember(Integer.class);
		callObject.addOutParamAsObject(new JIPointer(cleanlocalstorage),JIFlags.FLAG_NULL);
		
		vardesc.addMember(Integer.class);//memberid
		vardesc.addMember(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)));
		
		JIUnion union = new JIUnion(Integer.class);
		union.addMember(new Integer(VarDesc.VAR_PERINSTANCE),Integer.class);
		union.addMember(new Integer(VarDesc.VAR_DISPATCH),Integer.class);
		union.addMember(new Integer(VarDesc.VAR_STATIC),Integer.class);
		union.addMember(new Integer(VarDesc.VAR_CONST),JIVariant.class);
		vardesc.addMember(union);
		
		JIStruct elemDesc = new JIStruct();
		
		//SAFEARRAYBOUNDS
		JIStruct safeArrayBounds = new JIStruct();
		safeArrayBounds.addMember(Integer.class);
		safeArrayBounds.addMember(Integer.class);
		
		//arraydesc
		JIStruct arrayDesc = new JIStruct();
		//typedesc
		JIStruct typeDesc = new JIStruct();
		
		arrayDesc.addMember(typeDesc);
		arrayDesc.addMember(Short.class);
		arrayDesc.addMember(new JIArray(safeArrayBounds,new int[]{1},1,true));
		
		JIUnion forTypeDesc = new JIUnion(Short.class);
		JIPointer ptrToTypeDesc = new JIPointer(typeDesc);
		JIPointer ptrToArrayDesc = new JIPointer(arrayDesc);
		
		forTypeDesc.addMember(TypeDesc.VT_PTR,ptrToTypeDesc);
		forTypeDesc.addMember(TypeDesc.VT_SAFEARRAY,ptrToTypeDesc);
		forTypeDesc.addMember(TypeDesc.VT_CARRAY,ptrToArrayDesc);
		forTypeDesc.addMember(TypeDesc.VT_USERDEFINED,Integer.class);
		typeDesc.addMember(forTypeDesc);
		typeDesc.addMember(Short.class);//VARTYPE
		
		//PARAMDESC
		JIStruct paramDesc2 = new JIStruct();
		paramDesc2.addMember(Integer.class);
		paramDesc2.addMember(JIVariant.class);
		JIStruct paramDesc = new JIStruct();
		paramDesc.addMember(new JIPointer(paramDesc2,false));
		paramDesc.addMember(Short.class);
//		JIStruct paramDesc = new JIStruct();
//		paramDesc.addMember(new JIPointer(JIVariant.class,false));
//		//paramDesc.addMember(JIVariant.class);
//		paramDesc.addMember(Short.class);
		
		elemDesc.addMember(typeDesc);
		elemDesc.addMember(paramDesc);
		
		vardesc.addMember(elemDesc);
		vardesc.addMember(Short.class);
		vardesc.addMember(Integer.class);
		
		Object[] result = comObject.call(callObject);
		
		return new VarDesc((JIPointer)result[0]);
		
	}
	
	public Object[] getNames(int memberId, int maxNames) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(4);
		
		//for experiment only
//		JIArray arry = new JIArray(new Integer[]{new Integer(100),new Integer(200)},true);
//		JIStruct struct = new JIStruct();
//		struct.addMember(Short.valueOf((short)86));
//		struct.addMember(arry);
//		callObject.addInParamAsStruct(struct,JIFlags.FLAG_NULL);
		
		
		callObject.addInParamAsInt(memberId,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(maxNames,JIFlags.FLAG_NULL);
		
		callObject.addOutParamAsObject(new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),null,1,true,true),JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		
		return comObject.call(callObject);
	}
	
	public int getRefTypeOfImplType(int index) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(5);
		callObject.addInParamAsInt(index,JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		return ((Integer)(((Object[])comObject.call(callObject))[0])).intValue();
	}
	
	public int getImplTypeFlags(int index) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(6);
		callObject.addInParamAsInt(index,JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		return ((Integer)(((Object[])comObject.call(callObject))[0])).intValue();
	}
	
	public IJITypeInfo getRefTypeInfo(int hrefType) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(11);
		callObject.addInParamAsInt(hrefType,JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		return (IJITypeInfo) JIComFactory.narrowInstance((IJIComObject)result[0]);
	}
	
//	public int[] getIdOfNames(String[] names) throws JIException
//	{
//		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
//		callObject.setOpnum(7);
//
//		JIPointer[] pointers = new JIPointer[names.length];
//		
//		for (int i = 0;i < names.length;i++)
//		{
//			if (names[i] == null || names[i].trim().equals(""))
//			{
//				throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_DISP_INCORRECT_VALUE_FOR_GETIDNAMES));
//			}
//			pointers[i] = new JIPointer(new JIString(names[i].trim(),JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
//		}
//		
//		
//		JIArray array = new JIArray(pointers,true);
//		JIArray arrayOut = new JIArray(Integer.class,null,1,true);
//
//		callObject.addInParamAsArray(new JIArray(pointers,true),JIFlags.FLAG_NULL);
//		callObject.addInParamAsInt(names.length,JIFlags.FLAG_NULL);
//		callObject.addOutParamAsObject(arrayOut,JIFlags.FLAG_NULL);
//		
//		Object[] result = comObject.call(callObject);
//	
//		JIArray arrayOfResults = (JIArray)result[0];
//		Integer[] arrayOfDispIds = (Integer[])arrayOfResults.getArrayInstance();
//		int[] retVal = new int[names.length];
//		
//		for (int i = 0;i < names.length;i++)
//		{
//			retVal[i] = arrayOfDispIds[i].intValue(); 
//		}
//		
//		return retVal;
//
//	}
	
	public IJIComObject createInstance(String riid) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(13);
		
		callObject.addInParamAsUUID(riid,JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		return JIComFactory.narrowInstance((IJIComObject)result[0]);
	}
	
	public JIString getMops(int memberId) throws JIException
	{
		JICallObject callObject = new JICallObject(comObject.getIpid(),true);
		callObject.setOpnum(14);
		callObject.addInParamAsInt(memberId,JIFlags.FLAG_NULL);
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		Object[] result = comObject.call(callObject);
		return (JIString)result[0];
	}
}
