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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public abstract class TransportFactory {

    private static Properties defaultProperties;

    @Deprecated //FIXME: Seem unused
    public static Properties getDefaultProperties() {
        synchronized (TransportFactory.class) {
            if (defaultProperties == null) {
                Properties properties = new Properties();
                String defaults = null;
                try {
                    defaults = System.getProperty("rpc.properties");
                } catch (Exception ex) {
                }
                if (defaults != null) {
                    URL url = null;
                    try {
                        url = new URL(new File(".").toURI().toURL(), defaults);
                        properties.load(url.openStream());
                    } catch (MalformedURLException ex) {
                        throw new IllegalArgumentException("Bad location " + defaults + ": " + ex.getMessage());
                    } catch (IOException | RuntimeException ex) {
                        throw new IllegalArgumentException("Unable to load " + " RPC properties from " + url + ": " + ex.getMessage());
                    }
                } else {
                    try {
                        properties.load(TransportFactory.class.getResourceAsStream("/rpc.properties"));
                    } catch (IOException | RuntimeException ex) {
                        try {
                            properties.load(ClassLoader.getSystemResourceAsStream("/rpc.properties"));
                        } catch (IOException | RuntimeException ignore) {
                        }
                    }
                }
                defaultProperties = properties;
            }
        }
        Properties properties = new Properties(defaultProperties);
        try {
            properties.putAll(System.getProperties());
        } catch (Exception ex) {
        }
        return properties;
    }

    public abstract Transport createTransport(String address, Properties properties) throws ProviderException;
}
