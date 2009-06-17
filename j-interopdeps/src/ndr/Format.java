/* Donated by Jarapac (http://jarapac.sourceforge.net/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

package ndr;

public class Format {

    public static final int LITTLE_ENDIAN = 0x10000000;

    public static final int BIG_ENDIAN = 0x00000000;

    public static final int ASCII_CHARACTER = 0x00000000;

    public static final int EBCDIC_CHARACTER = 0x01000000;

    public static final int IEEE_FLOATING_POINT = 0x00000000;

    public static final int VAX_FLOATING_POINT = 0x00010000;

    public static final int CRAY_FLOATING_POINT = 0x00100000;

    public static final int IBM_FLOATING_POINT = 0x00110000;

    public static final int DEFAULT_DATA_REPRESENTATION = LITTLE_ENDIAN |
            ASCII_CHARACTER | IEEE_FLOATING_POINT;

    public static final Format DEFAULT_FORMAT =
            new Format(DEFAULT_DATA_REPRESENTATION);

    static final int BYTE_ORDER_MASK = 0xf0000000;

    static final int CHARACTER_MASK = 0x0f000000;

    static final int FLOATING_POINT_MASK = 0x00ff0000;

    private final int dataRepresentation;

    public Format(int dataRepresentation) {
        this.dataRepresentation = dataRepresentation;
        if ((dataRepresentation & BYTE_ORDER_MASK) != LITTLE_ENDIAN) {
            throw new IllegalArgumentException(
                    "Only little-endian byte order is currently supported.");
        }
        if ((dataRepresentation & CHARACTER_MASK) != ASCII_CHARACTER) {
            throw new IllegalArgumentException(
                    "Only ASCII character set is currently supported.");
        }
        if ((dataRepresentation & FLOATING_POINT_MASK) != IEEE_FLOATING_POINT) {
            throw new IllegalArgumentException(
                    "Only IEEE floating point is currently supported.");
        }
    }

    public int getDataRepresentation() {
        return dataRepresentation;
    }

    public static Format readFormat(byte[] src, int index,
            boolean connectionless) {
        int value = src[index++] << 24;
        value |= (src[index++] & 0xff) << 16;
        value |= (src[index++] & 0xff) << 8;
        if (!connectionless) value |= src[index] & 0xff;
        return new Format(value);
    }

    public void writeFormat(byte[] dest, int index, boolean connectionless) {
        int val = getDataRepresentation();
        dest[index++] = (byte) ((val >> 24) & 0xff);
        dest[index++] = (byte) ((val >> 16) & 0xff);
        dest[index] = (byte) 0x00;
        if (!connectionless) dest[++index] = (byte) 0x00;
    }

}
