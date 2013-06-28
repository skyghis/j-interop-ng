/**
* Donated by Jarapac (http://jarapac.sourceforge.net/) and released under EPL.
* 
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v1.
*  
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
