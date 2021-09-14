/*
 * Copyright NellArmonia
 */
package org.jinterop.dcom.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import jcifs.util.Hexdump;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.JISystem;

class PingObject extends NdrObject {

    int opnum = -1;
    List<JIObjectId> listOfAdds = new ArrayList<>();
    List<JIObjectId> listOfDels = new ArrayList<>();
    byte[] setId = null;
    int seqNum = 0;

    @Override
    public int getOpnum() {
        return opnum;
    }

    //read follows write...please remember
    @Override
    public void write(NetworkDataRepresentation ndr) {
        switch (opnum) {
            case 2: //complex ping
                int newlength = 8 + 6 + 8 + listOfAdds.size() * 8 + 8 + listOfDels.size() * 8 + 16;
                if (newlength > ndr.getBuffer().buf.length) {
                    ndr.getBuffer().buf = new byte[newlength + 16];
                }

                if (setId == null) {
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().info("Complex Ping going for the first time, will get the setId as response of this call ");
                    }
                    setId = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
                } else {
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().log(Level.INFO, "Complex Ping going for setId: {0}", Hexdump.toHexString(setId));
                    }
                }

                if (JISystem.getLogger().isLoggable(Level.INFO)) {
                    JISystem.getLogger().log(Level.INFO, "Complex ping going : listOfAdds -> Size : {0} , {1}", new Object[]{listOfAdds.size(), listOfAdds});
                    JISystem.getLogger().log(Level.INFO, "listOfDels -> Size : {0} , {1}", new Object[]{listOfDels.size(), listOfDels});
                }

                JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, setId);

                JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) seqNum, null, JIFlags.FLAG_NULL);//seq
                JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) listOfAdds.size(), null, JIFlags.FLAG_NULL);//add
                JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) listOfDels.size(), null, JIFlags.FLAG_NULL);//del

                if (listOfAdds.size() > 0) {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, new Object().hashCode(), null, JIFlags.FLAG_NULL);//pointer
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, listOfAdds.size(), null, JIFlags.FLAG_NULL);

                    for (int i = 0; i < listOfAdds.size(); i++) {
                        JIObjectId oid = listOfAdds.get(i);
                        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oid.getOID());
                        //JISystem.getLogger().info("[" + oid.toString() + "]");
                    }
                } else {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL);//null pointer
                }

                if (listOfDels.size() > 0) {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, new Object().hashCode(), null, JIFlags.FLAG_NULL);//pointer
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, listOfDels.size(), null, JIFlags.FLAG_NULL);

                    //now align for array
                    double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
                    long k = (k = Math.round(index % 8.0)) == 0 ? 0 : 8 - k;
                    ndr.writeOctetArray(new byte[(int) k], 0, (int) k);

                    for (int i = 0; i < listOfDels.size(); i++) {
                        JIObjectId oid = listOfDels.get(i);
                        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oid.getOID());
                        //JISystem.getLogger().info("[" + oid + "]");
                    }
                } else {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL); //null pointer
                }
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL);
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL);
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL);
                JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL);
                break;

            case 1:// simple ping
                if (setId != null) {
                    JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, setId);//setid
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().log(Level.INFO, "Simple Ping going for setId: {0}", Hexdump.toHexString(setId));
                    }
                } else {
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().info("Some error ! Simple ping requested , but has no setID ");
                    }
                }
                break;

            default:
            //nothing.
        }
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {
        //read response and fill DSs accordingly
        switch (opnum) {
            case 2: //complex ping
                setId = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8);
                //ping factor
                JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, JIFlags.FLAG_NULL, null);

                //hresult
                int hresult = ((Number) (JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null))).intValue();

                if (hresult != 0) {
                    if (JISystem.getLogger().isLoggable(Level.SEVERE)) {
                        JISystem.getLogger().log(Level.SEVERE, "Some error ! Complex ping failed , hresult: {0}", hresult);
                    }
                } else {
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().log(Level.INFO, "Complex Ping Succeeded,  setId is : {0}", Hexdump.toHexString(setId));
                    }
                }

                break;
            case 1:// simple ping
                //hresult
                hresult = ((Number) (JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null))).intValue();

                if (hresult != 0) {
                    if (JISystem.getLogger().isLoggable(Level.SEVERE)) {
                        JISystem.getLogger().log(Level.SEVERE, "Some error ! Simple ping failed , hresult: {0}", hresult);
                    }
                } else {
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().info("Simple Ping Succeeded");
                    }
                }
                break;

            default:
            //nothing.
        }
    }
}
