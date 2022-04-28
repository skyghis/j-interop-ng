package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class Kain {

    private JIComServer comServer = null;
    private final IJIDispatch dispatch = null;
    private IJIComObject unknown = null;

    public Kain(String address, String[] args) throws JIException, UnknownHostException {
        JISession session = JISession.createSession(args[1], args[2], args[3]);
        comServer = new JIComServer(JIProgId.valueOf("Word.Application"), address, session);
    }

    public void startWord() throws JIException {
        unknown = comServer.createInstance();
        IJIDispatch dispatch = (IJIDispatch) JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));
    }

    public void showWord() throws JIException {
        int dispId = dispatch.getIDsOfNames("Visible");
        JIVariant variant = new JIVariant(true);
        dispatch.put(dispId, variant);
    }

    public void performOp() throws JIException, InterruptedException {
        String sDir = "c:\\tmp\\";
        String sInputDoc = sDir + "file_in.doc";
        String sOutputDoc = sDir + "file_out.doc";

        String sOldText = "[label:import:1]";
        String sNewText = "I am some horribly long sentence, so long that [insert something long here]";
        boolean tVisible = true;
        boolean tSaveOnExit = false;

        System.out.println(dispatch.get("Version").getObjectAsString().getString());
        System.out.println(dispatch.get("Path").getObjectAsString().getString());

        JIVariant variant = dispatch.get("Documents");
        IJIDispatch documents = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        //String has to be a JIString.
        JIString filePath = new JIString(sInputDoc);
        //this "open" is of Word 2003
        JIVariant variant2[] = documents.callMethodA("open", new Object[]{new JIVariant(filePath, true), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});

        IJIDispatch document = (IJIDispatch) JIObjectFactory.narrowObject(variant2[0].getObjectAsComObject());
        variant = dispatch.get("Selection");
        IJIDispatch selection = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());

        variant = selection.get("Find");
        IJIDispatch find = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());

        Thread.sleep(2000);

        find.put("Text", new JIVariant(new JIString(sOldText)));
        find.callMethodA("Execute", new Object[]{JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM()});

        Thread.sleep(2000);

        selection.put("Text", new JIVariant(new JIString(sNewText)));
        selection.callMethodA("MoveDown", new Object[]{JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM()});
        selection.put("Text", new JIVariant(new JIString("\nSo we got the next line including BR.\n")));

        variant = selection.get("Font");
        IJIDispatch font = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        font.put("Bold", new JIVariant(1));
        font.put("Italic", new JIVariant(1));
        font.put("Underline", new JIVariant(0));

        variant = selection.get("ParagraphFormat");
        IJIDispatch align = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        align.put("Alignment", new JIVariant(3));

        Thread.sleep(5000);

        JIString sImgFile = new JIString(sDir + "image.png");
        selection.callMethodA("MoveDown", new Object[]{JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM()});
        variant = selection.get("InLineShapes");
        IJIDispatch image = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        image.callMethodA("AddPicture", new Object[]{new JIVariant(sImgFile), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM()});

        JIString sHyperlink = new JIString("http://www.google.com");
        selection.put("Text", new JIVariant(new JIString("Text for the link to Google")));
        variant = selection.get("Range");
        IJIDispatch range = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        variant = document.get("Hyperlinks");
        IJIDispatch link = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        link.callMethod("Add", new Object[]{range, sHyperlink, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});

        variant = dispatch.get("WordBasic");
        IJIDispatch wordBasic = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
        wordBasic.callMethod("FileSaveAs", new Object[]{new JIString(sOutputDoc)});

        dispatch.callMethod("Quit", new Object[]{new JIVariant(-1, true), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        JISession.destroySession(dispatch.getAssociatedSession());
    }

    public static void main(String[] args) {

        try {
            if (args.length < 4) {
                System.out.println("Please provide address domain username password");
                return;
            }
            Kain test = new Kain(args[0], args);
            test.startWord();
            test.showWord();
            test.performOp();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
