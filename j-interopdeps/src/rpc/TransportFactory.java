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



package rpc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public abstract class TransportFactory {

//    private static final TransportFactory META_FACTORY;

//    private static final List FACTORIES;

    private static Properties defaultProperties;

//    static {
//        META_FACTORY = new MetaTransportFactory();
//        FACTORIES = new ArrayList();
//        String service = "META-INF/services/" +
//                TransportFactory.class.getName();
//        Set locations = new HashSet();
//        ClassLoader loader = TransportFactory.class.getClassLoader();
//        if (loader != null) {
//            try {
//                Enumeration resources = loader.getResources(service);
//                while (resources.hasMoreElements()) {
//                    locations.add(resources.nextElement());
//                }
//            } catch (IOException ex) { }
//        }
//        try {
//            Enumeration resources = ClassLoader.getSystemResources(service);
//            while (resources.hasMoreElements()) {
//                locations.add(resources.nextElement());
//            }
//        } catch (IOException ex) { }
//        Iterator iterator = locations.iterator();
//        while (iterator.hasNext()) {
//            try {
//                Properties properties = new Properties();
//                properties.load(((URL) iterator.next()).openStream());
//                Enumeration classNames = properties.propertyNames();
//                while (classNames.hasMoreElements()) {
//                    Class factoryClass =
//                            Class.forName((String) classNames.nextElement());
//                    TransportFactory factory = (TransportFactory)
//                            factoryClass.newInstance();
//                    FACTORIES.add(factory);
//                }
//            } catch (Exception ex) { }
//        }
//    }
//
//    public static TransportFactory getInstance() {
//        return META_FACTORY;
//    }

    public static Properties getDefaultProperties() {
        synchronized (TransportFactory.class) {
            if (defaultProperties == null) {
                Properties properties = new Properties();
                String defaults = null;
                try {
                    defaults = System.getProperty("rpc.properties");
                } catch (Exception ex) { }
                if (defaults != null) {
                    URL url = null;
                    try {
                        url = new URL(new File(".").toURL(), defaults);
                        properties.load(url.openStream());
                    } catch (MalformedURLException ex) {
                        throw new IllegalArgumentException("Bad location " +
                                defaults + ": " + ex.getMessage());
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Unable to load " +
                                " RPC properties from " + url + ": " +
                                        ex.getMessage());
                    }
                } else {
                    try {
                        properties.load(
                                TransportFactory.class.getResourceAsStream(
                                        "/rpc.properties"));
                    } catch (Exception ex) {
                        try {
                            properties.load(
                                    ClassLoader.getSystemResourceAsStream(
                                            "/rpc.properties"));
                        } catch (Exception ignore) { }
                    }
                }
                defaultProperties = properties;
            }
        }
        Properties properties = new Properties(defaultProperties);
        try {
            properties.putAll(System.getProperties());
        } catch (Exception ex) { }
        return properties;
    }



    public abstract Transport createTransport(String address,
            Properties properties) throws ProviderException;

//    private static class MetaTransportFactory extends TransportFactory {
//
//        public Transport createTransport(String address, Properties properties)
//                throws ProviderException {
//            if (address == null) {
//                throw new ProviderException("No address specified.");
//            }
//            if (properties == null) {
//                properties = TransportFactory.getDefaultProperties();
//            }
//            Iterator factories = FACTORIES.iterator();
//            while (factories.hasNext()) {
//                try {
//                    return ((TransportFactory)
//                            factories.next()).createTransport(address,
//                                    properties);
//                } catch (ProviderException ex) { }
//            }
//            throw new ProviderException(
//                    "Unable to find suitable provider for \"" + address +
//                            "\".");
//        }
//
//    }

}
