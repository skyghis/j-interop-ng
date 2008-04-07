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

import ndr.NdrBuffer;
import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class ProtocolVersion extends NdrObject {

	int majorVersion, minorVersion;

    public int getMajorVersion() {
		return majorVersion;
    }

    public void setMajorVersion(short majorVersion) {
		this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
		return minorVersion;
    }

    public void setMinorVersion(short minorVersion) {
		this.minorVersion = minorVersion;
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        dst.enc_ndr_small(majorVersion);
        dst.enc_ndr_small(minorVersion);
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
        majorVersion = src.dec_ndr_small();
        minorVersion = src.dec_ndr_small();
    }
}
