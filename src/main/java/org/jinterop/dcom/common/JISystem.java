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
package org.jinterop.dcom.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class implemented for defining system wide changes.
 * <p>
 * A note on logging: The framework exposes JRE based logger "org.jinterop".
 * Applications need to attach their own handler to this logger.
 * <p>
 * <b>Note</b>: Methods starting with <i>internal_</i> keyword are internal to the framework and must not be called by the developer.
 *
 * @since 1.0
 */
public final class JISystem {

    private JISystem() {
    }

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final Properties mapOfProgIdsVsClsids = new Properties();
    private static final List<Socket> socketQueue = new ArrayList<>();
    private static final Map<String, String> mapOfHostnamesVsIPs = new HashMap<>();
    private static String pathToDB = null;
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle resourceBundle = null;
    private static JIComVersion comVersion = new JIComVersion();
    private static boolean autoRegister = false;
    private static boolean autoCollection = true;

    @Deprecated
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Sets the COM version which the library would use for communicating with COM servers. Default is 5.2.
     *
     * @param comVersion new COM version
     */
    public static void setCOMVersion(JIComVersion comVersion) {
        JISystem.comVersion = comVersion;
    }

    /**
     * Returns COM version currently being used by the library.
     *
     * @return
     */
    public static JIComVersion getCOMVersion() {
        return JISystem.comVersion;
    }

    /**
     * Sets the locale, this locale will be used to retrieve the resource bundle
     * for Error Messages.
     *
     * @param locale default is <code>Locale.getDefault()</code>.
     */
    public static void setLocale(Locale locale) {
        JISystem.locale = locale;
    }

    /**
     * Returns current locale associated with the library.
     *
     * @return
     */
    public static Locale getLocale() {
        return JISystem.locale;
    }

    /**
     * Returns the ResourceBundle associated with current locale.
     *
     * @return
     */
    public static ResourceBundle getErrorMessages() {
        if (resourceBundle == null) {
            synchronized (JISystem.class) {
                try {
                    if (resourceBundle == null) {
                        resourceBundle = ResourceBundle.getBundle("org.jinterop.dcom.jierrormessages", locale);
                    }
                } catch (MissingResourceException ex) {
                    //now use the parent US english bundle , which you already have
                    resourceBundle = ResourceBundle.getBundle("org.jinterop.dcom.jierrormessages");
                }
            }
        }

        return resourceBundle;
    }

    /**
     * Returns the localized error messages for the error code.
     *
     * @param code error code
     * @return
     */
    public static String getLocalizedMessage(int code) {
        String strKey = Integer.toHexString(code).toUpperCase();
        char buffer[] = {'0', 'x', '0', '0', '0', '0', '0', '0', '0', '0'};
        System.arraycopy(strKey.toCharArray(), 0, buffer, buffer.length - strKey.length(), strKey.length());
        return getLocalizedMessage(String.valueOf(buffer));
    }

    private static String getLocalizedMessage(String key) {
        String message;
        try {
            message = JISystem.getErrorMessages().getString(key);
            message = message + " [" + key + "]";
        } catch (MissingResourceException r) {
            message = "Message not found for errorCode: " + key;
        }
        return message;
    }

    /**
     * Queries the property file maintaining the <code>PROGID</code> Vs
     * <code>CLSID</code> mappings and returns the <code>CLSID</code> if found
     * or null otherwise.
     *
     * @param progId user friendly string such as "Excel.Application".
     * @return
     */
    public static String getClsidFromProgId(String progId) {
        if (progId == null) {
            return null;
        }
        if (pathToDB == null) {
            synchronized (JISystem.class) {
                if (pathToDB == null) {
                    saveDBPathAndLoadFile();
                }
            }
        }

        return ((String) mapOfProgIdsVsClsids.get(progId));
    }

