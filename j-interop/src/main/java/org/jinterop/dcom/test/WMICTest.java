package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.impls.wmi.WMIC;
import org.jinterop.dcom.impls.wmi.structures.JICIMEncoding;

/**
 *
 * @author danny
 */
public class WMICTest {

    static public void main(String[] args) throws InterruptedException {

        try {
//            if (args.length == 0) {
//                System.out.println("Usage: server workgroup user passowrd");
//                return;
//            }
            String server = "192.168.5.123";
            String workgroup = "WORKSPACE";
            String user = "Administrator";
            String password = "P@ssw0rd";
            WMICTest wmic = new WMICTest();
            wmic.test(server, workgroup, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private JISession session;

    private void test(String hostName, String domain, String user, String password) throws JIException, UnknownHostException {
        session = JISession.createSession(domain, user, password);
        session.useSessionSecurity(true);
        int timeout = 50000;
        session.setGlobalSocketTimeout(timeout);
        WMIC wmic = new WMIC(hostName, session, "ROOT\\CIMV2");
        executeQuery(wmic, "SELECT * FROM Win32_OperatingSystem");
        //  createProcess(wmic, "ipconfig.exe"); //not implemented yet
    }

    private void executeQuery(WMIC wmic, String query) throws JIException {
        List<JICIMEncoding> objects = wmic.execQuery(query);
        for (JICIMEncoding object : objects) {
            System.out.println(object.getName());
            System.out.println("====================");
            for (Map.Entry<String, String> property : object.getProperties().entrySet()) {
                System.out.println("\t" + property.getKey() + "=" + property.getValue());
            }
            System.out.println();
        }
    }
//
//    private void createProcess(WMIC wmic, String cmdLine) throws JIException {
//        wmic.createProcess(cmdLine);
//    }
}
