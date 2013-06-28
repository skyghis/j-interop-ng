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
