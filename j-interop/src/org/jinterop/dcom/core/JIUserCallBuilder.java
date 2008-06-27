/**j-Interop (Pure Java implementation of DCOM protocol)  
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jinterop.dcom.core;

import ndr.NetworkDataRepresentation;

//Users can implement this object to provide for custom handling of there objects
/**
 * Users can implement this class to provide for custom handling of there objects
 * @since 1.0
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
