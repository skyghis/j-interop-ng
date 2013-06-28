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
import java.util.Properties;

import rpc.core.PresentationContext;

public interface ConnectionContext {

    public static final String MAX_TRANSMIT_FRAGMENT =
            "rpc.connectionContext.maxTransmitFragment";

    public static final String MAX_RECEIVE_FRAGMENT =
            "rpc.connectionContext.maxReceiveFragment";

    public static final int DEFAULT_MAX_TRANSMIT_FRAGMENT = 4280;

    public static final int DEFAULT_MAX_RECEIVE_FRAGMENT = 4280;

    public ConnectionOrientedPdu init(PresentationContext context,
            Properties properties) throws IOException;

    public ConnectionOrientedPdu alter(PresentationContext context)
            throws IOException;

    public ConnectionOrientedPdu accept(ConnectionOrientedPdu pdu)
            throws IOException;

    public Connection getConnection();

    public boolean isEstablished();

}
