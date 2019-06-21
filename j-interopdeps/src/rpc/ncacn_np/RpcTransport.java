/* Donated by Jarapac (http://jarapac.sourceforge.net/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
package rpc.ncacn_np;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import jcifs.CIFSContext;
import jcifs.Credentials;
import jcifs.SmbPipeHandle;
import jcifs.SmbPipeResource;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbNamedPipe;
import ndr.NdrBuffer;
import rpc.CifsContextSingleton;
import rpc.ConnectionOrientedEndpoint;
import rpc.ConnectionOrientedPdu;
import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;

public class RpcTransport implements Transport {

    public static final String PROTOCOL = "ncacn_np";
    private static final String LOCALHOST = CifsContextSingleton.instance().getNameServiceClient().getLocalHost().getHostName();
    private String address;
    private CIFSContext smbContext;
    private Properties properties;
    private SmbNamedPipe pipe;
    OutputStream out;
    InputStream in;
    InputStream in2;
    private int writeSize;
    private int readSize;
    private boolean attached;
    private boolean first;

    public RpcTransport(String address, Properties properties) throws ProviderException {
        this.properties = properties;
        parse(address);
    }

    public String getProtocol() {
        return PROTOCOL;
    }

    public Properties getProperties() {
        return properties;
    }

    public Endpoint attach(PresentationSyntax syntax) throws IOException {
        if (attached) {
            throw new RpcException("Transport already attached.");
        }

        //with the first flag an access denied exception occurs
        //with the second one file not found. so changing code here.
        /* pipe = new SmbNamedPipe(address, (0x2019f << 16) |
         * SmbNamedPipe.PIPE_TYPE_RDWR | SmbNamedPipe.PIPE_TYPE_DCE_TRANSACT);
         * */
        pipe = new SmbNamedPipe(address, (0x2019F << 16) | SmbPipeResource.PIPE_TYPE_RDWR | SmbPipeResource.PIPE_TYPE_DCE_TRANSACT, smbContext);
//        pipe = new SmbNamedPipe(address, SmbNamedPipe.PIPE_TYPE_RDWR | SmbNamedPipe.PIPE_TYPE_DCE_TRANSACT, smbContext);
        in2 = pipe.getInputStream();
        final SmbPipeHandle pipeHandle = pipe.openPipe();
        out = pipeHandle.getOutput();
        in = pipeHandle.getInput();
//        out = pipe.getNamedPipeOutputStream();
//        in = pipe.getNamedPipeInputStream();

        attached = true;
        return new ConnectionOrientedEndpoint(this, syntax);
    }

    public void close() throws IOException {
        try {
            if (pipe != null) {
                in.close();
                out.close();
                in2.close();
            }
        } finally {
            attached = false;
            pipe = null;
        }
    }

    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }
        out.write(buffer.getBuffer(), 0, buffer.getLength());
        first = true;
    }

    public void receive(NdrBuffer buffer) throws IOException {
        byte[] buf = buffer.getBuffer();
        int off = 0, bytes_to_read, n;

        if (!attached) {
            throw new RpcException("Transport not attached.");
        }

        if (first) {
            n = in.read(buf, 0, 1024);
            /* TransactNamedPipe */
            first = false;
        } else {
            /* Plain read */
            n = (in2).read(buf, off, buf.length);
        }

        buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
        bytes_to_read = buffer.dec_ndr_short();

        off += n;
        bytes_to_read -= n;

        while (bytes_to_read > 0) {
            n = (in2).read(buf, off, bytes_to_read);
            off += n;
            bytes_to_read -= n;
        }
        buffer.length = off;
    }

    protected void parse(String address) throws ProviderException {
        if (address == null) {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith("ncacn_np:")) {
            throw new ProviderException("Not an ncacn_np address.");
        }
        address = address.substring(9);
        int index = address.indexOf('[');
        if (index == -1) {
            throw new ProviderException("No port specifier present.");
        }
        String server = address.substring(0, index);
        address = address.substring(index + 1);
        index = address.indexOf(']');
        if (index == -1) {
            throw new ProviderException("Port specifier not terminated.");
        }
        address = address.substring(0, index);
        while (address.startsWith("\\")) {
            address = address.substring(1);
        }
        if (!address.regionMatches(true, 0, "PIPE", 0, 4)) {
            throw new ProviderException("Not a named pipe address.");
        }
        address = address.substring(4);
        while (address.startsWith("\\")) {
            address = address.substring(1);
        }
        if ("".equals(address)) {
            throw new ProviderException("Empty port.");
        }
        while (server.startsWith("\\")) {
            server = server.substring(1);
        }
        if ("".equals(server)) {
            server = LOCALHOST;
        }
        Credentials credentials = null;
        Properties properties = getProperties();
        if (properties != null) {
            String username = properties.getProperty("rpc.ncacn_np.username");
            if (username == null) {
                username = CifsContextSingleton.instance().getConfig().getDefaultUsername();
            }
            if (username != null && !username.isEmpty()) {
                String domain = properties.getProperty("rpc.ncacn_np.domain");
                if (domain == null) {
                    domain = CifsContextSingleton.instance().getConfig().getDefaultDomain();
                }
                String password = properties.getProperty("rpc.ncacn_np.password");
                if (password == null) {
                    password = CifsContextSingleton.instance().getConfig().getDefaultPassword();
                }
                try {
                    password = URLDecoder.decode(password, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException ex) {
                    throw new IllegalStateException(ex);
                }
                credentials = new NtlmPasswordAuthenticator(domain, username, password);
            }
        }
        this.address = "smb://" + server + "/IPC$/" + address;
        if (credentials == null) {
            this.smbContext = CifsContextSingleton.instance().withGuestCrendentials();
        } else {
            this.smbContext = CifsContextSingleton.instance().withCredentials(credentials);
        }
    }
}
