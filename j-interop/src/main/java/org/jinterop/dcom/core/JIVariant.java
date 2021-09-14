/**
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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
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

/**
 * < p>
 * Class representing the <code>VARIANT</code> datatype.
 * <p>
 * Please use the <code>byRef</code> flag based constructors for <i>by
 * reference</i>
 * parameters in COM calls. For <code>[optional]</code> parameters use the
 * {@link #OPTIONAL_PARAM()}
 * <p>
 * In case of direct calls to COM server using <code>JICallBuilder</code>, if
 * the <code>byRef</code> flag is set then that variant should also be added as
 * the <code>[out]</code> parameter in the <code>JICallBuilder</code>. For
 * developers using the <code>IJIDispatch </code> this is not required and
 * variant would be returned back to them via <code>JIVariant[]</code>
 * associated with <code>IJIDispatch</code> apis.
 * <p>
 *
 * An <b>important</b> note for <code>Boolean</code> Arrays
 * (<code>JIArray</code> of <code>Boolean</code>), please set the
 * <code>JIFlag.FLAG_REPRESENTATION_VARIANT_BOOL</code> using the
 * {@link #setFlag(int)} method before making a call on this object. This is
 * required since in DCOM ,  <code>VARIANT_BOOL</code> are 2 bytes and standard
 * <code>boolean</code>s are 1 byte in length.
 * </p>
 *
 * @since 1.0
 */
public final class JIVariant implements Serializable {

    private static final long serialVersionUID = 5101290038004040628L;

    private static final class EMPTY {
    }

    private static final class NULL {
    }

    private static final class SCODE {
    }

    public static final int VT_NULL = 0x00000001;
    public static final int VT_EMPTY = 0x00000000;
    public static final int VT_I4 = 0x00000003;
    public static final int VT_UI1 = 0x00000011;
    public static final int VT_I2 = 0x00000002;
    public static final int VT_R4 = 0x00000004;
    public static final int VT_R8 = 0x00000005;
    public static final int VT_VARIANT = 0x0000000c;
    public static final int VT_BOOL = 0x0000000b;
    public static final int VT_ERROR = 0x0000000a;
    public static final int VT_CY = 0x00000006;
    public static final int VT_DATE = 0x00000007;
    public static final int VT_BSTR = 0x00000008;
    public static final int VT_UNKNOWN = 0x0000000d;
    public static final int VT_DECIMAL = 0x0000000e;
    public static final int VT_DISPATCH = 0x00000009;
    public static final int VT_ARRAY = 0x00002000;
    public static final int VT_BYREF = 0x00004000;
    public static final int VT_BYREF_VT_UI1 = VT_BYREF | VT_UI1;//0x00004011;
    public static final int VT_BYREF_VT_I2 = VT_BYREF | VT_I2;//0x00004002;
    public static final int VT_BYREF_VT_I4 = VT_BYREF | VT_I4;//0x00004003;
    public static final int VT_BYREF_VT_R4 = VT_BYREF | VT_R4;//0x00004004;
    public static final int VT_BYREF_VT_R8 = VT_BYREF | VT_R8;//0x00004005;
    public static final int VT_BYREF_VT_BOOL = VT_BYREF | VT_BOOL;//0x0000400b;
    public static final int VT_BYREF_VT_ERROR = VT_BYREF | VT_ERROR;//0x0000400a;
    public static final int VT_BYREF_VT_CY = VT_BYREF | VT_CY;//0x00004006;
    public static final int VT_BYREF_VT_DATE = VT_BYREF | VT_DATE;//0x00004007;
    public static final int VT_BYREF_VT_BSTR = VT_BYREF | VT_BSTR;//0x00004008;
    public static final int VT_BYREF_VT_UNKNOWN = VT_BYREF | VT_UNKNOWN;//0x0000400d;
    public static final int VT_BYREF_VT_DISPATCH = VT_BYREF | VT_DISPATCH;//0x00004009;
    public static final int VT_BYREF_VT_ARRAY = VT_BYREF | VT_ARRAY;//0x00006000;
    public static final int VT_BYREF_VT_VARIANT = VT_BYREF | VT_VARIANT;//0x0000400c;

    public static final int VT_I1 = 0x00000010;
    public static final int VT_UI2 = 0x00000012;
    public static final int VT_UI4 = 0x00000013;
    public static final int VT_I8 = 0x00000014;
    public static final int VT_INT = 0x00000016;
    public static final int VT_UINT = 0x00000017;
    public static final int VT_BYREF_VT_DECIMAL = VT_BYREF | VT_DECIMAL;//0x0000400e;
    public static final int VT_BYREF_VT_I1 = VT_BYREF | VT_I1;//0x00004010;
    public static final int VT_BYREF_VT_UI2 = VT_BYREF | VT_UI2;//0x00004012;
    public static final int VT_BYREF_VT_UI4 = VT_BYREF | VT_UI4;//0x00004013;
    public static final int VT_BYREF_VT_I8 = VT_BYREF | VT_I8;//0x00004014;
    public static final int VT_BYREF_VT_INT = VT_BYREF | VT_INT;//0x00004016;
    public static final int VT_BYREF_VT_UINT = VT_BYREF | VT_UINT;//0x00004017;

