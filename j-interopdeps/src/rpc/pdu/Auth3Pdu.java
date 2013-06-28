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
