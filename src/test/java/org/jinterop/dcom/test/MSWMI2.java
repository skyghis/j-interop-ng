package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.jinterop.dcom.impls.automation.IJIEnumVariant;

/**
 * WMI example showing how to use a new logger implementation.
 *
 * @since 1.23
 *
 */
public class MSWMI2 {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private JIComServer comStub = null;
    private IJIComObject comObject = null;
    private IJIDispatch dispatch = null;
    private String address = null;
    private JISession session = null;

    public MSWMI2(String address, String[] args) throws JIException, UnknownHostException {
        this.address = address;
        session = JISession.createSession(args[1], args[2], args[3]);
        // session.useSessionSecurity(true);
        // session.setGlobalSocketTimeout(5000);
        comStub = new JIComServer(JIClsid.valueOf("76a64158-cb41-11d1-8b02-00600806d9b6"), address, session);
        IJIComObject unknown = comStub.createInstance();
        comObject = unknown.queryInterface("76A6415B-CB41-11d1-8B02-00600806D9B6");//ISWbemLocator
        //This will obtain the dispatch interface
        dispatch = (IJIDispatch) JIObjectFactory.narrowObject(comObject.queryInterface(IJIDispatch.IID));
    }

    public void performOp() throws JIException, InterruptedException {
//    IJIDispatch securityDisp = (IJIDispatch)JIObjectFactory.narrowObject(dispatch.get("Security_").getObjectAsComObject());
//    securityDisp.put("ImpersonationLevel", new JIVariant(3));
        JIVariant results[] = dispatch.callMethodA("ConnectServer", new Object[]{JIVariant.OPTIONAL_PARAM(), new JIString("ROOT\\CIMV2"), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), 0, JIVariant.OPTIONAL_PARAM()});

        IJIDispatch wbemServices_dispatch = (IJIDispatch) JIObjectFactory.narrowObject((results[0]).getObjectAsComObject());
        results = wbemServices_dispatch.callMethodA("ExecQuery", new Object[]{new JIString("select * from Win32_OperatingSystem where Primary=True"), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        IJIDispatch wbemObjectSet_dispatch = (IJIDispatch) JIObjectFactory.narrowObject((results[0]).getObjectAsComObject());
        JIVariant variant = wbemObjectSet_dispatch.get("_NewEnum");
        IJIComObject object2 = variant.getObjectAsComObject();

        IJIEnumVariant enumVARIANT = (IJIEnumVariant) JIObjectFactory.narrowObject(object2.queryInterface(IJIEnumVariant.IID));

        JIVariant Count = wbemObjectSet_dispatch.get("Count");
        int count = Count.getObjectAsInt();
        for (int i = 0; i < count; i++) {
            Object[] values = enumVARIANT.next(1);
            JIArray array = (JIArray) values[0];
            Object[] arrayObj = (Object[]) array.getArrayInstance();
            for (Object arrayObj1 : arrayObj) {
                IJIDispatch wbemObject_dispatch = (IJIDispatch) JIObjectFactory.narrowObject(((JIVariant) arrayObj1).getObjectAsComObject());
                JIVariant variant2 = (wbemObject_dispatch.callMethodA("GetObjectText_", new Object[]{1}))[0];
                System.out.println(variant2.getObjectAsString().getString());
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }

    }

    private void killme() throws JIException {
        JISession.destroySession(session);
    }

    public static void main(String[] args) {

        try {
            if (args.length < 4) {
                System.out.println("Please provide address domain username password");
                return;
            }

            LOGGER.setLevel(Level.FINEST);
            JISystem.setAutoRegisteration(true);
            MSWMI2 test = new MSWMI2(args[0], args);
            for (int i = 0; i < 2; i++) {
                System.out.println("Index i: " + i);
                test.performOp();
            }
            test.killme();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