    public static final int FADF_AUTO = 0x0001;
    /* array is allocated on the stack */
    public static final int FADF_STATIC = 0x0002;
    /* array is staticly allocated */
    public static final int FADF_EMBEDDED = 0x0004;
    /* array is embedded in a structure */
    public static final int FADF_FIXEDSIZE = 0x0010;
    /* may not be resized or reallocated */
    public static final int FADF_RECORD = 0x0020;
    /* an array of records */
    public static final int FADF_HAVEIID = 0x0040;
    /* with FADF_DISPATCH, FADF_UNKNOWN */
 /* array has an IID for interfaces */
    public static final int FADF_HAVEVARTYPE = 0x0080;
    /* array has a VT type */
    public static final int FADF_BSTR = 0x0100;
    /* an array of BSTRs */
    public static final int FADF_UNKNOWN = 0x0200;
    /* an array of IUnknown* */
    public static final int FADF_DISPATCH = 0x0400;
    /* an array of IDispatch* */
    public static final int FADF_VARIANT = 0x0800;
    /* an array of VARIANTs */
    public static final int FADF_RESERVED = 0xF008;
    /* reserved bits */
    private static final Map<Class<?>, Integer> supportedTypes = new HashMap<>();
    private static final Map<Integer, Class<?>> supportedTypes_classes = new HashMap<>();
    private static final Map<Class<?>, Object> outTypesMap = new HashMap<>();

    static {
        //CAUTION NO PTR TYPE SHOULD BE PART OF THIS MAP !!!
        outTypesMap.put(int.class, new Integer(0));
        outTypesMap.put(Integer.class, new Integer(0));
        outTypesMap.put(short.class, new Short((short) 0));
        outTypesMap.put(Short.class, new Short((short) 0));
        outTypesMap.put(float.class, new Float(0.0));
        outTypesMap.put(Float.class, new Float(0.0));
        outTypesMap.put(double.class, new Double(0.0));
        outTypesMap.put(Double.class, new Double(0.0));
        outTypesMap.put(boolean.class, Boolean.FALSE);
        outTypesMap.put(Boolean.class, Boolean.FALSE);
        outTypesMap.put(String.class, "");
        outTypesMap.put(JICurrency.class, new JICurrency("0.0"));
        outTypesMap.put(Date.class, new Date());
        outTypesMap.put(char.class, new Character('9'));
        outTypesMap.put(Character.class, new Character('9'));
        outTypesMap.put(JIUnsignedByte.class, JIUnsignedFactory.getUnsigned(new Short((short) 0), JIFlags.FLAG_REPRESENTATION_UNSIGNED_BYTE));
        outTypesMap.put(JIUnsignedShort.class, JIUnsignedFactory.getUnsigned(new Integer(0), JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT));
        outTypesMap.put(JIUnsignedInteger.class, JIUnsignedFactory.getUnsigned(new Long(0), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT));
        outTypesMap.put(long.class, new Long(0));
        outTypesMap.put(Long.class, new Long(0));

        supportedTypes.put(Object.class, new Integer(VT_VARIANT));
        supportedTypes.put(JIVariant.class, new Integer(VT_VARIANT));
        supportedTypes.put(Integer.class, new Integer(VT_I4));
        supportedTypes.put(JIUnsignedInteger.class, new Integer(VT_UI4));
        supportedTypes.put(Float.class, new Integer(VT_R4));
        supportedTypes.put(Boolean.class, new Integer(VT_BOOL));
        supportedTypes.put(Double.class, new Integer(VT_R8));
        supportedTypes.put(Short.class, new Integer(VT_I2));
        supportedTypes.put(JIUnsignedShort.class, new Integer(VT_UI2));
        supportedTypes.put(Byte.class, new Integer(VT_I1));
        supportedTypes.put(Character.class, new Integer(VT_I1));
        supportedTypes.put(JIUnsignedByte.class, new Integer(VT_UI1));
        supportedTypes.put(JIString.class, new Integer(VT_BSTR));
        // supportedTypes.put(IJIUnknown.class,new Integer(VT_UNKNOWN));
        //supportedTypes.put(IJIDispatch.class,new Integer(VT_DISPATCH));
        supportedTypes.put(JIVariant.SCODE.class, new Integer(VT_ERROR));
        supportedTypes.put(JIVariant.EMPTY.class, new Integer(VT_EMPTY));
        supportedTypes.put(JIVariant.NULL.class, new Integer(VT_NULL));
        supportedTypes.put(VariantBody.SCODE.class, new Integer(VT_ERROR));
        supportedTypes.put(VariantBody.EMPTY.class, new Integer(VT_EMPTY));
        supportedTypes.put(VariantBody.NULL.class, new Integer(VT_NULL));
        supportedTypes.put(JIArray.class, new Integer(VT_ARRAY));
        // supportedTypes.put(JIComObjectImpl.class,new Integer(VT_UNKNOWN));
        // supportedTypes.put(JIDispatchImpl.class,new Integer(VT_DISPATCH));
        supportedTypes.put(Date.class, new Integer(VT_DATE));
        supportedTypes.put(JICurrency.class, new Integer(VT_CY));
        supportedTypes.put(Long.class, new Integer(VT_I8));

        supportedTypes_classes.put(new Integer(VT_DATE), Date.class);
        supportedTypes_classes.put(new Integer(VT_CY), JICurrency.class);
        supportedTypes_classes.put(new Integer(VT_VARIANT), JIVariant.class);
        supportedTypes_classes.put(new Integer(VT_I4), Integer.class);
        supportedTypes_classes.put(new Integer(VT_INT), Integer.class);
        supportedTypes_classes.put(new Integer(VT_UI4), JIUnsignedInteger.class);
        supportedTypes_classes.put(new Integer(VT_UINT), JIUnsignedInteger.class);
        supportedTypes_classes.put(new Integer(VT_R4), Float.class);
        supportedTypes_classes.put(new Integer(VT_BOOL), Boolean.class);
        supportedTypes_classes.put(new Integer(VT_R8), Double.class);
        supportedTypes_classes.put(new Integer(VT_I2), Short.class);
        supportedTypes_classes.put(new Integer(VT_UI2), JIUnsignedShort.class);
        supportedTypes_classes.put(new Integer(VT_I1), Character.class);
        supportedTypes_classes.put(new Integer(VT_UI1), JIUnsignedByte.class);
        supportedTypes_classes.put(new Integer(VT_BSTR), JIString.class);
        supportedTypes_classes.put(new Integer(VT_ERROR), JIVariant.SCODE.class);
        supportedTypes_classes.put(new Integer(VT_EMPTY), EMPTY.class);
        supportedTypes_classes.put(new Integer(VT_NULL), NULL.class);
        supportedTypes_classes.put(new Integer(VT_ARRAY), JIArray.class);
        supportedTypes_classes.put(new Integer(VT_UNKNOWN), IJIComObject.class);
        supportedTypes_classes.put(new Integer(VT_DISPATCH), IJIComObject.class);
        supportedTypes_classes.put(new Integer(VT_I8), Long.class);

        //for by ref types, do it at runtime.
    }

