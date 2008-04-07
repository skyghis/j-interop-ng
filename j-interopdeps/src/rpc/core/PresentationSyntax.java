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

public class PresentationSyntax extends NdrObject {

    private static final int UUID_INDEX = 0;

    private static final int VERSION_INDEX = 1;

    UUID uuid;
    int version;

    public PresentationSyntax() {
    }

    public PresentationSyntax(String syntax) {
        this();
        parse(syntax);
    }

    public PresentationSyntax(UUID uuid, int majorVersion, int minorVersion) {
        this();
        setUuid(uuid);
        setVersion(majorVersion, minorVersion);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMajorVersion() {
        return version & 0xffff;
    }

    public int getMinorVersion() {
        return (version >> 16) & 0xffff;
    }

    public void setVersion(int majorVersion, int minorVersion) {
        setVersion((majorVersion & 0xffff) | (minorVersion << 16));
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        uuid.encode(ndr, dst);
        dst.enc_ndr_long(version);
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
		uuid = new UUID();
        uuid.decode(ndr, src);
        version = src.dec_ndr_long();
    }

    public String toString() {
        return getUuid().toString() + ":" + getMajorVersion() + "." +
                getMinorVersion();
    }

    public void parse(String syntax) {
        StringTokenizer tokenizer = new StringTokenizer(syntax, ":.");
        uuid = new UUID();
        uuid.parse(tokenizer.nextToken());
        setVersion(Integer.parseInt(tokenizer.nextToken()),
                Integer.parseInt(tokenizer.nextToken()));
    }

}
