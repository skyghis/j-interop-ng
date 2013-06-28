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

package ndr;

import java.io.IOException;

public class NdrException extends IOException {

	public static final String NO_NULL_REF = "ref pointer cannot be null";
	public static final String INVALID_CONFORMANCE = "invalid array conformance";

	public NdrException( String msg ) {
		super( msg );
	}
}
