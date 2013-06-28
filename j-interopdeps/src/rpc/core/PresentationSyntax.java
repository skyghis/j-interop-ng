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

import ndr.NdrBuffer;
import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public class PresentationSyntax extends NdrObject {

    private static final int UUID_INDEX = 0;

    private static final int VERSION_INDEX = 1;

    UUID uuid;
    int version;

    public PresentationSyntax() {
    }

    public PresentationSyntax(String syntax) {
        this();
        parse(syntax);
    }

    public PresentationSyntax(UUID uuid, int majorVersion, int minorVersion) {
        this();
        setUuid(uuid);
        setVersion(majorVersion, minorVersion);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMajorVersion() {
        return version & 0xffff;
    }

    public int getMinorVersion() {
        return (version >> 16) & 0xffff;
    }

    public void setVersion(int majorVersion, int minorVersion) {
        setVersion((majorVersion & 0xffff) | (minorVersion << 16));
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
        uuid.encode(ndr, dst);
        dst.enc_ndr_long(version);
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
		uuid = new UUID();
        uuid.decode(ndr, src);
        version = src.dec_ndr_long();
    }

    public String toString() {
        return getUuid().toString() + ":" + getMajorVersion() + "." +
                getMinorVersion();
    }

    public void parse(String syntax) {
        StringTokenizer tokenizer = new StringTokenizer(syntax, ":.");
        uuid = new UUID();
        uuid.parse(tokenizer.nextToken());
        setVersion(Integer.parseInt(tokenizer.nextToken()),
                Integer.parseInt(tokenizer.nextToken()));
    }

}
