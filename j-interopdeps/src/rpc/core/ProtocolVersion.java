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
