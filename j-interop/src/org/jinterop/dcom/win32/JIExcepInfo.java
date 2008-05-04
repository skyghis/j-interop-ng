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

package org.jinterop.dcom.win32;

/**
 * Exposes errorCode , exception Source, exception description and help file path
 * for an unsuccessful IJIDispatch operation.
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
    
    public int getErrorCode()
    {
        return errorCode;
    }
    
    public String getExcepSource()
    {
        return excepSource;
    }
    
    public String getExcepDesc()
    {
        return excepDesc;
    }
    
    public String getHelpFilePath()
    {
        return excepHelpfile;
    }
}
