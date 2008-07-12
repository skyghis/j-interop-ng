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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.impls.automation.IJIDispatch;

/**<p>Class representing the <code>VARIANT</code> datatype. 
 * <p>Please use the <code>byRef</code> flag based constructors for <i>by reference</i>
 * parameters in COM calls. For <code>[optional]</code> parameters use the 
 * {@link #OPTIONAL_PARAM()}
 * <p>In case of direct calls to COM server using <code>JICallBuilder</code>, if the <code>byRef</code> flag is set then  
 * that variant should also be added as the <code>[out]</code> parameter in the <code>JICallBuilder</code>. 
 * For developers using the <code>IJIDispatch </code> this is not required and variant would be returned back to them 
 * via <code>JIVariant[]</code> associated with <code>IJIDispatch</code> apis. 
 * <p>
 * 
 * An <b>important</b> note for <code>Boolean</code> Arrays (<code>JIArray</code> of <code>Boolean</code>), 
 * please set the <code>JIFlag.FLAG_REPRESENTATION_VARIANT_BOOL</code> using the {@link #setFlag(int)}
 * method before making a call on this object. This is required since in DCOM ,  <code>VARIANT_BOOL</code> are 2 bytes 
 * and standard <code>boolean</code>s are 1 byte in length.
 * </p>
 * @since 1.0
 */
public final class JIVariant implements Serializable {

	private static final long serialVersionUID = 5101290038004040628L;
	private static final class EMPTY {}
	private static final class NULL  {}
	private static final class SCODE {}
	
	public static final int VT_NULL 			 = 0x00000001;
	public static final int VT_EMPTY 			 = 0x00000000;
	public static final int VT_I4 				 = 0x00000003;
	public static final int VT_UI1				 = 0x00000011;
	public static final int VT_I2 				 = 0x00000002;
	public static final int VT_R4 				 = 0x00000004;
	public static final int VT_R8 				 = 0x00000005;
	public static final int VT_VARIANT			 = 0x0000000c;
	public static final int VT_BOOL 			 = 0x0000000b;
	public static final int VT_ERROR 			 = 0x0000000a;
	public static final int VT_CY 				 = 0x00000006;
	public static final int VT_DATE 			 = 0x00000007;
	public static final int VT_BSTR 			 = 0x00000008;
	public static final int VT_UNKNOWN 			 = 0x0000000d;
	public static final int VT_DECIMAL 			 = 0x0000000e;
	public static final int VT_DISPATCH 		 = 0x00000009;
	public static final int VT_ARRAY			 = 0x00002000;
	public static final int VT_BYREF  			 = 0x00004000;
	public static final int VT_BYREF_VT_UI1 	 = VT_BYREF|VT_UI1;//0x00004011;
	public static final int VT_BYREF_VT_I2 	 	 = VT_BYREF|VT_I2;//0x00004002;
	public static final int VT_BYREF_VT_I4 	 	 = VT_BYREF|VT_I4;//0x00004003;
	public static final int VT_BYREF_VT_R4 	 	 = VT_BYREF|VT_R4;//0x00004004;
	public static final int VT_BYREF_VT_R8 	 	 = VT_BYREF|VT_R8;//0x00004005;
	public static final int VT_BYREF_VT_BOOL 	 = VT_BYREF|VT_BOOL;//0x0000400b;
	public static final int VT_BYREF_VT_ERROR 	 = VT_BYREF|VT_ERROR;//0x0000400a;
	public static final int VT_BYREF_VT_CY 	 	 = VT_BYREF|VT_CY;//0x00004006;
	public static final int VT_BYREF_VT_DATE 	 = VT_BYREF|VT_DATE;//0x00004007;
	public static final int VT_BYREF_VT_BSTR 	 = VT_BYREF|VT_BSTR;//0x00004008;
	public static final int VT_BYREF_VT_UNKNOWN  = VT_BYREF|VT_UNKNOWN;//0x0000400d;
	public static final int VT_BYREF_VT_DISPATCH = VT_BYREF|VT_DISPATCH;//0x00004009;
	public static final int VT_BYREF_VT_ARRAY 	 = VT_BYREF|VT_ARRAY;//0x00006000;
	public static final int VT_BYREF_VT_VARIANT  = VT_BYREF|VT_VARIANT;//0x0000400c;
	
	public static final int VT_I1 				 = 0x00000010;
	public static final int VT_UI2 				 = 0x00000012;
	public static final int VT_UI4 				 = 0x00000013;
	public static final int VT_I8				 = 0x00000014;
	public static final int VT_INT 				 = 0x00000016;
	public static final int VT_UINT 			 = 0x00000017;
	public static final int VT_BYREF_VT_DECIMAL  = VT_BYREF|VT_DECIMAL;//0x0000400e;
	public static final int VT_BYREF_VT_I1 		 = VT_BYREF|VT_I1;//0x00004010;
	public static final int VT_BYREF_VT_UI2 	 = VT_BYREF|VT_UI2;//0x00004012;
	public static final int VT_BYREF_VT_UI4 	 = VT_BYREF|VT_UI4;//0x00004013;
	public static final int VT_BYREF_VT_I8		 = VT_BYREF|VT_I8;//0x00004014;
	public static final int VT_BYREF_VT_INT 	 = VT_BYREF|VT_INT;//0x00004016;
	public static final int VT_BYREF_VT_UINT 	 = VT_BYREF|VT_UINT;//0x00004017;

	public static final int FADF_AUTO       = 0x0001;  /* array is allocated on the stack */
	public static final int FADF_STATIC     = 0x0002;  /* array is staticly allocated */
	public static final int FADF_EMBEDDED   = 0x0004;  /* array is embedded in a structure */
	public static final int FADF_FIXEDSIZE  = 0x0010;  /* may not be resized or reallocated */
	public static final int FADF_RECORD     = 0x0020;  /* an array of records */
	public static final int FADF_HAVEIID    = 0x0040;  /* with FADF_DISPATCH, FADF_UNKNOWN */
	                                        /* array has an IID for interfaces */
	public static final int FADF_HAVEVARTYPE= 0x0080;  /* array has a VT type */
	public static final int FADF_BSTR       = 0x0100;  /* an array of BSTRs */
	public static final int FADF_UNKNOWN    = 0x0200;  /* an array of IUnknown* */
	public static final int FADF_DISPATCH   = 0x0400;  /* an array of IDispatch* */
	public static final int FADF_VARIANT    = 0x0800;  /* an array of VARIANTs */
	public static final int FADF_RESERVED   = 0xF008;  /* reserved bits */

	
	static HashMap supportedTypes = new HashMap();
	static HashMap supportedTypes_classes = new HashMap();
	static
	{
		supportedTypes.put(Object.class,new Integer(VT_VARIANT));
		supportedTypes.put(JIVariant.class,new Integer(VT_VARIANT));
		supportedTypes.put(Integer.class,new Integer(VT_I4));
		supportedTypes.put(JIUnsignedInteger.class,new Integer(VT_UI4));
		supportedTypes.put(Float.class,new Integer(VT_R4));
		supportedTypes.put(Boolean.class,new Integer(VT_BOOL));
		supportedTypes.put(Double.class,new Integer(VT_R8));
		supportedTypes.put(Short.class,new Integer(VT_I2));
		supportedTypes.put(JIUnsignedShort.class,new Integer(VT_UI2));
		supportedTypes.put(Byte.class,new Integer(VT_I1));
		supportedTypes.put(Character.class,new Integer(VT_I1));
		supportedTypes.put(JIUnsignedByte.class,new Integer(VT_UI1));
		supportedTypes.put(JIString.class,new Integer(VT_BSTR));
//		supportedTypes.put(IJIUnknown.class,new Integer(VT_UNKNOWN));
//		supportedTypes.put(IJIDispatch.class,new Integer(VT_DISPATCH));
		supportedTypes.put(JIVariant.SCODE.class,new Integer(VT_ERROR));
		supportedTypes.put(JIVariant.EMPTY.class,new Integer(VT_EMPTY));
		supportedTypes.put(JIVariant.NULL.class,new Integer(VT_NULL));
		supportedTypes.put(VariantBody.SCODE.class,new Integer(VT_ERROR));
		supportedTypes.put(VariantBody.EMPTY.class,new Integer(VT_EMPTY));
		supportedTypes.put(VariantBody.NULL.class,new Integer(VT_NULL));
		supportedTypes.put(JIArray.class,new Integer(VT_ARRAY));
//		supportedTypes.put(JIComObjectImpl.class,new Integer(VT_UNKNOWN));
//		supportedTypes.put(JIDispatchImpl.class,new Integer(VT_DISPATCH));
		supportedTypes.put(Date.class,new Integer(VT_DATE));
		supportedTypes.put(JICurrency.class,new Integer(VT_CY));
		supportedTypes.put(Long.class,new Integer(VT_I8));
		
		supportedTypes_classes.put(new Integer(VT_DATE),Date.class);
		supportedTypes_classes.put(new Integer(VT_CY),JICurrency.class);
		supportedTypes_classes.put(new Integer(VT_VARIANT),JIVariant.class);
		supportedTypes_classes.put(new Integer(VT_I4),Integer.class);
		supportedTypes_classes.put(new Integer(VT_INT),Integer.class);
		supportedTypes_classes.put(new Integer(VT_UI4),JIUnsignedInteger.class);
		supportedTypes_classes.put(new Integer(VT_UINT),JIUnsignedInteger.class);
		supportedTypes_classes.put(new Integer(VT_R4),Float.class);
		supportedTypes_classes.put(new Integer(VT_BOOL),Boolean.class);
		supportedTypes_classes.put(new Integer(VT_R8),Double.class);
		supportedTypes_classes.put(new Integer(VT_I2),Short.class);
		supportedTypes_classes.put(new Integer(VT_UI2),JIUnsignedShort.class);
		supportedTypes_classes.put(new Integer(VT_I1),Character.class);
		supportedTypes_classes.put(new Integer(VT_UI1),JIUnsignedByte.class);
		supportedTypes_classes.put(new Integer(VT_BSTR),JIString.class);
		supportedTypes_classes.put(new Integer(VT_ERROR),JIVariant.SCODE.class);
		supportedTypes_classes.put(new Integer(VT_EMPTY),EMPTY.class);
		supportedTypes_classes.put(new Integer(VT_NULL),NULL.class);
		supportedTypes_classes.put(new Integer(VT_ARRAY),JIArray.class);
		supportedTypes_classes.put(new Integer(VT_UNKNOWN),IJIComObject.class);
		supportedTypes_classes.put(new Integer(VT_DISPATCH),IJIComObject.class);
		supportedTypes_classes.put(new Integer(VT_I8),Long.class);
		
		//for by ref types, do it at runtime.
		
		
		
	}
	
