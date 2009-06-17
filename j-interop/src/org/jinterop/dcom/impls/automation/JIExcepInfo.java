/**j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
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
