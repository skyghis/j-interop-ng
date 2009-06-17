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



package rpc.pdu;

import ndr.NetworkDataRepresentation;
import rpc.ConnectionOrientedPdu;

public class Auth3Pdu extends ConnectionOrientedPdu {

    public static final int AUTH3_TYPE = 0x10;

    public Auth3Pdu()
    {
    	//Really useless value
    	setCallId(0);
    }
    public int getType() {
        return AUTH3_TYPE;
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
    	ndr.writeUnsignedLong(0);
    }
}