	static Class getSupportedClass(Integer type)
	{
		return (Class)supportedTypes_classes.get(type);
	}
	
	static Integer getSupportedType(Class c, int FLAG)
	{
		Integer retVal = (Integer)supportedTypes.get(c);
		
		if (retVal == null && IJIComObject.class.equals(c))
		{
			retVal = new Integer(VT_UNKNOWN);
		}
		
		if (retVal == null && IJIDispatch.class.equals(c))
		{
			retVal = new Integer(VT_DISPATCH);
		}
		//means that if retval came back as VT_I4, we should make that VT_INT
		if (retVal.intValue() == VT_I4 && 
		   (FLAG & JIFlags.FLAG_REPRESENTATION_VT_INT) == JIFlags.FLAG_REPRESENTATION_VT_INT)
		{
			retVal = new Integer(VT_INT);
		}
		else
		if (retVal.intValue() == VT_UI4 && 
			(FLAG & JIFlags.FLAG_REPRESENTATION_VT_UINT) == JIFlags.FLAG_REPRESENTATION_VT_UINT)
		{
			retVal = new Integer(VT_UINT);
		}
		
		return retVal;
	}
	
	static Integer getSupportedType(Object o, int defaultType)
	{
		Class c = o.getClass();
		Integer retval = (Integer)supportedTypes.get(c);
		
		// Order is important since IJIDispatch derieves from IJIComObject
		if(retval == null && o instanceof IJIDispatch)
		{
			retval = new Integer(VT_DISPATCH);
		}
		
		if(retval == null && o instanceof IJIComObject)
		{
			retval = new Integer(VT_UNKNOWN);
		}
				
		return retval;
	}
	
	/**
	 * EMPTY <code>VARIANT</code>
	 */
	static final JIVariant EMPTY = new JIVariant(new EMPTY());

	/**
	 * EMPTY <code>VARIANT</code>. This is not Thread Safe , hence a new instance must be taken each time.
	 * 
	 */
	public static JIVariant EMPTY()
	{
		return new JIVariant(new EMPTY());
	}
	
	/**
	 * EMPTY BYREF <code>VARIANT</code>
	 */
	static final JIVariant EMPTY_BYREF = new JIVariant(EMPTY);


	/**
	 * EMPTY BYREF <code>VARIANT</code>. This is not Thread Safe , hence a new instance must be taken each time.
	 * 
	 */
	public static JIVariant EMPTY_BYREF()
	{
		return new JIVariant(EMPTY());
	}
	
	/**
	 * NULL <code>VARIANT</code>
	 */
	static final JIVariant NULL = new JIVariant(new NULL());
	
	/**
	 * NULL <code>VARIANT</code> . This is not Thread Safe , hence a new instance must be taken each time.
	 * 
	 */
	public static JIVariant NULL()
	{
		return new JIVariant(new NULL());
	}
	
	/**
	 * OPTIONAL PARAM. Pass this when a parameter is optional for a COM api call.
	 */
	static final JIVariant OPTIONAL_PARAM = new JIVariant(JIVariant.SCODE,JIErrorCodes.DISP_E_PARAMNOTFOUND);
	
	/**
	 * OPTIONAL PARAM. Pass this when a parameter is <code>[optional]</code> for a COM call. 
	 * This is not Thread Safe , hence a new instance must be taken each time.
	 * 
	 */
	public static JIVariant OPTIONAL_PARAM()
	{
		return new JIVariant(JIVariant.SCODE,JIErrorCodes.DISP_E_PARAMNOTFOUND);
	}
	
	
	/**
	 * SCODE <code>VARIANT</code>
	 */
	public static final SCODE SCODE = new SCODE();
	
	/** Helper method for creating an array of <code>BSTR</code>s , IDL signature <code>[in, out] SAFEARRAY(BSTR) *p</code>. 
	 * The return value can directly be used in an <code>IJIDispatch</code>call.
	 * 
	 * @return
	 */
	public static JIVariant BSTRARRAY()
	{
		return new JIVariant(new JIArray(new JIString[]{new JIString("")}, true),true);
	}
	
	/** Helper method for creating an array of <code>VARIANT</code>s , IDL signature <code>[in, out] SAFEARRAY(VARIANT) *p</code> 
	 * OR <code>[in,out] VARIANT *pArray</code>. The return value can directly be used in an <code>IJIDispatch</code> call.
	 * 
	 * @return
	 */
	public static JIVariant VARIANTARRAY()
	{
		return new JIVariant(new JIArray(new JIVariant[]{JIVariant.EMPTY()}, true),true);
	}
	
	
	JIPointer member = null;
	
	private JIVariant(){}
	
	//private boolean isArray = false;
	//private boolean isByRef = false;
	//private boolean isNull = false;
	
	//The class of the object determines its type.
	/**
	 * Setting up a <code>VARIANT</code> with an object. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param obj
	 */
	public JIVariant(Object obj)
	{
		init(obj,false);
	}
	
	/** For internal use only !. 
	 * @exclude
	 * @param obj
	 * @param isByRef
	 */		
	public JIVariant(Object obj, boolean isByRef)
	{
		init(obj,isByRef);
	}
	
