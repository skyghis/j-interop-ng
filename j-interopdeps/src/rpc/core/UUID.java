/* Jarapac DCE/RPC Framework
 * Copyright (C) 2003  Eric Glass
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package rpc.core;

import java.util.StringTokenizer;

import ndr.NdrBuffer;
import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class UUID extends NdrObject {

    public static final String NIL_UUID =
            "00000000-0000-0000-0000-000000000000";

    private static final int TIMELOW_INDEX = 0;

    private static final int TIMEMID_INDEX = 1;

    private static final int TIMEHIGHANDVERSION_INDEX = 2;

    private static final int CLOCKSEQHIGHANDRESERVED_INDEX = 3;

    private static final int CLOCKSEQLOW_INDEX = 4;

    private static final int NODE_INDEX = 5;

    int timeLow, timeMid, timeHighAndVersion, clockSeqHighAndReserved, clockSeqLow;
    byte[] node = new byte[6];

    public UUID() { }
    public UUID(String uuid) {
        parse(uuid);
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        dst.enc_ndr_long(timeLow);
        dst.enc_ndr_short(timeMid);
        dst.enc_ndr_short(timeHighAndVersion);
        dst.enc_ndr_small(clockSeqHighAndReserved);
        dst.enc_ndr_small(clockSeqLow);
        System.arraycopy(node, 0, dst.buf, dst.index, 6);
        dst.index += 6;
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
        timeLow = src.dec_ndr_long();
        timeMid = src.dec_ndr_short();
        timeHighAndVersion = src.dec_ndr_short();
        clockSeqHighAndReserved = src.dec_ndr_small();
        clockSeqLow = src.dec_ndr_small();
        System.arraycopy(src.buf, src.index, node, 0, 6);
        src.index += 6;
    }
/*
    public long getTimeLow() {
        return ((UnsignedLongHolder)
                structure.get(TIMELOW_INDEX)).getUnsignedLong();
    }

    public void setTimeLow(long timeLow) {
        ((UnsignedLongHolder) structure.get(TIMELOW_INDEX)).setUnsignedLong(
                timeLow);
    }

    public int getTimeMid() {
        return ((UnsignedShortHolder)
                structure.get(TIMEMID_INDEX)).getUnsignedShort();
    }

    public void setTimeMid(int timeMid) {
        ((UnsignedShortHolder) structure.get(TIMEMID_INDEX)).setUnsignedShort(
                timeMid);
    }

    public int getTimeHighAndVersion() {
        return ((UnsignedShortHolder)
                structure.get(TIMEHIGHANDVERSION_INDEX)).getUnsignedShort();
    }

    public void setTimeHighAndVersion(int timeHighAndVersion) {
        ((UnsignedShortHolder)
                structure.get(TIMEHIGHANDVERSION_INDEX)).setUnsignedShort(
                        timeHighAndVersion);
    }

    public short getClockSeqHighAndReserved() {
        return ((UnsignedSmallHolder) structure.get(
                CLOCKSEQHIGHANDRESERVED_INDEX)).getUnsignedSmall();
    }

    public void setClockSeqHighAndReserved(short clockSeqHighAndReserved) {
        ((UnsignedSmallHolder) structure.get(
                CLOCKSEQHIGHANDRESERVED_INDEX)).setUnsignedSmall(
                        clockSeqHighAndReserved);
    }

    public short getClockSeqLow() {
        return ((UnsignedSmallHolder) structure.get(
                CLOCKSEQLOW_INDEX)).getUnsignedSmall();
    }

    public void setClockSeqLow(short clockSeqLow) {
        ((UnsignedSmallHolder) structure.get(
                CLOCKSEQLOW_INDEX)).setUnsignedSmall(clockSeqLow);
    }

    public byte[] getNode() {
        return (byte[]) ((FixedArray) structure.get(NODE_INDEX)).getArray();
    }

    public void setNode(byte[] node) {
        ((FixedArray) structure.get(NODE_INDEX)).setArray(node);
    }
*/

    public String toString() {
        StringBuffer buffer = new StringBuffer();
//        int timeLow = (int) (getTimeLow() & 0xffffffffl);
        buffer.append(Integer.toHexString((timeLow >> 28) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 24) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 20) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 16) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 12) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 8) & 0x0f));
        buffer.append(Integer.toHexString((timeLow >> 4) & 0x0f));
        buffer.append(Integer.toHexString(timeLow & 0x0f));
        buffer.append('-');
//        int timeMid = getTimeMid();
        buffer.append(Integer.toHexString((timeMid >> 12) & 0x0f));
        buffer.append(Integer.toHexString((timeMid >> 8) & 0x0f));
        buffer.append(Integer.toHexString((timeMid >> 4) & 0x0f));
        buffer.append(Integer.toHexString(timeMid & 0x0f));
        buffer.append('-');
//        int timeHighAndVersion = getTimeHighAndVersion();
        buffer.append(Integer.toHexString((timeHighAndVersion >> 12) & 0x0f));
        buffer.append(Integer.toHexString((timeHighAndVersion >> 8) & 0x0f));
        buffer.append(Integer.toHexString((timeHighAndVersion >> 4) & 0x0f));
        buffer.append(Integer.toHexString(timeHighAndVersion & 0x0f));
        buffer.append('-');
//        short clockSeqHighAndReserved = getClockSeqHighAndReserved();
        buffer.append(Integer.toHexString((clockSeqHighAndReserved >> 4) &
                0x0f));
        buffer.append(Integer.toHexString(clockSeqHighAndReserved & 0x0f));
//        short clockSeqLow = getClockSeqLow();
        buffer.append(Integer.toHexString((clockSeqLow >> 4) & 0x0f));
        buffer.append(Integer.toHexString(clockSeqLow & 0x0f));
        buffer.append('-');
//        byte[] node = getNode();
        for (int i = 0; i < 6; i++) {
            buffer.append(Integer.toHexString((node[i] >> 4) & 0x0f));
            buffer.append(Integer.toHexString(node[i] & 0x0f));
        }
        return buffer.toString();
    }

    public void parse(String uuid) {
        StringTokenizer tokenizer = new StringTokenizer(uuid, "-");
        timeLow = (int)Long.parseLong(tokenizer.nextToken(), 16);
        timeMid = Integer.parseInt(tokenizer.nextToken(), 16);
        timeHighAndVersion = Integer.parseInt(tokenizer.nextToken(), 16);
        String token = tokenizer.nextToken();
        clockSeqHighAndReserved = Integer.parseInt(token.substring(0, 2), 16);
        clockSeqLow = Integer.parseInt(token.substring(2), 16);
        token = tokenizer.nextToken();
        node = new byte[6];
        for (int i = 0; i < 6; i++) {
            int offset = i * 2;
            node[i] = (byte) ((Character.digit(token.charAt(offset), 16) << 4) |
                    Character.digit(token.charAt(offset + 1), 16));
        }
/*
        setTimeLow(timeLow);
        setTimeMid(timeMid);
        setTimeHighAndVersion(timeHighAndVersion);
        setClockSeqHighAndReserved(clockSeqHighAndReserved);
        setClockSeqLow(clockSeqLow);
        setNode(node);
*/
    }
}