    public static JIVariant OUTPARAMforType(Class c, boolean isArray) {
        JIVariant variant = null;
        if (!isArray) {
            try {
                variant = makeVariant(outTypesMap.get(c), true);
            } catch (Exception e) {
                //eaten and now try from other types

            }

            if (c.equals(IJIDispatch.class)) {
                return OUT_IDISPATCH();
            } else if (c.equals(IJIComObject.class)) {
                return OUT_IUNKNOWN();
            } else if (c.equals(JIVariant.class)) {
                return EMPTY_BYREF();
            } else if (c.equals(JIString.class)) {
                return new JIVariant("", true);
            }
        } else {
            try {
                Object oo = outTypesMap.get(c);
                if (oo != null) {
                    //we will always send a single dimension array.
                    Object x = Array.newInstance(c, 1);
                    Array.set(x, 0, oo);
                    variant = new JIVariant(new JIArray(x, true), true);
                }
            } catch (Exception e) {
                //eaten and now try from other types

            }

            if (c.equals(IJIDispatch.class)) {
                IJIComObject[] arry = new IJIComObject[]{new JIComObjectImpl(null, new JIInterfacePointer(null, -1, null))};
                variant = new JIVariant(new JIArray(arry, true), true);
                variant.setFlag(JIFlags.FLAG_REPRESENTATION_IDISPATCH_NULL_FOR_OUT | JIFlags.FLAG_REPRESENTATION_SET_JIINTERFACEPTR_NULL_FOR_VARIANT);
            } else if (c.equals(IJIComObject.class)) {
                IJIComObject[] arry = new IJIComObject[]{new JIComObjectImpl(null, new JIInterfacePointer(null, -1, null))};
                variant = new JIVariant(new JIArray(arry, true), true);
                variant.setFlag(JIFlags.FLAG_REPRESENTATION_IUNKNOWN_NULL_FOR_OUT | JIFlags.FLAG_REPRESENTATION_SET_JIINTERFACEPTR_NULL_FOR_VARIANT);
            } else if (c.equals(JIVariant.class)) {
                return VARIANTARRAY();
            } else if (c.equals(JIString.class) || c.equals(String.class)) {
                return BSTRARRAY();
            }
        }

        return variant;
    }

    /**
     * Returns a JIVariant (of the right type) based on the
     * <code>o.getClass()</code>
     *
     * @param o
     * @return
     */
    public static JIVariant makeVariant(Object o) {
        return makeVariant(o, false);
    }

