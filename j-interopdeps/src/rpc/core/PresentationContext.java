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

import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class PresentationContext extends NdrObject {

    public int contextId;

    public PresentationSyntax abstractSyntax;

    public PresentationSyntax[] transferSyntaxes;

    public PresentationContext() {
        this(0, new PresentationSyntax(), new PresentationSyntax[] {
                new PresentationSyntax(NetworkDataRepresentation.NDR_SYNTAX) });
    }

    public PresentationContext(int contextId,
            PresentationSyntax abstractSyntax) {
        this(contextId, abstractSyntax, new PresentationSyntax[] {
                new PresentationSyntax(NetworkDataRepresentation.NDR_SYNTAX) });
    }

    public PresentationContext(int contextId, PresentationSyntax abstractSyntax,
            PresentationSyntax[] transferSyntaxes) {
        this.contextId = contextId;
        this.abstractSyntax = abstractSyntax;
        this.transferSyntaxes = transferSyntaxes;
    }

    public void read(NetworkDataRepresentation ndr) {
        ndr.getBuffer().align(4);
        contextId = ndr.readUnsignedShort();
        int count = ndr.readUnsignedSmall();

		try {
	        abstractSyntax.decode(ndr, ndr.getBuffer());
       	    transferSyntaxes = new PresentationSyntax[count];
			for (int i = 0; i < count; i++) {
				transferSyntaxes[i] = new PresentationSyntax();
				transferSyntaxes[i].decode(ndr, ndr.getBuffer());
			}
		} catch (NdrException ne) {
		}
    }

    public void write(NetworkDataRepresentation ndr) {
		ndr.getBuffer().align(4, (byte)0xcc);
        ndr.writeUnsignedShort(contextId);
        ndr.writeUnsignedShort((short) transferSyntaxes.length);

		try {
        	abstractSyntax.encode(ndr, ndr.getBuffer());
			for (int i = 0; i < transferSyntaxes.length; i++) {
				transferSyntaxes[i].encode(ndr, ndr.getBuffer());
			}
		} catch (NdrException ne) {
		}
    }

}
