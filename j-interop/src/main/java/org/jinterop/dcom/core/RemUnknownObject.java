/*
 * Copyright NellArmonia
 */
package org.jinterop.dcom.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.IJICOMRuntimeWorker;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import rpc.core.UUID;

//This object should have serialized access only , i.e at a time only 1 read --> write , cycle should happen
//it is not multithreaded safe.
class RemUnknownObject extends NdrObject implements IJICOMRuntimeWorker {

    private static final JIStruct REM_INTERFACE_REF = new JIStruct();

    static {
        try {
            REM_INTERFACE_REF.addMember(UUID.class);
            REM_INTERFACE_REF.addMember(Integer.class);
            REM_INTERFACE_REF.addMember(Integer.class);
        } catch (JIException shouldnothappen) {
            JISystem.getLogger().throwing("RemUnknownObject", "Static Initialiser", shouldnothappen);
        }
    }
    private final String selfIPID;
    private final List<String> listOfIIDsQIed = new ArrayList<>();
    private final Map<String, Integer> mapOfIpidsVsRef = new HashMap<>();
    private static final JIArray remInterfaceRefArray = new JIArray(REM_INTERFACE_REF, null, 1, true);
    //override read\write\opnum etc. here, use the util apis to decompose this.
    private int opnum = -1;
    private NdrBuffer buffer = null;
    //component tells you the JILocalCoClass to act on , sent via the AlterContext calls
    //for all Altercontexts with IRemUnknown , this will be null.
    private JILocalCoClass component = null; //will hold the current instance to act on.
    /* the component and object id duo work together. 1 component could export many ipids. */
    //ObjectID tells you the IPID to act on, sent via the Request calls
    private UUID objectId = null;
    //this would be the ipid of this RemUnknownObject
    private boolean workerOver = false;

    RemUnknownObject(String ipidOfme, String ipidOfComponent) {
        selfIPID = ipidOfme;
        mapOfIpidsVsRef.put(ipidOfComponent.toUpperCase(), 5);
    }

    //this list will get cleared after this call.
    @Override
    public List<String> getQIedIIDs() {
        return listOfIIDsQIed;
    }

