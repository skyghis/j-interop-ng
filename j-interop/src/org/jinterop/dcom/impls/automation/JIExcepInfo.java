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

package org.jinterop.dcom.impls.automation;

/**
 * Exposes error code , exception source, exception description and help file path
 * for an unsuccessful {@link IJIDispatch} operation.
 *
 * @since 2.0
 */
public final class JIExcepInfo
{
    String excepSource = null;
    String excepDesc = null;
    String excepHelpfile = null;

    int errorCode = -1;
    JIExcepInfo()
    {
    }

    void clearAll()
    {
        errorCode = -1;
        excepSource = null;
        excepDesc = null;
        excepHelpfile = null;
    }

    /**
     * An error code identifying the error.
     *
     * @return
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * A textual, human-readable name of the source of the exception. Typically, this is an
     * application name.
     *
     * @return
     */
    public String getExcepSource()
    {
        return excepSource;
    }

    /**
     * A textual, human-readable description of the error intended for the customer. If no
     * description is available it returns <code>null</code>.
     *
     * @return
     */
    public String getExcepDesc()
    {
        return excepDesc;
    }

    /**
     * The fully qualified drive, path, and file name of a Help file that has more information
     * about the error. If no Help is available it returns <code>null</code>.
     *
     * @return
     */
    public String getHelpFilePath()
    {
        return excepHelpfile;
    }
}