    private static void saveDBPathAndLoadFile() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = JISystem.class.getClassLoader(); // fallback
        }

        Set<URL> locations = new HashSet<>();
        if (loader != null) {
            try {
                Enumeration<URL> resources = loader.getResources("progIdVsClsidDB.properties");
                while (resources.hasMoreElements()) {
                    locations.add(resources.nextElement());
                    break;
                }
            } catch (IOException ex) {
            }
        }
        try {
            if (locations.isEmpty()) {
                Enumeration<URL> resources = ClassLoader.getSystemResources("progIdVsClsidDB.properties");
                while (resources.hasMoreElements()) {
                    locations.add(resources.nextElement());
                    break;
                }
            }
        } catch (IOException ex) {
        }

        Iterator<URL> iterator = locations.iterator();
        while (iterator.hasNext()) {
            try {
                URL url = iterator.next();
                pathToDB = url.getPath();
                try {
                    if (!pathToDB.startsWith("file:")) {
                        url = new URL("file:" + pathToDB);
                    }
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "progIdVsClsidDB file located at: {0}", url);
                    }
                    URLConnection con = url.openConnection();
                    try (final InputStream inputStream = con.getInputStream()) {
                        mapOfProgIdsVsClsids.load(inputStream);
                    }
                } catch (IOException e) {
                }
                //mapOfProgIdsVsClsids.load(new FileInputStream(pathToDB));
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "progIdVsClsidDB: {0}", mapOfProgIdsVsClsids);
        }
    }

    //should be called from system shut down only
    /**
     * Should be called from system shut down only
     *
     * @exclude
     */
    public static void internal_writeProgIdsToFile() {
        if (pathToDB != null) {
            try {
                try (final FileOutputStream outputStream = new FileOutputStream(pathToDB)) {
                    mapOfProgIdsVsClsids.store(outputStream, "progId Vs ClsidDB");
                }
            } catch (FileNotFoundException e) {
                LOGGER.throwing("JISystem", "writeProgIdsToFile", e);
            } catch (IOException e) {
                LOGGER.throwing("JISystem", "writeProgIdsToFile", e);
            }
        }
    }

    /**
     * Stores it in a temporary hash map here, and this is later persisted when the library is shutdown
     *
     * @param progId
     * @param clsid
     */
    public static void internal_setClsidtoProgId(String progId, String clsid) {
        mapOfProgIdsVsClsids.put(progId, clsid);
    }

    /**
     * Synchronization will be performed by the oxid master
     *
     * @return
     */
    public static Socket internal_getSocket() {
        //synchronized (socketQueue)
        {
            return socketQueue.remove(0);
        }
    }

    /**
     * Synchronization will be performed by the oxid master
     *
     * @param socket
     */
    public static void internal_setSocket(Socket socket) {
        //synchronized (socketQueue)
        {
            socketQueue.add(socket);
        }
    }

    public static synchronized void internal_initLogger() {
        logSystemPropertiesAndVersion();
    }

    private static void logSystemPropertiesAndVersion() {
        Properties pr = System.getProperties();
        Iterator<Object> itr = pr.keySet().iterator();
        String str = "";
        String jinteropVersion = JISystem.class.getPackage().getImplementationVersion();
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "j-Interop Version = {0}\n", jinteropVersion);
            while (itr.hasNext()) {
                String key = (String) itr.next();
                str = str + key + " = " + pr.getProperty(key) + "\n";
            }
            LOGGER.info(str);
        }
    }

    /**
     * Indicates to the framework, if Windows Registry settings for DLL\OCX
     * component identified by this object should be modified to add a
     * <code>Surrogate</code> automatically. A <code>Surrogate</code> is a
     * process which provides resources such as memory and cpu for a DLL\OCX to
     * execute.
     * <p>
     * This API overrides the instance specific flags set on JIClsid or
     * JIProgID.
     *
     * @param autoRegisteration <code>true</code> if auto registration should be
     * done by the framework.
     */
    public static void setAutoRegisteration(boolean autoRegisteration) {
        autoRegister = autoRegisteration;
    }

    /**
     * Returns true is auto registration is enabled.
     *
     * @return
     */
    public static boolean isAutoRegistrationSet() {
        return autoRegister;
    }

    /**
     * Sometimes the DCOM runtime of Windows will not send a ping on time to the
     * Framework. It is not very abnormal, since Windows can sometimes resort to
     * mechanisms other than DCOM to keep a reference count for the instances
     * they imported. In case of j-Interop framework, if a ping is not received
     * in 8 minutes , the Java Local Class is collected for GC. And if the COM
     * server requires a reference to it or acts on a previously obtained
     * reference , it is sent back an <i>Exception</i>. Please use this flag to
     * set the Auto Collection status to ON or OFF. By Default, it is ON.
     *
     * @param autoCollection <code>false</code> if auto collection should be
     * turned off.
     */
    public static void setJavaCoClassAutoCollection(boolean autoCollection) {
        JISystem.autoCollection = autoCollection;
    }

    /**
     * Status of autoCollection flag.
     *
     * @return <code>true</code> if autoCollection is enabled,
     * <code>false</code> otherwise.
     */
    public static boolean isJavaCoClassAutoCollectionSet() {
        return autoCollection;
    }

    @Deprecated
    public static void setInBuiltLogHandler(boolean useParentHandlers) throws SecurityException, IOException {
        LOGGER.setUseParentHandlers(useParentHandlers);
        FileHandler fileHandler = new FileHandler("%t/j-Interop%g.log", 0, 1, true);
        fileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fileHandler);
    }

    /**
     * Adds a mapping between the <code>hostname</code> and its <code>IP</code>.
     * This method should be used when there is a possibility of multiple
     * adapters (for example from a Virtual Machine) on the COM server.
     * j-Interop Framework only uses the host name and ignores the I.P addresses
     * supplied in the interface reference of a COM object. If this hostname is
     * not reachable from the machine where library is currently running (such
     * as a Linux machine with no name mappings) then the call to this COM
     * server would fail with an <code>UnknownHostException</code>. To avoid
     * that either add the binding in the host machine or add the binding here.
     * <p>
     * This method stores the name vs I.P binding in a <code>Map</code>.
     * Providing the same <code>hostname</code> will overwrite the binding
     * specified before.
     *
     * @param hostname name of target machine.
     * @param IP address of target machine in I.P format.
     * @throws UnknownHostException if the <code>IP</code> is invalid or cannot
     * be reached.
     * @throws IllegalArgumentException if any parameter is <code>null</code> or
     * of 0 length.
     */
    public static synchronized void mapHostNametoIP(String hostname, String IP) throws UnknownHostException {
        if (hostname == null || IP == null || hostname.trim().length() == 0 || IP.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        //just check the validity of IP
        InetAddress.getByName(IP.trim());
        mapOfHostnamesVsIPs.put(hostname.trim().toUpperCase(), IP.trim());
    }

    /**
     * Returns I.P address for the given <code>hostname</code>.
     *
     * @param hostname
     * @return <code>null</code> if a mapping could not be found.
     */
    public static synchronized String getIPForHostName(String hostname) {
        return mapOfHostnamesVsIPs.get(hostname.trim().toUpperCase());
    }

    public static synchronized void internal_dumpMap() {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "mapOfHostnamesVsIPs: {0}", mapOfHostnamesVsIPs);
        }
    }
}
