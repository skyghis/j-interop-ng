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

import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class PresentationResult extends NdrObject {

    public static final int ACCEPTANCE = 0;

    public static final int USER_REJECTION = 1;

    public static final int PROVIDER_REJECTION = 2;

    public static final int REASON_NOT_SPECIFIED = 0;

    public static final int ABSTRACT_SYNTAX_NOT_SUPPORTED = 1;

    public static final int PROPOSED_TRANSFER_SYNTAXES_NOT_SUPPORTED = 2;

    public static final int LOCAL_LIMIT_EXCEEDED = 3;

    public int result;

    public int reason;

    public PresentationSyntax transferSyntax;

    public PresentationResult() {
        this(ACCEPTANCE, REASON_NOT_SPECIFIED,
                new PresentationSyntax(NetworkDataRepresentation.NDR_SYNTAX));
    }

    public PresentationResult(PresentationSyntax transferSyntax) {
        this(ACCEPTANCE, REASON_NOT_SPECIFIED, transferSyntax);
    }

    public PresentationResult(int result, int reason) {
        this(result, reason, null);
    }

    public PresentationResult(int result, int reason,
            PresentationSyntax transferSyntax) {
        this.result = result;
        this.reason = reason;
        this.transferSyntax = transferSyntax;
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {
        ndr.getBuffer().align(4);
        result = ndr.readUnsignedShort();
        reason = ndr.readUnsignedShort();
        //if (result == ACCEPTANCE) //commenting this since the entire packet should be decoded VRC
        {
            transferSyntax = new PresentationSyntax();
            transferSyntax.decode(ndr, ndr.getBuffer());
        }
    }

    @Override
    public void write(NetworkDataRepresentation ndr) {
        ndr.getBuffer().align(4, (byte) 0);
        ndr.writeUnsignedShort(result);
        ndr.writeUnsignedShort(reason);
        //if (result == ACCEPTANCE && transferSyntax != null)
        if (transferSyntax != null) //commenting this since the entire packet should be written VRC
        {
            transferSyntax.encode(ndr, ndr.getBuffer());
        }
    }
}
