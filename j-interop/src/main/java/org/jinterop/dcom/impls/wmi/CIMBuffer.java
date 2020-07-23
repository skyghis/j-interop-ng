/**j-Interop (Pure Java implementation of DCOM protocol)    
 * Copyright (C) 2011  Danny Tylman
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
package org.jinterop.dcom.impls.wmi;

import java.lang.reflect.Array;
import java.util.UUID;
import jcifs.util.Encdec;

/**
 * 
 * @author danny
 */
public class CIMBuffer {

    private final byte[] buff;
    private int pos;

    public CIMBuffer(byte[] buff) {
        this.buff = buff;
        this.pos = 0;

    }

    public void move(int index) {
        this.pos += index;
    }

    public short getInt16() {
        short val = Array.getShort(this.buff, pos);
        this.pos += 2;
        return val;
    }

    public short getUint16() {
        short val = Encdec.dec_uint16le(this.buff, this.pos);
        this.pos += 2;
        return val;
    }

    public int getUint32() {
        int val = Encdec.dec_uint32le(this.buff, this.pos);
        this.pos += 4;
        return val;
    }

    public byte[] getBytes(int len) {
        byte[] ret = new byte[len];
        System.arraycopy(this.buff, pos, ret, 0, len);
        this.pos += len;
        return ret;
    }

    public byte getByte() {
        byte b = this.buff[this.pos];
        this.pos += 1;
        return b;
    }

    public UUID getUUID() {
        byte[] uuid = new byte[16];
        System.arraycopy(this.buff, this.pos, uuid, 0, 16);
        this.pos += 16;
        return UUID.nameUUIDFromBytes(uuid);
    }

    public float getFloat() {
        float f = Encdec.dec_floatle(this.buff, this.pos);
        this.pos += 4;
        return f;
    }

    public double getDouble() {
        double d = Encdec.dec_doublele(this.buff, this.pos);
        this.pos += 8;
        return d;
    }

    public long getUint64() {
        long l = Encdec.dec_uint64le(this.buff, this.pos);
        this.pos += 8;
        return l;
    }

    public char getChar() {
        char c = Array.getChar(this.buff, this.pos);
        this.pos += 2;
        return c;
    }

    /**
     * 
     * @param array
     */
    public void copy(byte[] array) {
        System.arraycopy(this.buff, this.pos, array, 0, array.length);
        this.pos += array.length;
    }

    public int remaining() {
        return this.buff.length - this.pos;
    }

    public int getInt32() {
        int val = Array.getInt(this.buff, this.pos);
        this.pos += 4;
        return val;
    }

    public int getPosition() {
        return this.pos;
    }

    public void setOffset(int offset) {
        this.pos = offset;
    }

    public void temp(int start) {
        System.out.println("{");
        for (int i = start; i < this.pos; i++) {
            if (i % 20 == 0) {
                System.out.println();
            }
            System.out.print(String.valueOf(this.buff[i]) + ",");
        }
        System.out.println("}");
    }

    void copyTo(byte[] stub, int from, int len) {
        System.arraycopy(this.buff, from, stub, 0, len);
    }
}
