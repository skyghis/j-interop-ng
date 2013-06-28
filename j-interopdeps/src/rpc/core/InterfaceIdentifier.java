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

import java.util.StringTokenizer;

import ndr.NdrObject;

public class InterfaceIdentifier extends NdrObject {

	UUID uuid;
	int majorVersion, minorVersion;

    public InterfaceIdentifier(String syntax) {
        parse(syntax);
    }

    public InterfaceIdentifier(UUID uuid, int majorVersion, int minorVersion) {
        setUuid(uuid);
        setMajorVersion(majorVersion);
        setMinorVersion(minorVersion);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
		this.uuid = uuid;
    }

    public int getMajorVersion() {
		return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
		return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
    }

    public String toString() {
        return getUuid().toString() + ":" + getMajorVersion() + "." +
                getMinorVersion();
    }

    public void parse(String syntax) {
        StringTokenizer tokenizer = new StringTokenizer(syntax, ":.");
        getUuid().parse(tokenizer.nextToken());
        setMajorVersion(Integer.parseInt(tokenizer.nextToken()));
        setMinorVersion(Integer.parseInt(tokenizer.nextToken()));
    }

}
