/*
 * Copyright NellArmonia
 */
package org.jinterop.dcom.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.IJICOMRuntimeWorker;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import rpc.core.UUID;

//This object should have serialized access only , i.e at a time only 1 read --> write , cycle should happen
// it is not multithreaded safe.
class OxidResolverImpl extends NdrObject implements IJICOMRuntimeWorker {
    //override read\write\opnum etc. here, use the util apis to decompose this.

    private final Random random = new Random(System.currentTimeMillis());
    private int opnum = -1;
    private NdrBuffer buffer = null;

    OxidResolverImpl(Properties p) {
        super();
    }

    @Override
    public void setCurrentObjectID(UUID objectId) {
        //does nothing.
    }

    @Override
    public void setOpnum(int opnum) {
        this.opnum = opnum;
    }

    @Override
    public int getOpnum() {
        return opnum;
    }

    @Override
    public void write(NetworkDataRepresentation ndr) {
        ndr.setBuffer(buffer); //this buffer is prepared via read.
    }

    @Override
    public void read(NetworkDataRepresentation ndr) {
        //will read according to the opnum. The setOpnum should have been called before this
        //call.

        switch (opnum) {
            case 1:
                buffer = SimplePing(ndr);
                break;
            case 2:
                buffer = ComplexPing(ndr);
                break;
            case 3: //ServerAlive
                buffer = ServerAlive();
                break;
            case 5: //This is ServerAlive2
                buffer = ServerAlive2();
                break;
            case 4: //This is ResolveOxid2
                buffer = ResolveOxid2(ndr);
                break;
            default: //should not have arrived here.
                if (JISystem.getLogger().isLoggable(Level.WARNING)) {
                    JISystem.getLogger().warning("Oxid Object: DEFAULTED !!!");
                }
                throw new JIRuntimeException(JIErrorCodes.RPC_S_PROCNUM_OUT_OF_RANGE);
        }

    }