	private void init(Object obj, boolean isByRef)
	{
		if (obj != null && obj.getClass().isArray())
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_VARIANT_ONLY_JIARRAY_EXCEPTED));
		}
		
		if (obj != null && obj.getClass().equals(JIInterfacePointer.class))
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_VARIANT_TYPE_INCORRECT));
		}
		
		//this case comes only for SCODE and EMPTY, and in these cases the isByRef flag will be set in the
		//previous call itself.
		if (obj instanceof VariantBody)
		{
			member = new JIPointer(obj);
		}
		else
		{
			VariantBody variantBody = new VariantBody(obj,isByRef);
			member = new JIPointer(variantBody);
//			if (obj != null && obj instanceof JIVariant)
//			{
//				VariantBody var = (VariantBody)((JIVariant)obj).member.getReferent();
//				try {
//					variantBody.variantType = var.getVariantType() + 3 + 1;
//				} catch (JIException e) {
//					throw new JIRuntimeException(e.getErrorCode());
//				}
//			}
		}
		
		member.setReferent(0x72657355);//"User" in LEndian.
		
	}
	
	/**Called when this variant is nested
	 * 
	 * @param deffered
	 */
	void setDeffered(boolean deffered)
	{
		if (member != null && !member.isReference())
		{
			member.setDeffered(deffered);
		}
	}
	
	/** Sets a <code>JIFlags</code> value to be used while encoding (marshalling) this Variant.  
	 * 
	 * @param FLAG
	 */
	public void setFlag(int FLAG)
	{
		VariantBody variantBody = ((VariantBody)member.getReferent());
		variantBody.FLAG |= FLAG;
	}
	
	/**Returns the flag value for this variant.
	 * 
	 * @return
	 */
	public int getFlag()
	{
		VariantBody variantBody = ((VariantBody)member.getReferent());
		return variantBody.FLAG;
	}
	
	
	/**Returns whether this variant is a <code>NULL</code> variant.
	 * 
	 * @return <code>true</code> if the variant is a <code>NULL</code>
	 */
	public boolean isNull()
	{
		if (member == null)
		{
			return true;
		}
		VariantBody variantBody = ((VariantBody)member.getReferent());
		return variantBody == null ? true: variantBody.isNull();
	}
	
	/**Setting up a <code>VARIANT</code> as reference to another. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param variant
	 */
	public JIVariant(JIVariant variant)
	{
		this((Object)variant,true);
	}
	

	
	/**Setting up a <code>VARIANT</code> with an <code>int</code>. Used via serializing the <code>VARIANT</code>.
	 * Used when the variant type is VT_I4.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. LONG*
	 */
	public JIVariant(int value, boolean isByRef)
	{
		this(new Integer(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>long</code>. Used via serializing the <code>VARIANT</code>.
	 * Used when the variant type is VT_I8.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. 
	 */
	public JIVariant(long value, boolean isByRef)
	{
		this(new Long(value),isByRef);
	}
	
	
	
	/**
	 * Setting up a <code>VARIANT</code> with a <code>float</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. FLOAT*
	 */
	public JIVariant(float value, boolean isByRef)
	{
		this(new Float(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>boolean</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. VARIANT_BOOL*
	 */
	public JIVariant(boolean value, boolean isByRef)
	{
		this(Boolean.valueOf(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>double</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. DOUBLE*
	 */
	public JIVariant(double value, boolean isByRef)
	{
		this(new Double(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>short</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. SHORT*
	 */
	public JIVariant(short value, boolean isByRef)
	{
		this(new Short(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>char</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. CHAR*
	 */
	public JIVariant(char value, boolean isByRef)
	{
		this(new Character(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>JIString</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. BSTR*
	 */
	public JIVariant(JIString value, boolean isByRef)
	{
		this((Object)value,isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>String</code>. Used via serializing the <code>VARIANT</code>. Internally a 
	 * <code>JIString</code> is formed with it's default type <code>BSTR</code>. 
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. BSTR*
	 */
	public JIVariant(String value, boolean isByRef)
	{
		this(new JIString(value),isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>String</code>. Used via serializing the <code>VARIANT</code>. Internally a 
	 * <code>JIString</code> is formed with it's default type <code>BSTR</code>. 
	 * 
	 * @param value
	 */
	public JIVariant(String value)
	{
		this(new JIString(value));
	}
	
//	/**Setting up a <code>VARIANT</code> with a IJIDispatch. Used via serializing the <code>VARIANT</code>.
//	 * 
//	 * @param value
//	 * @param isByRef true if the value is to be represented as a pointer. IJIDispatch**
//	 */
//	public JIVariant(IJIDispatch value, boolean isByRef)
//	{
//		this((Object)value,isByRef);
//	}
	
	/**Setting up a <code>VARIANT</code> with an <code>IJIComObject</code>. Used via serializing the <code>VARIANT</code>. 
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. IJIComObject**
	 */
	public JIVariant(IJIComObject value, boolean isByRef)
	{
		this((Object)value,isByRef);
		if (value instanceof IJIDispatch)
		{
			setFlag(JIFlags.FLAG_REPRESENTATION_USE_IDISPATCH_IID);
		}
		else
		{
			setFlag(JIFlags.FLAG_REPRESENTATION_USE_IUNKNOWN_IID);
		}
	}
	
	
	
	/**Setting up a <code>VARIANT</code> with a <code>SCODE</code> value and it's <code>errorCode</code>. Used via serializing the <code>VARIANT</code>.
	 *
	 * 
	 * @param value
	 * @param errorCode
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. SCODE*
	 */
	public JIVariant(SCODE value,int errorCode, boolean isByRef)
	{
		this(new VariantBody(VariantBody.SCODE,errorCode,isByRef),isByRef);
	}
	
	
	/**Setting up a <code>VARIANT</code> with an <code>int</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(int value)
	{
		this(new Integer(value));
	}
	
	/**
	 * Setting up a <code>VARIANT</code> with a <code>float</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(float value)
	{
		this(new Float(value));
	}
	
	/**Setting up a <code>VARIANT</code> with a  <code>boolean</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(boolean value)
	{
		this(Boolean.valueOf(value));
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>double</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(double value)
	{
		this(new Double(value));
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>short</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(short value)
	{
		this(new Short(value));
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>char</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(char value)
	{
		this(new Character(value));
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>JIString</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(JIString value)
	{
		this((Object)value);
	}
	
//	/**Setting up a <code>VARIANT</code> with a IJIDispatch. Used via serializing the <code>VARIANT</code>.
//	 * 
//	 * @param value
//	 */
//	public JIVariant(IJIDispatch value)
//	{
//		this((Object)value);
//	}
	
	/**Setting up a <code>VARIANT</code> with an <code>IJIComObject</code>. Used via serializing the <code>VARIANT</code>. 
	 * 
	 * @param value
	 */
	public JIVariant(IJIComObject value)
	{
		this((Object)value);
		if (value instanceof IJIDispatch)
		{
			setFlag(JIFlags.FLAG_REPRESENTATION_USE_IDISPATCH_IID);
		}
		else
		{
			setFlag(JIFlags.FLAG_REPRESENTATION_USE_IUNKNOWN_IID);
		}
	}
	
	/**Setting up a <code>VARIANT</code> with an <code>java.util.Date</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(Date value)
	{
		this((Object)value);
	}
	
	/**Setting up a <code>VARIANT</code> with an <code>java.util.Date</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. Date*
	 */
	public JIVariant(Date value, boolean isByRef)
	{
		this((Object)value,isByRef);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>JICurrency</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	public JIVariant(JICurrency value)
	{
		this((Object)value);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>JICurrency</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. JICurrency*
	 */
	public JIVariant(JICurrency value, boolean isByRef)
	{
		this((Object)value,isByRef);
	}
	
	
	/** Setting up a <code>VARIANT</code> with an <code>EMPTY</code> value. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	private JIVariant(EMPTY value)
	{
		this((Object)null);
	}
	
	
	/**Setting up a <code>VARIANT</code> with a <code>NULL</code> value. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	private JIVariant(NULL value)
	{
		this(new VariantBody(VariantBody.NULL)); 
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>SCODE</code> value and it's <code>errorCode</code>. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param errorCode
	 */
	public JIVariant(SCODE value,int errorCode)
	{
		this(new VariantBody(VariantBody.SCODE,errorCode,false));
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via serializing the <code>VARIANT</code>.
	 * Only 1 and 2 dimensional array is supported.
	 * 
	 * @param array
	 * @param FLAG JIFlag value
	 */
	public JIVariant(JIArray array, int FLAG)
	{
		this(array,false,FLAG);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via serializing the <code>VARIANT</code>.
	 * Only 1 and 2 dimensional array is supported.
	 * 
	 * @param array
	 * @param isByRef
	 * @param FLAG JIFlag value
	 */
	public JIVariant(JIArray array, boolean isByRef, int FLAG)
	{
		initArrays(array, isByRef, FLAG);
	}
	/** Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via serializing the <code>VARIANT</code>.
	 * Only 1 and 2 dimensional array is supported. 
	 * 
	 * @param array
	 * @param isByRef
	 */	
	public JIVariant(JIArray array, boolean isByRef)
	{
		initArrays(array, isByRef, JIFlags.FLAG_NULL);
	}
	
	private final static List arryInits = new ArrayList();
	static
	{
		arryInits.add(JIString.class);
		arryInits.add(JIPointer.class);
//		arryInits.add(JIComObjectImpl.class);
//		arryInits.add(JIDispatchImpl.class);
//		arryInits.add(IJIUnknown.class);
		arryInits.add(IJIComObject.class);
		arryInits.add(IJIDispatch.class); //this can only happen in case of an array
	}
	private void initArrays(JIArray array, boolean isByRef, int FLAG)
	{
		VariantBody variant2 = null;
		JIArray array2 = null;
		Class c = null;
		Object[] newArrayObj = null;
		boolean is2Dim = false;
		
		if (array == null)
		{
			init(null,false);
			return;
		}
		
		switch(array.getDimensions())
		{
			case 1:
				Object[] obj = (Object [])array.getArrayInstance();
				newArrayObj = obj;
				c = obj.getClass().getComponentType();
				break;
			case 2:
				/*The 2 dimensional array is serialized like this first the index [0,0]  and then [1,0] then [0,1] then [1,1], then [0,2] then [1,2] 
				 and so on . so what i will do here is that create a single dimension flat array of the members in the order specified above, after examining this Object[][] and let the
				 1 dimension serializing logic take over.*/
				Object[][] obj2 = (Object [][])array.getArrayInstance();
				//variants = new JIVariant[array.getNumElementsInAllDimensions()];
				
				String name = obj2.getClass().getName();
				Object subArray = obj2;
				name = name.substring(1);
				int firstDim = ((Object[])subArray).length;
				subArray = Array.get(subArray,0);
				int secondDim = ((Object[])subArray).length;
				int k = 0;
				newArrayObj = (Object[])Array.newInstance(subArray.getClass().getComponentType(), array.getNumElementsInAllDimensions());
				for (int i = 0; i < secondDim;i++)
				{
					for (int j = 0;j < firstDim;j++)
					{
						newArrayObj[k++] = obj2[j][i];
					}
				}
				
				
				c = subArray.getClass().getComponentType();
				is2Dim = true;
				break;
			default:
				throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_VARIANT_VARARRAYS_2DIMRES));
		}	
		
		array2 = new JIArray(newArrayObj,true); //should always be conformant since this is part of a safe array.
	
		JIStruct safeArray = new JIStruct();
		try {
			safeArray.addMember(new Short((short)array.getDimensions()));//dim
			int elementSize = -1;
			short flags = JIVariant.FADF_HAVEVARTYPE;
			if (c.equals(JIVariant.class))
			{
				flags = (short)(flags | JIVariant.FADF_VARIANT);
				elementSize = 16; //(Variant is pointer whose size is 16)
			}
			else
			if (arryInits.contains(c))
			{
				if (c.equals(JIString.class))
				{
					flags = (short)(flags | JIVariant.FADF_BSTR);
				}
				else
				if (c.equals(IJIComObject.class))
				{
					flags = (short)(flags | JIVariant.FADF_UNKNOWN);
					FLAG |= JIFlags.FLAG_REPRESENTATION_USE_IUNKNOWN_IID;
				}else
				if (c.equals(IJIDispatch.class))
				{
					flags = (short)(flags | JIVariant.FADF_DISPATCH);
					FLAG |= JIFlags.FLAG_REPRESENTATION_USE_IDISPATCH_IID;
				}
				elementSize = 4; //Since all these are pointers inherently 
			}
			else
			{
				//JStruct and JIUnions are expected to be encapsulated within pointers...they usually are :)
				elementSize = JIMarshalUnMarshalHelper.getLengthInBytes(c, null, c == Boolean.class ? JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL : JIFlags.FLAG_NULL); //All other types, basic types 
			}
			
			
			JIStruct safeArrayBound = null;
			 
			int upperBounds[] = array.getUpperBounds();
			JIStruct[] arrayOfSafeArrayBounds = new JIStruct[array.getDimensions()];
			for (int i = 0; i < array.getDimensions();i++)
			{
				safeArrayBound = new JIStruct();
				safeArrayBound.addMember(new Integer(upperBounds[i]));
				safeArrayBound.addMember(new Integer(0)); //starts at 0
				arrayOfSafeArrayBounds[i] = safeArrayBound;
			}
			
			JIArray arrayOfSafeArrayBounds2 = new JIArray(arrayOfSafeArrayBounds,true);
			
			safeArray.addMember(new Short(flags));//flags
			if (elementSize > 0)
			{
				safeArray.addMember(new Integer(elementSize));
			}
			else 
			{
				elementSize = JIMarshalUnMarshalHelper.getLengthInBytes(c, null, FLAG);
				safeArray.addMember(new Integer(elementSize));//size
			}
			
			safeArray.addMember(new Short((short)0));//locks
			safeArray.addMember(new Short(JIVariant.getSupportedType(c, FLAG).shortValue()));//variant array, safearrayunion
			//peculiarity here, windows seems to be sending the signed type in VarType32...
			if (c.equals(JIUnsignedByte.class))
			{
				safeArray.addMember(JIVariant.getSupportedType(Byte.class,FLAG));//safearrayunion
			}
			else if (c.equals(JIUnsignedShort.class))
			{
				safeArray.addMember(JIVariant.getSupportedType(Short.class,FLAG));//safearrayunion
			}
			else if (c.equals(JIUnsignedInteger.class))
			{
				safeArray.addMember(JIVariant.getSupportedType(Integer.class,FLAG));//safearrayunion
			}
			else if (c.equals(Boolean.class))
			{
				safeArray.addMember(JIVariant.getSupportedType(Short.class,FLAG));//safearrayunion
			}
			else if (c.equals(Double.class))
			{
				safeArray.addMember(JIVariant.getSupportedType(Long.class,FLAG));//safearrayunion
			}
			else if (c.equals(Float.class))
			{
				safeArray.addMember(JIVariant.getSupportedType(Integer.class,FLAG));//safearrayunion
			}
			else
			{
				safeArray.addMember(JIVariant.getSupportedType(c,FLAG));//safearrayunion
			}
			safeArray.addMember(new Integer(array2.getNumElementsInAllDimensions()));//size in safearrayunion
			JIPointer ptr2RealArray = new JIPointer(array2);
			safeArray.addMember(ptr2RealArray);
			safeArray.addMember(arrayOfSafeArrayBounds2);
		} catch (JIException e) {
			throw new JIRuntimeException(e.getErrorCode());
		}
		
		variant2 = new VariantBody(safeArray,c,is2Dim,isByRef,FLAG); 
		init(variant2,false);
		
	}
	
	/** Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via serializing the <code>VARIANT</code>. <br>
	 * Only 1 and 2 dimensional array is supported. 
	 * 
	 * @param array
	 */	
	public JIVariant(JIArray array)
	{
		this(array,false);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>unsigned</code> value. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param number
	 */
	public JIVariant(IJIUnsigned number)
	{
		this((Object)number);
	}
	
	/**Setting up a <code>VARIANT</code> with a <code>unsigned</code> value. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param number
	 * @param isByRef <code>true</code> if the value is to be represented as a pointer. 
	 */
	public JIVariant(IJIUnsigned number, boolean isByRef)
	{
		this((Object)number,isByRef);
	}
	
	/** Returns the contained object.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public Object getObject() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObject();
	}
	
	/**Retrieves the contained object as <code>int</code>. 
	 * 
	 * @return
	 * @throws JIException 
	 */
	public int getObjectAsInt() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsInt();
	}
	
	/**Retrieves the contained object as <code>float</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public float getObjectAsFloat() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsFloat();
	}
	
	/**Retrieves the contained objects errorCode.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public int getObjectAsSCODE() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsSCODE();
	}
	
	/**Retrieves the contained object as <code>double</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public double getObjectAsDouble() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsDouble();
	}
	
	/**Retrieves the contained object as <code>short</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public short getObjectAsShort() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsShort();
	}
	
	/**Retrieves the contained object as <code>boolean</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public boolean getObjectAsBoolean() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsBoolean();
	}
	
	/**Retrieves the contained object as <code>JIString</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public JIString getObjectAsString() throws JIException
	{
		checkValidity();
		return  ((VariantBody)member.getReferent()).getObjectAsString();
	}

	/**Retrieves the contained object as <code>String</code>.
	 * 
	 * @return
	 * @throws JIException
	 */
	public String getObjectAsString2() throws JIException
	{
		checkValidity();
		return  ((VariantBody)member.getReferent()).getObjectAsString().getString();
	}
	
	/**Retrieves the contained object as <code>java.util.Date</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public Date getObjectAsDate() throws JIException
	{
		checkValidity();
		return  ((VariantBody)member.getReferent()).getObjectAsDate();
	}
	
	/**Retrieves the contained object as <code>char</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public char getObjectAsChar() throws JIException
	{
		checkValidity();
		return  ((VariantBody)member.getReferent()).getObjectAsChar();
	}
	

//	/**Retrieves the contained object as JIInterfacePointer.
//	 * 
//	 * @return
//	 * @throws JIException
//	 * @deprecated Please use getObjectAsComObject instead.
//	 */
//	public JIInterfacePointer getObjectAsInterfacePointer() throws JIException
//	{
//		checkValidity();
//		return  ((VariantBody)member.getReferent()).getObjectAsInterfacePointer();
//	}
	
//	/**Retrieves the contained object as IJIComObject. Return value can be safely typecasted to the expected type. for e.g. :- If expected type is an IJIDispatch , 
//	 * then the return value can be safely type casted to it.
//	 * 
//	 * @param template <code>IJIComObject</code> whose basic parameters such as <code>JIComServer</code> will be used while creating the new Instance.
//	 * @return
//	 * @throws JIException
//	 * @deprecated
//	 */
//	public IJIComObject getObjectAsComObject(IJIComObject template) throws JIException
//	{
//		checkValidity();
//		return JIObjectFactory.createCOMInstance(template,((VariantBody)member.getReferent()).getObjectAsInterfacePointer());
//	}

	/**Retrieves the contained object as <code>IJIComObject</code>. Return value must be "narrowed" to get the expected type. 
	 * <p>for example :- If expected type is an <code>IJIDispatch</code>, 
	 * then the return value must pass through <code>JIObjectFactory.narrowInstance(IJIComObject)</code> to get to the right type.
	 * 
	 * @return
	 * @throws JIException
	 */
	public IJIComObject getObjectAsComObject() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsComObject();
	}
	
	/**Retrieves the contained object as <code>JIVariant</code>.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public JIVariant getObjectAsVariant() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsVariant();
	}

	/**Retrieves the contained object as <code>JIArray</code>. Only 1 and 2 dim arrays are supported currently.
	 * Please note that this array is <b>not</b> backed by this variant and is a <b>new</b> copy. If the array 
	 * is <code>IJIComObject</code>s, please make sure to use <code>JIObjectFactory.narrowObject()</code> to 
	 * get the right instance.
	 * 
	 * @return
	 * @throws JIException
	 */
	public JIArray getObjectAsArray() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getArray();
	}

	/**Retrieves the contained object as <code>long</code>, used when the expected type is VT_I8.
	 * 
	 * @return
	 * @throws JIException
	 */
	public long getObjectAsLong() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsLong();
	}
		
	/**Retrieves the contained object as <code>unsigned</code> number.
	 * 
	 * @return
	 * @throws JIException
	 */
	public IJIUnsigned getObjectAsUnsigned() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getObjectAsUnsigned();
	}
	
	void encode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG)
	{
		member.setDeffered(true);//this is since this could be part of an array or a struct...for normal calls
		//as soon as this call finishes a call will be given from JICallobject for it's variantbody.
		JIMarshalUnMarshalHelper.serialize(ndr,member.getClass(),member,defferedPointers,FLAG);	
	}
	
	
	static JIVariant decode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG, Map additionalData)
	{
		JIVariant variant = new JIVariant();
		JIPointer ref = new JIPointer(VariantBody.class);
		ref.setDeffered(true);//this is since this could be part of an array or a struct...for normal calls
		//as soon as this call finishes a call will be given from JICallobject for it's variantbody.
		variant.member = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,ref,defferedPointers,FLAG,additionalData);
		return variant;
	}
	
	public boolean isArray() throws JIException {
		checkValidity();
		return  ((VariantBody)member.getReferent()).isArray();
	}
	
	int getLengthInBytes(int FLAG) throws JIException
	{
		checkValidity();
		return JIMarshalUnMarshalHelper.getLengthInBytes(member.getClass(),member,FLAG);
	}

	
	public boolean isByRefFlagSet() throws JIException {
		checkValidity();
		return ((VariantBody)member.getReferent()).isByRef();
	}
	
	/** Returns the referent as integer.
	 * 
	 * @return
	 * @throws JIException 
	 */
	public int getType() throws JIException
	{
		checkValidity();
		return ((VariantBody)member.getReferent()).getType();
	}
	
	private void checkValidity() throws JIException
	{
		if (member == null || member.isNull())
		{
			throw new JIException(JIErrorCodes.JI_VARIANT_IS_NULL);
		}
	}
	
	public String toString()
	{
		return member == null ? "[null]" : "[" + member.toString() + "]";
	}
	
}

class VariantBody implements Serializable
{
	
	private static final long serialVersionUID = -8484108480626831102L;
	public static final short VT_PTR = 0x1A; 
	public static final short VT_SAFEARRAY = 0x1B;
	public static final short VT_CARRAY = 0x1C;
	public static final short  VT_USERDEFINED = 0x1D;
	
	
	static final class EMPTY {}
	static final class NULL {}
	static final class SCODE { private int errorCode; private SCODE(){} 
									   private SCODE(int errorCode) {this.errorCode = errorCode;}}
	
	/**
	 * EMPTY <code>VARIANT</code>
	 */
	public static final EMPTY EMPTY = new EMPTY();
	
	/**
	 * NULL <code>VARIANT</code>
	 */
	public static final NULL NULL = new NULL();
	
	/**
	 * SCODE <code>VARIANT</code>
	 */
	public static final SCODE SCODE = new SCODE();
	
	private boolean is2Dimensional = false;
	private Object obj = null;
	private int type = -1;
	//private JIArray objArray = null;
	private JIStruct safeArrayStruct = null;
	private boolean isArray = false;
	private boolean isScode = false;
	private boolean isNull = false;
	private Class nestedArraysRealClass = null;
	private static ArrayList type3 = new ArrayList();
	private boolean isByRef = false;
	
	int FLAG = JIFlags.FLAG_NULL;
//	int variantType = 0x1d; //base jump
	
	static
	{
		type3.add(Integer.class);
		type3.add(Short.class);
		type3.add(Float.class);
		type3.add(Boolean.class);
		type3.add(Character.class);
		type3.add(Byte.class);
		type3.add(EMPTY.class);
		type3.add(NULL.class);
		type3.add(SCODE.class);
		type3.add(JIUnsignedByte.class);
		type3.add(JIUnsignedShort.class);
		type3.add(JIUnsignedInteger.class);
	}
	
	boolean isByRef()
	{
		return isByRef;
	}

	boolean isNull()
	{
		return isNull;
	}

	int getType()
	{
		return isArray ? JIVariant.VT_ARRAY | type : type;
	}
	//The class of the object determines its type.
	/**
	 * Setting up a <code>VARIANT</code> with an object. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param referent
	 */
	VariantBody(Object referent, boolean isByRef)
	{
		this(referent,isByRef,-1);
	}
	
	
	private VariantBody(Object referent, boolean isByRef, int dataType)
	{
		this.obj = referent == null ? VariantBody.EMPTY : referent;
		
		if (obj instanceof JIString && ((JIString)obj).getType() != JIFlags.FLAG_REPRESENTATION_STRING_BSTR)
		{
			throw new JIRuntimeException(JIErrorCodes.JI_VARIANT_BSTR_ONLY);
		}
		
		if (obj instanceof Boolean)
		{
			FLAG = JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL;
		}
		
		
		this.isByRef = isByRef;
//		variantType = getMaxLength(this.obj.getClass(),isByRef,obj);
		
		//for an unsupported type this could be null
		//but then this is my bug, any thread entering this ctor , will support a type.
		Integer types = ((Integer)JIVariant.getSupportedType(obj,dataType));
		if (types != null)
		{
			type = types.intValue() | (isByRef ? JIVariant.VT_BYREF:0);
		}else
		{
			throw new JIRuntimeException(JIErrorCodes.JI_VARIANT_UNSUPPORTED_TYPE);
		}
		
//		if (JISystem.getLogger().isLoggable(Level.INFO))
//		{
//			JISystem.getLogger().info("In VariantBody(Object,boolean,int) : dataType is " + dataType + " , referent class is " + this.obj.getClass() + " , byRef is " + isByRef);
//		}
		if (dataType == JIVariant.VT_NULL)
		{
			isNull = true;
			obj = new Integer(0);
		}
	}
		
	/**Setting up a <code>VARIANT</code> with a NULL value. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 */
	VariantBody(NULL value)
	{
		this(new Integer(0),false);
		isNull = true;
		type = JIVariant.VT_NULL;
	}
	
	/**Setting up a <code>VARIANT</code> with a SCODE value and it's errorCode. Used via serializing the <code>VARIANT</code>.
	 * 
	 * @param value
	 * @param errorCode
	 */
	 VariantBody(SCODE value,int errorCode, boolean isByRef)
	{
		this(new Integer(errorCode),isByRef);
		isScode = true;
		type = JIVariant.VT_ERROR;
	}
	
	
	
	VariantBody(JIStruct safeArray, Class nestedClass,boolean is2Dimensional,boolean isByRef,int FLAG)
	{
		this.FLAG = FLAG;
		//can't convert the array here , since this will have deffered pointers which may not be complete.
		safeArrayStruct = safeArray;
		isArray = true;
		if (safeArrayStruct == null)
		{
			isNull = true;
		}
		
		this.nestedArraysRealClass = nestedClass;
		this.is2Dimensional = is2Dimensional;
		//please remember JIVariant is a pointer and VariantBody is just the referent part of that.

		
		//for an unsupported type this could be null
		//but then this is my bug, any thread entering this ctor , will support a type.
		this.isByRef = isByRef;
		Integer types = ((Integer)JIVariant.getSupportedType(nestedClass,FLAG));
		if (types != null)
		{
			type = types.intValue() | (isByRef ? JIVariant.VT_BYREF:0);
		}else
		{
			throw new JIRuntimeException(JIErrorCodes.JI_VARIANT_UNSUPPORTED_TYPE);
		}
	}
	 
//	VariantBody(JIArray obj, Class nestedClass, boolean is2Dimensional,boolean isByRef)
//	{
//		
//		this.objArray = obj;
//		isArray = true;
//		this.nestedArraysRealClass = nestedClass;
//		this.is2Dimensional = is2Dimensional;
//		//please remember JIVariant is a pointer and VariantBody is just the referent part of that.
//
//		
//		//for an unsupported type this could be null
//		//but then this is my bug, any thread entering this ctor , will support a type.
//		this.isByRef = isByRef;
//		Integer types = ((Integer)JIVariant.supportedTypes.get(obj.getClass()));
//		if (types != null)
//		{
//			type = types.intValue() | (isByRef ? JIVariant.VT_BYREF:0);
//		}
//
//	}
	
	/** Returns the contained object.
	 * 
	 * @return
	 */
	Object getObject() throws JIException
	{
		return obj == null ? getArray() : obj;
	}
	
	JIArray getArray() throws JIException
	{
		JIArray retVal = null;
		//TODO convert it to the right type based on the variantType before returning it.
		//everything is sent encapsulated in a variant(in safearray) , so an Integer[] will
		//go as a variant array for each integer, only the variantType = arry of ints. so convert the 
		//array in the right format before returning it to the user. That is he must get Int[] within a JIArray
		//back.
		if (safeArrayStruct != null)
		{
			retVal = (JIArray)((JIPointer)safeArrayStruct.getMember(7)).getReferent();
			
			if (is2Dimensional)
			{
				Object[] obj3 = (Object[])retVal.getArrayInstance(); //these will all be variants
				//correct the array here , i.e reform the 2 dimensional array before returning back.
				JIArray safeArrayBound = (JIArray)safeArrayStruct.getMember(8);
				
				JIStruct[] safeArrayBound2 = (JIStruct[]) safeArrayBound.getArrayInstance();
				//should only be 2 since we support only 2 dim.
				
				int firstDim = ((Integer)safeArrayBound2[0].getMember(0)).intValue();
				int secondDim = ((Integer)safeArrayBound2[1].getMember(0)).intValue();
				
				Object obj = Array.newInstance(nestedArraysRealClass,new int[]{firstDim,secondDim});
				Object[][] obj2 = (Object[][])obj;
				int k = 0;
				for (int i = 0; i < secondDim;i++)
				{
					for (int j = 0;j < firstDim;j++)
					{
//						if (nestedArraysRealClass == JIVariant.class)
//						{
//							obj2[j][i] = ((JIVariant[])obj3)[k++];
//						}
//						else
//						{
//							obj2[j][i] = ((JIVariant[])obj3)[k++].getObject();
//						}
						obj2[j][i] = obj3[k++];
					}
				}
				
				retVal = new JIArray(obj2);
				
			}
			else
			{
				
				if (nestedArraysRealClass != null)
				{
					Object[] obj = (Object[])retVal.getArrayInstance(); //these will all be variants
					Object obj2 = Array.newInstance(nestedArraysRealClass,obj.length);
					for (int i = 0;i < obj.length;i++)
					{
//						if (nestedArraysRealClass == JIVariant.class)
//						{
//							Array.set(obj2,i,((JIVariant[])obj)[i]);//should be the native type
//						}
//						else
//						{
//							Array.set(obj2,i,((JIVariant[])obj)[i].getObject());//should be the native type
//						}
						
						//Array.set(obj2,i,obj[i]);
						((Object[])obj2)[i] = obj[i];
					}
					retVal = new JIArray(obj2);
				}
				else
				{
					throw new JIException(JIErrorCodes.JI_VARIANT_UNSUPPORTED_TYPE);
				}
			}
		}
		return retVal;
	}
	
	/**Retrieves the contained object as int. 
	 * 
	 * @return
	 */
	int getObjectAsInt()
	{
		try{
			return ((Integer)obj).intValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	long getObjectAsLong()
	{
		try{
			return ((Long)obj).longValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	IJIUnsigned getObjectAsUnsigned()
	{
		try{
			return ((IJIUnsigned)obj);
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	int getObjectAsSCODE()
	{
		try{
			return ((SCODE)obj).errorCode;
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as float.
	 * 
	 * @return
	 */
	float getObjectAsFloat()
	{
		try{
			return ((Float)obj).floatValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as double.
	 * 
	 * @return
	 */
	double getObjectAsDouble()
	{
		try{
			return ((Double)obj).doubleValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as short.
	 * 
	 * @return
	 */
	short getObjectAsShort()
	{
		try{
			return ((Short)obj).shortValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as boolean.
	 * 
	 * @return
	 */
	boolean getObjectAsBoolean()
	{
		try{
			return ((Boolean)obj).booleanValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as JIString.
	 * 
	 * @return
	 */
	JIString getObjectAsString()
	{
		try{
			return ((JIString)obj);
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as Date.
	 * 
	 * @return
	 */
	Date getObjectAsDate()
	{
		try{
			return ((Date)obj);
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as char.
	 * 
	 * @return
	 */
	char getObjectAsChar()
	{
		try{
			return ((Character)obj).charValue();
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**Retrieves the contained object as Variant. 
	 * 
	 * @return
	 */
	JIVariant getObjectAsVariant()
	{
		try{
			return ((JIVariant)obj);
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	IJIComObject getObjectAsComObject()
	{
		try{
			return ((IJIComObject)obj);
		}catch(ClassCastException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	
	void encode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG)
	{

//		try
		{
			FLAG |= this.FLAG; 
			//align with 8 boundary
			double index = new Integer(ndr.getBuffer().getIndex()).doubleValue(); 
			if (index%8.0 != 0)
			{
				long i = (i=Math.round(index%8.0)) == 0 ? 0 : 8 - i ;
				ndr.writeOctetArray(new byte[(int)i],0,(int)i);
			}
	
			int start = ndr.getBuffer().getIndex();
			
//			if (safeArrayStruct != null)
//			{
//				//length for the array 
//				length = fillArrayType(ndr);
//			}
//			else
//			{
//				ndr.writeUnsignedLong(variantType);
//			}
			
			//just a place holder for length
			ndr.writeUnsignedLong(0xFFFFFFFF);
			
			ndr.writeUnsignedLong(0);
			
			
			//Type
			int varType = getVarType(obj != null ? obj.getClass() : nestedArraysRealClass, obj);
			ndr.writeUnsignedShort(varType);
			
			//reserved bytes
			ndr.writeUnsignedSmall(0xCC);
			ndr.writeUnsignedSmall(0xCC);
			ndr.writeUnsignedSmall(0xCC);
			ndr.writeUnsignedSmall(0xCC);
			ndr.writeUnsignedSmall(0xCC);
			ndr.writeUnsignedSmall(0xCC);
	
			if (obj != null)
			{
				ndr.writeUnsignedLong(varType);
			}
			else
			{
				if (!isByRef)
					ndr.writeUnsignedLong(JIVariant.VT_ARRAY);
				else
					ndr.writeUnsignedLong(JIVariant.VT_BYREF_VT_ARRAY);
			}
	
			
			if (isByRef)
			{
				int flag = -1;
				if (isArray) //object arrays will come here....
				{
					flag = 4;
				}
				else
				{
					//no idea what these flags are but 0x10 is for variant, 0x8 for date, and 0x4 is for others
					switch(type)
					{
						case JIVariant.VT_BYREF_VT_VARIANT:
							flag = 0x10;
						break;
						case JIVariant.VT_BYREF_VT_DATE:
						case JIVariant.VT_BYREF_VT_CY:	
							flag = 8;
							break;
						default: 
							flag = 4;
					}
				}
				ndr.writeUnsignedLong(flag);
				
			}
			
			//we should not use the deffered pointers here, but pass our own one, so that only they are written...
			List varDefferedPointers = new ArrayList();
			
			//we should use FLAG here, since the decision should be based on this only.
			setValue(ndr,obj,varDefferedPointers,FLAG);
			
			//making changes to write the deffered pointers here itself , since we need to put the entire Variant completed to the length 
			//as in varType.
			int x = 0;
			while (x < varDefferedPointers.size())
			{
				ArrayList newList = new ArrayList();
				JIMarshalUnMarshalHelper.serialize(ndr,JIPointer.class,(JIPointer)varDefferedPointers.get(x),newList,FLAG);
				x++; //incrementing index
				varDefferedPointers.addAll(x,newList);
			}
			
			int currentIndex = 0;
			int length = (currentIndex = ndr.getBuffer().getIndex()) - start;
			int value =  (int) length/8;
			if (length%8.0 != 0) //entire variant is aligned by 8 bytes.
			{
				value++;
			}
			ndr.getBuffer().setIndex(start);
			ndr.writeUnsignedLong(value);
			ndr.getBuffer().setIndex(currentIndex);
			
			if (JISystem.getLogger().isLoggable(Level.FINEST))
			{
				JISystem.getLogger().finest("Variant length is " + length + " , value " + value + " , variant type" + type);
			}
//			if (safeArrayStruct != null && isArray)
//			{
//				//SafeArray have the alignment rule , that all Size <=4 are aligned by 4 and size 8 is aligned by 8.
//				//Variant is aligned by 4, Interface pointers are aligned by 4 as well.
//				//but this should not exceed the length
//				index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
//				length = length * 8 + start;
//				if (index < length)
//				{
//					Integer size = (Integer)safeArrayStruct.getMember(2);
//					long i = 0;
//					if (size.intValue() == 8)
//					{
//						if (index%8.0 != 0)
//						{
//							i = (i=Math.round(index%8.0)) == 0 ? 0 : 8 - i ;
//							if (index + i <= length)
//							{
//								ndr.writeOctetArray(new byte[(int)i],0,(int)i);
//							}
//							else
//							{
//								ndr.writeOctetArray(new byte[(length - (int)index)],0,(int)(length - (int)index));
//							}
//						}
//					}
//					else
//					{
//						//align by 4...
//						//TODO this needs to be tested for Structs and Unions.
//						if (index%4.0 != 0)
//						{
//							i = (i=Math.round(index%4.0)) == 0 ? 0 : 4 - i ;
//							if (index + i <= length)
//							{
//								ndr.writeOctetArray(new byte[(int)i],0,(int)i);
//							}
//							else
//							{
//								ndr.writeOctetArray(new byte[(length - (int)index)],0,(int)(length - (int)index));
//							}
//						}
//					}
//					
//					
//				}
//			}
			
			
		}
//		catch (JIException e)
//		{
//			throw new JIRuntimeException(e.getErrorCode());
//		}
	}
	
	//multiple of 8.
//	private int getMaxLength(Class c, boolean isByRef, Object obj)
//	{
//		int length = 3; //Empty
//		if (type3.contains(c))
//		{
//			length = 3;
//			if (isByRef)
//			{
//				length = length + 1; //for the pointer
//			}
//		}
//		else
//		if(c.equals(Long.class) || c.equals(Double.class) || c.equals(Date.class) || c.equals(JICurrency.class))
//		{
//			length = 4;
//			//here the byref can be left out since it will cover 24 bytes properly
//		}
//		else
//		if(c.equals(JIString.class))
//		{
//	
//			int strlen = 0;
//			if (obj != null && ((JIString)obj).getString() != null)
//			{
//				strlen = ((JIString)obj).getString().length();
//			}
//			
//			//20 is of variant, 4+4+4+4 of bstr(user,maxlen,actlen,offset) , (strlen*2) of the actual array
//			double value = 20 + 16 + strlen*2;
//			if (isByRef)
//			{
//				value = value + 4;
//			}
//			double d = value%8.0;
//			length = (int)value/8;
//			if (d != 0.0)
//			{
//				length++;
//			}
//			
//			
//		}else // for Interface pointers without  
//		if((obj instanceof IJIComObject))
//		{
//			double value = ((IJIComObject)obj).internal_getInterfacePointer().getLength();
//			if (isByRef)
//			{
//				value = value + 4;
//			}
//			
//			value = value + 20 + 4 + 4 + 4; //20 of variant , 4 of the ptr, 4 of max count, 4 of actual count
//			
//			double d = value%8.0;
//			length = (int)value/8;
//			if (d != 0.0)
//			{
//				length++;
//			}
//			//length += 4;
//			//double a = ((IJIComObject)obj).getInterfacePointer().getLength()/8.0;
//			//length = 4 + (int)Math.ceil(a);
//		}
//	
//		
//		return length;
//		
//	}
	
	//returns the length in bytes
	private int getMaxLength2(Class c, Object obj)
	{
		int length = 0; 
		
		//since this is getMaxLength2 and hence will either contain 
		//proper type 3 elements and not EMPTY,NULL,SCODE since these are parts of Variant.
		//and not simple types like Integer, JIUnsignedXXX or Float etc. 
		if (type3.contains(c))
		{
			length = JIMarshalUnMarshalHelper.getLengthInBytes(c, obj, FLAG);
		}
		else
		if(c.equals(Long.class) || c.equals(Double.class) || c.equals(Date.class) || c.equals(JICurrency.class))
		{
			length = 8;
		}
		else
		if(c.equals(JIString.class))
		{
			length = JIMarshalUnMarshalHelper.getLengthInBytes(c, obj, FLAG);
		}else // for Interface pointers without  
		if(obj instanceof IJIComObject)
		{
			double value = ((IJIComObject)obj).internal_getInterfacePointer().getLength();
			value = value + 4 + 4 + 4; //20 of variant , 4 of the ptr, 4 of max count, 4 of actual count
		}
		
		return length;
		
	}
	
//	int getVariantType() throws JIException
//	{
//		return safeArrayStruct == null ? variantType : getArrayLengthForVarType();
//	}
	
//	private int fillArrayType(NetworkDataRepresentation ndr) throws JIException
//	{
//		int length = getArrayLengthForVarType();
//		ndr.writeUnsignedLong(length);
//		return length;
//	}
	
	private int getArrayLengthForVarType() throws JIException
	{
		//now the array will be of variants, nestedArraysRealClass identifies the class itself
		//for iteration we need the variants and then there members.
		
		JIArray objArray = (JIArray)((JIPointer)safeArrayStruct.getMember(7)).getReferent();
		Object[] array = (Object[])objArray.getArrayInstance();
		
		double length = 20;//variant
		if (isByRef)
		{
			length = length + 4;//byref
		}
		
		//SafeArray is 44 
		length += 44 ;
		
		
		boolean isVariantArray = (((Short)safeArrayStruct.getMember(1)).shortValue() & JIVariant.FADF_VARIANT) == JIVariant.FADF_VARIANT ? true : false;

		if (array != null)
		{
			length += 4; //for max count of the array.
			if (isVariantArray)
			{
				//each variant is 3 (size 20 = 20/8 = 3)
				for (int i = 0;i < array.length;i++)
				{
					JIVariant variant = (JIVariant)array[i];
					length += variant.getLengthInBytes(FLAG) ;//* 8;//((VariantBody)(variant.member.getReferent())).variantType * 8;
				}
				
				//now for the "user" pointer part
				//length = length + array.length * 4;
			}
			else
			{
				//normal non variant array has been sent...
				for (int i = 0;i < array.length;i++)
				{
					length += getMaxLength2(array[i].getClass(),array[i]);
				}
			}
		}
		else
		{
			length += 4; //for the null 0000.
		}
		
		int value =  (int) length/8;
		if (length%8.0 != 0)
		{
			value++;
		}
		
		return value;
	}
	
	static VariantBody decode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG,Map additionalData)
	{
		//boolean readLong = false;
		double index = new Integer(ndr.getBuffer().getIndex()).doubleValue(); 
		if (index%8.0 != 0)
		{
			long i = (i=Math.round(index%8.0)) == 0 ? 0 : 8 - i ;
			ndr.readOctetArray(new byte[(int)i],0,(int)i);
		}
		
		int start = ndr.getBuffer().getIndex();
		int length = ndr.readUnsignedLong(); //read the potential length
		ndr.readUnsignedLong(); //read the reserved byte
		
		int variantType = ndr.readUnsignedShort(); //varType
		
		//read reserved bytes
		ndr.readUnsignedShort(); 
		ndr.readUnsignedShort();
		ndr.readUnsignedShort();
		
		ndr.readUnsignedLong(); //32 bit varType
		
		VariantBody variant = null;
		
		List varDefferedPointers = new ArrayList();
		if((variantType & JIVariant.VT_ARRAY) == 0x2000)
		{
			boolean isByRef = (variantType & JIVariant.VT_BYREF) == 0 ? false : true;
			//the struct may be null if the array has nothing
			JIStruct safeArray = getDecodedValueAsArray(ndr,varDefferedPointers,variantType & ~JIVariant.VT_ARRAY,isByRef,additionalData,FLAG);
			int type2 = variantType;
			if (isByRef)
			{
				type2 = type2 & ~JIVariant.VT_BYREF; //so that actual type can be determined
			}
			
			type2 = type2 & 0x0FFF ; 
			int flagofFlags = FLAG;
			if (type2 == JIVariant.VT_INT) 
			{
				flagofFlags |= JIFlags.FLAG_REPRESENTATION_VT_INT;
			}
			else
			if (type2 == JIVariant.VT_UINT) 
			{
				flagofFlags |= JIFlags.FLAG_REPRESENTATION_VT_UINT;
			}
			else
			if (type2 == JIVariant.VT_BOOL) 
			{
				FLAG = flagofFlags |= JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL;
			}	
			
			if(safeArray != null)
			{
				variant = new VariantBody(safeArray, (Class)JIVariant.getSupportedClass(new Integer(type2 & ~JIVariant.VT_ARRAY)),((Object[])((JIArray)safeArray.getMember(8)).getArrayInstance()).length > 1 ? true : false,isByRef,flagofFlags);
			}
			else
			{
				variant = new VariantBody(null, (Class)JIVariant.getSupportedClass(new Integer(type2 & ~JIVariant.VT_ARRAY)),false,isByRef,flagofFlags);
			}

			variant.FLAG = flagofFlags;
			
		}
		else
		{
			boolean isByRef = (variantType & JIVariant.VT_BYREF) == 0 ? false : true;
			variant = new VariantBody(getDecodedValue(ndr,varDefferedPointers,variantType,isByRef,additionalData,FLAG),isByRef,variantType);
			int type2 = variantType & 0x0FFF ; 
			if (type2 == JIVariant.VT_INT) 
			{
				variant.FLAG = JIFlags.FLAG_REPRESENTATION_VT_INT;
			}
			else
			if (type2 == JIVariant.VT_UINT) 
			{
				variant.FLAG = JIFlags.FLAG_REPRESENTATION_VT_UINT;
			}
		}
		
		
		int x = 0;
		while (x < varDefferedPointers.size())
		{
			
			ArrayList newList = new ArrayList();
			JIPointer replacement = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,(JIPointer)varDefferedPointers.get(x),newList,FLAG,additionalData);
			((JIPointer)varDefferedPointers.get(x)).replaceSelfWithNewPointer(replacement); //this should replace the value in the original place.	
			x++;
			varDefferedPointers.addAll(x,newList);
		}

		if (variant.isArray && variant.safeArrayStruct != null)
		{
			//SafeArray have the alignment rule , that all Size <=4 are aligned by 4 and size 8 is aligned by 8.
			//Variant is aligned by 4, Interface pointers are aligned by 4 as well.
			//but this should not exceed the length
			index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			length = length * 8 + start;
			if (index < length)
			{
				JIStruct safeArrayStruct = variant.safeArrayStruct;
				Integer size = (Integer)safeArrayStruct.getMember(2);
				long i = 0;
				if (size.intValue() == 8)
				{
					if (index%8.0 != 0)
					{
						i = (i=Math.round(index%8.0)) == 0 ? 0 : 8 - i ;
						if (index + i <= length)
						{
							ndr.readOctetArray(new byte[(int)i],0,(int)i);
						}
						else
						{
							ndr.readOctetArray(new byte[(length - (int)index)],0,(int)(length - (int)index));
						}
					}
				}
				else
				{
					//align by 4...
					//TODO this needs to be tested for Structs and Unions.
					if (index%4.0 != 0)
					{
						i = (i=Math.round(index%4.0)) == 0 ? 0 : 4 - i ;
						if (index + i <= length)
						{
							ndr.readOctetArray(new byte[(int)i],0,(int)i);
						}
						else
						{
							ndr.readOctetArray(new byte[(length - (int)index)],0,(int)(length - (int)index));
						}
					}
				}
				
				
				
			}
			
			//SafeArray is complete
			JIArray array = null;
			try {
				array = variant.getArray();
			} catch (JIException e) {
				throw new JIRuntimeException(e.getErrorCode());
			} 
			JIVariant variantMain = new JIVariant(array,variant.isByRef,variant.FLAG);
			variant = (VariantBody)variantMain.member.getReferent();
		}
		
		return variant;
	}
	
	
	//Variants need specialised handling and the standard serializers may or maynot be used.
	private static Class getVarClass(int type)
	{
		Class c = null;
		//now first to check if this is a pointer or not.
		type = type & 0x0FFF ; //0x4XXX & 0x0FFF = real type
		switch(type)
		{
			case 0:  //VT_EMPTY , Not specified.
				c = VariantBody.EMPTY.class;
				break;
			case 1:	// VT_NULL , Null.
				c = VariantBody.NULL.class;
				break;
			case 10: 
				c = VariantBody.SCODE.class; //VT_ERROR,Scodes.
				break;
			default:
				c = (Class)JIVariant.getSupportedClass(new Integer(type));
				if (c == null)
				{
					//TODO log this , what has come that i don't support.
				}
			    break;		
		}
		
		return c;
	}

	
	private int getVarType(Class c, Object obj)
	{
		int type = 0; //EMPTY
		
		if (obj instanceof IJIDispatch)
		{
			return JIVariant.VT_DISPATCH;
		}
		
		if (obj instanceof IJIComObject)
		{
			return JIVariant.VT_UNKNOWN;
		}
		
		if (c != null)
		{
			Integer type2 = (Integer)JIVariant.getSupportedType(c,FLAG);
			
			if (type2 != null)
			{
				type = type2.intValue();
			}
			else
			{
				JISystem.getLogger().warning("In getVarType: Unsupported Type found ! " + c + " , please add this to the supportedType map ! ");
				//make that an array of variants
				type2 = (Integer)JIVariant.getSupportedType(JIVariant.class,FLAG);
			}
			
			if (isNull)
			{
				type = 1;
			}
			else if (isScode)
			{
				type = 10; //scode
			}
			else if (isArray)
			{
				type =(int) 0x2000 | type ; //0xC; should not assume an array of variants anymore
			}
		}
		
		if (isByRef && type != 0 && !c.equals(JIArray.class))
		{
			//then it is a pointer. have to set it correctly
			type = type | 0x4000;
		}
		return type;
	}
	
	private static Object getDecodedValue(NetworkDataRepresentation ndr,List defferedPointers, int type, boolean isByRef, Map additionalData, int FLAG)
	{
 
		Object obj = null;
		Class c = getVarClass(type);
		if (c != null)
		{
			if (isByRef)
			{
				ndr.readUnsignedLong(); //Read the Pointer
			} 
			
			if (c.equals(VariantBody.SCODE.class))
			{
				obj = JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,FLAG,additionalData);
				obj = new SCODE(((Integer)obj).intValue());
				type = JIVariant.VT_ERROR;
			}else
			if (c.equals(VariantBody.NULL.class))
			{
				//have read 20 bytes
				//JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL);//read the last 4 bytes, since there could be parameters before this.
				obj = NULL;
				type = JIVariant.VT_NULL;
			}else
			if (c.equals(VariantBody.EMPTY.class)) //empty is 20 bytes
			{
				obj = VariantBody.EMPTY;
				type = JIVariant.VT_EMPTY;
			}else
			if (c.equals(JIString.class))
			{
				obj = new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
				obj = ((JIString)obj).decode(ndr,null,FLAG,additionalData);
			}
			else
				if (c.equals(Boolean.class))
				{
					obj = JIMarshalUnMarshalHelper.deSerialize(ndr,c,defferedPointers,FLAG | JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL,additionalData);
				}
			else
				{
					obj = JIMarshalUnMarshalHelper.deSerialize(ndr,c,defferedPointers,FLAG,additionalData);
				}
		}
		
		return obj;
	}
	
	private static JIStruct getDecodedValueAsArray(NetworkDataRepresentation ndr,List defferedPointers, int type,boolean isByRef,Map additionalData, int FLAG)
	{
		//int newFLAG = FLAG;
		if (isByRef)
		{
			ndr.readUnsignedLong();//read the pointer
			type = type & ~JIVariant.VT_BYREF; //so that actual type can be determined
		}
		
		if (ndr.readUnsignedLong() == 0)//read pointer referent id
		{
			return null;
		}
		
		ndr.readUnsignedLong();//1
		
		JIStruct safeArray = new JIStruct();
		try {
			safeArray.addMember(Short.class);//dim
			
			JIStruct safeArrayBound = new JIStruct();
			safeArrayBound.addMember(Integer.class);
			safeArrayBound.addMember(Integer.class); //starts at 0
			
			safeArray.addMember(Short.class);//flags
			safeArray.addMember(Integer.class);//size
			safeArray.addMember(Short.class);//locks
			safeArray.addMember(Short.class);//locks
			safeArray.addMember(Integer.class);//safearrayunion
			safeArray.addMember(Integer.class);//size in safearrayunion
			
			Class c = (Class)JIVariant.supportedTypes_classes.get(new Integer(type));
			if (c == null)
			{
				if (JISystem.getLogger().isLoggable(Level.WARNING))
				{
					JISystem.getLogger().warning("From JIVariant: while decoding an Array, type " + type + " , was not found in supportedTypes_classes map , hence using JIVariant instead...");
				}
				//not available , lets try with JIVariant.
				//This is a bug, I should have the type.
				c = JIVariant.class;
			}

			if (c == Boolean.class)
			{
				FLAG |= JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL;
			}
			//HARDCODING to JIVariant...kindof forgotten why I even wrote the code below.
			//since all of the examples I have come across always return a Variant array.
			//then why did I typify this thing to it's class (like JIString), it produces an
			//exception when the result is returned back is not an array of strings... 
			//c = JIVariant.class;
			JIArray values = null;
			if (c == JIString.class)
			{
				values = new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),null,1,true);
				safeArray.addMember(new JIPointer(values));//single dimension array, will convert it into the 
				//[] or [][] after inspecting dimension read.
			}
			else
			{
				values = new JIArray(c,null,1,true);
				safeArray.addMember(new JIPointer(values));//single dimension array, will convert it into the 
																						//[] or [][] after inspecting dimension read.
			}
			
			safeArray.addMember(new JIArray(safeArrayBound,null,1,true));
			
			safeArray = (JIStruct)JIMarshalUnMarshalHelper.deSerialize(ndr,safeArray,defferedPointers,FLAG,additionalData);
			
			//now set the right class after examining the flags , only set for JIVariant.class now., the BSTR would already be set previously. 
			Short features = (Short)safeArray.getMember(1);
			//this condition is being kept in the front since the feature flags can be a combination of FADF_VARIANT and the 
			//other flags , in which case the Variant takes priority (since they will all be wrapped as variants).
			if ((features.shortValue() & JIVariant.FADF_VARIANT) == JIVariant.FADF_VARIANT)
			{
				values.updateClazz(JIVariant.class);
			}
			else if (((features.shortValue() & JIVariant.FADF_DISPATCH) == JIVariant.FADF_DISPATCH) ||
					((features.shortValue() & JIVariant.FADF_UNKNOWN) == JIVariant.FADF_UNKNOWN))
			{
				values.updateClazz(IJIComObject.class);
			}
			//For JIStrings , it will be done before these above conditions are examined.
			
			
		} catch (JIException e) {
			throw new JIRuntimeException(e.getErrorCode());
		}
		
		
		return safeArray;
	}
	
	
	private void setValue(NetworkDataRepresentation ndr, Object obj,List defferedPointers,int FLAG)
	{
		if (isNull)
		{
			return; //null , is only 20 bytes
		}
		if (obj != null)
		{
			Class c = obj.getClass();
			
				if (c.equals(EMPTY.class)) //20 bytes
				{
					return;
				}
//				else
//				if (c.equals(Boolean.class))
//				{
//					ndr.writeUnsignedShort(((Boolean)obj).booleanValue() == true ? 0xFFFF: 0x0000);
//					ndr.writeUnsignedShort(0);
//				}
				else
				{
					if(obj instanceof IJIComObject)
					{
						c = IJIComObject.class;
					}
					JIMarshalUnMarshalHelper.serialize(ndr,c,obj,defferedPointers,FLAG);
				}
		}
		else
		{
			
			ndr.writeUnsignedLong(new Object().hashCode());//pointer referentId
			ndr.writeUnsignedLong(1);

			JIMarshalUnMarshalHelper.serialize(ndr,JIStruct.class,safeArrayStruct,defferedPointers,FLAG);
			
			
			
			
		}
	}

	
	
	boolean isArray() {
		return isArray;
	}
	
	int getLengthInBytes()
	{
		if (safeArrayStruct == null && obj.getClass().equals(VariantBody.EMPTY.class))
		{
			return 28;
		}
		
		if (isArray)
		{
			int length = 0;
//			JIArray objArray = (JIArray)((JIPointer)safeArrayStruct.getMember(7)).getReferent();
//			Object[] array = (Object[])objArray.getArrayInstance();
//			for (int i = 0; i < array.length; i++)
//			{
//				Class c = array[i].getClass();
//				length = length + JIMarshalUnMarshalHelper.getLengthInBytes(c,array[i],FLAG);
//			}
//			return length;
			try {
				length = getArrayLengthForVarType() * 8;
			} catch (JIException e) {
				throw new RuntimeException(e);
			}
			
			return length;
		}
		else
		{
			Class c = obj.getClass();
			if(obj instanceof IJIComObject)
			{
				c = IJIComObject.class;
			}
			return 24 + JIMarshalUnMarshalHelper.getLengthInBytes(c,obj,FLAG);
		}
	}
	
	public String toString()
	{
		String retVal = "";
		if (obj == null)
		{
			retVal += "obj is null , ";
		}
		else
		{
			retVal += obj.toString();
		}
		if (isArray)
		{
			if (is2Dimensional)
			{
				retVal += "2 dimensional array , ";
			}
			else
			{
				retVal = "1 dimensional array , ";
			}
			
			if (safeArrayStruct != null)
			{
				retVal += safeArrayStruct.toString();
			}
		}
		
		return retVal;
	}

}
