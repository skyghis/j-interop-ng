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
package org.jinterop.dcom.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**<p>Class implemented for defining system wide changes. 
 * 
 * <p>A note on logging: The framework exposes JRE based logger "org.jinterop". Applications need to 
 * attach their own handler to this logger. If you would like to set the in-built handler, which 
 * writes to a file <code>j-Interop.log</code> in the <code>java.io.tmpdir</code> directory, please use 
 * the {@link #setInBuiltLogHandler(boolean)}. Please note that the <code>level</code> for the logger 
 * and all other configuration parameters should be set directly on the logger instance, 
 * using <code>LogManager.getLogger("org.jinterop")</code></p>
 * 
 * <p><b>Note</b>: Methods starting with <i>internal_</i> keyword are internal to the framework 
 * and must not be called by the developer.
 * 
 * @since 1.0
 *
 */
public final class JISystem {

	
	private JISystem() {}
	
	private static String pathToDB = null;
	private static Locale locale = Locale.getDefault(); 
	private static ResourceBundle resourceBundle = null;
	private static Properties mapOfProgIdsVsClsids = new Properties();
	private static ArrayList socketQueue = new ArrayList();
	private static JIComVersion comVersion = new JIComVersion();
	private static boolean autoRegister = false;
	private static boolean autoCollection = true;
	private static final Logger logger = Logger.getLogger("org.jinterop");

	/** Returns the framework logger identified by the name "org.jinterop".
	 * 
	 * @return
	 */
	public static Logger getLogger()
	{
		return logger;
	}
	
	/** Sets the COM version which the library would use for communicating with COM servers. 
	 * Default is 5.2. 
	 * 
	 * @param comVersion new COM version
	 */
	public static void setCOMVersion(JIComVersion comVersion)
	{
		JISystem.comVersion = comVersion;
	}
	
	/** Returns COM version currently being used by the library.
	 * 
	 * 
	 * @return
	 */
	public static JIComVersion getCOMVersion()
	{
		return JISystem.comVersion;
	}
	
	/** Sets the locale, this locale will be used to retrieve the resource bundle for Error Messages. 
	 * 
	 * @param locale default is <code>Locale.getDefault()</code>.
	 */
	public static void setLocale(Locale locale)
	{
		JISystem.locale = locale;
	}
	
	/** Returns current locale associated with the library.
	 * 
	 * @return
	 */
	public static Locale getLocale()
	{
		return JISystem.locale;
	}
	
	/** Returns the ResourceBundle associated with current locale.
	 * 
	 * @return
	 */
	public static ResourceBundle getErrorMessages()
	{
		if (resourceBundle == null)
		{
			synchronized (JISystem.class) {
				try{
					if (resourceBundle == null)
					{
						resourceBundle = ResourceBundle.getBundle("org.jinterop.dcom.jierrormessages", locale);
					}
				}catch(MissingResourceException ex)
				{
					//now use the parent US english bundle , which you already have
					resourceBundle = ResourceBundle.getBundle("org.jinterop.dcom.jierrormessages");
				}
			}
		}
		
		return resourceBundle;
	}
	
	/** Returns the localized error messages for the error code.
	 * 
	 * @param code error code 
	 * @return
	 */
	public static String getLocalizedMessage(int code)
	{
		String strKey = Integer.toHexString(code).toUpperCase();
	  	char buffer[] = {'0','x','0','0','0','0','0','0','0','0'};
	  	System.arraycopy(strKey.toCharArray(),0,buffer,buffer.length - strKey.length(),strKey.length());
		return getLocalizedMessage(String.valueOf(buffer));
	}
	
	private static String getLocalizedMessage(String key)
	{
		String message = null;
		try{
			message = JISystem.getErrorMessages().getString(key);
			message = message + " [" + key + "]";
		}catch(MissingResourceException r)
		{
			message = "Message not found for errorCode: " + key;
		}
		
		return message;
	}
	
	/** Queries the property file maintaining the <code>PROGID</code> Vs <code>CLSID</code> mappings 
	 * and returns the <code>CLSID</code> if found or null otherwise.
	 * 
	 * @param progId user friendly string such as "Excel.Application".
	 * @return
	 */
	public static String getClsidFromProgId(String progId)
	{
		if (progId == null)
		{
			return null;
		}
		
		if (pathToDB == null)
		{
			synchronized (JISystem.class) 
			{
				if (pathToDB == null)
				{
					saveDBPathAndLoadFile();
				}
			}
		}
		
		return ((String)mapOfProgIdsVsClsids.get(progId));
	}
	
	private static void saveDBPathAndLoadFile()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) 
		{
			loader = JISystem.class.getClassLoader(); // fallback
		}
		
		Set locations = new HashSet();
		   if (loader != null) {
	            try {
	                Enumeration resources = loader.getResources("progIdVsClsidDB.properties");
	                while (resources.hasMoreElements()) {
	                	locations.add(resources.nextElement());
	                	break;
	                }
	            } catch (IOException ex) { }
	        }
	        try {
	        	if (locations.size() == 0)
	        	{
	        		Enumeration resources = ClassLoader.getSystemResources("progIdVsClsidDB.properties");
		            while (resources.hasMoreElements()) {
		                locations.add(resources.nextElement());
		                break;
		            }
	        	}
	        } catch (IOException ex) { }
	        
	        Iterator iterator = locations.iterator();
	        while (iterator.hasNext()) {
	            try {
	            		URL url = (URL) iterator.next();
	            		pathToDB = url.getPath();

	            		try {
	            			
	            			if (!pathToDB.startsWith("file:"))
	            			{
	            			  url = new URL("file:" + pathToDB);
	            			}
	        		    	
	            			if (logger.isLoggable(Level.INFO))
	        				{
	        		    		logger.info("progIdVsClsidDB file located at: " + url);
	        				}
	        		    	
	        		    	URLConnection con = url.openConnection();
	        		    	InputStream inputStream = con.getInputStream();
	        		    	mapOfProgIdsVsClsids.load(inputStream);
	        		    	inputStream.close();
	        		    	//outputStream = con.getOutputStream();
	        		    } catch (Exception e) {
	        			}
	        		    
	            		//mapOfProgIdsVsClsids.load(new FileInputStream(pathToDB));
	                }
	            catch (Exception ex) { 
	            	//ex.printStackTrace();
	            }
	        }
	        
	        if (logger.isLoggable(Level.INFO))
			{
	        	logger.info("progIdVsClsidDB: " + mapOfProgIdsVsClsids);
			}
	}
	
	//should be called from system shut down only
	/**
	 * Should be called from system shut down only
	 * 
	 * @exclude
	 */
	public static void internal_writeProgIdsToFile()
	{
		if (pathToDB != null)
		{
			try {
				FileOutputStream outputStream = new FileOutputStream(pathToDB);
				mapOfProgIdsVsClsids.store(outputStream,"progId Vs ClsidDB");
				outputStream.close();
			} catch (FileNotFoundException e) {
				
				logger.throwing("JISystem", "writeProgIdsToFile", e);
			} catch (IOException e) {
				
				logger.throwing("JISystem", "writeProgIdsToFile", e);
			}
		}
	}
	
	
	
	//stores it in a temporary hash map here, and this is later persisted when the library is shutdown
	/**Stores it in a temporary hash map here, and this is later persisted when the library is shutdown
	 * @exclude
	 */
	public static void internal_setClsidtoProgId(String progId, String clsid)
	{
		mapOfProgIdsVsClsids.put(progId,clsid);
	}
	
	/** synchronisation will be performed by the oxid master
	 * @exclude
	 * @return
	 */
	public static Object internal_getSocket()
	{
		//synchronized (socketQueue) 
		{
			return socketQueue.remove(0);
		}
	}
	
	/**synchronisation will be performed by the oxid master
	 * @exclude
	 */
	public static void internal_setSocket(Object socket)
	{
		//synchronized (socketQueue) 
		{
			socketQueue.add(socket);
		}
	}
	
	/**
	 * @exclude
	 * @return
	 */
	public static synchronized void internal_initLogger()
	{
		logSystemPropertiesAndVersion();
	}
	
	private static void logSystemPropertiesAndVersion()
	{
		Properties pr = System.getProperties();
		Iterator itr = pr.keySet().iterator();
		String str = "";
		String jinteropVersion = JISystem.class.getPackage().getImplementationVersion();
		Logger logger = Logger.getLogger("org.jinterop");
		if (logger.isLoggable(Level.INFO))
		{
			logger.info("j-Interop Version = " + jinteropVersion + "\n");
			while(itr.hasNext())
			{
				String key = (String)itr.next();
				str = str + key + " = " + pr.getProperty(key) + "\n";
			}
			logger.info(str);
		}
	}
	
	/**Indicates to the framework, if Windows Registry settings for DLL\OCX
	 * component identified by this object should be modified to add a <code>Surrogate</code> 
	 * automatically. A <code>Surrogate</code> is a process which provides resources
	 * such as memory and cpu for a DLL\OCX to execute.
	 * <p> This API overrides the instance specific flags set on JIClsid or JIProgID. 
	 *  
	 * @param autoRegisteration <code>true</code> if auto registration should be done by the framework.
	 */
	public static void setAutoRegisteration(boolean autoRegisteration)
	{
		autoRegister = autoRegisteration;
	}
	
	/**Returns true is auto registration is enabled.
	 * 
	 * @return
	 */
	public static boolean isAutoRegistrationSet()
	{
		return autoRegister;
	}
	
	/**<p>Sometimes the DCOM runtime of Windows will not send a ping on time to the Framework. 
	 * It is not very abnormal, since Windows can sometimes resort to mechanisms other than
	 * DCOM to keep a reference count for the instances they imported. In case of j-Interop
	 * framework, if a ping is not received in 8 minutes , the Java Local Class is collected for 
	 * GC. And if the COM server requires a reference to it or acts on a previously obtained reference
	 * , it is sent back an <i>Exception</i>. Please use this flag to set the Auto Collection status 
	 * to ON or OFF. By Default, it is ON. </p>  
	 * 
	 * @param autoCollection <code>true</code> if auto collection should be turned off.
	 */
	public static void setJavaCoClassAutoCollection(boolean autoCollection)
	{
		JISystem.autoCollection = autoCollection;
	}
	
	/** Status of autoCollection flag.   
	 * 
	 * @return <code>true</code> if autoCollection is enabled, <code>false</code> otherwise.
	 */
	public static boolean isJavaCoClassAutoCollectionSet()
	{
		return autoCollection;
	}
	
	/** Used to set the in built log handler. 
	 * 
	 * @param useParentHandlers true if parent handlers should be used.
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static void setInBuiltLogHandler(boolean useParentHandlers) throws SecurityException, IOException
	{
		logger.setUseParentHandlers(useParentHandlers);
		FileHandler fileHandler = new FileHandler("%t/j-Interop%g.log",0,1,true);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
	}
	
}
