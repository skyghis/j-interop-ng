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

import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class Port extends NdrObject {

    public String portSpec;

    public Port() {
        this(null);
    }

    public Port(String portSpec) {
        this.portSpec = portSpec;
    }

    public void read(NetworkDataRepresentation ndr) {
        int length = ndr.readUnsignedShort();
        if (length > 0) {
			NdrBuffer buf = ndr.getBuffer();
            char[] portSpec = new char[length - 1];
            ndr.readCharacterArray(portSpec, 0, portSpec.length);
			ndr.readUnsignedSmall(); // null terminator
            this.portSpec = new String(portSpec);
        } else {
            this.portSpec = null;
        }
    }

    public void write(NetworkDataRepresentation ndr) {
        char[] spec;
        if (portSpec != null) {
            spec = new char[portSpec.length() + 1];
            portSpec.getChars(0, portSpec.length(), spec, 0);
        } else {
            spec = new char[0];
        }
        ndr.writeUnsignedShort(spec.length);
        if (spec.length > 0) ndr.writeCharacterArray(spec, 0, spec.length);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Port)) return false;
        return (portSpec != null) ? portSpec.equals(((Port) obj).portSpec) :
                ((Port) obj).portSpec == null;
    }

}
