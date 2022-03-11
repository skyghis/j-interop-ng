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

import java.util.Objects;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class Port extends NdrObject {

    private String portSpec;

    public Port() {
        this(null);
    }

    public Port(String portSpec) {
        this.portSpec = portSpec;
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {
        int length = ndr.readUnsignedShort();
        if (length > 0) {
            char[] portSpecChars = new char[length - 1];
            ndr.readCharacterArray(portSpecChars, 0, portSpecChars.length);
            ndr.readUnsignedSmall(); // null terminator
            this.portSpec = new String(portSpecChars);
        } else {
            this.portSpec = null;
        }
    }

    @Override
    public void write(NetworkDataRepresentation ndr) {
        char[] spec;
        if (portSpec != null) {
            spec = new char[portSpec.length() + 1];
            portSpec.getChars(0, portSpec.length(), spec, 0);
        } else {
            spec = new char[0];
        }
        ndr.writeUnsignedShort(spec.length);
        if (spec.length > 0) {
            ndr.writeCharacterArray(spec, 0, spec.length);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(portSpec);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Port other = (Port) obj;
        return Objects.equals(this.portSpec, other.portSpec);
    }

    public String getPortSpec() {
        return portSpec;
    }
}
