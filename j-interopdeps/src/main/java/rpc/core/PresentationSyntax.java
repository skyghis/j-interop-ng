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
import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public final class PresentationSyntax extends NdrObject {

    private UUID uuid;
    private int version;

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

    @Override
    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        UUID.encodeToBuffer(uuid, dst);
        dst.enc_ndr_long(version);
    }

    @Override
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
        uuid = new UUID(src);
        version = src.dec_ndr_long();
    }

    @Override
    public String toString() {
        return getUuid().toString() + ":" + getMajorVersion() + "." + getMinorVersion();
    }

    public void parse(String syntax) {
        StringTokenizer tokenizer = new StringTokenizer(syntax, ":.");
        uuid = new UUID(tokenizer.nextToken());
        setVersion(Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()));
    }
}
