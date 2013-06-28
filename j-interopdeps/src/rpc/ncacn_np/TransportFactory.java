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



package rpc.ncacn_np;

import java.util.Properties;

import rpc.ProviderException;
import rpc.Transport;

public class TransportFactory extends rpc.TransportFactory {

    public Transport createTransport(String address, Properties properties)
            throws ProviderException {
        return new RpcTransport(address, properties);
    }

}
