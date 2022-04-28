/** j-Interop (Pure Java implementation of DCOM protocol)
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JISystem;

/**
 * Represents a C++ array which can display both <i>conformant and standard</i>
 * behaviors. Since this class forms a wrapper on the actual array, the
 * developer is expected to provide complete and final arrays (of Objects) to
 * this class. Modifying the wrapped array afterwards <b>will</b> have
 * unexpected results.
 * <p>
 * <i>Please refer to <b>MSExcel</b> examples for more details on how to use
 * this class.</i>
 * <p>
 * <b>Note</b>: Wrapped Arrays can be at most two dimensional in nature. Above
 * that is not supported by the library.
 *
 * @since 1.0
 */
public final class JIArray implements Serializable {

    private static final long serialVersionUID = -8267477025978489665L;
    private Object memberArray = null;
    private Class<?> clazz = null;
    private int[] upperBounds = null;
    private int dimension = -1;
    private int numElementsInAllDimensions = 0;
    private boolean isConformant = false;
    private boolean isVarying = false;
    private boolean isConformantProxy = false;
    private boolean isVaryingProxy = false;
    private List<Integer> conformantMaxCounts = new ArrayList<>(); //list of integers
    private Object template = null;
    private int sizeOfNestedArrayInBytes = 0; //used in both encoding and decoding.

    private JIArray() {

    }

    /**
     * Creates an array object of the type specified by <code>clazz</code>. This
     * is used to prepare a template for decoding an array of that type. Used
     * only for setting as an <code>[out]</code> parameter in a JICallBuilder.
     * <p>
     * For example:- <br>
     * This call creates a template for a single dimension Integer array of size
     * 10.
     * <code>
     * JIArray array = new JIArray(Integer.class,new int[]{10},1,false);
     * </code>
     *
     * @param clazz class whose instances will be members of the deserialized
     * array.
     * @param upperBounds highest index for each dimension.
     * @param dimension number of dimensions
     * @param isConformant declares whether the array is <i>conformant</i> or
     * not.
     * @throws IllegalArgumentException if <code>upperBounds</code> is supplied
     * and its length is not equal to the <code>dimension</code> parameter.
     */
    public JIArray(Class<?> clazz, int[] upperBounds, int dimension, boolean isConformant) {
        this.clazz = clazz;
        init2(upperBounds, dimension, isConformant, false);
    }

    /**
     * <p>
     * Refer to {@link #JIArray(Class, int[], int, boolean)}
     *
     * @param clazz class whose instances will be members of the deserialized
     * array.
     * @param upperBounds highest index for each dimension.
     * @param dimension number of dimensions
     * @param isConformant declares whether the array is <i>conformant</i> or not.
     * @param isVarying declares whether the array is <i>varying</i> or not.
     * @throws IllegalArgumentException if <code>upperBounds</code> is supplied
     * and its length is not equal to the <code>dimension</code> parameter.
     *
     */
    public JIArray(Class<?> clazz, int[] upperBounds, int dimension, boolean isConformant, boolean isVarying) {
        this.clazz = clazz;
        init2(upperBounds, dimension, isConformant, isVarying);
    }

