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

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import jcifs.util.Encdec;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import rpc.core.UUID;

final class JIMarshalUnMarshalHelper {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final Map<Class<?>, SerializerDeserializer> mapOfSerializers = new HashMap<>();

    //TODO This is very important , please note that arrays in C++ have a fixed size and unlike Java have to be
    //declared with there Max index right in the beginning. therefore all arrays (of any type) , will
    //already come padded here to there Max size., this has to be ensured by the caller
    // Basically the index on COMs side should match with the array length here...otherwise exception
    // will come. This has to be managed by IDL generator.
    static {
        mapOfSerializers.put(Date.class, new JIMarshalUnMarshalHelper.DateImpl());
        mapOfSerializers.put(JICurrency.class, new JIMarshalUnMarshalHelper.JICurrencyImpl());
        mapOfSerializers.put(VariantBody.class, new JIMarshalUnMarshalHelper.JIVariant2Impl());
        mapOfSerializers.put(JIVariant.class, new JIMarshalUnMarshalHelper.JIVariantImpl());
        mapOfSerializers.put(Double.class, new JIMarshalUnMarshalHelper.DoubleImpl());
        mapOfSerializers.put(Boolean.class, new JIMarshalUnMarshalHelper.BooleanImpl());
        mapOfSerializers.put(Short.class, new JIMarshalUnMarshalHelper.ShortImpl());
        mapOfSerializers.put(Integer.class, new JIMarshalUnMarshalHelper.IntegerImpl());
        mapOfSerializers.put(Float.class, new JIMarshalUnMarshalHelper.FloatImpl());
        mapOfSerializers.put(String.class, new JIMarshalUnMarshalHelper.StringImpl());
        mapOfSerializers.put(UUID.class, new JIMarshalUnMarshalHelper.UUIDImpl());
        mapOfSerializers.put(Byte.class, new JIMarshalUnMarshalHelper.ByteImpl());
        mapOfSerializers.put(Long.class, new JIMarshalUnMarshalHelper.LongImpl());//LONG , 8 bytes, written as 4+4 in LE.
        mapOfSerializers.put(Character.class, new JIMarshalUnMarshalHelper.CharacterImpl());
        mapOfSerializers.put(JIInterfacePointer.class, new JIMarshalUnMarshalHelper.MInterfacePointerImpl());
        mapOfSerializers.put(JIInterfacePointerBody.class, new JIMarshalUnMarshalHelper.MInterfacePointerImpl2());
        mapOfSerializers.put(IJIDispatch.class, new JIMarshalUnMarshalHelper.IJIComObjectSerDer());
        mapOfSerializers.put(IJIComObject.class, new JIMarshalUnMarshalHelper.IJIComObjectSerDer());
        mapOfSerializers.put(JIPointer.class, new JIMarshalUnMarshalHelper.PointerImpl());
        mapOfSerializers.put(JIStruct.class, new JIMarshalUnMarshalHelper.StructImpl());
        mapOfSerializers.put(JIUnion.class, new JIMarshalUnMarshalHelper.UnionImpl());
        mapOfSerializers.put(JIString.class, new JIMarshalUnMarshalHelper.JIStringImpl());
        mapOfSerializers.put(JIUnsignedByte.class, new JIMarshalUnMarshalHelper.JIUnsignedByteImpl());
        mapOfSerializers.put(JIUnsignedShort.class, new JIMarshalUnMarshalHelper.JIUnsignedShortImpl());
        mapOfSerializers.put(JIUnsignedInteger.class, new JIMarshalUnMarshalHelper.JIUnsignedIntImpl());
        //mapOfSerializers.put(IJIUnsigned.class,new JIMarshalUnMarshalHelper.JIUnsignedImpl());
    }

    static byte[] readOctetArrayLE(NetworkDataRepresentation ndr, int length) {
        byte[] bytes = new byte[8];
        ndr.readOctetArray(bytes, 0, 8);
        for (int i = 0; i < 4; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[7 - i];
            bytes[7 - i] = t;
        }
        return bytes;
    }

    static void writeOctetArrayLE(NetworkDataRepresentation ndr, byte[] b) {
        for (int i = 0; i < b.length; i++) {
            ndr.writeUnsignedSmall(b[b.length - i - 1]);
        }
    }

