package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class WshShell {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private JIComServer comServer = null;
    private IJIDispatch dispatch = null;
    private IJIComObject unknown = null;
    private JISession session = null;

    public WshShell(String address, String domain, String username, String password) throws JIException, UnknownHostException {

        LOGGER.setLevel(Level.SEVERE);

        session = JISession.createSession(domain, username, password);

        comServer = new JIComServer(JIProgId.valueOf("WScript.Shell"), address, session);

    }

    public void startWScript() throws JIException {

        System.out.println(comServer.getProperties());

        unknown = comServer.createInstance();

        dispatch = (IJIDispatch) JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));

        JIVariant jv = dispatch.get("CurrentDirectory");

        System.out.println(jv.getObjectAsString().getString());

        int dispId = dispatch.getIDsOfNames("CurrentDirectory");

        System.out.println(dispId);

        JIVariant variant = new JIVariant("C://WINDOWS");

        dispatch.put(dispId, variant);

        jv = dispatch.get("CurrentDirectory");

        System.out.println(jv.getObjectAsString().getString());

        try {
            Thread.sleep(60 * 1000 * 3);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //WshShell.Exec
        System.out.println(dispatch.callMethodA("Exec", new Object[]{new JIString("calc")})[0]);

        try {
            Thread.sleep(60 * 1000 * 3);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //WshShell.Run

        System.out.println(dispatch.callMethodA("Run", new Object[]{new JIString("notepad"), new JIVariant(10), JIVariant.OPTIONAL_PARAM()})[0]);

        //JISession.destroySession(session);
    }

    public static void main(String[] args) {

        try {

            JISystem.setAutoRegisteration(true);

            WshShell wScript = new WshShell("localhost", "domain", "username", "password");

            wScript.startWScript();

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

}
