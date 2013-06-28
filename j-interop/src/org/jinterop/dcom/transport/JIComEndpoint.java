/**
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
* Vikram Roopchand  - Moving to EPL from LGPL v3.
*  
*/

package org.jinterop.dcom.transport;

import java.io.IOException;

import rpc.ConnectionOrientedEndpoint;
import rpc.Transport;
import rpc.core.PresentationSyntax;

/**
 * @exclude
 * @since 1.0
 *
 */
public final class JIComEndpoint extends ConnectionOrientedEndpoint {

  JIComEndpoint(Transport transport,
            PresentationSyntax syntax) {
        super(transport,syntax);
    }

  public void rebindEndPoint() throws IOException
  {
	  rebind();
  }
}
