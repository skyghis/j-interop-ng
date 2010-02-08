// jTDS JDBC Driver for Microsoft SQL Server and Sybase
// Copyright (C) 2004 The jTDS Project
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
package net.sourceforge.jtds.util;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * COPIED FROM jtds PROJECT FOR SSO CAPABILITIES.
 * 
 * A JNI client to SSPI based CPP program (DLL) that returns the user
 * credentials for NTLM authentication.
 * <p/>
 * The DLL name is ntlmauth.dll.
 *
 * @author Magendran Sathaiah (mahi@aztec.soft.net)
 */
public class SSPIJNIClient {
    /** Singleton instance. */
    private static SSPIJNIClient thisInstance;

    /** SSPI native library loaded flag. */
    private static boolean libraryLoaded;

    /** SSPI client initialized flag. */
    private boolean initialized;

    /** Initializes the SSPI client. */
    private native void initialize();

    /** Uninitializes the SSPI client. */
    private native void unInitialize();

    /**
     * Prepares the NTLM TYPE-1 message and returns it as a
     * <code>byte[]</code>.
     */
    private native byte[] prepareSSORequest();

    /**
     * Prepares the NTLM TYPE-3 message using the current user's credentials.
     * <p>
     * It needs the challenge BLOB and it's size as input. The challenge BLOB
     * is nothig but the TYPE-2 message that is received from the SQL Server.
     *
     * @param buf  challenge BLOB
     * @param size challenge BLOB size
     * @return NTLM TYPE-3 message
     */
    private native byte[] prepareSSOSubmit(byte[] buf, long size);


    /**
     * Private constructor for singleton.
     */
    private SSPIJNIClient() {
        try {
        	if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
    		{
        		System.loadLibrary("ntlmauth");
        		SSPIJNIClient.libraryLoaded = true;
    		}
    		else
    		{
    			throw new IllegalArgumentException("This functionality is available only under \"Microsoft Windows\" line of Operating systems.");
    		}
        } catch (UnsatisfiedLinkError err) {
        	Logger.getLogger("org.jinterop").severe("Unable to load library: " + err);
        	throw new IllegalStateException("Native SSPI library not loaded. "
                  + "Check the java.library.path system property."
                  + "This functionality is available only under \"Microsoft Windows\" line of Operating systems.");
            
        }
    }

    /**
     * Returns the singleton <code>SSPIJNIClient</code> instance.
     *
     * @throws SQLException if an error occurs during initialization
     */
    public static SSPIJNIClient getInstance() {

        if (thisInstance == null) {
//            if (!libraryLoaded) {
//                throw new IllegalStateException("Native SSPI library not loaded. "
//                        + "Check the java.library.path system property."
//                        + "This functionality is available only under \"Microsoft Windows\" line of Operating systems.");
//            }
            thisInstance = new SSPIJNIClient();
            thisInstance.invokeInitialize();
        }
        return thisInstance;
    }

    /**
     * Calls <code>#initialize()</code> if the SSPI client is not already inited.
     */
    public void invokeInitialize() {
        if (!initialized) {
            initialize();
            initialized = true;
        }
    }

    /**
     * Calls <code>#unInitialize()</code> if the SSPI client is inited.
     */
    public void invokeUnInitialize() {
        if (initialized) {
            unInitialize();
            initialized = false;
        }
    }

    /**
     * Calls <code>#prepareSSORequest()</code> to prepare the NTLM TYPE-1 message.
     *
     * @throws Exception if an error occurs during the call or the SSPI client
     *                   is uninitialized
     */
    public byte[] invokePrepareSSORequest() {
        if (!initialized) {
            throw new IllegalStateException("SSPI Not Initialized");
        }
        return prepareSSORequest();
    }

    /**
     * Calls <code>#prepareSSOSubmit(byte[], long)</code> to prepare the NTLM TYPE-3
     * message.
     *
     * @throws Exception if an error occurs during the call or the SSPI client
     *                   is uninitialized
     */
    public byte[] invokePrepareSSOSubmit(byte[] buf) {
        if (!initialized) {
            throw new IllegalStateException("SSPI Not Initialized");
        }
        return prepareSSOSubmit(buf, buf.length);
    }
}