    /**
     * <p>
     * Creates an array object with members of the type <code>template</code>.
     * This constructor is used to prepare a template for decoding an array and
     * is exclusively for composites like <code>JIStruct</code>,
     * <code>JIPointer</code>, <code>JIUnion</code>, <code>JIString</code> where
     * more information on the structure of the composite is required before
     * trying to deserialize it.
     *
     * <p>
     *
     * Sample Usage:-
     * <br>
     * <code>
     *  JIStruct safeArrayBounds = new JIStruct(); <br>
     * safeArrayBounds.addMember(Integer.class); <br>
     * safeArrayBounds.addMember(Integer.class); <br><br>
     *
     *  //arraydesc <br>
     * JIStruct arrayDesc = new JIStruct(); <br>
     *  //typedesc <br>
     * JIStruct typeDesc = new JIStruct(); <br><br>
     *
     * arrayDesc.addMember(typeDesc);<br>
     * arrayDesc.addMember(Short.class);<br>
     * arrayDesc.addMember(<b>new JIArray(safeArrayBounds,new
     * int[]{1},1,true)</b>);<br>
     * </code>
     * </p>
     *
     * @param template can be only of the type <code>JIStruct</code>,
     * <code>JIPointer</code>, <code>JIUnion</code>, <code>JIString</code>
     * @param upperBounds highest index for each dimension.
     * @param dimension number of dimensions
     * @param isConformant declares whether the array is <i>conformant</i> or
     * not.
     * @throws IllegalArgumentException if <code>upperBounds</code> is supplied
     * and its length is not equal to the <code>dimension</code> parameter.
     * @throws IllegalArgumentException if <code>template</code> is null or is
     * not of the specified types.
     */
    //for structs, pointers , unions.
    public JIArray(Object template, int[] upperBounds, int dimension, boolean isConformant) {
        if (template == null) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_TEMPLATE_NULL));
        }

        if (!template.getClass().equals(JIStruct.class) && !template.getClass().equals(JIUnion.class)
                && !template.getClass().equals(JIPointer.class) && !template.getClass().equals(JIString.class)) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_INCORRECT_TEMPLATE_PARAM));
        }

        this.template = template;
        this.clazz = template.getClass();

        init2(upperBounds, dimension, isConformant, false);
    }

    /**
     * Refer to {@link #JIArray(Object, int[], int, boolean)} for details.
     *
     * @param template can be only of the type <code>JIStruct</code>,
     * <code>JIPointer</code>, <code>JIUnion</code>, <code>JIString</code>
     * @param upperBounds highest index for each dimension.
     * @param dimension number of dimensions
     * @param isConformant declares whether the array is <i>conformant</i> or
     * not.
     * @param isVarying declares whether the array is <i>varying</i> or not.
     * @throws IllegalArgumentException if <code>upperBounds</code> is supplied
     * and its length is not equal to the <code>dimension</code> parameter.
     * @throws IllegalArgumentException if <code>template</code> is null or is
     * not of the specified types.
     */
    //for structs, pointers , unions.
    public JIArray(Object template, int[] upperBounds, int dimension, boolean isConformant, boolean isVarying) {
        if (template == null) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_TEMPLATE_NULL));
        }

        if (!template.getClass().equals(JIStruct.class) && !template.getClass().equals(JIUnion.class)
                && !template.getClass().equals(JIPointer.class) && !template.getClass().equals(JIString.class)) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_INCORRECT_TEMPLATE_PARAM));
        }

        this.template = template;
        this.clazz = template.getClass();

        init2(upperBounds, dimension, isConformant, isVarying);
    }

    private void init2(int[] upperBounds, int dimension, boolean isConformant, boolean isVarying) {
        this.upperBounds = upperBounds;
        this.dimension = dimension;
        this.isConformant = isConformant;
        this.isConformantProxy = isConformant;
        this.isVarying = isVarying;
        this.isVaryingProxy = isVarying;

        if (upperBounds != null) {
            //have to supply the upperbounds for each dimension , no gaps in between
            if (upperBounds.length != dimension) {
                throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_UPPERBNDS_DIM_NOTMATCH));
            }
        }

        for (int i = 0; upperBounds != null && i < upperBounds.length; i++) {
            numElementsInAllDimensions += upperBounds[i];
            if (isConformant) {
                conformantMaxCounts.add(upperBounds[i]);
            }
        }
        //numElementsInAllDimensions = numElementsInAllDimensions * dimension;
    }

    /**
     * Creates an object with <i>array</i> parameter as the nested Array. This
     * constructor is used when the developer wants to send an array to COM
     * server.
     * <p>
     * Sample Usage :
     * <br>
     * <code>
     * JIArray array = new JIArray(new JIString[]{new JIString(name)},true);
     * </code>
     *
     * @param array Array of any type. Primitive arrays are not allowed.
     * @param isConformant declares whether the array is <code>conformant</code>
     * or not.
     * @throws IllegalArgumentException if the <code>array</code> is not an
     * array or is of primitive type or is an array of
     * <code>java.lang.Object</code>.
     */
    public JIArray(Object array, boolean isConformant) {
        this.isConformant = isConformant;
        this.isConformantProxy = isConformant;
        init(array);
    }

    /**
     * Refer {@link #JIArray(Object, boolean)}
     *
     * @param array Array of any type. Primitive arrays are not allowed.
     * @param isConformant declares whether the array is <code>conformant</code>
     * or not.
     * @param isVarying declares whether the array is <code>varying</code> or
     * not.
     * @throws IllegalArgumentException if the <code>array</code> is not an
     * array or is of primitive type or is an array of
     * <code>java.lang.Object</code>.
     */
    public JIArray(Object array, boolean isConformant, boolean isVarying) {
        this.isConformant = isConformant;
        this.isConformantProxy = isConformant;
        this.isVarying = isVarying;
        this.isVaryingProxy = isVarying;
        init(array);
    }

    /**
     * Creates an object with <i>array</i> parameter as the nested Array. This
     * constructor forms a <code>non-conformant</code> array and is used when
     * the developer wants to send an array to COM server.
     * <p>
     * Sample Usage :
     * <code>
     * JIArray array = new JIArray(new JIString[]{new JIString(name)},true);
     * </code>
     *
     * @param array Array of any type. Primitive arrays are not allowed.
     * @throws IllegalArgumentException if the <code>array</code> is not an
     * array or is of primitive type or is an array of <code>java.lang.Object</code>.
     */
    public JIArray(Object array) {
        init(array);
    }

    private void init(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_PARAM_ONLY));
        }

        if (array.getClass().isPrimitive()) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_PRIMITIVE_NOTACCEPT));
        }

        //bad way...but what the heck...
        if (array.getClass().toString().indexOf("java.lang.Object") != -1) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_ARRAY_TYPE_INCORRECT));
        }

        this.memberArray = array;

        List<Integer> upperBounds2 = new ArrayList<>();
        String name = array.getClass().getName();
        Object subArray = array;
        numElementsInAllDimensions = 1;
        while (name.startsWith("[")) {
            name = name.substring(1);
            int x = ((Object[]) subArray).length;
            upperBounds2.add(x);
            numElementsInAllDimensions *= x;
            if (isConformant) {
                conformantMaxCounts.add(x);
            }
            clazz = subArray.getClass().getComponentType();
            if (x == 0) //In which ever index the length is 0 , the array stops there, example Byte[0],Byte[0][10],Byte[10][0]
            {
                break;
            }
            subArray = Array.get(subArray, 0);
            dimension++;
        }

        if (dimension == -1) {
            numElementsInAllDimensions = 0;
            dimension++;
        }

        upperBounds = new int[upperBounds2.size()];
        for (int i = 0; i < upperBounds2.size(); i++) {
            upperBounds[i] = upperBounds2.get(i);
        }
        dimension++; //since it starts from -1.
        sizeOfNestedArrayInBytes = computeLengthArray(array);
    }

    private int computeLengthArray(Object array) {
        int length = 0;
        String name = array.getClass().getName();
        Object o[] = (Object[]) array;
        for (int i = 0; i < o.length; i++) {
            if (name.charAt(1) != '[') {
                Object o1[] = (Object[]) array;
                for (final Object o11 : o1) {
                    length += JIMarshalUnMarshalHelper.getLengthInBytes(o1.getClass().getComponentType(), o11, JIFlags.FLAG_NULL);
                }
                return length;
            }
            length += computeLengthArray(Array.get(array, i));
        }

        return length;
    }

    /**
     * Returns the nested Array.
     *
     * @return array Object which can be type casted based on value returned by
     * {@link #getArrayClass()}.
     */
    public Object getArrayInstance() {
        return memberArray;
    }

    /**
     * Class of the nested Array.
     *
     * @return <code>class</code>
     */
    public Class<?> getArrayClass() {
        return clazz;
    }

    /**
     * Array of integers depicting highest index for each dimension.
     *
     * @return <code>int[]</code>
     */
    public int[] getUpperBounds() {
        return upperBounds;
    }

    /**
     * Returns the dimensions of the Array.
     *
     * @return <code>int</code>
     */
    public int getDimensions() {
        return dimension;
    }

    int getSizeOfAllElementsInBytes() {
        //int length = numElementsInAllDimensions * JIMarshalUnMarshalHelper.getLengthInBytes(clazz,((Object[])memberArray)[0],JIFlags.FLAG_NULL);
        //this means that decode has created this array, and we need to compute the size to stay consistent.
        if (sizeOfNestedArrayInBytes == -1) {
            sizeOfNestedArrayInBytes = computeLengthArray(memberArray);
        }

        return sizeOfNestedArrayInBytes;
    }

    void encode(NetworkDataRepresentation ndr, Object array, List<JIPointer> defferedPointers, int FLAG) {
        //ArrayList listofDefferedPointers = new ArrayList();
        if (isConformantProxy) {
            //first write the max counts ...First to last dimension.
            int i = 0;
            while (i < conformantMaxCounts.size()) {
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, conformantMaxCounts.get(i), defferedPointers, FLAG);
                i++;
            }

            isConformantProxy = false; //this is since encode is recursive.
        }

        if (isVaryingProxy) {
            //write the offset and the actual count
            int i = 0;
            while (i < conformantMaxCounts.size()) {
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, defferedPointers, FLAG);//offset
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, conformantMaxCounts.get(i), defferedPointers, FLAG);//actual count
                i++;
            }

            isVaryingProxy = false; //this is since encode is recursive.
        }

        String name = array.getClass().getName();
        Object o[] = (Object[]) array;
        for (int i = 0; i < o.length; i++) {
            if (name.charAt(1) != '[') {
                Object o1[] = (Object[]) array;
                for (Object o11 : o1) {
                    JIMarshalUnMarshalHelper.serialize(ndr, clazz, o11, defferedPointers, FLAG | JIFlags.FLAG_REPRESENTATION_ARRAY);
                }
                return;
            }
            encode(ndr, Array.get(array, i), defferedPointers, FLAG);
        }

    }

    /**
     * Status whether the array is <code>conformant</code> or not.
     *
     * @return <code>true</code> is array is <code>conformant</code>.
     */
    public boolean isConformant() {
        return isConformant;
    }

    /**
     * Status whether the array is <code>varying</code> or not.
     *
     * @return <code>true</code> is array is <code>varying</code>.
     */
    public boolean isVarying() {
        return isVarying;
    }

    Object decode(NetworkDataRepresentation ndr, Class arrayType, int dimension, List<JIPointer> defferedPointers, int FLAG, Map additionalData) {
        JIArray retVal = new JIArray();
        retVal.isConformantProxy = isConformantProxy;
        retVal.isVaryingProxy = isVaryingProxy;
        if (isConformantProxy) {

            //first read the max counts ...First to last dimension.
            int i = 0;
            while (i < dimension) {
                retVal.conformantMaxCounts.add((Integer) JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, defferedPointers, FLAG, additionalData));
                i++;
            }

            //isConformantProxy = false; //this is since decode is recursive.
            if (upperBounds == null) {
                //max elements will come now.
                retVal.numElementsInAllDimensions = 0;
                retVal.upperBounds = new int[retVal.conformantMaxCounts.size()];
                i = 0;
                while (i < retVal.conformantMaxCounts.size()) {
                    retVal.upperBounds[i] = ((Number) retVal.conformantMaxCounts.get(i)).intValue();
                    retVal.numElementsInAllDimensions *= retVal.upperBounds[i];
                    i++;
                }
                if (i == 0) {
                    numElementsInAllDimensions = 0;
                }
                //retVal.numElementsInAllDimensions = retVal.numElementsInAllDimensions * dimension;
            }
        } else {//this is the case when it is non conformant or coming from struct.
            retVal.upperBounds = upperBounds;
            retVal.conformantMaxCounts = conformantMaxCounts;
            retVal.numElementsInAllDimensions = numElementsInAllDimensions;
        }

        if (isVaryingProxy) {
            //first read the max counts ...First to last dimension.
            int i = 0;
            retVal.conformantMaxCounts.clear();//can't take the max count size now
            retVal.upperBounds = null;
            retVal.numElementsInAllDimensions = 0;

            while (i < dimension) {
                JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, defferedPointers, FLAG, null);///offset
                retVal.conformantMaxCounts.add((Integer) JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, defferedPointers, FLAG, additionalData));//actual count
                i++;
            }

            //isConformantProxy = false; //this is since decode is recursive.
            if (upperBounds == null) {
                //max elements will come now.
                retVal.numElementsInAllDimensions = 1;
                retVal.upperBounds = new int[retVal.conformantMaxCounts.size()];
                i = 0;
                while (i < retVal.conformantMaxCounts.size()) {
                    retVal.upperBounds[i] = ((Number) retVal.conformantMaxCounts.get(i)).intValue();
                    retVal.numElementsInAllDimensions *= retVal.upperBounds[i];
                    i++;
                }
                if (i == 0) {
                    numElementsInAllDimensions = 0;
                }
                //retVal.numElementsInAllDimensions = retVal.numElementsInAllDimensions * dimension;
            }

        }

        retVal.isConformant = isConformant;
        retVal.isVarying = isVarying;
        retVal.template = template;
        retVal.memberArray = recurseDecode(retVal, ndr, arrayType, dimension, defferedPointers, FLAG, additionalData);
        retVal.clazz = clazz;
        retVal.dimension = this.dimension;
        retVal.sizeOfNestedArrayInBytes = -1; // setting here so that when a call actually comes for it's lenght , the getLength will compute. This is required since while decoding many pointers are still not complete and their length cannot be decided.
        return retVal;
    }

    private Object recurseDecode(JIArray retVal, NetworkDataRepresentation ndr, Class arrayType, int dimension, List<JIPointer> defferedPointers, int FLAG, Map additionalData) {
        Object array = null;
        Class c = arrayType;
        for (int j = 0; j < dimension; j++) {
            array = Array.newInstance(c, retVal.upperBounds[retVal.upperBounds.length - j - 1]);
            c = array.getClass();
        }

        for (int i = 0; i < retVal.upperBounds[retVal.upperBounds.length - dimension]; i++) {
            if (dimension == 1) {
                //fill value here
                //Array.set(array,i,new Float(i));
                if (template == null) {
                    Array.set(array, i, JIMarshalUnMarshalHelper.deSerialize(ndr, c.getComponentType() == null ? c : c.getComponentType(), defferedPointers, FLAG | JIFlags.FLAG_REPRESENTATION_ARRAY, additionalData));
                } else {
                    Array.set(array, i, JIMarshalUnMarshalHelper.deSerialize(ndr, template, defferedPointers, FLAG | JIFlags.FLAG_REPRESENTATION_ARRAY, additionalData));
                }
            } else {
                Array.set(array, i, recurseDecode(retVal, ndr, arrayType, dimension - 1, defferedPointers, FLAG, additionalData));
            }
        }

        return array;
    }

    /**
     * Reverses Array elements for IJIDispatch.
     *
     * @return
     */
    int reverseArrayForDispatch() {
        if (memberArray == null) {
            return 0;
        }

        int i = 0;
        Stack stack = new Stack();
        for (i = 0; i < ((Object[]) memberArray).length; i++) {
            stack.push(((Object[]) memberArray)[i]);
        }

        i = 0;
        while (!stack.isEmpty()) {
            ((Object[]) memberArray)[i++] = stack.pop();
        }

        return i;
    }

    List getConformantMaxCounts() {
        return conformantMaxCounts;
    }

    void setConformant(boolean isConformant) {
        isConformantProxy = isConformant;
    }

    void setVarying(boolean isVarying) {
        isVaryingProxy = isVarying;
    }

    void setMaxCountAndUpperBounds(List maxCount) {
        conformantMaxCounts = maxCount;
        //if (upperBounds == null) this will always be null since this api will get called from a decode and
        //in that the upperBounds is always null, since one does not know the dim expected.
        if (!conformantMaxCounts.isEmpty()) {
            //max elements will come now.
            numElementsInAllDimensions = 1;
            upperBounds = new int[conformantMaxCounts.size()];
            int i = 0;
            while (i < conformantMaxCounts.size()) {
                upperBounds[i] = ((Number) conformantMaxCounts.get(i)).intValue();
                numElementsInAllDimensions *= upperBounds[i];
                i++;
            }
            if (i == 0) {
                numElementsInAllDimensions = 0;
            }
        } else {
            upperBounds = null;
            numElementsInAllDimensions = 0;
        }
    }

    int getNumElementsInAllDimensions() {
        return numElementsInAllDimensions;
    }

    /**
     * <p>
     * Used only from the JIVariant.getDecodedValueAsArray. It is required when
     * the real class of the array is determined after the SafeArray Struct has
     * been processed. SA in COM can contain these along with normal types as
     * well :- FADF_BSTR 0x0100 An array of BSTRs. <br>
     * FADF_UNKNOWN 0x0200 An array of IUnknown*. <br>
     * FADF_DISPATCH 0x0400 An array of IDispatch*.  <br>
     * FADF_VARIANT 0x0800 An array of VARIANTs. <br>
     *
     * I have noticed that the "type" of the array doesn't always convey the
     * right thing, so this "feature" flag of the SA shas to be looked into. As
     * can be seen above except only BSTR require a template others do not. But
     * the logic for the JIString(BSTR) already works fine. So I will use this
     * flag only to set the JIVariant.class , whereever the "type" does not
     * specify it but the "feature" does.
     * </p>
     *
     * @exclude
     * @param c
     */
    void updateClazz(Class c) {
        clazz = c;
    }

    @Override
    public String toString() {
        String retVal = "[Type: " + clazz + " , ";
        if (memberArray == null) {
            retVal += "memberArray is null , ";
        } else {
            retVal += memberArray + " , ";
        }

        if (isConformant) {
            retVal += " conformant , ";
        }
        if (isVarying) {
            retVal += " varying , ";
        }

        return retVal + "]";
    }
}
