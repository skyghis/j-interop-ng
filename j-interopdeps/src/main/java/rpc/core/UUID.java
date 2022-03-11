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
package rpc.core;

import java.util.StringTokenizer;
import ndr.NdrBuffer;

public final class UUID {

    public static final String NIL_UUID = "00000000-0000-0000-0000-000000000000";
    private int timeLow;
    private int timeMid;
    private int timeHighAndVersion;
    private int clockSeqHighAndReserved;
    private int clockSeqLow;
    private byte[] node = new byte[6];

    public static String createHexString() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public UUID(NdrBuffer src) {
        decode(src);
    }

    public UUID(String uuid) {
        parse(uuid);
    }

    public static void encodeToBuffer(UUID uuid, NdrBuffer dst) {
        dst.enc_ndr_long(uuid.timeLow);
        dst.enc_ndr_short(uuid.timeMid);
        dst.enc_ndr_short(uuid.timeHighAndVersion);
        dst.enc_ndr_small(uuid.clockSeqHighAndReserved);
        dst.enc_ndr_small(uuid.clockSeqLow);
        System.arraycopy(uuid.node, 0, dst.buf, dst.index, 6);
        dst.index += 6;
    }

    public void decode(NdrBuffer src) {
        timeLow = src.dec_ndr_long();
        timeMid = src.dec_ndr_short();
        timeHighAndVersion = src.dec_ndr_short();
        clockSeqHighAndReserved = src.dec_ndr_small();
        clockSeqLow = src.dec_ndr_small();
        System.arraycopy(src.buf, src.index, node, 0, 6);
        src.index += 6;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Integer.toHexString((timeLow >> 28) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 24) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 20) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 16) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 12) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 8) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 4) & 0x0f));
        buffer.append(Integer.toHexString(timeLow & 0x0f));
        buffer.append('-');
        buffer.append(Integer.toHexString((timeMid >> 12) & 0x0f));
        buffer.append(Integer.toHexString((timeMid >> 8) & 0x0f));
        buffer.append(Integer.toHexString((timeMid >> 4) & 0x0f));
        buffer.append(Integer.toHexString(timeMid & 0x0f));
        buffer.append('-');
        buffer.append(Integer.toHexString((timeHighAndVersion >> 12) & 0x0f));
        buffer.append(Integer.toHexString((timeHighAndVersion >> 8) & 0x0f));
        buffer.append(Integer.toHexString((timeHighAndVersion >> 4) & 0x0f));
        buffer.append(Integer.toHexString(timeHighAndVersion & 0x0f));
        buffer.append('-');
        buffer.append(Integer.toHexString((clockSeqHighAndReserved >> 4) & 0x0f));
        buffer.append(Integer.toHexString(clockSeqHighAndReserved & 0x0f));

        buffer.append(Integer.toHexString((clockSeqLow >> 4) & 0x0f));
        buffer.append(Integer.toHexString(clockSeqLow & 0x0f));
        buffer.append('-');

        for (int i = 0; i < 6; i++) {
            buffer.append(Integer.toHexString((node[i] >> 4) & 0x0f));
            buffer.append(Integer.toHexString(node[i] & 0x0f));
        }
        return buffer.toString();
    }

    public void parse(String uuid) {
        StringTokenizer tokenizer = new StringTokenizer(uuid, "-");
        timeLow = (int) Long.parseLong(tokenizer.nextToken(), 16);
        timeMid = Integer.parseInt(tokenizer.nextToken(), 16);
        timeHighAndVersion = Integer.parseInt(tokenizer.nextToken(), 16);
        String token = tokenizer.nextToken();
        clockSeqHighAndReserved = Integer.parseInt(token.substring(0, 2), 16);
        clockSeqLow = Integer.parseInt(token.substring(2), 16);
        token = tokenizer.nextToken();
        node = new byte[6];
        for (int i = 0; i < 6; i++) {
            int offset = i * 2;
            node[i] = (byte) ((Character.digit(token.charAt(offset), 16) << 4) | Character.digit(token.charAt(offset + 1), 16));
        }
    }
}