    @Override
    public boolean isResolver() {
        return false;
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
        String ipid = objectId.toString();

        //    if (!mapOfIpidsVsRef.containsKey(ipid.toUpperCase()))
        //    {
        //        System.out.println(Thread.currentThread() + " -->> " + ipid.toUpperCase());
        //        //we always give 5 references
        //        mapOfIpidsVsRef.put(ipid.toUpperCase(),new Integer(5));
        //    }
        //this means the call came for IRemUnknown apis, since selfIpid is null or matches the objectID
        //if (selfIPID == null || selfIPID.equalsIgnoreCase(ipid))
        //    if ("00000131-0000-0000-C000-000000000046".equalsIgnoreCase(currentIID))
        if (selfIPID.equalsIgnoreCase(ipid)) {
            switch (opnum) {
                case 3: //IRemUnknown QI.
                    buffer = QueryInterface(ndr);
                    break;
                case 4: //addref
                    JIOrpcThis.decode(ndr);
                    int length = ndr.readUnsignedShort();

                    int[] retvals = new int[length];
                    JIArray array = (JIArray) JIMarshalUnMarshalHelper.deSerialize(ndr, remInterfaceRefArray, new ArrayList<>(), JIFlags.FLAG_REPRESENTATION_ARRAY, new HashMap<>());
                    //saving the ipids with there references. considering public + private references together for now.
                    JIStruct[] structs = (JIStruct[]) array.getArrayInstance();
                    for (int i = 0; i < length; i++) {
                        String ipidref = structs[i].getMember(0).toString().toUpperCase();
                        int publicRefs = ((Number) structs[i].getMember(1)).intValue();
                        int privateRefs = ((Number) structs[i].getMember(2)).intValue();

                        if (!mapOfIpidsVsRef.containsKey(ipidref)) {
                            //this would be strange, since all the ipids we give should be part of the map already.
                            //have to set 0x80000003 (INVALID ARG here)
                            retvals[i] = 0x80000003;
                            continue;
                        }
                        int total = mapOfIpidsVsRef.get(ipidref) + publicRefs + privateRefs;
                        mapOfIpidsVsRef.put(ipidref, total);
                    }

                    //preparing the response
                    buffer = new NdrBuffer(new byte[length * 4 + 16], 0);
                    NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
                    ndr2.setBuffer(buffer);
                    JIOrpcThat.encode(ndr2);
                    for (int i = 0; i < length; i++) {
                        buffer.enc_ndr_long(retvals[i]);
                    }

                    buffer.enc_ndr_long(0);
                    buffer.enc_ndr_long(0);

                    break;
                case 5: //release

                    JIOrpcThis.decode(ndr);
                    length = ndr.readUnsignedShort();
                    array = (JIArray) JIMarshalUnMarshalHelper.deSerialize(ndr, remInterfaceRefArray, new ArrayList<>(), JIFlags.FLAG_REPRESENTATION_ARRAY, new HashMap<>());
                    //saving the ipids with there references. considering public + private references together for now.
                    structs = (JIStruct[]) array.getArrayInstance();
                    for (int i = 0; i < length; i++) {
                        String ipidref = structs[i].getMember(0).toString().toUpperCase();
                        int publicRefs = ((Number) structs[i].getMember(1)).intValue();
                        int privateRefs = ((Number) structs[i].getMember(2)).intValue();
                        if (!mapOfIpidsVsRef.containsKey(ipidref)) {
                            continue;
                        }

                        int total = mapOfIpidsVsRef.get(ipidref) - publicRefs - privateRefs;
                        if (total == 0) {
                            mapOfIpidsVsRef.remove(ipidref);
                        } else {
                            mapOfIpidsVsRef.put(ipidref, total);
                        }
                    }

                    //all references to all IPIDs exported are over, this is now done.
                    if (mapOfIpidsVsRef.isEmpty()) {
                        workerOver = true;
                    }

                    //I have 1 OID == 1 IPID == 1 java instance.
                    buffer = new NdrBuffer(new byte[32], 0);
                    ndr2 = new NetworkDataRepresentation();
                    ndr2.setBuffer(buffer);
                    JIOrpcThat.encode(ndr2);
                    buffer.enc_ndr_long(0);
                    buffer.enc_ndr_long(0);
                    break;
                default:
                    throw new JIRuntimeException(JIErrorCodes.RPC_S_PROCNUM_OUT_OF_RANGE);
            }
        } else {
            //now use the objectId , just set in before this call to read. That objectId is the IPID on which the
            //call is being made , and was previously exported during Q.I. The component value was filled during an
            //alter context or bind, again made some calls before.
            if (component == null) {
                JISystem.getLogger().log(Level.SEVERE, "JIComOxidRuntimeHelper RemUnknownObject read(): component is null , opnum is {0} , IPID is {1} , selfIpid is {2}", new Object[]{opnum, ipid, selfIPID});
            }
            byte b[] = null;
            Object result = null;
            NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
            int hresult = 0;
            Object[] retArray = null;
            try {
                result = component.invokeMethod(ipid, opnum, ndr);
            } catch (JIException e) {
                hresult = e.getErrorCode();
                JISystem.getLogger().log(Level.SEVERE, "Exception occured: {0}", e.getErrorCode());
                JISystem.getLogger().throwing("RemUnknownObject", "read", e);
            }

            //now if opnum was 6 then this is a dispatch call , so response has to be dispatch response
            //not the normal one.
            if (component.getInterfaceDefinitionFromIPID(ipid).isDispInterface() && opnum == 6) {
                Object result2 = result;
                //orpcthat
                //[out] VARIANT * pVarResult,
                //[out] EXCEPINFO * pExcepInfo,
                //[out] UINT * pArgErr,
                //[in, out, size_is(cVarRef)] VARIANTARG * rgVarRef
                result = new Object[4]; //orpcthat gets filled outside
                JIStruct excepInfo = new JIStruct();
                try {
                    excepInfo.addMember((short) 0);
                    excepInfo.addMember((short) 0);
                    excepInfo.addMember(new JIString(""));
                    excepInfo.addMember(new JIString(""));
                    excepInfo.addMember(new JIString(""));
                    excepInfo.addMember(0);
                    excepInfo.addMember(new JIPointer(null, true));
                    excepInfo.addMember(new JIPointer(null, true));
                    excepInfo.addMember(0);
                } catch (JIException e) { //not expecting any here
                }

                if (result2 == null) {
                    ((Object[]) result)[0] = JIVariant.EMPTY();
                } else {
                    //now check whether the variant is by ref or not.
                    JIVariant variant = (JIVariant) ((Object[]) result2)[0];

                    try {
                        if (variant.isByRefFlagSet()) {
                            //add empty inplace of this.
                            ((Object[]) result)[0] = JIVariant.EMPTY();
                            //now update the array at the end.
                            ((Object[]) result)[3] = new JIArray(new JIVariant[]{variant}, true);

                        } else {
                            ((Object[]) result)[0] = ((Object[]) result2)[0]; //will have only a single index.
                            ((Object[]) result)[3] = 0; //Array
                        }
                    } catch (JIException e) {
                        throw new JIRuntimeException(e.getErrorCode());
                    }
                }

                ((Object[]) result)[1] = excepInfo;
                ((Object[]) result)[2] = 0; //argErr is null, for now.
                retArray = (Object[]) result;
            }

            buffer = new NdrBuffer(b, 0);
            ndr2.setBuffer(buffer);

            //JIOrpcThat.encode(ndr2);
            //have to create a call Object, since these return types could be structs , unions etc. having deffered pointers
            JICallBuilder callObject = new JICallBuilder();
            callObject.attachSession(component.getSession());
            if (result != null) {

                if (retArray != null) {
                    //serialize all members sequentially.
                    for (final Object item : retArray) {
                        callObject.addInParamAsObject(item, JIFlags.FLAG_NULL);
                    }
                } else {
                    //serialize all members sequentially.
                    for (final Object item : ((Object[]) result)) {
                        callObject.addInParamAsObject(item, JIFlags.FLAG_NULL);
                    }
                }
            }
            callObject.write2(ndr2);
            JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, hresult, null, JIFlags.FLAG_NULL);
        }
    }

    @Override
    public void setCurrentObjectID(UUID objectId) {
        this.objectId = objectId;
        component = JIComOxidRuntime.getJavaComponentFromIPID(objectId.toString());
    }

    @Override
    public UUID getCurrentObjectID() {
        return objectId;
    }

    @Override
    public void setCurrentIID(String iid) {
        //does nothing
    }

    @Override
    public boolean workerOver() {
        return workerOver;
    }

    private NdrBuffer QueryInterface(NetworkDataRepresentation ndr) {
        //now to decompose all
        if (JISystem.getLogger().isLoggable(Level.FINEST)) {
            JISystem.getLogger().finest("Within RemUnknownObject: QueryInterface");
            JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] Before call terminated listOfIIDsQIed are: {0}", listOfIIDsQIed);
        }
        JIOrpcThis.decode(ndr);

        //now get the IPID and export the component with a new IPID and IID.
        String ipid = new UUID(ndr.getBuffer()).toString();
        if (JISystem.getLogger().isLoggable(Level.FINEST)) {
            JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] IPID is {0}", ipid);
        }
        //set the JILocalCoClass., the ipid should not be null in this call.
        JIComOxidDetails details = JIComOxidRuntime.getComponentFromIPID(ipid);

        if (details == null) {
            //not found, now throw an JIRuntimeException , so that a FaultPdu could be sent.
            throw new JIRuntimeException(JIErrorCodes.RPC_E_INVALID_OXID);
        }

        JILocalCoClass componentRef = details.getReferent();
        if (JISystem.getLogger().isLoggable(Level.FINEST)) {
            JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] JIJavcCoClass is {0}", componentRef.getCoClassIID());
        }

        ((Number) (JIMarshalUnMarshalHelper.deSerialize(ndr, Integer.class, null, JIFlags.FLAG_NULL, null))).intValue();//refs , don't really care about this.
        int length = ((Number) (JIMarshalUnMarshalHelper.deSerialize(ndr, Short.class, null, JIFlags.FLAG_NULL, null))).intValue();//length of the requested Interfaces
        JIArray array = (JIArray) JIMarshalUnMarshalHelper.deSerialize(ndr, new JIArray(UUID.class, null, 1, true), null, JIFlags.FLAG_REPRESENTATION_ARRAY, null);

        //now to build the buffer and export the IIDs with new IPIDs
        byte[] b = new byte[8 + 4 + 4 + length * (4 + 4 + 40) + 16];
        //start with response
        NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
        ndr2.setBuffer(new NdrBuffer(b, 0));
        JIOrpcThat.encode(ndr2);
        //pointer
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, new Object().hashCode(), null, JIFlags.FLAG_NULL);
        //length of array
        JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, length, null, JIFlags.FLAG_NULL);

        Object[] arrayOfUUIDs = (Object[]) array.getArrayInstance();

        for (int i = 0; i < arrayOfUUIDs.length; i++) {
            UUID iid = (UUID) arrayOfUUIDs[i];
            if (JISystem.getLogger().isLoggable(Level.FINEST)) {
                JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] Array iid[{0}] is {1}", new Object[]{i, iid});
            }
            //now for each QueryResult
            try {
                int hresult = 0;
                String ipid2 = UUID.createHexString();
                if (!componentRef.isPresent(iid.toString())) {
                    hresult = JIErrorCodes.E_NOINTERFACE;
                } else {
                    String tmpIpid = null;
                    try {
                        tmpIpid = componentRef.getIpidFromIID(iid.toString());
                    } catch (Exception e) {
                        JISystem.getLogger().throwing("JIComOxidRuntimeHelper", "QueryInterface", e);
                    }

                    if (tmpIpid == null) {
                        if (JISystem.getLogger().isLoggable(Level.FINEST)) {
                            JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] tmpIpid is null for iid {0}", iid);
                        }
                        componentRef.exportInstance(iid.toString(), ipid2);
                    } else {
                        if (JISystem.getLogger().isLoggable(Level.FINEST)) {
                            JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] tmpIpid is NOT null for iid {0} and ipid sent back is {1}", new Object[]{iid, ipid2});
                        }
                        ipid2 = tmpIpid;
                    }
                }
                //hresult
                JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, hresult, null, JIFlags.FLAG_NULL);
                JIMarshalUnMarshalHelper.serialize(ndr2, Integer.class, 0xCCCCCCCC, null, JIFlags.FLAG_NULL);
                //now generate the IPID and export a java instance with this.
                JIStdObjRef objRef = new JIStdObjRef(ipid2, details.getOxid(), details.getOid());
                objRef.encode(ndr2);
                //add it to the exported Ipids map
                mapOfIpidsVsRef.put(ipid2.toUpperCase(), objRef.getPublicRefs());

                if (JISystem.getLogger().isLoggable(Level.FINEST)) {
                    JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] for which the stdObjRef is {0}", objRef);
                }
            } catch (IllegalAccessException | InstantiationException e) {
                JISystem.getLogger().throwing("JIComOxidRuntimeHelper", "QueryInterface", e);
            }

            String iidtemp = iid.toString().toUpperCase() + ":0.0";
            if (!listOfIIDsQIed.contains(iidtemp)) {
                listOfIIDsQIed.add(iidtemp);
            }
        }

        if (JISystem.getLogger().isLoggable(Level.FINEST)) {
            JISystem.getLogger().log(Level.FINEST, "RemUnknownObject: [QI] After call terminated listOfIIDsQIed are: {0}", listOfIIDsQIed);
        }

        return buffer;
    }
}
