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



package rpc;

import java.io.IOException;
import java.util.Properties;

import ndr.NdrObject;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;

public abstract class Stub {

    private TransportFactory transportFactory;

    private Endpoint endpoint;

    private String object;

    private String address;

    private Properties properties;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if ((address == null) ? this.address == null :
                address.equals(this.address)) {
            return;
        }
        this.address = address;
        try {
            detach();
        } catch (IOException ex) { }
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public TransportFactory getTransportFactory() {
//        return (transportFactory != null) ? transportFactory :
//                (transportFactory = TransportFactory.getInstance());
    	return transportFactory; //Will never be null
    }

    public void setTransportFactory(TransportFactory transportFactory) {
        this.transportFactory = transportFactory;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    protected Endpoint getEndpoint() {
        return endpoint;
    }

    protected void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    protected void detach() throws IOException {
        Endpoint endpoint = getEndpoint();
        if (endpoint == null) return;
        try {
            endpoint.detach();
        } finally {
            setEndpoint(null);
        }
    }

    protected void attach() throws IOException {
        Endpoint endpoint = getEndpoint();
        if (endpoint != null) return;
        String address = getAddress();
        if (address == null) throw new RpcException("No address specified.");
        setEndpoint(getTransportFactory().createTransport(address,
                getProperties()).attach(new PresentationSyntax(getSyntax())));
    }

    public void call(int semantics, NdrObject ndrobj) throws IOException {
        attach();
        String object = getObject();
        UUID uuid = (object == null) ? null : new UUID(object);
        getEndpoint().call(semantics, uuid, ndrobj.getOpnum(), ndrobj);
    }

    protected abstract String getSyntax();

}
