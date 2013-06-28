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

public abstract class NdrObject {

	static final int opnum = -1;

    public Object value;

	public int getOpnum() {
		return opnum;
	}

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
		ndr.buf = dst;
		write(ndr); /* just for compatibility with jarapac < 0.2 */
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
		ndr.buf = src;
		read(ndr);
    }
	public void write(NetworkDataRepresentation ndr) { }
	public void read(NetworkDataRepresentation ndr) { }
}