    private NdrBuffer SimplePing(NetworkDataRepresentation ndr) {
        if (JISystem.getLogger().isLoggable(Level.INFO)) {
            JISystem.getLogger().info("Oxid Object: SimplePing");
        }
        byte b[] = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8);//setid
        JIComOxidRuntime.addUpdateSets(new JISetId(b), new ArrayList<>(), new ArrayList<>());
        buffer = new NdrBuffer(new byte[16], 0);
        buffer.enc_ndr_long(0);
        buffer.enc_ndr_long(0);
        buffer.enc_ndr_long(0);
        buffer.enc_ndr_long(0);
        return buffer;
    }

    private NdrBuffer ComplexPing(NetworkDataRepresentation ndr) {
        if (JISystem.getLogger().isLoggable(Level.INFO)) {
            JISystem.getLogger().info("Oxid Object: ComplexPing");
        }
        byte b[] = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8);//setid
        JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, JIFlags.FLAG_NULL, null);//seqId.
        Short lengthAdds = (Short) JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, JIFlags.FLAG_NULL, null);//
        Short lengthDels = (Short) JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, JIFlags.FLAG_NULL, null);//
        JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null);//

        JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null);//length
        List<JIObjectId> listOfAdds = new ArrayList<>();
        for (int i = 0; i < lengthAdds.intValue(); i++) {
            listOfAdds.add(new JIObjectId(JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8), false));
        }
        JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null);//length
        List<JIObjectId> listOfDels = new ArrayList<>();
        for (int i = 0; i < lengthDels.intValue(); i++) {
            listOfDels.add(new JIObjectId(JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8), false));
        }
        if (Arrays.equals(b, new byte[]{0, 0, 0, 0, 0, 0, 0, 0})) {
            random.nextBytes(b);
        }
        JIComOxidRuntime.addUpdateSets(new JISetId(b), listOfAdds, listOfDels);

        buffer = new NdrBuffer(new byte[32], 0);
        NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
        ndr2.setBuffer(buffer);

        JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr2, b);
        JIMarshalUnMarshalHelper.serialize(ndr2, Short.class, (short) 0, null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, 0, null, JIFlags.FLAG_NULL);//hresult
        return buffer;
    }

    private NdrBuffer ServerAlive() {
        if (JISystem.getLogger().isLoggable(Level.INFO)) {
            JISystem.getLogger().info("Oxid Object: ServerAlive");
        }
        byte[] buffer = new byte[32]; //16 + 16=just in case
        NdrBuffer ndrBuffer = new NdrBuffer(buffer, 0);
        ndrBuffer.enc_ndr_long(0);
        ndrBuffer.enc_ndr_long(0);
        ndrBuffer.enc_ndr_long(0);
        ndrBuffer.enc_ndr_long(0);
        return ndrBuffer;
    }

    private NdrBuffer ServerAlive2() {
        if (JISystem.getLogger().isLoggable(Level.INFO)) {
            JISystem.getLogger().info("Oxid Object: ServerAlive2");
        }
        //there is no in params for this.
        //only out params

        //want no port information associated with this.
        //    byte[] buffer = new byte[120];
        //    FileInputStream inputStream;
        //    try {
        //      inputStream = new FileInputStream("c:/serveralive2");
        //      inputStream.read(buffer,0,120);
        //    } catch (Exception e) {
        //      // TODO Auto-generated catch block
        //      e.printStackTrace();
        //    }
        //
        //    NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
        JIDualStringArray dualStringArray = new JIDualStringArray(-1);

        byte[] buffer = new byte[dualStringArray.getLength() + 4 /* COMVERSION */ + 16 /* 2 unknown 8 bytes */ + 16/* just in case */];
        NdrBuffer ndrBuffer = new NdrBuffer(buffer, 0);

        NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
        ndr2.setBuffer(ndrBuffer);

        //serialize COMVERSION
        JIMarshalUnMarshalHelper.serialize(ndr2, Short.class, (short) JISystem.getCOMVersion().getMajorVersion(), null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Short.class, (short) JISystem.getCOMVersion().getMinorVersion(), null, JIFlags.FLAG_NULL);

        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, 0, null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, dualStringArray.getLength(), null, JIFlags.FLAG_NULL);
        dualStringArray.encode(ndr2);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, 0, null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, 0, null, JIFlags.FLAG_NULL);
        return ndrBuffer;
    }
    //will prepare a NdrBuffer for reply to this call

    private NdrBuffer ResolveOxid2(NetworkDataRepresentation ndr) {
        if (JISystem.getLogger().isLoggable(Level.INFO)) {
            JISystem.getLogger().info("Oxid Object: ResolveOxid2");
        }
        //System.err.println("VIKRAM: resolve oxid thread Id = " + Thread.currentThread().getId());
        //first read the OXID, then consult the oxid master about it's details.
        JIOxid oxid = new JIOxid(JIMarshalUnMarshalHelper.readOctetArrayLE(ndr, 8));
        //now get the RequestedProtoSeq length.
        JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, JIFlags.FLAG_NULL, null);
        //now for the array.
        JIMarshalUnMarshalHelper.deSerialize(ndr, new JIArray(Short.class, null, 1, true), null, JIFlags.FLAG_REPRESENTATION_ARRAY, null);
        //now query the Resolver master for this data.
        JIComOxidDetails details = JIComOxidRuntime.getOxidDetails(oxid);

        if (details == null) {
            //not found, now throw an JIRuntimeException , so that a FaultPdu could be sent.
            throw new JIRuntimeException(JIErrorCodes.RPC_E_INVALID_OXID);
        }

        //    byte[] buffer = new byte[424];
        //    FileInputStream inputStream;
        //    try {
        //      inputStream = new FileInputStream("c:/resolveoxid2");
        //      inputStream.read(buffer,0,424);
        //    } catch (Exception e) {
        //      // TODO Auto-generated catch block
        //      e.printStackTrace();
        //    }
        //
        //    try {
        //      details.getCOMRuntimeHelper().startRemUnknown();
        //    } catch (IOException e) {
        //      // TODO Auto-generated catch block
        //      e.printStackTrace();
        //    }
        //
        //    NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
        //
        //randomly create IPID and send, this is the ipid of the remunknown, we store it with remunknown object
        UUID uuid = details.getRemUnknownIpid() == null ? new UUID(UUID.createHexString()) : new UUID(details.getRemUnknownIpid());

        //create the bindings for this Java Object.
        //this port will go in the new bindings sent to the COM client.
        int port = -1;
        try {
            //this is so that repeated calls for Oxid resolution return the same rem unknwon.
            port = details.getPortForRemUnknown();
            if (port == -1) {
                String remunknownipid = uuid.toString();
                Object[] portandthread = details.getCOMRuntimeHelper().startRemUnknown(details.getIID(), remunknownipid, details.getIpid(), details.getReferent().getSupportedInterfaces());
                port = ((Number) portandthread[0]).intValue();
                details.setRemUnknownThreadGroup((ThreadGroup) portandthread[1]);
                details.setRemUnknownIpid(remunknownipid);
            }
            details.setPortForRemUnknown(port);
        } catch (IOException e) {

            throw new JIRuntimeException(JIErrorCodes.E_UNEXPECTED);
        }

        //can support only TCP connections
        //JIDualStringArray.test = true;
        JIDualStringArray dualStringArray = new JIDualStringArray(port);

        Integer authnHint = details.getProtectionLevel();

        //have all data now prepare the response
        //the response expected here is defines the byte array size.
        final int bufferLength = 4 + 4 + dualStringArray.getLength() + 16 + 4 + 2 + 2 + 4 + 16;
        NdrBuffer ndrBuffer = new NdrBuffer(new byte[bufferLength], 0);

        NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
        ndr2.setBuffer(ndrBuffer);

        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, new Object().hashCode(), null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, ((dualStringArray.getLength() - 4) / 2), null, JIFlags.FLAG_NULL);
        dualStringArray.encode(ndr2);

        JIMarshalUnMarshalHelper.serialize(ndr2, UUID.class, uuid, null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, authnHint, null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Short.class, (short) JISystem.getCOMVersion().getMajorVersion(), null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Short.class, (short) JISystem.getCOMVersion().getMinorVersion(), null, JIFlags.FLAG_NULL);
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, 0, null, JIFlags.FLAG_NULL); //hresult

        return ndrBuffer;
    }

    @Override
    public List<String> getQIedIIDs() {
        return null;
    }

    @Override
    public UUID getCurrentObjectID() {
        return null;
    }

    @Override
    public boolean isResolver() {
        return true;
    }

    @Override
    public void setCurrentIID(String iid) {
        //does nothing
    }

    @Override
    public boolean workerOver() {
        //oxid resolver gets over when the client connected to it releases socket.
        return false;
    }
}
