/** j-Interop (Pure Java implementation of DCOM protocol)
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
package org.jinterop.winreg.smb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import jcifs.smb.SmbException;
import org.jinterop.dcom.common.IJIAuthInfo;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.winreg.IJIWinReg;
import org.jinterop.winreg.JIPolicyHandle;
import rpc.Endpoint;
import rpc.Stub;

/**
 * @exclude @since 1.0
 */
public final class JIWinRegStub extends Stub implements IJIWinReg {

    public JIWinRegStub(IJIAuthInfo authInfo, String serverName) throws UnknownHostException {
        super();
        if (authInfo == null) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_AUTH_NOT_SUPPLIED));
        }

        super.setTransportFactory(new rpc.ncacn_np.TransportFactory());
        super.setProperties(new Properties());
        super.getProperties().setProperty("rpc.ncacn_np.username", authInfo.getUserName());
        super.getProperties().setProperty("rpc.ncacn_np.password", authInfo.getPassword());
        super.getProperties().setProperty("rpc.ncacn_np.domain", authInfo.getDomain());
        final String cleanServerName = InetAddress.getByName(serverName.trim()).getHostAddress();
        super.setAddress("ncacn_np:" + cleanServerName + "[\\PIPE\\winreg]");
    }

    @Deprecated // SSO Windows only
    public JIWinRegStub(String serverName) throws UnknownHostException {
        super();
        super.setTransportFactory(new rpc.ncacn_np.TransportFactory());
        super.setProperties(new Properties());
        super.getProperties().setProperty("rpc.ntlm.sso", "true");
        final String cleanServerName = InetAddress.getByName(serverName.trim()).getHostAddress();
        super.setAddress("ncacn_np:" + cleanServerName + "[\\PIPE\\winreg]");

    }

    @Override
    public JIPolicyHandle winreg_OpenHKLM() throws JIException {
        openHKLM openhklm = new openHKLM();
        JIPolicyHandle handle = new JIPolicyHandle(false);
        try {
            call(Endpoint.IDEMPOTENT, openhklm);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }
        System.arraycopy(openhklm.policyhandle, 0, handle.handle, 0, 20);
        return handle;
    }

    @Override
    public JIPolicyHandle winreg_OpenHKCR() throws JIException {
        openHKCR openhkcr = new openHKCR();
        JIPolicyHandle handle = new JIPolicyHandle(false);
        try {
            call(Endpoint.IDEMPOTENT, openhkcr);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        System.arraycopy(openhkcr.policyhandle, 0, handle.handle, 0, 20);

        return handle;
    }

    @Override
    public JIPolicyHandle winreg_OpenHKCU() throws JIException {
        openHKCU openhkcu = new openHKCU();
        JIPolicyHandle handle = new JIPolicyHandle(false);
        try {
            call(Endpoint.IDEMPOTENT, openhkcu);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        System.arraycopy(openhkcu.policyhandle, 0, handle.handle, 0, 20);

        return handle;
    }

    @Override
    public JIPolicyHandle winreg_OpenHKU() throws JIException {
        openHKU openhku = new openHKU();
        JIPolicyHandle handle = new JIPolicyHandle(false);
        try {
            call(Endpoint.IDEMPOTENT, openhku);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        System.arraycopy(openhku.policyhandle, 0, handle.handle, 0, 20);

        return handle;
    }

    @Override
    public JIPolicyHandle winreg_OpenKey(JIPolicyHandle handle, String key, int accessMask) throws JIException {
        openKey openkey = new openKey();
        openkey.accessMask = accessMask;
        openkey.key = key;
        openkey.parentKey = handle;
        JIPolicyHandle newHandle = new JIPolicyHandle(false);
        try {
            call(Endpoint.IDEMPOTENT, openkey);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        System.arraycopy(openkey.policyhandle, 0, newHandle.handle, 0, 20);

        return newHandle;
    }

    @Override
    public void winreg_CloseKey(JIPolicyHandle handle) throws JIException {
        closeKey closekey = new closeKey();
        closekey.key = handle;
        try {
            call(Endpoint.IDEMPOTENT, closekey);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }
    }

    @Override
    public void winreg_DeleteKeyOrValue(JIPolicyHandle handle, String valueName, boolean isKey) throws JIException {
        deleteValueOrKey delete = new deleteValueOrKey();
        delete.parentKey = handle;
        delete.valueName = valueName;
        delete.isKey = isKey;
        try {
            call(Endpoint.IDEMPOTENT, delete);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }
    }

    @Override
    public byte[] winreg_QueryValue(JIPolicyHandle handle, int bufferSize) throws JIException {
        queryValue queryvalue = new queryValue();
        queryvalue.parentKey = handle;
        queryvalue.bufferLength = bufferSize;
        try {
            call(Endpoint.IDEMPOTENT, queryvalue);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }
        return queryvalue.buffer;
    }

    @Override
    public Object[] winreg_QueryValue(JIPolicyHandle handle, String valueName, int bufferSize) throws JIException {
        queryValue queryvalue = new queryValue();
        queryvalue.parentKey = handle;
        queryvalue.bufferLength = bufferSize;
        queryvalue.key = valueName;

        try {
            call(Endpoint.IDEMPOTENT, queryvalue);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        return new Object[]{queryvalue.type, (queryvalue.buffer != null ? (Object) queryvalue.buffer : (Object) queryvalue.buffer2)};
    }

    @Override
    public void winreg_SaveFile(JIPolicyHandle handle, String fileName) throws JIException {
        saveFile savefile = new saveFile();
        savefile.parentKey = handle;
        savefile.fileName = fileName;

        try {
            call(Endpoint.IDEMPOTENT, savefile);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

    }

    @Override
    public JIPolicyHandle winreg_CreateKey(JIPolicyHandle handle, String subKey, int options, int accessMask) throws JIException {
        createKey createkey = new createKey();
        createkey.accessMask = accessMask;
        createkey.key = subKey;
        createkey.parentKey = handle;
        createkey.options = options;

        try {
            call(Endpoint.IDEMPOTENT, createkey);
        } catch (SmbException e) {
            throw new JIException(e.getNtStatus(), e);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        JIPolicyHandle newHandle = new JIPolicyHandle((createkey.actiontaken == 1));
        System.arraycopy(createkey.policyhandle, 0, newHandle.handle, 0, 20);

        return newHandle;
    }

    @Override
    public void winreg_SetValue(JIPolicyHandle handle, String valueName, byte[][] data) throws JIException {
        if (data == null) {
            throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_WINREG_EXCEPTION5));
        }

        //calculate length of all strings + extra null in the end
        int totalStrings = data.length;
        int length = 0;
        for (int i = 0; i < totalStrings; i++) {
            int j = data[i].length;
            length += (j + 1) * 2; //including null termination
        }

        length += 2; //final termination

        setValue setvalue = new setValue();
        setvalue.clazzType = REG_MULTI_SZ;
        setvalue.data2 = data;
        setvalue.lengthInBytes = length;
        setvalue.parentKey = handle;
        setvalue.valueName = valueName;
        setValue(setvalue);
    }

    @Override
    public void winreg_SetValue(JIPolicyHandle handle, String valueName) throws JIException {
        setValue setvalue = new setValue();
        setvalue.clazzType = REG_NONE;
        setvalue.parentKey = handle;
        setvalue.valueName = valueName;
        setValue(setvalue);
    }

    @Override
    public void winreg_SetValue(JIPolicyHandle handle, String valueName, byte[] data, boolean isBinary, boolean expand_sz) throws JIException {
        setValue setvalue = new setValue();
        if (isBinary) {
            setvalue.clazzType = REG_BINARY;
        } else {
            if (expand_sz) {
                setvalue.clazzType = REG_EXPAND_SZ;
            } else {
                setvalue.clazzType = REG_SZ;
            }
        }

        setvalue.data = data;
        setvalue.lengthInBytes = data.length;
        setvalue.parentKey = handle;
        setvalue.valueName = valueName;
        setValue(setvalue);
    }

    @Override
    public void winreg_SetValue(JIPolicyHandle handle, String valueName, int data) throws JIException {
        setValue setvalue = new setValue();
        setvalue.clazzType = REG_DWORD;
        setvalue.lengthInBytes = 4;
        setvalue.dword = data;
        setvalue.parentKey = handle;
        setvalue.valueName = valueName;
        setValue(setvalue);
    }

    @Override
    public String[] winreg_EnumKey(JIPolicyHandle handle, int index) throws JIException {
        enumKey enumkey = new enumKey();
        enumkey.parentKey = handle;
        enumkey.index = index;

        try {
            call(Endpoint.IDEMPOTENT, enumkey);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        return enumkey.retval;
    }

    @Override
    public Object[] winreg_EnumValue(JIPolicyHandle handle, int index) throws JIException {
        enumValue enumvalue = new enumValue();
        enumvalue.parentKey = handle;
        enumvalue.index = index;

        try {
            call(Endpoint.IDEMPOTENT, enumvalue);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }

        return enumvalue.retval;
    }

    @Override
    public void closeConnection() throws JIException {
        try {
            super.detach();
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        }
    }

    @Override
    protected String getSyntax() {
        // WinReg Service
        return "338cd001-2244-31f1-aaaa-900038001003:1.0";
    }

    private void setValue(setValue setvalue) throws JIException {
        try {
            call(Endpoint.IDEMPOTENT, setvalue);
        } catch (IOException e) {
            throw new JIException(JIErrorCodes.RPC_E_UNEXPECTED, e);
        } catch (JIRuntimeException e) {
            throw new JIException(e);
        }
    }
}