    /**
     * Returns a JIVariant (of the right type) based on the
     * <code>o.getClass()</code>
     *
     * @param o
     * @param isByRef
     * @return
     */
    public static JIVariant makeVariant(Object o, boolean isByRef) {
        if (o == null || o.getClass().equals(Object.class)) {
            if (isByRef) {
                return JIVariant.EMPTY_BYREF();
            } else {
                return JIVariant.EMPTY();
            }
        }

        Class c = o.getClass();
        if (c.isArray()) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(0x00001029));
        }

        if (c.equals(JIVariant.class)) {
            return new JIVariant((JIVariant) o);
        }

        try {

            Constructor ctor = null;
            //now we look at the class and return a JIVariant.
            if (c.equals(Boolean.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{boolean.class, boolean.class});
            } else if (c.equals(Character.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{char.class, boolean.class});
            } else if (c.equals(Byte.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{byte.class, boolean.class});
            } else if (c.equals(Short.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{short.class, boolean.class});
            } else if (c.equals(Integer.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{int.class, boolean.class});
            } else if (c.equals(Long.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{long.class, boolean.class});
            } else if (c.equals(Float.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{float.class, boolean.class});
            } else if (c.equals(Double.class)) {
                ctor = JIVariant.class.getConstructor(new Class[]{double.class, boolean.class});
            } else if (o instanceof IJIComObject) {
                ctor = JIVariant.class.getConstructor(new Class[]{IJIComObject.class, boolean.class});
            } else {
                //should cover all the rest cases.
                ctor = JIVariant.class.getConstructor(new Class[]{c, boolean.class});
            }
            return (JIVariant) ctor.newInstance(new Object[]{o, isByRef});
        } catch (Exception e) {
            if (JISystem.getLogger().isLoggable(Level.WARNING)) {
                JISystem.getLogger().log(Level.WARNING, "Could not create Variant for {0} , isByRef {1}", new Object[]{o, isByRef});
            }
        }

        return null;
    }

    static Class getSupportedClass(Integer type) {
        return (Class) supportedTypes_classes.get(type);
    }

    static Integer getSupportedType(Class c, int FLAG) {
        Integer retVal = supportedTypes.get(c);

        if (retVal == null && IJIComObject.class.equals(c)) {
            retVal = VT_UNKNOWN;
        }

        if (retVal == null && IJIDispatch.class.equals(c)) {
            retVal = VT_DISPATCH;
        }
        //means that if retval came back as VT_I4, we should make that VT_INT
        if (retVal == VT_I4
                && (FLAG & JIFlags.FLAG_REPRESENTATION_VT_INT) == JIFlags.FLAG_REPRESENTATION_VT_INT) {
            retVal = VT_INT;
        } else if (retVal == VT_UI4
                && (FLAG & JIFlags.FLAG_REPRESENTATION_VT_UINT) == JIFlags.FLAG_REPRESENTATION_VT_UINT) {
            retVal = VT_UINT;
        }

        return retVal;
    }

    static Integer getSupportedType(Object o, int defaultType) {
        Class c = o.getClass();
        Integer retval = supportedTypes.get(c);

        // Order is important since IJIDispatch derieves from IJIComObject
        if (retval == null && o instanceof IJIDispatch) {
            retval = VT_DISPATCH;
        }

        if (retval == null && o instanceof IJIComObject) {
            retval = VT_UNKNOWN;
        }

        return retval;
    }

    /**
     * EMPTY <code>VARIANT</code>
     */
    static final JIVariant EMPTY = new JIVariant(new EMPTY());

    /**
     * EMPTY <code>VARIANT</code>.This is not Thread Safe,
     * hence a new instance must be taken each time.
     *
     * @return
     */
    public static JIVariant EMPTY() {
        return new JIVariant(new EMPTY());
    }

    /**
     * EMPTY BYREF <code>VARIANT</code>
     */
    static final JIVariant EMPTY_BYREF = new JIVariant(EMPTY);

    /**
     * EMPTY BYREF <code>VARIANT</code>. This is not Thread Safe,
     * hence a new instance must be taken each time.
     * Used for a <code>[out] VARIANT*</code>.
     */
    public static JIVariant EMPTY_BYREF() {
        return new JIVariant(EMPTY());
    }

    /**
     * <code>VARIANT</code> for <code>([out] IUnknown*)</code>. This is not
     * Thread Safe , hence a new instance must be taken each time.
     */
    public static JIVariant OUT_IUNKNOWN() {
        JIVariant retval = new JIVariant(new JIComObjectImpl(null, new JIInterfacePointer(null, -1, null)), true);
        retval.setFlag(JIFlags.FLAG_REPRESENTATION_IUNKNOWN_NULL_FOR_OUT | JIFlags.FLAG_REPRESENTATION_SET_JIINTERFACEPTR_NULL_FOR_VARIANT);
        return retval;
    }

    /**
     * <code>VARIANT</code> for <code>([out] IDispatch*)</code>. This is not
     * Thread Safe , hence a new instance must be taken each time.
     * <p>
     * Note that this must also be used when the interface pointer is a
     * subclass of <code>IDispatch</code> i.e. supports automation (or is a
     * <code>dispinterface</code>).
     */
    public static JIVariant OUT_IDISPATCH() {
        JIVariant retval = new JIVariant(new JIComObjectImpl(null, new JIInterfacePointer(null, -1, null)), true);
        retval.setFlag(JIFlags.FLAG_REPRESENTATION_IDISPATCH_NULL_FOR_OUT | JIFlags.FLAG_REPRESENTATION_SET_JIINTERFACEPTR_NULL_FOR_VARIANT);
        return retval;
    }

    /**
     * NULL <code>VARIANT</code>
     */
    static final JIVariant NULL = new JIVariant(new NULL());

    /**
     * NULL <code>VARIANT</code> . This is not Thread Safe , hence a new
     * instance must be taken each time.
     *
     */
    public static JIVariant NULL() {
        return new JIVariant(new NULL());
    }

    /**
     * OPTIONAL PARAM. Pass this when a parameter is optional for a COM api
     * call.
     */
    static final JIVariant OPTIONAL_PARAM = new JIVariant(JIVariant.SCODE, JIErrorCodes.DISP_E_PARAMNOTFOUND);

    /**
     * OPTIONAL PARAM. Pass this when a parameter is <code>[optional]</code> for
     * a COM call. This is not Thread Safe , hence a new instance must be taken
     * each time.
     *
     */
    public static JIVariant OPTIONAL_PARAM() {
        return new JIVariant(JIVariant.SCODE, JIErrorCodes.DISP_E_PARAMNOTFOUND);
    }

    /**
     * SCODE <code>VARIANT</code>
     */
    public static final SCODE SCODE = new SCODE();

    /**
     * Helper method for creating an array of <code>BSTR</code>s , IDL signature
     * <code>[in, out] SAFEARRAY(BSTR) *p</code>. The return value can directly
     * be used in an <code>IJIDispatch</code>call.
     *
     * @return
     */
    public static JIVariant BSTRARRAY() {
        return new JIVariant(new JIArray(new JIString[]{new JIString("")}, true), true);
    }

    /**
     * Helper method for creating an array of <code>VARIANT</code>s , IDL
     * signature <code>[in, out] SAFEARRAY(VARIANT) *p</code> OR
     * <code>[in,out] VARIANT *pArray</code>. The return value can directly be
     * used in an <code>IJIDispatch</code> call.
     *
     * @return
     */
    public static JIVariant VARIANTARRAY() {
        return new JIVariant(new JIArray(new JIVariant[]{JIVariant.EMPTY()}, true), true);
    }

    JIPointer member = null;

    private JIVariant() {
    }

    //The class of the object determines its type.
    /**
     * Setting up a <code>VARIANT</code> with an object. Used via serializing the <code>VARIANT</code>.
     *
     * @param obj
     */
    private void init(Object obj) {
        init(obj, false);
    }

    private void init(Object obj, boolean isByRef) {
        if (obj != null && obj.getClass().isArray()) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_VARIANT_ONLY_JIARRAY_EXCEPTED));
        }

        if (obj != null && obj.getClass().equals(JIInterfacePointer.class)) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_VARIANT_TYPE_INCORRECT));
        }

        //this case comes only for SCODE and EMPTY, and in these cases the isByRef flag will be set in the
        //previous call itself.
        if (obj instanceof VariantBody) {
            member = new JIPointer(obj);
        } else {
            VariantBody variantBody = new VariantBody(obj, isByRef);
            member = new JIPointer(variantBody);
            //if (obj != null && obj instanceof JIVariant)
            //{
            //  VariantBody var = (VariantBody)((JIVariant)obj).member.getReferent();
            //  try {
            //    variantBody.variantType = var.getVariantType() + 3 + 1;
            //  } catch (JIException e) {
            //    throw new JIRuntimeException(e.getErrorCode());
            //  }
            //}
        }
        member.setReferent(0x72657355);//"User" in LEndian.
    }

    /**
     * Called when this variant is nested
     *
     * @param deffered
     */
    void setDeffered(boolean deffered) {
        if (member != null && !member.isReference()) {
            member.setDeffered(deffered);
        }
    }

    /**
     * Sets a <code>JIFlags</code> value to be used while encoding (marshalling)
     * this Variant.
     *
     * @param FLAG
     */
    public void setFlag(int FLAG) {
        VariantBody variantBody = ((VariantBody) member.getReferent());
        variantBody.FLAG |= FLAG;
    }

    /**
     * Returns the flag value for this variant.
     *
     * @return
     */
    public int getFlag() {
        VariantBody variantBody = ((VariantBody) member.getReferent());
        return variantBody.FLAG;
    }

    /**
     * Returns whether this variant is a <code>NULL</code> variant.
     *
     * @return <code>true</code> if the variant is a <code>NULL</code>
     */
    public boolean isNull() {
        if (member == null) {
            return true;
        }
        VariantBody variantBody = ((VariantBody) member.getReferent());
        return variantBody == null ? true : variantBody.isNull();
    }

    /**
     * Setting up a <code>VARIANT</code> as reference to another. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param variant
     */
    public JIVariant(JIVariant variant) {
        init(variant, true);
    }

    /**
     * Setting up a <code>VARIANT</code> with an <code>int</code>. Used via
     * serializing the <code>VARIANT</code>. Used when the variant type is
     * VT_I4.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. LONG*
     */
    public JIVariant(int value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>long</code>. Used via
     * serializing the <code>VARIANT</code>. Used when the variant type is
     * VT_I8.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer.
     */
    public JIVariant(long value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>float</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. FLOAT*
     */
    public JIVariant(float value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>boolean</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. VARIANT_BOOL*
     */
    public JIVariant(boolean value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>double</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. DOUBLE*
     */
    public JIVariant(double value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>short</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. SHORT*
     */
    public JIVariant(short value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>char</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. CHAR*
     */
    public JIVariant(char value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JIString</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. BSTR*
     */
    public JIVariant(JIString value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>String</code>. Used via
     * serializing the <code>VARIANT</code>. Internally a <code>JIString</code>
     * is formed with it's default type <code>BSTR</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. BSTR*
     */
    public JIVariant(String value, boolean isByRef) {
        init(new JIString(value), isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>String</code>. Used via
     * serializing the <code>VARIANT</code>. Internally a <code>JIString</code>
     * is formed with it's default type <code>BSTR</code>.
     *
     * @param value
     */
    public JIVariant(String value) {
        this(new JIString(value));
    }

    //  /**Setting up a <code>VARIANT</code> with a IJIDispatch. Used via serializing the <code>VARIANT</code>.
    //   *
    //   * @param value
    //   * @param isByRef true if the value is to be represented as a pointer. IJIDispatch**
    //   */
    //  public JIVariant(IJIDispatch value, boolean isByRef)
    //  {
    //    this((Object)value,isByRef);
    //  }
    /**
     * Setting up a <code>VARIANT</code> with an <code>IJIComObject</code>. Used
     * via serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. IJIComObject**
     */
    public JIVariant(IJIComObject value, boolean isByRef) {
        init(value, isByRef);
        if (value instanceof IJIDispatch) {
            setFlag(JIFlags.FLAG_REPRESENTATION_USE_IDISPATCH_IID);
        } else {
            setFlag(JIFlags.FLAG_REPRESENTATION_USE_IUNKNOWN_IID);
        }
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>SCODE</code> value and
     * it's <code>errorCode</code>. Used via serializing the
     * <code>VARIANT</code>.
     *
     *
     * @param value
     * @param errorCode
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. SCODE*
     */
    public JIVariant(SCODE value, int errorCode, boolean isByRef) {
        init(new VariantBody(VariantBody.SCODE, errorCode, isByRef), isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with an <code>int</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(int value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>float</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(float value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a  <code>boolean</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(boolean value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>double</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(double value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>short</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(short value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>char</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(char value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JIString</code>. Used via
     * serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(JIString value) {
        this(value, false);
    }

    //  /**Setting up a <code>VARIANT</code> with a IJIDispatch. Used via serializing the <code>VARIANT</code>.
    //   *
    //   * @param value
    //   */
    //  public JIVariant(IJIDispatch value)
    //  {
    //    this((Object)value);
    //  }
    /**
     * Setting up a <code>VARIANT</code> with an <code>IJIComObject</code>. Used
     * via serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(IJIComObject value) {
        this(value, false);
        if (value instanceof IJIDispatch) {
            setFlag(JIFlags.FLAG_REPRESENTATION_USE_IDISPATCH_IID);
        } else {
            setFlag(JIFlags.FLAG_REPRESENTATION_USE_IUNKNOWN_IID);
        }
    }

    /**
     * Setting up a <code>VARIANT</code> with an <code>java.util.Date</code>.
     * Used via serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(Date value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with an <code>java.util.Date</code>.
     * Used via serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. Date*
     */
    public JIVariant(Date value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JICurrency</code>. Used
     * via serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    public JIVariant(JICurrency value) {
        this(value, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JICurrency</code>. Used
     * via serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer. JICurrency*
     */
    public JIVariant(JICurrency value, boolean isByRef) {
        init(value, isByRef);
    }

    /**
     * Setting up a <code>VARIANT</code> with an <code>EMPTY</code> value. Used
     * via serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    private JIVariant(EMPTY value) {
        init(null);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>NULL</code> value. Used
     * via serializing the <code>VARIANT</code>.
     *
     * @param value
     */
    private JIVariant(NULL value) {
        init(new VariantBody(VariantBody.NULL));
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>SCODE</code> value and
     * it's <code>errorCode</code>. Used via serializing the
     * <code>VARIANT</code>.
     *
     * @param value
     * @param errorCode
     */
    public JIVariant(SCODE value, int errorCode) {
        init(new VariantBody(VariantBody.SCODE, errorCode, false));
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via
     * serializing the <code>VARIANT</code>. Only 1 and 2 dimensional array is
     * supported.
     *
     * @param array
     * @param FLAG JIFlag value
     */
    public JIVariant(JIArray array, int FLAG) {
        this(array, false, FLAG);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via
     * serializing the <code>VARIANT</code>. Only 1 and 2 dimensional array is
     * supported.
     *
     * @param array
     * @param isByRef
     * @param FLAG JIFlag value
     */
    public JIVariant(JIArray array, boolean isByRef, int FLAG) {
        initArrays(array, isByRef, FLAG);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via
     * serializing the <code>VARIANT</code>. Only 1 and 2 dimensional array is
     * supported.
     *
     * @param array
     * @param isByRef
     */
    public JIVariant(JIArray array, boolean isByRef) {
        initArrays(array, isByRef, JIFlags.FLAG_NULL);
    }

    private final static List<Object> ARRYINITS = new ArrayList<>();

    static {
        ARRYINITS.add(JIString.class);
        ARRYINITS.add(JIPointer.class);
        // arryInits.add(JIComObjectImpl.class);
        // arryInits.add(JIDispatchImpl.class);
        // arryInits.add(IJIUnknown.class);
        ARRYINITS.add(IJIComObject.class);
        ARRYINITS.add(IJIDispatch.class); //this can only happen in case of an array
    }

    private void initArrays(JIArray array, boolean isByRef, int FLAG) {
        VariantBody variant2 = null;
        JIArray array2 = null;
        Class c = null;
        Object[] newArrayObj = null;
        boolean is2Dim = false;

        if (array == null) {
            init(null, false);
            return;
        }

        switch (array.getDimensions()) {
            case 1:
                Object[] obj = (Object[]) array.getArrayInstance();
                newArrayObj = obj;
                c = obj.getClass().getComponentType();
                break;
            case 2:
                /* The 2 dimensional array is serialized like this first the index [0,0] and then [1,0] then [0,1] then [1,1], then [0,2] then [1,2]
                 * and so on . so what i will do here is that create a single dimension flat array of the members in the order specified above, after examining this Object[][] and let the
                 * 1 dimension serializing logic take over. */
                Object[][] obj2 = (Object[][]) array.getArrayInstance();
                //variants = new JIVariant[array.getNumElementsInAllDimensions()];

                String name = obj2.getClass().getName();
                Object subArray = obj2;
                name = name.substring(1);
                int firstDim = ((Object[]) subArray).length;
                subArray = Array.get(subArray, 0);
                int secondDim = ((Object[]) subArray).length;
                int k = 0;
                newArrayObj = (Object[]) Array.newInstance(subArray.getClass().getComponentType(), array.getNumElementsInAllDimensions());
                for (int i = 0; i < secondDim; i++) {
                    for (int j = 0; j < firstDim; j++) {
                        newArrayObj[k++] = obj2[j][i];
                    }
                }

                c = subArray.getClass().getComponentType();
                is2Dim = true;
                break;
            default:
                throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_VARIANT_VARARRAYS_2DIMRES));
        }

        array2 = new JIArray(newArrayObj, true); //should always be conformant since this is part of a safe array.

        JIStruct safeArray = new JIStruct();
        try {
            safeArray.addMember((short) array.getDimensions());//dim
            int elementSize;
            short flags = JIVariant.FADF_HAVEVARTYPE;
            if (c.equals(JIVariant.class)) {
                flags = (short) (flags | JIVariant.FADF_VARIANT);
                elementSize = 16; //(Variant is pointer whose size is 16)
            } else if (ARRYINITS.contains(c)) {
                if (c.equals(JIString.class)) {
                    flags = (short) (flags | JIVariant.FADF_BSTR);
                } else if (c.equals(IJIComObject.class)) {
                    flags = (short) (flags | JIVariant.FADF_UNKNOWN);
                    FLAG |= JIFlags.FLAG_REPRESENTATION_USE_IUNKNOWN_IID;
                } else if (c.equals(IJIDispatch.class)) {
                    flags = (short) (flags | JIVariant.FADF_DISPATCH);
                    FLAG |= JIFlags.FLAG_REPRESENTATION_USE_IDISPATCH_IID;
                }
                elementSize = 4; //Since all these are pointers inherently
            } else {
                //JStruct and JIUnions are expected to be encapsulated within pointers...they usually are :)
                elementSize = JIMarshalUnMarshalHelper.getLengthInBytes(c, null, c == Boolean.class ? JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL : JIFlags.FLAG_NULL); //All other types, basic types
            }

            JIStruct safeArrayBound;
            int upperBounds[] = array.getUpperBounds();
            JIStruct[] arrayOfSafeArrayBounds = new JIStruct[array.getDimensions()];
            for (int i = 0; i < array.getDimensions(); i++) {
                safeArrayBound = new JIStruct();
                safeArrayBound.addMember(upperBounds[i]);
                safeArrayBound.addMember(0); //starts at 0
                arrayOfSafeArrayBounds[i] = safeArrayBound;
            }

            JIArray arrayOfSafeArrayBounds2 = new JIArray(arrayOfSafeArrayBounds, true);

            safeArray.addMember(flags);//flags
            if (elementSize > 0) {
                safeArray.addMember(elementSize);
            } else {
                elementSize = JIMarshalUnMarshalHelper.getLengthInBytes(c, null, FLAG);
                safeArray.addMember(elementSize);//size
            }

            safeArray.addMember((short) 0);//locks
            safeArray.addMember(JIVariant.getSupportedType(c, FLAG).shortValue());//variant array, safearrayunion
            //peculiarity here, windows seems to be sending the signed type in VarType32...
            if (c.equals(JIUnsignedByte.class)) {
                safeArray.addMember(JIVariant.getSupportedType(Byte.class, FLAG));//safearrayunion
            } else if (c.equals(JIUnsignedShort.class)) {
                safeArray.addMember(JIVariant.getSupportedType(Short.class, FLAG));//safearrayunion
            } else if (c.equals(JIUnsignedInteger.class)) {
                safeArray.addMember(JIVariant.getSupportedType(Integer.class, FLAG));//safearrayunion
            } else if (c.equals(Boolean.class)) {
                safeArray.addMember(JIVariant.getSupportedType(Short.class, FLAG));//safearrayunion
            } else if (c.equals(Double.class)) {
                safeArray.addMember(JIVariant.getSupportedType(Long.class, FLAG));//safearrayunion
            } else if (c.equals(Float.class)) {
                safeArray.addMember(JIVariant.getSupportedType(Integer.class, FLAG));//safearrayunion
            } else {
                safeArray.addMember(JIVariant.getSupportedType(c, FLAG));//safearrayunion
            }
            safeArray.addMember(array2.getNumElementsInAllDimensions());//size in safearrayunion
            JIPointer ptr2RealArray = new JIPointer(array2);
            safeArray.addMember(ptr2RealArray);
            safeArray.addMember(arrayOfSafeArrayBounds2);
        } catch (JIException e) {
            throw new JIRuntimeException(e.getErrorCode());
        }

        variant2 = new VariantBody(safeArray, c, is2Dim, isByRef, FLAG);
        init(variant2, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>JIArray</code>. Used via
     * serializing the <code>VARIANT</code>. <br>
     * Only 1 and 2 dimensional array is supported.
     *
     * @param array
     */
    public JIVariant(JIArray array) {
        this(array, false);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>unsigned</code> value.
     * Used via serializing the <code>VARIANT</code>.
     *
     * @param number
     */
    public JIVariant(IJIUnsigned number) {
        init(number);
    }

    /**
     * Setting up a <code>VARIANT</code> with a <code>unsigned</code> value.
     * Used via serializing the <code>VARIANT</code>.
     *
     * @param number
     * @param isByRef <code>true</code> if the value is to be represented as a
     * pointer.
     */
    public JIVariant(IJIUnsigned number, boolean isByRef) {
        init(number, isByRef);
    }

    /**
     * Returns the contained object.
     *
     * @return
     * @throws JIException
     */
    public Object getObject() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObject();
    }

    /**
     * Retrieves the contained object as <code>int</code>.
     *
     * @return
     * @throws JIException
     */
    public int getObjectAsInt() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsInt();
    }

    /**
     * Retrieves the contained object as <code>float</code>.
     *
     * @return
     * @throws JIException
     */
    public float getObjectAsFloat() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsFloat();
    }

    /**
     * Retrieves the contained objects errorCode.
     *
     * @return
     * @throws JIException
     */
    public int getObjectAsSCODE() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsSCODE();
    }

    /**
     * Retrieves the contained object as <code>double</code>.
     *
     * @return
     * @throws JIException
     */
    public double getObjectAsDouble() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsDouble();
    }

    /**
     * Retrieves the contained object as <code>short</code>.
     *
     * @return
     * @throws JIException
     */
    public short getObjectAsShort() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsShort();
    }

    /**
     * Retrieves the contained object as <code>boolean</code>.
     *
     * @return
     * @throws JIException
     */
    public boolean getObjectAsBoolean() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsBoolean();
    }

    /**
     * Retrieves the contained object as <code>JIString</code>.
     *
     * @return
     * @throws JIException
     */
    public JIString getObjectAsString() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsString();
    }

    /**
     * Retrieves the contained object as <code>String</code>.
     *
     * @return
     * @throws JIException
     */
    public String getObjectAsString2() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsString().getString();
    }

    /**
     * Retrieves the contained object as <code>java.util.Date</code>.
     *
     * @return
     * @throws JIException
     */
    public Date getObjectAsDate() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsDate();
    }

    /**
     * Retrieves the contained object as <code>char</code>.
     *
     * @return
     * @throws JIException
     */
    public char getObjectAsChar() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsChar();
    }

    public IJIComObject getObjectAsComObject() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsComObject();
    }

    /**
     * Retrieves the contained object as <code>JIVariant</code>.
     *
     * @return
     * @throws JIException
     */
    public JIVariant getObjectAsVariant() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsVariant();
    }

    /**
     * Retrieves the contained object as <code>JIArray</code>. Only 1 and 2 dim
     * arrays are supported currently. Please note that this array is <b>not</b>
     * backed by this variant and is a <b>new</b> copy. If the array is
     * <code>IJIComObject</code>s, please make sure to use
     * <code>JIObjectFactory.narrowObject()</code> to get the right instance.
     *
     * @return
     * @throws JIException
     */
    public JIArray getObjectAsArray() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getArray();
    }

    /**
     * Retrieves the contained object as <code>long</code>, used when the
     * expected type is VT_I8.
     *
     * @return
     * @throws JIException
     */
    public long getObjectAsLong() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsLong();
    }

    /**
     * Retrieves the contained object as <code>unsigned</code> number.
     *
     * @return
     * @throws JIException
     */
    public IJIUnsigned getObjectAsUnsigned() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getObjectAsUnsigned();
    }

    void encode(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, int FLAG) {
        member.setDeffered(true);//this is since this could be part of an array or a struct...for normal calls
        //as soon as this call finishes a call will be given from JICallobject for it's variantbody.
        JIMarshalUnMarshalHelper.serialize(ndr, member.getClass(), member, defferedPointers, FLAG);
    }

    static JIVariant decode(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, int FLAG, Map additionalData) {
        JIVariant variant = new JIVariant();
        JIPointer ref = new JIPointer(VariantBody.class);
        ref.setDeffered(true);//this is since this could be part of an array or a struct...for normal calls
        //as soon as this call finishes a call will be given from JICallobject for it's variantbody.
        variant.member = (JIPointer) JIMarshalUnMarshalHelper.deSerialize(ndr, ref, defferedPointers, FLAG, additionalData);
        return variant;
    }

    public boolean isArray() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).isArray();
    }

    int getLengthInBytes(int FLAG) throws JIException {
        checkValidity();
        return JIMarshalUnMarshalHelper.getLengthInBytes(member.getClass(), member, FLAG);
    }

    public boolean isByRefFlagSet() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).isByRef();
    }

    /**
     * Returns the referent as integer. This can be used along with the
     * <code>JIVariant.VT_<i>XX</i></code> flags to find out the type of the
     * referent.
     * <P>
     * For example :-
     * <p>
     * <code>
     * switch(variant.getType())<br>
     * {<br>
     * case JIVariant.VT_VARIANT: value = variant.getObjectAsVariant();<br>
     * break; <br>
     * case JIVariant.VT_NULL: ... <br>
     * break; <br>
     * }<br>
     * </code>
     *
     * @return
     * @throws JIException
     */
    public int getType() throws JIException {
        checkValidity();
        return ((VariantBody) member.getReferent()).getType();
    }

    private void checkValidity() throws JIException {
        if (member == null || member.isNull()) {
            throw new JIException(JIErrorCodes.JI_VARIANT_IS_NULL);
        }
    }

    @Override
    public String toString() {
        return member == null ? "[null]" : "[" + member.toString() + "]";
    }
}
