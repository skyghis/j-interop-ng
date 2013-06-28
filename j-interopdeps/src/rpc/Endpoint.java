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

package rpc;

import java.io.IOException;

import ndr.NdrObject;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;

public interface Endpoint {

    public static final int MAYBE = 0x01;

    public static final int IDEMPOTENT = 0x02;

    public static final int BROADCAST = 0x04;

    public Transport getTransport();

    public PresentationSyntax getSyntax();

    public void call(int semantics, UUID object, int opnum,
			NdrObject ndrobj) throws IOException;

    public void detach() throws IOException;

}
