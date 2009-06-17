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

import ndr.NdrObject;

public class InterfaceIdentifier extends NdrObject {

	UUID uuid;
	int majorVersion, minorVersion;

    public InterfaceIdentifier(String syntax) {
        parse(syntax);
    }

    public InterfaceIdentifier(UUID uuid, int majorVersion, int minorVersion) {
        setUuid(uuid);
        setMajorVersion(majorVersion);
        setMinorVersion(minorVersion);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
		this.uuid = uuid;
    }

    public int getMajorVersion() {
		return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
		return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
    }

    public String toString() {
        return getUuid().toString() + ":" + getMajorVersion() + "." +
                getMinorVersion();
    }

    public void parse(String syntax) {
        StringTokenizer tokenizer = new StringTokenizer(syntax, ":.");
        getUuid().parse(tokenizer.nextToken());
        setMajorVersion(Integer.parseInt(tokenizer.nextToken()));
        setMinorVersion(Integer.parseInt(tokenizer.nextToken()));
    }

}
