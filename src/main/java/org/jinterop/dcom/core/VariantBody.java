/**
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.impls.automation.IJIDispatch;

class VariantBody implements Serializable {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final long serialVersionUID = -8484108480626831102L;
    public static final short VT_PTR = 0x1A;
    public static final short VT_SAFEARRAY = 0x1B;
    public static final short VT_CARRAY = 0x1C;
    public static final short VT_USERDEFINED = 0x1D;

    static final class EMPTY {
    }

    static final class NULL {
    }

    static final class SCODE {

        private int errorCode;

        private SCODE() {
        }

        private SCODE(int errorCode) {
            this.errorCode = errorCode;
        }
    }

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
    private JIStruct safeArrayStruct = null;
    private boolean isArray = false;
    private boolean isScode = false;
    private boolean isNull = false;
    private Class nestedArraysRealClass = null;
    private static final List<Class<?>> type3 = new ArrayList<>();
    private boolean isByRef = false;

    int FLAG = JIFlags.FLAG_NULL;
//  int variantType = 0x1d; //base jump

    static {
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

    boolean isByRef() {
        return isByRef;
    }

    boolean isNull() {
        return isNull;
    }

    int getType() {
        return isArray ? JIVariant.VT_ARRAY | type : type;
    }
    //The class of the object determines its type.

    /**
     * Setting up a <code>VARIANT</code> with an object. Used via serializing
     * the <code>VARIANT</code>.
     *
     * @param referent
     */
    VariantBody(Object referent, boolean isByRef) {
        this(referent, isByRef, -1);
    }

    private VariantBody(Object referent, boolean isByRef, int dataType) {
        this.obj = referent == null ? VariantBody.EMPTY : referent;

        if (obj instanceof JIString && ((JIString) obj).getType() != JIFlags.FLAG_REPRESENTATION_STRING_BSTR) {
            throw new JIRuntimeException(JIErrorCodes.JI_VARIANT_BSTR_ONLY);
        }

        if (obj instanceof Boolean) {
            FLAG = JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL;
        }

        this.isByRef = isByRef;
        //for an unsupported type this could be null
        //but then this is my bug, any thread entering this ctor , will support a type.
        Integer types = JIVariant.getSupportedType(obj, dataType);
        if (types != null) {
            type = types | (isByRef ? JIVariant.VT_BYREF : 0);
        } else {
            throw new JIRuntimeException(JIErrorCodes.JI_VARIANT_UNSUPPORTED_TYPE);
        }
        if (dataType == JIVariant.VT_NULL) {
            isNull = true;
            obj = 0;
        }
    }

    /**
     * Setting up a <code>VARIANT</code> with a NULL value. Used via serializing
     * the <code>VARIANT</code>.
     *
     * @param value
     */
    VariantBody(NULL value) {
        this(new Integer(0), false);
        isNull = true;
        type = JIVariant.VT_NULL;
    }

    /**
     * Setting up a <code>VARIANT</code> with a SCODE value and it's errorCode.
     * Used via serializing the <code>VARIANT</code>.
     *
     * @param value
     * @param errorCode
     */
    VariantBody(SCODE value, int errorCode, boolean isByRef) {
        this(new Integer(errorCode), isByRef);
        isScode = true;
        type = JIVariant.VT_ERROR;
    }

    VariantBody(JIStruct safeArray, Class nestedClass, boolean is2Dimensional, boolean isByRef, int FLAG) {
        this.FLAG = FLAG;
        //can't convert the array here , since this will have deffered pointers which may not be complete.
        safeArrayStruct = safeArray;
        isArray = true;
        if (safeArrayStruct == null) {
            isNull = true;
        }

        this.nestedArraysRealClass = nestedClass;
        this.is2Dimensional = is2Dimensional;
        //please remember JIVariant is a pointer and VariantBody is just the referent part of that.

        //for an unsupported type this could be null
        //but then this is my bug, any thread entering this ctor , will support a type.
        this.isByRef = isByRef;
        Integer types = JIVariant.getSupportedType(nestedClass, FLAG);
        if (types != null) {
            type = types | (isByRef ? JIVariant.VT_BYREF : 0);
        } else {
            throw new JIRuntimeException(JIErrorCodes.JI_VARIANT_UNSUPPORTED_TYPE);
        }
    }

    /**
     * Returns the contained object.
     *
     * @return
     */
    Object getObject() throws JIException {
        return obj == null ? getArray() : obj;
    }

    JIArray getArray() throws JIException {
        JIArray retVal = null;
        //TODO convert it to the right type based on the variantType before returning it.
        //everything is sent encapsulated in a variant(in safearray) , so an Integer[] will
        //go as a variant array for each integer, only the variantType = arry of ints. so convert the
        //array in the right format before returning it to the user. That is he must get Int[] within a JIArray
        //back.
        if (safeArrayStruct != null) {
            retVal = (JIArray) ((JIPointer) safeArrayStruct.getMember(7)).getReferent();

            if (is2Dimensional) {
                Object[] obj3 = (Object[]) retVal.getArrayInstance(); //these will all be variants
                //correct the array here , i.e reform the 2 dimensional array before returning back.
                JIArray safeArrayBound = (JIArray) safeArrayStruct.getMember(8);

                JIStruct[] safeArrayBound2 = (JIStruct[]) safeArrayBound.getArrayInstance();
                //should only be 2 since we support only 2 dim.

                int firstDim = ((Number) safeArrayBound2[0].getMember(0)).intValue();
                int secondDim = ((Number) safeArrayBound2[1].getMember(0)).intValue();

                Object obj = Array.newInstance(nestedArraysRealClass, new int[]{firstDim, secondDim});
                Object[][] obj2 = (Object[][]) obj;
                int k = 0;
                for (int i = 0; i < secondDim; i++) {
                    for (int j = 0; j < firstDim; j++) {
                        //if (nestedArraysRealClass == JIVariant.class)
                        //{
                        //  obj2[j][i] = ((JIVariant[])obj3)[k++];
                        //}
                        //else
                        //{
                        //  obj2[j][i] = ((JIVariant[])obj3)[k++].getObject();
                        //}
                        obj2[j][i] = obj3[k++];
                    }
                }

                retVal = new JIArray(obj2);

            } else {

                if (nestedArraysRealClass != null) {
                    Object[] obj = (Object[]) retVal.getArrayInstance(); //these will all be variants
                    Object obj2 = Array.newInstance(nestedArraysRealClass, obj.length);
                    System.arraycopy(obj, 0, (Object[]) obj2, 0, obj.length);//            if (nestedArraysRealClass == JIVariant.class)
                    // {
                    //   Array.set(obj2,i,((JIVariant[])obj)[i]);//should be the native type
                    // }
                    // else
                    // {
                    //   Array.set(obj2,i,((JIVariant[])obj)[i].getObject());//should be the native type
                    // }
                    //Array.set(obj2,i,obj[i]);
                    retVal = new JIArray(obj2);
                } else {
                    throw new JIException(JIErrorCodes.JI_VARIANT_UNSUPPORTED_TYPE);
                }
            }
        }
        return retVal;
    }

    /**
     * Retrieves the contained object as int.
     *
     * @return
     */
    int getObjectAsInt() {
        try {
            return ((Number) obj).intValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    long getObjectAsLong() {
        try {
            return ((Number) obj).longValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    IJIUnsigned getObjectAsUnsigned() {
        try {
            return ((IJIUnsigned) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    int getObjectAsSCODE() {
        try {
            return ((SCODE) obj).errorCode;
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as float.
     *
     * @return
     */
    float getObjectAsFloat() {
        try {
            return ((Number) obj).floatValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as double.
     *
     * @return
     */
    double getObjectAsDouble() {
        try {
            return ((Number) obj).doubleValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as short.
     *
     * @return
     */
    short getObjectAsShort() {
        try {
            return ((Number) obj).shortValue();
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as boolean.
     *
     * @return
     */
    boolean getObjectAsBoolean() {
        try {
            return ((Boolean) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as JIString.
     *
     * @return
     */
    JIString getObjectAsString() {
        try {
            return ((JIString) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as Date.
     *
     * @return
     */
    Date getObjectAsDate() {
        try {
            return ((Date) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as char.
     *
     * @return
     */
    char getObjectAsChar() {
        try {
            return ((Character) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Retrieves the contained object as Variant.
     *
     * @return
     */
    JIVariant getObjectAsVariant() {
        try {
            return ((JIVariant) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    IJIComObject getObjectAsComObject() {
        try {
            return ((IJIComObject) obj);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    void encode(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, int FLAG) {

//    try
        {
            FLAG |= this.FLAG;
            //align with 8 boundary
            double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
            if (index % 8.0 != 0) {
                long i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
                ndr.writeOctetArray(new byte[(int) i], 0, (int) i);
            }

            int start = ndr.getBuffer().getIndex();

            // if (safeArrayStruct != null)
            // {
            //   //length for the array
            //   length = fillArrayType(ndr);
            // }
            // else
            // {
            //   ndr.writeUnsignedLong(variantType);
            // }
            //just a place holder for length
            ndr.writeUnsignedLong(0xFFFFFFFF);

            ndr.writeUnsignedLong(0);

            //Type
            int varType = getVarType(obj != null ? obj.getClass() : nestedArraysRealClass, obj);

            //For IUnknown , since the inner object is a JIComObjectImpl it will be fine.
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_IDISPATCH_NULL_FOR_OUT) == JIFlags.FLAG_REPRESENTATION_IDISPATCH_NULL_FOR_OUT) {
                varType = isByRef ? 0x4000 | JIVariant.VT_DISPATCH : JIVariant.VT_DISPATCH;
            }
            ndr.writeUnsignedShort(varType);

            //reserved bytes
            ndr.writeUnsignedSmall(0xCC);
            ndr.writeUnsignedSmall(0xCC);
            ndr.writeUnsignedSmall(0xCC);
            ndr.writeUnsignedSmall(0xCC);
            ndr.writeUnsignedSmall(0xCC);
            ndr.writeUnsignedSmall(0xCC);

            if (obj != null) {
                ndr.writeUnsignedLong(varType);
            } else {
                if (!isByRef) {
                    ndr.writeUnsignedLong(JIVariant.VT_ARRAY);
                } else {
                    ndr.writeUnsignedLong(JIVariant.VT_BYREF_VT_ARRAY);
                }
            }

            if (isByRef) {
                int flag = -1;
                if (isArray) //object arrays will come here....
                {
                    flag = 4;
                } else {
                    //no idea what these flags are but 0x10 is for variant, 0x8 for date, and 0x4 is for others
                    switch (type) {
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
            List<JIPointer> varDefferedPointers = new ArrayList<>();
            //we should use FLAG here, since the decision should be based on this only.
            setValue(ndr, obj, varDefferedPointers, FLAG);
            //making changes to write the deffered pointers here itself , since we need to put the entire Variant completed to the length
            //as in varType.
            int x = 0;
            while (x < varDefferedPointers.size()) {
                List<JIPointer> newList = new ArrayList<>();
                JIMarshalUnMarshalHelper.serialize(ndr, JIPointer.class, varDefferedPointers.get(x), newList, FLAG);
                x++; //incrementing index
                varDefferedPointers.addAll(x, newList);
            }

            int currentIndex = ndr.getBuffer().getIndex();
            int length = currentIndex - start;
            int value = length / 8;
            if (length % 8.0 != 0) { //entire variant is aligned by 8 bytes.
                value++;
            }
            ndr.getBuffer().setIndex(start);
            ndr.writeUnsignedLong(value);
            ndr.getBuffer().setIndex(currentIndex);

            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "Variant length is {0} , value {1} , variant type{2}", new Object[]{length, value, type});
            }
            // if (safeArrayStruct != null && isArray)
            // {
            //   //SafeArray have the alignment rule , that all Size <=4 are aligned by 4 and size 8 is aligned by 8.
            //   //Variant is aligned by 4, Interface pointers are aligned by 4 as well.
            //   //but this should not exceed the length
            //   index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
            //   length = length * 8 + start;
            //   if (index < length)
            //   {
            //     Integer size = (Integer)safeArrayStruct.getMember(2);
            //     long i = 0;
            //     if (size.intValue() == 8)
            //     {
            //       if (index%8.0 != 0)
            //       {
            //         i = (i=Math.round(index%8.0)) == 0 ? 0 : 8 - i ;
            //         if (index + i <= length)
            //         {
            //           ndr.writeOctetArray(new byte[(int)i],0,(int)i);
            //         }
            //         else
            //         {
            //           ndr.writeOctetArray(new byte[(length - (int)index)],0,(int)(length - (int)index));
            //         }
            //       }
            //     }
            //     else
            //     {
            //       //align by 4...
            //       //TODO this needs to be tested for Structs and Unions.
            //       if (index%4.0 != 0)
            //       {
            //         i = (i=Math.round(index%4.0)) == 0 ? 0 : 4 - i ;
            //         if (index + i <= length)
            //         {
            //           ndr.writeOctetArray(new byte[(int)i],0,(int)i);
            //         }
            //         else
            //         {
            //           ndr.writeOctetArray(new byte[(length - (int)index)],0,(int)(length - (int)index));
            //         }
            //       }
            //     }
            //
            //
            //   }
            // }
        }
        //catch (JIException e)
        //{
        //  throw new JIRuntimeException(e.getErrorCode());
        //}
    }

    //multiple of 8.
    //  private int getMaxLength(Class c, boolean isByRef, Object obj)
    //  {
    //    int length = 3; //Empty
    //    if (type3.contains(c))
    //    {
    //      length = 3;
    //      if (isByRef)
    //      {
    //        length = length + 1; //for the pointer
    //      }
    //    }
    //    else
    //    if(c.equals(Long.class) || c.equals(Double.class) || c.equals(Date.class) || c.equals(JICurrency.class))
    //    {
    //      length = 4;
    //      //here the byref can be left out since it will cover 24 bytes properly
    //    }
    //    else
    //    if(c.equals(JIString.class))
    //    {
    //
    //      int strlen = 0;
    //      if (obj != null && ((JIString)obj).getString() != null)
    //      {
    //        strlen = ((JIString)obj).getString().length();
    //      }
    //
    //      //20 is of variant, 4+4+4+4 of bstr(user,maxlen,actlen,offset) , (strlen*2) of the actual array
    //      double value = 20 + 16 + strlen*2;
    //      if (isByRef)
    //      {
    //        value = value + 4;
    //      }
    //      double d = value%8.0;
    //      length = (int)value/8;
    //      if (d != 0.0)
    //      {
    //        length++;
    //      }
    //
    //
    //    }else // for Interface pointers without
    //    if((obj instanceof IJIComObject))
    //    {
    //      double value = ((IJIComObject)obj).internal_getInterfacePointer().getLength();
    //      if (isByRef)
    //      {
    //        value = value + 4;
    //      }
    //
    //      value = value + 20 + 4 + 4 + 4; //20 of variant , 4 of the ptr, 4 of max count, 4 of actual count
    //
    //      double d = value%8.0;
    //      length = (int)value/8;
    //      if (d != 0.0)
    //      {
    //        length++;
    //      }
    //      //length += 4;
    //      //double a = ((IJIComObject)obj).getInterfacePointer().getLength()/8.0;
    //      //length = 4 + (int)Math.ceil(a);
    //    }
    //
    //
    //    return length;
    //
    //  }
    //returns the length in bytes
    private int getMaxLength2(Class c, Object obj) {
        int length = 0;

        //since this is getMaxLength2 and hence will either contain
        //proper type 3 elements and not EMPTY,NULL,SCODE since these are parts of Variant.
        //and not simple types like Integer, JIUnsignedXXX or Float etc.
        if (type3.contains(c)) {
            length = JIMarshalUnMarshalHelper.getLengthInBytes(c, obj, FLAG);
        } else if (c.equals(Long.class) || c.equals(Double.class) || c.equals(Date.class) || c.equals(JICurrency.class)) {
            length = 8;
        } else if (c.equals(JIString.class)) {
            length = JIMarshalUnMarshalHelper.getLengthInBytes(c, obj, FLAG);
        } else // for Interface pointers without
        if (obj instanceof IJIComObject) {
            double value = ((IJIComObject) obj).internal_getInterfacePointer().getLength();
            value = value + 4 + 4 + 4; //20 of variant , 4 of the ptr, 4 of max count, 4 of actual count
        }

        return length;

    }

    //  int getVariantType() throws JIException
    //  {
    //    return safeArrayStruct == null ? variantType : getArrayLengthForVarType();
    //  }
    //  private int fillArrayType(NetworkDataRepresentation ndr) throws JIException
    //  {
    //    int length = getArrayLengthForVarType();
    //    ndr.writeUnsignedLong(length);
    //    return length;
    //  }
    private int getArrayLengthForVarType() throws JIException {
        //now the array will be of variants, nestedArraysRealClass identifies the class itself
        //for iteration we need the variants and then there members.

        JIArray objArray = (JIArray) ((JIPointer) safeArrayStruct.getMember(7)).getReferent();
        Object[] array = (Object[]) objArray.getArrayInstance();

        double length = 20;//variant
        if (isByRef) {
            length += 4;//byref
        }

        //SafeArray is 44
        length += 44;

        boolean isVariantArray = (((Short) safeArrayStruct.getMember(1)) & JIVariant.FADF_VARIANT) == JIVariant.FADF_VARIANT;

        if (array != null) {
            length += 4; //for max count of the array.
            if (isVariantArray) {
                //each variant is 3 (size 20 = 20/8 = 3)
                for (Object array1 : array) {
                    JIVariant variant = (JIVariant) array1;
                    length += variant.getLengthInBytes(FLAG);//* 8;//((VariantBody)(variant.member.getReferent())).variantType * 8;
                }
                //now for the "user" pointer part
                //length = length + array.length * 4;
            } else {
                //normal non variant array has been sent...
                for (Object array1 : array) {
                    length += getMaxLength2(array1.getClass(), array1);
                }
            }
        } else {
            length += 4; //for the null 0000.
        }

        int value = (int) length / 8;
        if (length % 8.0 != 0) {
            value++;
        }

        return value;
    }

    static VariantBody decode(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, int FLAG, Map additionalData) {
        //boolean readLong = false;
        double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
        if (index % 8.0 != 0) {
            long i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
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

        VariantBody variant;
        List<JIPointer> varDefferedPointers = new ArrayList<>();
        if ((variantType & JIVariant.VT_ARRAY) == 0x2000) {
            boolean isByRef = (variantType & JIVariant.VT_BYREF) != 0;
            //the struct may be null if the array has nothing
            JIStruct safeArray = getDecodedValueAsArray(ndr, varDefferedPointers, variantType & ~JIVariant.VT_ARRAY, isByRef, additionalData, FLAG);
            int type2 = variantType;
            if (isByRef) {
                type2 &= ~JIVariant.VT_BYREF; //so that actual type can be determined
            }

            type2 &= 0x0FFF;
            int flagofFlags = FLAG;
            if (type2 == JIVariant.VT_INT) {
                flagofFlags |= JIFlags.FLAG_REPRESENTATION_VT_INT;
            } else if (type2 == JIVariant.VT_UINT) {
                flagofFlags |= JIFlags.FLAG_REPRESENTATION_VT_UINT;
            } else if (type2 == JIVariant.VT_BOOL) {
                FLAG = flagofFlags |= JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL;
            }

            if (safeArray != null) {
                variant = new VariantBody(safeArray, JIVariant.getSupportedClass(type2 & ~JIVariant.VT_ARRAY), (((Object[]) ((JIArray) safeArray.getMember(8)).getArrayInstance()).length > 1), isByRef, flagofFlags);
            } else {
                variant = new VariantBody(null, JIVariant.getSupportedClass(type2 & ~JIVariant.VT_ARRAY), false, isByRef, flagofFlags);
            }

            variant.FLAG = flagofFlags;

        } else {
            boolean isByRef = (variantType & JIVariant.VT_BYREF) != 0;
            variant = new VariantBody(getDecodedValue(ndr, varDefferedPointers, variantType, isByRef, additionalData, FLAG), isByRef, variantType);
            int type2 = variantType & 0x0FFF;
            if (type2 == JIVariant.VT_INT) {
                variant.FLAG = JIFlags.FLAG_REPRESENTATION_VT_INT;
            } else if (type2 == JIVariant.VT_UINT) {
                variant.FLAG = JIFlags.FLAG_REPRESENTATION_VT_UINT;
            }
        }

        int x = 0;
        while (x < varDefferedPointers.size()) {

            List<JIPointer> newList = new ArrayList<>();
            JIPointer replacement = (JIPointer) JIMarshalUnMarshalHelper.deSerialize(ndr, varDefferedPointers.get(x), newList, FLAG, additionalData);
            varDefferedPointers.get(x).replaceSelfWithNewPointer(replacement); //this should replace the value in the original place.
            x++;
            varDefferedPointers.addAll(x, newList);
        }

        if (variant.isArray && variant.safeArrayStruct != null) {
            //SafeArray have the alignment rule , that all Size <=4 are aligned by 4 and size 8 is aligned by 8.
            //Variant is aligned by 4, Interface pointers are aligned by 4 as well.
            //but this should not exceed the length
            index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
            length = length * 8 + start;
            if (index < length) {
                JIStruct safeArrayStruct = variant.safeArrayStruct;
                Integer size = (Integer) safeArrayStruct.getMember(2);
                long i = 0;
                if (size == 8) {
                    if (index % 8.0 != 0) {
                        i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
                        if (index + i <= length) {
                            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
                        } else {
                            ndr.readOctetArray(new byte[(length - (int) index)], 0, (length - (int) index));
                        }
                    }
                } else {
                    //align by 4...
                    //TODO this needs to be tested for Structs and Unions.
                    if (index % 4.0 != 0) {
                        i = (i = Math.round(index % 4.0)) == 0 ? 0 : 4 - i;
                        if (index + i <= length) {
                            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
                        } else {
                            ndr.readOctetArray(new byte[(length - (int) index)], 0, (length - (int) index));
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
            JIVariant variantMain = new JIVariant(array, variant.isByRef, variant.FLAG);
            variant = (VariantBody) variantMain.member.getReferent();
        }

        return variant;
    }

    //Variants need specialised handling and the standard serializers may or maynot be used.
    private static Class getVarClass(int type) {
        Class c = null;
        //now first to check if this is a pointer or not.
        type &= 0x0FFF; //0x4XXX & 0x0FFF = real type
        switch (type) {
            case 0:  //VT_EMPTY , Not specified.
                c = VariantBody.EMPTY.class;
                break;
            case 1:  // VT_NULL , Null.
                c = VariantBody.NULL.class;
                break;
            case 10:
                c = VariantBody.SCODE.class; //VT_ERROR,Scodes.
                break;
            default:
                c = JIVariant.getSupportedClass(type);
                if (c == null) {
                    //TODO log this , what has come that i don't support.
                }
                break;
        }

        return c;
    }

    private int getVarType(Class c, Object obj) {
        int type = 0; //EMPTY

        if (obj instanceof IJIDispatch) {
            return isByRef ? 0x4000 | JIVariant.VT_DISPATCH : JIVariant.VT_DISPATCH;
        }

        if (obj instanceof IJIComObject) {
            return isByRef ? 0x4000 | JIVariant.VT_UNKNOWN : JIVariant.VT_UNKNOWN;
        }

        if (c != null) {
            Integer type2 = JIVariant.getSupportedType(c, FLAG);

            if (type2 != null) {
                type = type2;
            } else {
                LOGGER.log(Level.WARNING, "In getVarType: Unsupported Type found ! {0} , please add this to the supportedType map ! ", c);
                //make that an array of variants
                type2 = JIVariant.getSupportedType(JIVariant.class, FLAG);
            }

            if (isNull) {
                type = 1;
            } else if (isScode) {
                type = 10; //scode
            } else if (isArray) {
                type = (int) 0x2000 | type; //0xC; should not assume an array of variants anymore
            }
        }

        if (isByRef && type != 0 && !c.equals(JIArray.class)) {
            //then it is a pointer. have to set it correctly
            type |= 0x4000;
        }
        return type;
    }

    private static Object getDecodedValue(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, int type, boolean isByRef, Map additionalData, int FLAG) {

        Object obj = null;
        Class c = getVarClass(type);
        if (c != null) {
            if (isByRef) {
                ndr.readUnsignedLong(); //Read the Pointer
            }

            if (c.equals(VariantBody.SCODE.class)) {
                obj = JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, FLAG, additionalData);
                obj = new SCODE(((Number) obj).intValue());
                type = JIVariant.VT_ERROR;
            } else if (c.equals(VariantBody.NULL.class)) {
                //have read 20 bytes
                //JIMarshalUnMarshalHelper.deSerialize(ndr,Integer.class,null,JIFlags.FLAG_NULL);//read the last 4 bytes, since there could be parameters before this.
                obj = NULL;
                type = JIVariant.VT_NULL;
            } else if (c.equals(VariantBody.EMPTY.class)) //empty is 20 bytes
            {
                obj = VariantBody.EMPTY;
                type = JIVariant.VT_EMPTY;
            } else if (c.equals(JIString.class)) {
                obj = new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
                obj = ((JIString) obj).decode(ndr, null, FLAG, additionalData);
            } else if (c.equals(Boolean.class)) {
                obj = JIMarshalUnMarshalHelper.deSerialize(ndr, c, defferedPointers, FLAG | JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL, additionalData);
            } else {
                obj = JIMarshalUnMarshalHelper.deSerialize(ndr, c, defferedPointers, FLAG, additionalData);
            }
        }

        return obj;
    }

    private static JIStruct getDecodedValueAsArray(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, int type, boolean isByRef, Map additionalData, int FLAG) {
        //int newFLAG = FLAG;
        if (isByRef) {
            ndr.readUnsignedLong();//read the pointer
            type &= ~JIVariant.VT_BYREF; //so that actual type can be determined
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

            Class c = JIVariant.getSupportedClass(type);
            if (c == null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "From JIVariant: while decoding an Array, type {0} , was not found in supportedTypes_classes map , hence using JIVariant instead...", type);
                }
                //not available , lets try with JIVariant.
                //This is a bug, I should have the type.
                c = JIVariant.class;
            }

            if (c == Boolean.class) {
                FLAG |= JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL;
            }
            //HARDCODING to JIVariant...kindof forgotten why I even wrote the code below.
            //since all of the examples I have come across always return a Variant array.
            //then why did I typify this thing to it's class (like JIString), it produces an
            //exception when the result is returned back is not an array of strings...
            //c = JIVariant.class;
            JIArray values = null;
            if (c == JIString.class) {
                values = new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR), null, 1, true);
                safeArray.addMember(new JIPointer(values));//single dimension array, will convert it into the
                //[] or [][] after inspecting dimension read.
            } else {
                values = new JIArray(c, null, 1, true);
                safeArray.addMember(new JIPointer(values));//single dimension array, will convert it into the
                //[] or [][] after inspecting dimension read.
            }

            safeArray.addMember(new JIArray(safeArrayBound, null, 1, true));

            safeArray = (JIStruct) JIMarshalUnMarshalHelper.deSerialize(ndr, safeArray, defferedPointers, FLAG, additionalData);

            //now set the right class after examining the flags , only set for JIVariant.class now., the BSTR would already be set previously.
            Short features = (Short) safeArray.getMember(1);
            //this condition is being kept in the front since the feature flags can be a combination of FADF_VARIANT and the
            //other flags , in which case the Variant takes priority (since they will all be wrapped as variants).
            if ((features & JIVariant.FADF_VARIANT) == JIVariant.FADF_VARIANT) {
                values.updateClazz(JIVariant.class);
            } else if (((features & JIVariant.FADF_DISPATCH) == JIVariant.FADF_DISPATCH)
                    || ((features & JIVariant.FADF_UNKNOWN) == JIVariant.FADF_UNKNOWN)) {
                values.updateClazz(IJIComObject.class);
            }
            //For JIStrings , it will be done before these above conditions are examined.

        } catch (JIException e) {
            throw new JIRuntimeException(e.getErrorCode());
        }

        return safeArray;
    }

    private void setValue(NetworkDataRepresentation ndr, Object obj, List<JIPointer> defferedPointers, int FLAG) {
        if (isNull) {
            return; //null , is only 20 bytes
        }
        if (obj != null) {
            Class c = obj.getClass();

            if (c.equals(EMPTY.class)) //20 bytes
            {
            } // else
            // if (c.equals(Boolean.class))
            // {
            //   ndr.writeUnsignedShort(((Boolean)obj).booleanValue() == true ? 0xFFFF: 0x0000);
            //   ndr.writeUnsignedShort(0);
            // }
            else {
                if (obj instanceof IJIComObject) {
                    c = IJIComObject.class;
                }
                JIMarshalUnMarshalHelper.serialize(ndr, c, obj, defferedPointers, FLAG);
            }
        } else {

            ndr.writeUnsignedLong(new Object().hashCode());//pointer referentId
            ndr.writeUnsignedLong(1);

            JIMarshalUnMarshalHelper.serialize(ndr, JIStruct.class, safeArrayStruct, defferedPointers, FLAG);

        }
    }

    boolean isArray() {
        return isArray;
    }

    int getLengthInBytes() {
        if (safeArrayStruct == null && obj.getClass().equals(VariantBody.EMPTY.class)) {
            return 28;
        }

        if (isArray) {
            int length = 0;
            try {
                length = getArrayLengthForVarType() * 8;
            } catch (JIException e) {
                throw new RuntimeException(e);
            }
            return length;
        } else {
            Class c = obj.getClass();

            if (obj instanceof IJIComObject) {
                c = IJIComObject.class;
            } else if (c.equals(VariantBody.SCODE.class)) {
                return 24 + 4; //4 for integer scode.
            } else if (c.equals(VariantBody.NULL.class) || c.equals(VariantBody.EMPTY.class)) {
                return 24;
            }

            return 24 + JIMarshalUnMarshalHelper.getLengthInBytes(c, obj, FLAG);
        }
    }

    @Override
    public String toString() {
        String retVal = "";
        if (obj == null) {
            retVal += "obj is null , ";
        } else {
            retVal += obj.toString();
        }
        if (isArray) {
            if (is2Dimensional) {
                retVal += "2 dimensional array , ";
            } else {
                retVal = "1 dimensional array , ";
            }

            if (safeArrayStruct != null) {
                retVal += safeArrayStruct.toString();
            }
        }
        return retVal;
    }
}
