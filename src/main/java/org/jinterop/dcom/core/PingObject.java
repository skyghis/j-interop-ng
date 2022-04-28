/**
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
package org.jinterop.dcom.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.util.Hexdump;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

class PingObject extends NdrObject {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
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
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("Complex Ping going for the first time, will get the setId as response of this call ");
                    }
                    setId = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Complex Ping going for setId: {0}", Hexdump.toHexString(setId));
                    }
                }

                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Complex ping going : listOfAdds -> Size : {0} , {1}", new Object[]{listOfAdds.size(), listOfAdds});
                    LOGGER.log(Level.INFO, "listOfDels -> Size : {0} , {1}", new Object[]{listOfDels.size(), listOfDels});
                }

                JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, setId);

                JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) seqNum, null, JIFlags.FLAG_NULL);//seq
                JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) listOfAdds.size(), null, JIFlags.FLAG_NULL);//add
                JIMarshalUnMarshalHelper.serialize(ndr, Short.class, (short) listOfDels.size(), null, JIFlags.FLAG_NULL);//del

                if (!listOfAdds.isEmpty()) {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, new Object().hashCode(), null, JIFlags.FLAG_NULL);//pointer
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, listOfAdds.size(), null, JIFlags.FLAG_NULL);

                    for (int i = 0; i < listOfAdds.size(); i++) {
                        JIObjectId oid = listOfAdds.get(i);
                        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oid.getOID());
                        //LOGGER.info("[" + oid.toString() + "]");
                    }
                } else {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, 0, null, JIFlags.FLAG_NULL);//null pointer
                }

                if (!listOfDels.isEmpty()) {
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, new Object().hashCode(), null, JIFlags.FLAG_NULL);//pointer
                    JIMarshalUnMarshalHelper.serialize(ndr, Integer.class, listOfDels.size(), null, JIFlags.FLAG_NULL);

                    //now align for array
                    double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
                    long k = (k = Math.round(index % 8.0)) == 0 ? 0 : 8 - k;
                    ndr.writeOctetArray(new byte[(int) k], 0, (int) k);

                    for (int i = 0; i < listOfDels.size(); i++) {
                        JIObjectId oid = listOfDels.get(i);
                        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr, oid.getOID());
                        //LOGGER.info("[" + oid + "]");
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
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Simple Ping going for setId: {0}", Hexdump.toHexString(setId));
                    }
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("Some error ! Simple ping requested , but has no setID ");
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
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Some error ! Complex ping failed , hresult: {0}", hresult);
                    }
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Complex Ping Succeeded,  setId is : {0}", Hexdump.toHexString(setId));
                    }
                }

                break;
            case 1:// simple ping
                //hresult
                hresult = ((Number) (JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null))).intValue();

                if (hresult != 0) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Some error ! Simple ping failed , hresult: {0}", hresult);
                    }
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("Simple Ping Succeeded");
                    }
                }
                break;

            default:
            //nothing.
        }
    }
}
