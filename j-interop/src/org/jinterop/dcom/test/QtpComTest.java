package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class QtpComTest {

    private JIComServer comServer = null;

    private IJIDispatch dispatch = null;

    private IJIComObject unknown = null;

    private JISession session = null;

   

   

    public QtpComTest(String address, String domain, String username, String password) throws JIException, UnknownHostException{

                    JISystem.getLogger().setLevel(Level.FINEST);

                    /*Let the j-Interop library do this for you. You can set the "autoRegistration" flag in the

                      JISystem class. When the library encounters a "Class not registered" exception, it will

                      perform all the registry changes if the autoRegistration flag is set. And then re-attempt

                      loading the COM Server. Please have a look at MSSysInfo,MSWMI examples.*/

                    JISystem.setAutoRegisteration(true);

                    session = JISession.createSession(domain,username,password);

                    comServer = new JIComServer(JIProgId.valueOf("QuickTest.Application"), address, session);

//                    session.setGlobalSocketTimeout(30000);

    }

   

    public void startQTP() throws JIException{

                    System.out.println(comServer.getProperties());

                    unknown = comServer.createInstance();

                    dispatch = (IJIDispatch)JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));

                    //System.out.println(((JIVariant)dispatch.get("Version")).getObjectAsString().getString());

    }

   

    public void showQtp() throws JIException{

                    int dispId = dispatch.getIDsOfNames("Visible");

                    JIVariant variant = new JIVariant(Boolean.TRUE);

                    dispatch.put(dispId,variant);

    }

   

    public void envQtp() throws JIException{

                    dispatch.callMethodA("Open", new Object[]{new JIString("C:\\Programme\\Mercury Interactive\\QuickTest Professional\\Tests\\Test1"), new JIVariant(Boolean.FALSE), new JIVariant(Boolean.TRUE)});

                    JIVariant variant = dispatch.get("Test");

                    IJIDispatch test = (IJIDispatch)JIObjectFactory.narrowObject(variant.getObjectAsComObject());
                    System.out.println(test.get("Author"));
                    
                    //and this is the original session associated with dispatch.
                    JISession.destroySession(session);
                    
    }

   

   

    public static void main(String[] args) {

                    //"localhost", "ctron", "mpitonia", "ChrisSarah1"

                    //"VPC003", "automation" , "automated_user", "@utom@tion"

                    //"automationsvr01", "AUTOMATION", "Automated_User", "@utom@tion"

                    try {

                                    QtpComTest comQtp = new QtpComTest("localhost", "domain" , "username", "password");

                                    comQtp.startQTP();

                                    comQtp.showQtp();

                                    comQtp.envQtp();

                    } catch (Exception e) {

                                    e.printStackTrace();

                    }

    }

   

   

   

   

   

}