    static <T> void serialize(NetworkDataRepresentation ndr, Class<?> c, T value, List<JIPointer> defferedPointers, int FLAG) {
        if (c.equals(JIArray.class)) {
            ((JIArray) value).encode(ndr, ((JIArray) value).getArrayInstance(), defferedPointers, FLAG);
        } else {
            if ((c != IJIComObject.class || c != IJIDispatch.class) && value instanceof IJIComObject) {
                c = IJIComObject.class;
            }
            alignMemberWhileEncoding(ndr, c, value);

            if (c.equals(JIString.class)) {
                ((JIString) value).encode(ndr, defferedPointers, FLAG);
                return;
            }
            if (c.equals(JIPointer.class)) {
                ((JIPointer) value).encode(ndr, defferedPointers, FLAG);
                return;
            }
            if (c.equals(JIStruct.class)) {
                ((JIStruct) value).encode(ndr, defferedPointers, FLAG);
                return;
            }
            if (c.equals(JIUnion.class)) {
                ((JIUnion) value).encode(ndr, defferedPointers, FLAG);
                return;
            }
            //if (c.equals(JIDispatchImpl.class) || c.equals(IJIDispatch.class))
            //{
            //  IJIComObject unknown = ((JIDispatchImpl)value).getCOMObject();
            //  JIInterfacePointer interfacePointer = new JIInterfacePointer(IJIDispatch.IID,unknown.getInterfacePointer());
            //  interfacePointer.encode(ndr,defferedPointers,FLAG);
            //  return ;
            //}
            //
            //if (c.equals(JIComObjectImpl.class) || c.equals(IJIComObject.class) || c.equals(IJIUnknown.class))
            //{
            //  JIInterfacePointer interfacePointer = ((IJIComObject)value).getInterfacePointer();
            //  interfacePointer.encode(ndr,defferedPointers,FLAG);
            //  return ;
            //}
            if (c.equals(JIInterfacePointer.class)) {
                ((JIInterfacePointer) value).encode(ndr, defferedPointers, FLAG);
                return;
            }

            if (c.equals(JIVariant.class)) {
                ((JIVariant) value).encode(ndr, defferedPointers, FLAG);
                return;
            }

            if (c.equals(VariantBody.class)) {
                ((VariantBody) value).encode(ndr, defferedPointers, FLAG);
                return;
            }
            if (mapOfSerializers.get(c) == null) {
                throw new IllegalStateException(MessageFormat.format(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_SERDESER_NOT_FOUND), c.toString()));
            }
            mapOfSerializers.get(c).serializeData(ndr, value, defferedPointers, FLAG);
        }
    }

    private static <T> void alignMemberWhileEncoding(NetworkDataRepresentation ndr, Class<?> c, T obj) {
        double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
        if (c.equals(JIStruct.class)) {
            double align = new Integer(((JIStruct) obj).getAlignment()).doubleValue();
            long i = (long) ((i = Math.round(index % align)) == 0 ? 0 : align - i);
            ndr.writeOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(JIUnion.class)) {
            double align = new Integer(((JIUnion) obj).getAlignment()).doubleValue();
            long i = (long) ((i = Math.round(index % align)) == 0 ? 0 : align - i);
            ndr.writeOctetArray(new byte[(int) i], 0, (int) i);
        } else if (/* c.equals(Character.class) || c.equals(Byte.class) || */c.equals(Integer.class)
                || c.equals(Float.class)
                || c.equals(JIVariant.class)
                || c.equals(String.class)
                || c.equals(JIPointer.class)) {
            //align with 4 bytes
            long i = (i = Math.round(index % 4.0)) == 0 ? 0 : 4 - i;
            ndr.writeOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(Double.class)) {
            //align with 8
            long i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
            ndr.writeOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(Short.class)) {
            long i = (i = Math.round(index % 2.0)) == 0 ? 0 : 2 - i;
            ndr.writeOctetArray(new byte[(int) i], 0, (int) i);
        }
    }

    private static <T> void alignMemberWhileDecoding(NetworkDataRepresentation ndr, Class<?> c, T obj) {
        double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
        if (c.equals(JIStruct.class)) {
            double align = new Integer(((JIStruct) obj).getAlignment()).doubleValue();
            long i = (long) ((i = Math.round(index % align)) == 0 ? 0 : align - i);
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(JIUnion.class)) {
            double align = new Integer(((JIUnion) obj).getAlignment()).doubleValue();
            long i = (long) ((i = Math.round(index % align)) == 0 ? 0 : align - i);
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(Integer.class) || c.equals(Float.class)
                || c.equals(JIVariant.class)
                || c.equals(String.class)
                || c.equals(JIPointer.class)) {
            //align with 4 bytes
            long i = (i = Math.round(index % 4.0)) == 0 ? 0 : 4 - i;
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(Double.class)) {
            //align with 8
            long i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
        } else if (c.equals(Short.class)) {
            long i = (i = Math.round(index % 2.0)) == 0 ? 0 : 2 - i;
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);
        }
    }

    static Object deSerialize(NetworkDataRepresentation ndr, Object obj, List<JIPointer> defferedPointers, int FLAG, Map additionalData) {
        Class<?> c = obj instanceof Class ? (Class) obj : obj.getClass();
        if (c.equals(JIArray.class)) {
            return ((JIArray) obj).decode(ndr, ((JIArray) obj).getArrayClass(), ((JIArray) obj).getDimensions(), defferedPointers, FLAG, additionalData);
        } else {
            alignMemberWhileDecoding(ndr, c, obj);

            if (c.equals(JIPointer.class)) {
                JIPointer retVal = ((JIPointer) obj).decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            if (c.equals(JIStruct.class)) {
                JIStruct retVal = ((JIStruct) obj).decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            if (c.equals(JIUnion.class)) {
                JIUnion retVal = ((JIUnion) obj).decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            if (c.equals(JIString.class)) {
                JIString retVal = ((JIString) obj).decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            //This will always be a class
            if (obj.equals(JIInterfacePointer.class)) {
                JIInterfacePointer retVal = JIInterfacePointer.decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            //This will always be a class
            if (obj.equals(JIVariant.class)) {
                JIVariant retVal = JIVariant.decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            //This will always be a class
            if (obj.equals(VariantBody.class)) {
                VariantBody retVal = VariantBody.decode(ndr, defferedPointers, FLAG, additionalData);
                return retVal;
            }

            if (mapOfSerializers.get(obj) == null) {
                throw new IllegalStateException(MessageFormat.format(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_SERDESER_NOT_FOUND), obj.toString()));
            }
            return mapOfSerializers.get(obj).deserializeData(ndr, defferedPointers, additionalData, FLAG);
        }
    }

    static int getLengthInBytes(Class c, Object obj, int FLAG) {
        if (obj != null && obj.getClass().equals(JIArray.class)) {
            return ((JIArray) obj).getSizeOfAllElementsInBytes();
        } else {
            if ((c != IJIComObject.class || c != IJIDispatch.class) && obj instanceof IJIComObject) {
                c = IJIComObject.class;
            }

            if (((SerializerDeserializer) mapOfSerializers.get(c)) == null) {
                throw new IllegalStateException(MessageFormat.format(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_SERDESER_NOT_FOUND), c.toString()));
            }
            return ((SerializerDeserializer) mapOfSerializers.get(c)).getLengthInBytes(obj, FLAG);
        }

    }

    private interface SerializerDeserializer {

        void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG);

        Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG);

        int getLengthInBytes(Object value, int FLAG);
    }

    private static class PointerImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return ((JIPointer) value).getLength();
        }

    }

    private static class JIUnsignedIntImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, ((IJIUnsigned) value).getValue().intValue(), null, FLAG);
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Integer b = (Integer) JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, FLAG, additionalData);
            return JIUnsignedFactory.getUnsigned((b & 0xFFFFFFFFL), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 4;
        }

    }

    private static class JIUnsignedByteImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            JIMarshalUnMarshalHelper.serialize(ndr, Byte.class, ((IJIUnsigned) value).getValue().byteValue(), null, FLAG);
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Byte b = (Byte) JIMarshalUnMarshalHelper.deSerialize(ndr, Byte.class, null, FLAG, additionalData);
            return JIUnsignedFactory.getUnsigned((short) (b & 0xFF), JIFlags.FLAG_REPRESENTATION_UNSIGNED_BYTE);
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 1;
        }

    }

    private static class JIUnsignedShortImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            JIMarshalUnMarshalHelper.serialize(ndr, Short.class, ((IJIUnsigned) value).getValue().shortValue(), null, FLAG);
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Short b = (Short) JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, FLAG, additionalData);
            return JIUnsignedFactory.getUnsigned((b & 0xFFFF), JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT);
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 2;
        }
    }

    private static class StructImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return ((JIStruct) value).getLength();
        }

    }

    private static class UnionImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return ((JIUnion) value).getLength();
        }

    }

    private static class IJIComObjectSerDer implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            serialize(ndr, JIInterfacePointer.class, ((IJIComObject) value).internal_getInterfacePointer(), defferedPointers, FLAG);
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            JISession session = (JISession) additionalData.get(JICallBuilder.CURRENTSESSION);
            IJIComObject comObject = new JIComObjectImpl(session, (JIInterfacePointer) deSerialize(ndr, JIInterfacePointer.class, defferedPointers, FLAG, additionalData));
            ((Collection) additionalData.get(JICallBuilder.COMOBJECTS)).add(comObject);
            return comObject;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            JIInterfacePointer interfacePointer = ((IJIComObject) value).internal_getInterfacePointer();
            return interfacePointer.getLength();
        }

    }

    private static class JIVariant2Impl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return ((VariantBody) value).getLengthInBytes();
        }

    }

    private static class JIVariantImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            //4 for pointer and rest for variant2
            try {
                return ((JIVariant) value).getLengthInBytes(FLAG);
            } catch (JIException e) {
                throw new JIRuntimeException(e.getErrorCode());
            }
        }

    }

    private static class CharacterImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            ndr.writeUnsignedSmall(((Character) value));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Character c = (char) ndr.readUnsignedSmall();
            return c;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 1;
        }

    }

    private static class ByteImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            ndr.writeUnsignedSmall(((Number) value).byteValue());
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Byte c = (byte) ndr.readUnsignedSmall();
            return c;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 1;
        }

    }

    private static class ShortImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if (value == null) {
                value = Short.MIN_VALUE;
            }
            ndr.writeUnsignedShort(((Number) value).shortValue());

        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Short s = (short) ndr.readUnsignedShort();
            return s;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            {
                return 2 + 2;
            }
        }

    }

    private static class BooleanImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if (value == null) {
                value = Boolean.FALSE;
            }

            if ((FLAG & JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL) == JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL) {
                ndr.writeUnsignedShort(((Boolean) value) == true ? 0xFFFF : 0x0000);
            } else {
                ndr.writeBoolean(((Boolean) value));
            }

        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            Boolean b = null;
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL) == JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL) {
                int s = ndr.readUnsignedShort();
                b = s != 0 ? Boolean.TRUE : Boolean.FALSE;
            } else {
                b = ndr.readBoolean();
            }

            return b;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL) == JIFlags.FLAG_REPRESENTATION_VARIANT_BOOL) {
                return 2;
            } else {
                return 1;
            }
        }
    }

    private static class IntegerImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if (value == null) {
                value = Integer.MIN_VALUE;
            }
            ndr.writeUnsignedLong(((Number) value).intValue());
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            return ndr.readUnsignedLong();
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            {
                return 4;
            }
        }

    }

    private static class LongImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if (value == null) {
                value = Long.MIN_VALUE;
            }
            ndr.getBuffer().align(8);
            Encdec.enc_uint64le(((Number) value).longValue(), ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(8);
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            ndr.getBuffer().align(8);
            Long b = Encdec.dec_uint64le(ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(8);
            return b;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 8;
        }

    }

    private static class DoubleImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if (value == null) {
                value = Double.NaN;
            }

            ndr.getBuffer().align(8);
            Encdec.enc_doublele(((Number) value).doubleValue(), ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(8);

        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            ndr.getBuffer().align(8);
            Double b = Encdec.dec_doublele(ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(8);

            return b;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            {
                return 8;
            }
        }

    }

    private static class JICurrencyImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            JICurrency currency = (JICurrency) value;

            int units = currency.getUnits();
            int fractionalUnits = currency.getFractionalUnits();

            double p = units + fractionalUnits / 10000;

            //scale the units by 10000 to remove the decimal and take two's compliment.
            int toSend = ~((int) (p * 10000.00)) + 1;

            String toSend2 = (Integer.toHexString(toSend));
            int hibytes = 0;
            int lowbytes = 0;
            if (toSend2.length() > 8) {
                lowbytes = Integer.valueOf(toSend2.substring(8), 16);
                hibytes = Integer.valueOf(toSend2.substring(0, 8), 16);
            } else {
                lowbytes = toSend;
                if (toSend < 0) {
                    hibytes = -1;
                }
            }

            // now align by 8 bytes, since this is struct has a hyper, which I don't support yet
            double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
            long i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
            ndr.writeOctetArray(new byte[(int) i], 0, (int) i);

            JIStruct struct = new JIStruct();
            try {
                struct.addMember(lowbytes);
                struct.addMember(hibytes);
            } catch (JIException e) {

            }
            serialize(ndr, JIStruct.class, struct, null, FLAG);

        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            //first align
            double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
            long i = (i = Math.round(index % 8.0)) == 0 ? 0 : 8 - i;
            ndr.readOctetArray(new byte[(int) i], 0, (int) i);

            //now read the low byte
            int lowbyte = ndr.readUnsignedLong();
            //hibyte
            int hibyte = ndr.readUnsignedLong();
            if (hibyte < 0) {
                lowbyte = -1 * Math.abs(lowbyte);
            }

            //String newValue = Integer.toHexString(hibyte) + Integer.toHexString(lowbyte);
            //long value = Long.parseLong(newValue,16);
            return new JICurrency(((lowbyte - lowbyte % 10000) / 10000), (lowbyte % 10000));

        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            {
                return 4 + 4;
            }
        }
    }
    //will only get called from a variant.

    private static class DateImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            // if (value == null && FLAG == JIFlags.FLAG_REPRESENTATION_ARRAY)
            // {
            //   value = new Double(Double.NaN);
            // }
            ndr.getBuffer().align(8);
            Encdec.enc_doublele(convertMillisecondsToWindowsTime(((Date) value).getTime()), ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(8);

        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            ndr.getBuffer().align(8);
            Date b = new Date(convertWindowsTimeToMilliseconds(Encdec.dec_doublele(ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex())));
            ndr.getBuffer().advance(8);
            return b;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            {
                return 8;
            }
        }

        /**
         * FROM JACAOB 1.10. www.danadler.com. Convert a COM time from functions
         * Date(), Time(), Now() to a Java time (milliseconds). Visual Basic
         * time values are based to 30.12.1899, Java time values are based to
         * 1.1.1970 (= 0 milliseconds). The difference is added to the Visual
         * Basic value to get the corresponding Java value. The Visual Basic
         * double value reads: <day count delta since 30.12.1899>.<1 day percentage
         * fraction>, e.g. "38100.6453" means: 38100 days since 30.12.1899 plus
         * (24 hours * 0.6453). Example usage:
         * <code>Date javaDate = new Date(toMilliseconds (vbDate));</code>.
         *
         * @param comTime COM time.
         * @return Java time.
         */
        private long convertWindowsTimeToMilliseconds(double comTime) {
            long result = 0;
            // code from jacobgen:
            comTime -= 25569D;
            Calendar cal = Calendar.getInstance();
            result = Math.round(86400000L * comTime) - cal.get(Calendar.ZONE_OFFSET);
            cal.setTime(new Date(result));
            result -= cal.get(Calendar.DST_OFFSET);
            return result;
        }

        /**
         * FROM JACAOB 1.10. www.danadler.com. Convert a Java time to a COM time.
         *
         * @param milliseconds Java time.
         * @return COM time.
         */
        private double convertMillisecondsToWindowsTime(long milliseconds) {
            double result = 0.0;
            // code from jacobgen:
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(milliseconds);
            milliseconds += (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)); // add GMT offset
            result = (milliseconds / 86400000D) + 25569D;
            return result;
        }
    }

    private static class FloatImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if (value == null) {
                value = Float.NaN;
            }
            ndr.getBuffer().align(4);
            Encdec.enc_floatle(((Number) value).floatValue(), ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(4);

        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            ndr.getBuffer().align(4);
            Float b = Encdec.dec_floatle(ndr.getBuffer().getBuffer(), ndr.getBuffer().getIndex());
            ndr.getBuffer().advance(4);

            return b;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 4;
        }
    }

    private static class StringImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_VALID_STRING) != JIFlags.FLAG_REPRESENTATION_VALID_STRING) {
                throw new JIRuntimeException(JIErrorCodes.JI_UTIL_STRING_INVALID);
            }

            String str = ((String) value);
            if (str == null) {
                str = "";
            }
            //BSTR encoding
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_BSTR) == JIFlags.FLAG_REPRESENTATION_STRING_BSTR) {
                byte[] strBytes = str.getBytes(StandardCharsets.UTF_16LE);
                //NDR representation Max count , then offset, then, actual count
                //length of String (Maximum count)
                ndr.writeUnsignedLong(strBytes.length / 2);
                //last index of String (length in bytes)
                ndr.writeUnsignedLong(strBytes.length);
                //length of String Again !! (Actual count)
                ndr.writeUnsignedLong(strBytes.length / 2);
                //write an array of unsigned shorts
                int i = 0;
                while (i < strBytes.length) {
                    //ndr.writeUnsignedShort(str.charAt(i));
                    ndr.writeUnsignedSmall(strBytes[i]);
                    i++;
                }

            } else //Normal String
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR) == JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR) {
                // the String is written as "short" so length is strlen/2+1
                int strlen = (int) Math.round(str.length() / 2.0);

                ndr.writeUnsignedLong(strlen + 1);
                ndr.writeUnsignedLong(0);
                ndr.writeUnsignedLong(strlen + 1);
                if (str.length() != 0) {
                    ndr.writeCharacterArray(str.toCharArray(), 0, str.length());
                    //odd length
                    if (str.length() % 2 != 0) {
                        //add a 0
                        ndr.writeUnsignedSmall(0);
                    }
                }

                //null termination
                ndr.writeUnsignedShort(0);
            } else if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR) == JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR) {
                byte[] strBytes = str.getBytes(StandardCharsets.UTF_16LE);
                //bytes + 1
                ndr.writeUnsignedLong(strBytes.length / 2 + 1);
                ndr.writeUnsignedLong(0);
                ndr.writeUnsignedLong(strBytes.length / 2 + 1);
                //write an array of unsigned shorts
                int i = 0;
                while (i < strBytes.length) {
                    //ndr.writeUnsignedShort(str.charAt(i));
                    ndr.writeUnsignedSmall(strBytes[i]);
                    i++;
                }
                // int strlen = str.length();
                // ndr.writeUnsignedLong(strlen + 1);
                // ndr.writeUnsignedLong(0);
                // ndr.writeUnsignedLong(strlen + 1);
                //
                // int i = 0;
                // while (i < str.length())
                // {
                //   ndr.writeUnsignedShort(str.charAt(i));
                //   i++;
                // }
                //null termination
                ndr.writeUnsignedShort(0);
            }
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_VALID_STRING) != JIFlags.FLAG_REPRESENTATION_VALID_STRING) {
                throw new JIRuntimeException(JIErrorCodes.JI_UTIL_STRING_INVALID);
            }
            int retVal = -1;
            //StringBuffer buffer = new StringBuffer();
            String retString = null;
            //BSTR Decoding
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_BSTR) == JIFlags.FLAG_REPRESENTATION_STRING_BSTR) {
                //Read for user
                ndr.readUnsignedLong();//eating max length
                ndr.readUnsignedLong();//eating length in bytes
                int actuallength = ndr.readUnsignedLong() * 2;
                byte[] buffer = new byte[actuallength];
                int i = 0;
                while (i < actuallength) {
                    retVal = ndr.readUnsignedSmall();
                    buffer[i] = (byte) retVal;
                    i++;
                }

                retString = new String(buffer, StandardCharsets.UTF_16LE);

            } else //Normal String
            if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR) == JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR) {
                {
                    int actuallength = ndr.readUnsignedLong(); //max length
                    if (actuallength == 0) {
                        return null;
                    }
                    ndr.readUnsignedLong();//eating offset
                    ndr.readUnsignedLong();//eating actuallength again
                    //now read array.
                    char[] ret = new char[actuallength * 2 - 2];
                    //read including the unsigned short (null chars)
                    ndr.readCharacterArray(ret, 0, actuallength * 2 - 2);
                    if (ret[ret.length - 1] == '0') {
                        retString = new String(ret, 0, ret.length - 1);
                    } else {
                        retString = new String(ret);
                    }
                    ndr.readUnsignedShort();
                }
            } else if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR) == JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR) {

                {
                    int maxlength = ndr.readUnsignedLong();
                    if (maxlength == 0) {
                        return null;
                    }
                    ndr.readUnsignedLong();//eating offset
                    int actuallength = ndr.readUnsignedLong() * 2;
                    byte buffer[] = new byte[actuallength - 2];
                    int i = 0;
                    //last 2 bytes , null termination will be eaten outside the loop
                    while (i < actuallength - 2) {
                        retVal = ndr.readUnsignedSmall();
                        buffer[i] = (byte) retVal;
                        i++;
                    }
                    if (actuallength != 0) {
                        ndr.readUnsignedShort();
                    }

                    retString = new String(buffer, StandardCharsets.UTF_16LE);
                }
            }
            return retString;
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            //rough estimate, this will vary from string to string
            int length = 4 + 4 + 4; //max len, offset ,actual length

            if (!((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_BSTR) == JIFlags.FLAG_REPRESENTATION_STRING_BSTR)) {
                length += 2; //adding null termination
            }

            if ((FLAG & JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR) == JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR) {
                length += ((CharSequence) value).length(); //this is only a character array, no unicode, each char is writen in 1 byte "abcd" --> ab, cd ,00 ; "abcde" --> ab,cd,e0, 00
                if (!(((CharSequence) value).length() % 2 == 0)) //odd
                {
                    length++;
                }
            } else {
                // if (value == null)
                // {
                //   int i = 0;
                // }
                length += ((CharSequence) value).length() * 2; //these are both unicode (utf-16le)
            }
            return length;
        }
    }

    private static class JIStringImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            int length = 4;

            if (((JIString) value).getString() == null) {
                return length;
            }

            //for LPWSTR and BSTR adding 2 for the null character.
            length += (((JIString) value).getType() == JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR ? 0 : 2);
            //Pointer referentId --> USER
            return length + JIMarshalUnMarshalHelper.getLengthInBytes(String.class, ((JIString) value).getString(), ((JIString) value).getType() | FLAG);
        }

    }

    private static class UUIDImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            try {
                ((NdrObject) value).encode(ndr, ndr.getBuffer());
            } catch (RuntimeException e) {
                LOGGER.throwing("UUIDImpl", "serializeData", e);
            }
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            return new UUID(ndr.getBuffer());
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return 16;
        }
    }

    private static class MInterfacePointerImpl implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            throw new IllegalStateException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UTIL_INCORRECT_CALL));
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return ((JIInterfacePointer) value).getLength();
        }
    }

    private static class MInterfacePointerImpl2 implements SerializerDeserializer {

        @Override
        public void serializeData(NetworkDataRepresentation ndr, Object value, List<JIPointer> defferedPointers, int FLAG) {
            ((JIInterfacePointerBody) value).encode(ndr, FLAG);
        }

        @Override
        public Object deserializeData(NetworkDataRepresentation ndr, List<JIPointer> defferedPointers, Map additionalData, int FLAG) {
            return JIInterfacePointerBody.decode(ndr, FLAG);
        }

        @Override
        public int getLengthInBytes(Object value, int FLAG) {
            return ((JIInterfacePointerBody) value).getLength();
        }
    }

    private JIMarshalUnMarshalHelper() {
    }
}
