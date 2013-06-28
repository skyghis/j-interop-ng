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

package org.jinterop.dcom.core;

import ndr.NetworkDataRepresentation;

//Users can implement this object to provide for custom handling of there objects
/**
 * Users can implement this class to provide for custom handling of there objects
 *
 * @since 2.0 (formerly JIUserCallObject)
 */
public abstract class JIUserCallBuilder extends JICallBuilder {

	public abstract void writeObject(NetworkDataRepresentation ndr);
	public abstract void readObject(NetworkDataRepresentation ndr);

	public JIUserCallBuilder(boolean dispatchNotSupported)
	{
		super(dispatchNotSupported);
	}

	public JIUserCallBuilder()
	{
		super();
	}

	public void write(NetworkDataRepresentation ndr)
	{
		writeObject(ndr);
	}

	public void read(NetworkDataRepresentation ndr)
	{
		readObject(ndr);
	}

}
