/**
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v3.
*  
*/

package org.jinterop.dcom.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ndr.NdrBuffer;
import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIComVersion;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;

 /** IRemoteSCMActivator implementation. 
  * 
  * @since 2.09
  *
  */
final class JIRemoteSCMActivator {

	class RemoteCreateInstance extends NdrObject implements JIIServerActivation
	{
		
//		HRESULT RemoteCreateInstance(
//				[in] handle_t hRpc,
//				[in] ORPCTHIS* orpcthis,
//				[out] ORPCTHAT* orpcthat,
//				[in, unique] MInterfacePointer* pUnkOuter,
//				[in, unique] MInterfacePointer* pActProperties,
//				[out] MInterfacePointer** ppActProperties
//				);
		
		private final String targetClsid;
		private final String targetServer;
		private byte[] oxid = null;
		private JIDualStringArray dualStringArrayForOxid = null;
		private String ipid = null;
		private int authenticationHint = -1;
		private JIComVersion comVersion = null;
		private JIInterfacePointer mInterfacePointer = null;
		private boolean isDual = false;
		private String dispIpid = null;
		private int dispRefs = 5;
		private byte[] dispOid = null;
		private boolean isActivationSuccessful = false;
		
		public RemoteCreateInstance(String targetServer, String clsid) 
		{
			this.targetClsid = clsid;
			this.targetServer = targetServer;
		}
		
		public int getOpnum() 
		{
			return 4;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{
			JIOrpcThis orpcThis = new JIOrpcThis();
			orpcThis.encode(ndr);
			
			 ndr.writeUnsignedLong(0); // pUnkOuter, setting it to NULL.
			 
			 ndr.writeUnsignedLong(0x00020000);
			 
			 int index = ndr.getBuffer().getIndex();//recording where we have to write length
			 
			 ndr.writeUnsignedLong(0); //Len 1
			 
			//alignment may kick in
			 int index2 = ndr.getBuffer().getIndex();//recording where we have to write length
			 
			 ndr.writeUnsignedLong(0); //Len 2
			 
			 int countFromIndex = ndr.getBuffer().getIndex();//recording from where we have to write
			 
			 ndr.writeUnsignedLong(0x574f454d); // Signature MEOW
		     ndr.writeUnsignedLong(4); // OBJREF_CUSTOM
			 
			 //now we will write the Custom Interface pointer to Activation Properties.
		     try {
		    	//IID_IActivationPropertiesIn
				 rpc.core.UUID iid_IActivationPropertiesIn = new rpc.core.UUID("000001a2-0000-0000-c000-000000000046");
				 iid_IActivationPropertiesIn.encode(ndr,ndr.getBuffer());
				 rpc.core.UUID clsid_IActivationPropertiesIn = new rpc.core.UUID("00000338-0000-0000-c000-000000000046");
				 clsid_IActivationPropertiesIn.encode(ndr,ndr.getBuffer());
				 
			 } catch (NdrException e) {
				 e.printStackTrace();
			 }
			 
			 int countEntirePayload = ndr.getBuffer().getIndex(); //Entire length of Payload for Custom Marshalling
			 ndr.writeUnsignedLong(0); //extension
			 
			 int writeCountEntirePayloadLength_Here = ndr.getBuffer().getIndex();
			 ndr.writeUnsignedLong(0); //write here (reserved from objref_custom) 
			 
			 
			//Activation Properties Blob 
			 int writeActivationPayload = ndr.getBuffer().getIndex();
			 ndr.writeUnsignedLong(0); //payload to be written here
			 
			 ndr.writeUnsignedLong(0); //reserved
			 
			 int countActivationPayload = ndr.getBuffer().getIndex(); //Only Activation Payload
			 
			 
			JIStruct tempStruct = getCustomHeader();
			int lentempStruct = getLengthOfStruct(tempStruct);
			tempStruct = getCustomHeader();
			addCommonTypeHeaderAndEncode(ndr, tempStruct, lentempStruct);

			tempStruct = getSpecialPropertyData();
			lentempStruct = getLengthOfStruct(tempStruct);
			tempStruct = getSpecialPropertyData();
			addCommonTypeHeaderAndEncode(ndr, tempStruct, lentempStruct);

			tempStruct = getInstantiationInfoData();
			lentempStruct = getLengthOfStruct(tempStruct);
			tempStruct = getInstantiationInfoData();
			addCommonTypeHeaderAndEncode(ndr, tempStruct, lentempStruct);

			tempStruct = getSecurityInfoData();
			lentempStruct = getLengthOfStruct(tempStruct);
			tempStruct = getSecurityInfoData();
			addCommonTypeHeaderAndEncode(ndr, tempStruct, lentempStruct);

			tempStruct = getServerLocationInfo();
			lentempStruct = getLengthOfStruct(tempStruct);
			tempStruct = getServerLocationInfo();
			addCommonTypeHeaderAndEncode(ndr, tempStruct, lentempStruct);

			tempStruct = getScmRequestInfoData();
			lentempStruct = getLengthOfStruct(tempStruct);
			tempStruct = getScmRequestInfoData();
			addCommonTypeHeaderAndEncode(ndr, tempStruct, lentempStruct);

			//now update the length in Common header struct.
			writeEncodingLength(countActivationPayload, countActivationPayload + 16, ndr); // Len for Activation Properties Blob
			
			 writeEncodingLength(countActivationPayload, writeActivationPayload, ndr); // Len for Activation Properties Blob
			 writeEncodingLength(countEntirePayload, writeCountEntirePayloadLength_Here, ndr); // Len for Activation Properties Blob
			 writeEncodingLength(countFromIndex, index, ndr); // Len 1 for the Custom Object Ref
			 writeEncodingLength(countFromIndex, index2, ndr); //Len 2 for the Custom Object Ref
			 
		}
		
		private void writeEncodingLength(int countFromIndex, int writeAtIndex, NetworkDataRepresentation ndr)
		{
			int length = ndr.getBuffer().getIndex() - countFromIndex;
			int temp = ndr.getBuffer().getIndex();
			ndr.getBuffer().setIndex(writeAtIndex);
			ndr.writeUnsignedLong(length);
			ndr.getBuffer().setIndex(temp);
		}
		
		private int getLength(int fromIndex, NetworkDataRepresentation ndr)
		{
			return ndr.getBuffer().getIndex() - fromIndex;
		}
		
		private void writeLength(int lenVal, int writeAtIndex, NetworkDataRepresentation ndr)
		{
			int temp = ndr.getBuffer().getIndex();
			ndr.getBuffer().setIndex(writeAtIndex);
			ndr.writeUnsignedLong(lenVal);
			ndr.getBuffer().setIndex(temp);
		}
		
		//Pass the length from outside as to calculate it we need to encode the struct and that mutates the internal data structs
		//will return total length of the structure including common header and padding.
		private int addCommonTypeHeaderAndEncode(NetworkDataRepresentation ndr, JIStruct struct, int lengthOfStruct)
		{
//			will add the common type header and write on wire
			
			//common header has to be a multiple of 8 bytes. If not it has to be padded at the end.
			int padding = lengthOfStruct%8;
		
			int startI = ndr.getBuffer().getIndex();
			
			//2.2.6.1 Common Type Header for the Serialization Stream (MS-RPCE)
			 ndr.writeUnsignedSmall(0x01);//version
			 ndr.writeUnsignedSmall(0x10);//endianness
			 ndr.writeUnsignedShort(0x08);//common header length
			 ndr.writeUnsignedLong(0xCCCCCCCC); //Filler

			 //now comes the length of the entire CustomHeader without the Common Type Header and this length and Filler.
			 int writeAtIndex = ndr.getBuffer().getIndex();
			 ndr.writeUnsignedLong(0); //write here
			 
			 ndr.writeUnsignedLong(0); //filler, set to NULL
			 
			 int countFromIndex = ndr.getBuffer().getIndex();
			 
			 int x = 0;
				List listOfDefferedPointers = new ArrayList();
				struct.encode(ndr, listOfDefferedPointers, JIFlags.FLAG_NULL);
				while (x < listOfDefferedPointers.size())
				{
					ArrayList newList = new ArrayList();
					Object referent = ((JIPointer)listOfDefferedPointers.get(x)).getReferent();
					if (referent instanceof JIStruct)
					{
						JIMarshalUnMarshalHelper.serialize(ndr,JIStruct.class,referent,newList, JIFlags.FLAG_NULL);
					}
					else
					if (referent instanceof JIString)
					{
						JIMarshalUnMarshalHelper.serialize(ndr,JIString.class,referent,newList, JIFlags.FLAG_NULL);
					}
					else
					{
						JIMarshalUnMarshalHelper.serialize(ndr,JIArray.class,referent,newList, JIFlags.FLAG_NULL);
					}
					x++; //incrementing index
					listOfDefferedPointers.addAll(x,newList);
				}
			
			 if (padding != 0)
			 {
				 padding = 8 - padding;
				 ndr.writeOctetArray(new byte[padding], 0, padding);
			 }
			
			writeEncodingLength(countFromIndex, writeAtIndex, ndr);
			
			return ndr.getBuffer().getIndex() - startI;
		}
		
		private JIStruct getCustomHeader()
		{
			JIStruct struct = _getCustomHeader();
			NetworkDataRepresentation ndr = new NetworkDataRepresentation();
			ndr.setBuffer(new NdrBuffer(new byte[512], 0));
			int lenOfStruct = getLengthOfStruct(struct);
			struct = _getCustomHeader();
			int len = addCommonTypeHeaderAndEncode(ndr, struct, lenOfStruct);
			//now we read the length to put into this struct
			ndr.getBuffer().setIndex(8);
			//int len = ndr.readUnsignedLong() + 16; //8 for common type header and (4 + 4) for header length and reserved.
			struct = _getCustomHeader();
			struct.removeMember(1);
			try {
				struct.addMember(1, new Integer(len)); //will push Reserved to the next place now.
			} catch (JIException e) {
				e.printStackTrace();
			}
			
			return struct;
		}

		
		private JIStruct _getCustomHeader()
		{
			/**
			 * typedef struct tagCustomHeader {
DWORD totalSize;
DWORD headerSize;
DWORD dwReserved;
DWORD destCtx;
[range(MIN_ACTPROP_LIMIT, MAX_ACTPROP_LIMIT)]
DWORD cIfs;
CLSID classInfoClsid;
[size_is(cIfs)] CLSID* pclsid;
[size_is(cIfs)] DWORD* pSizes;
DWORD* pdwReserved;
} CustomHeader;
			 */
			
			JIStruct struct = new JIStruct();
			
			try {

				struct.addMember(new Integer(0)); //Total Activation Blob size
				
				//Correct length set in getCustomHeader.
				struct.addMember(new Integer(0)); //Total Custom header size including the common type header (from this common type header to start of the next common type header)
				
				struct.addMember(new Integer(0));
				
				struct.addMember(new Integer(2));
				
				//sending 5 cIfs
				struct.addMember(new Integer(5));
				
				struct.addMember(new UUID(UUID.NIL_UUID));
				
				struct.addMember(new JIPointer(new JIArray(new UUID[]{
						new UUID("000001b9-0000-0000-c000-000000000046"), new UUID("000001ab-0000-0000-c000-000000000046"),
						new UUID("000001a6-0000-0000-c000-000000000046"), new UUID("000001a4-0000-0000-c000-000000000046"),
						new UUID("000001aa-0000-0000-c000-000000000046")
				},true)));
				
				//now come their sizes including their Common headers.
				NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
				ndr2.setBuffer(new NdrBuffer(new byte[512], 0));
				JIStruct tempStruct = getSpecialPropertyData();
				int lentempStruct = getLengthOfStruct(tempStruct);
				tempStruct = getSpecialPropertyData();
				int lenSpecialSystemProp = addCommonTypeHeaderAndEncode(ndr2, tempStruct, lentempStruct);
				
				ndr2 = new NetworkDataRepresentation();
				ndr2.setBuffer(new NdrBuffer(new byte[512], 0));
				tempStruct = getInstantiationInfoData();
				lentempStruct = getLengthOfStruct(tempStruct);
				tempStruct = getInstantiationInfoData();
				int lenInstantiationInfoProp = addCommonTypeHeaderAndEncode(ndr2, tempStruct, lentempStruct);
				
				ndr2 = new NetworkDataRepresentation();
				ndr2.setBuffer(new NdrBuffer(new byte[512], 0));
				tempStruct = getSecurityInfoData();
				lentempStruct = getLengthOfStruct(tempStruct);
				tempStruct = getSecurityInfoData();
				int lenSecurityInfoProp = addCommonTypeHeaderAndEncode(ndr2, tempStruct, lentempStruct);
				
				ndr2 = new NetworkDataRepresentation();
				ndr2.setBuffer(new NdrBuffer(new byte[512], 0));
				tempStruct = getServerLocationInfo();
				lentempStruct = getLengthOfStruct(tempStruct);
				tempStruct = getServerLocationInfo();
				int lenServerLocationProp = addCommonTypeHeaderAndEncode(ndr2, tempStruct, lentempStruct);
				
				ndr2 = new NetworkDataRepresentation();
				ndr2.setBuffer(new NdrBuffer(new byte[512], 0));
				tempStruct = getScmRequestInfoData();
				lentempStruct = getLengthOfStruct(tempStruct);
				tempStruct = getScmRequestInfoData();
				int lenScmRequestInfoDataProp = addCommonTypeHeaderAndEncode(ndr2, tempStruct, lentempStruct);
				
				
				struct.addMember(new JIPointer(new JIArray(new Integer[]{lenSpecialSystemProp, lenInstantiationInfoProp
						, lenSecurityInfoProp, lenServerLocationProp, lenScmRequestInfoDataProp }, true))); 
				
				struct.addMember(new Integer(0));//reserved
				
			} catch (JIException e) {
				e.printStackTrace();
			}//don't know will correct later.
			
			return struct;
		}
		
		private JIStruct _getInstantiationInfoData()
		{
			/**
			 * typedef struct tagInstantiationInfoData {
CLSID classId;
DWORD classCtx;
DWORD actvflags;
long fIsSurrogate;
[range(1, MAX_REQUESTED_INTERFACES)]
DWORD cIID;
DWORD instFlag;
[size_is(cIID)] IID* pIID;
DWORD thisSize;
COMVERSION clientCOMVersion;
} InstantiationInfoData
			 * */
			
			JIStruct struct = new JIStruct();
			try {
				
				struct.addMember(new UUID(targetClsid));
				struct.addMember(new Integer(0x14));//  CLSCTX_INPROC_HANDLER | CLSCTX_LOCAL_SERVER | CLSCTX_INPROC_SERVER16 
				struct.addMember(new Integer(0));
				struct.addMember(new Integer(0));
				struct.addMember(new Integer(2)); //IUnknown and IDispatch
				struct.addMember(new Integer(0));
				JIPointer ptr = new JIPointer(new JIArray(new UUID[]{new UUID("00000000-0000-0000-c000-000000000046"),
				new UUID("00020400-0000-0000-c000-000000000046"), }, true));
				ptr.setFlags(JIFlags.FLAG_REPRESENTATION_ARRAY);
				struct.addMember(ptr);
				
				//size of the current struct , why ? why ???
				struct.addMember(new Integer(0));//don't know will replace later on. (remove and add)
				struct.addMember(Short.valueOf((short)JISystem.getCOMVersion().getMajorVersion()));
				struct.addMember(Short.valueOf((short)JISystem.getCOMVersion().getMinorVersion()));
				
			} catch (JIException e) {
				e.printStackTrace();
			} //
			
			
			return struct;
		}
		
		private JIStruct getSecurityInfoData()
		{
			/**
			 * typedef struct tagSecurityInfoData {
DWORD dwAuthnFlags;
COSERVERINFO* pServerInfo;
DWORD* pdwReserved;
} SecurityInfoData
			 */
			
			JIStruct struct = new JIStruct();
			try {
				struct.addMember(new Integer(0));
				
				/**
				 * typedef struct _COSERVERINFO {
DWORD dwReserved1;
[string] wchar_t* pwszName;
DWORD* pdwReserved;
DWORD dwReserved2;
} COSERVERINFO;
				 */
				JIStruct coserver = new JIStruct();
				coserver.addMember(new Integer(0));
				coserver.addMember(new JIPointer(new JIString(targetServer, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)));
				coserver.addMember(new Integer(0));
				coserver.addMember(new Integer(0));
				struct.addMember(new JIPointer(coserver));
				struct.addMember(new Integer(0));
			} catch (JIException e) {
				e.printStackTrace();
			}
			
			return struct;
		}
		
		private JIStruct getServerLocationInfo()
		{
			/**
			 * typedef struct tagLocationInfoData {
[string] wchar_t* machineName;
DWORD processId;
DWORD apartmentId;
DWORD contextId;
} LocationInfoData;
			 */
			JIStruct struct = new JIStruct();
			try {
				
				struct.addMember(new Integer(0));
				struct.addMember(new Integer(0));
				struct.addMember(new Integer(0));
				struct.addMember(new Integer(0));
				
			} catch (JIException e) {
				e.printStackTrace();
			}
		
			return struct;
		}
		
		private JIStruct getScmRequestInfoData()
		{
			/**
			 * typedef struct tagScmRequestInfoData {
DWORD* pdwReserved;
customREMOTE_REQUEST_SCM_INFO* remoteRequest;
} ScmRequestInfoData
			 */
			JIStruct struct = new JIStruct();
			try {
				
				struct.addMember(new Integer(0));
				
				/**
				 * typedef struct _customREMOTE_REQUEST_SCM_INFO {
DWORD ClientImpLevel;
[range(0, MAX_REQUESTED_PROTSEQS)]
unsigned short cRequestedProtseqs;
[size_is(cRequestedProtseqs)] unsigned short* pRequestedProtseqs;
} customREMOTE_REQUEST_SCM_INFO;
				 */
				JIStruct _customRemoteRequestSCMInfo = new JIStruct();
				_customRemoteRequestSCMInfo.addMember(new Integer(2));
				_customRemoteRequestSCMInfo.addMember(new Short((short)1));
				_customRemoteRequestSCMInfo.addMember(new JIPointer(new JIArray(new Short[]{(short)0x07}, true)));
				struct.addMember(new JIPointer(_customRemoteRequestSCMInfo));
				
			} catch (JIException e) {
				e.printStackTrace();
			}
		
			return struct;
		}
		
		
		private JIStruct getInstantiationInfoData()
		{
			JIStruct struct = _getInstantiationInfoData();
			int lenOfStruct = getLengthOfStruct(struct);
			struct = _getInstantiationInfoData();
			NetworkDataRepresentation ndr = new NetworkDataRepresentation();
			ndr.setBuffer(new NdrBuffer(new byte[512], 0));
			int len = addCommonTypeHeaderAndEncode(ndr, struct, lenOfStruct);
			//now we read the length to put into this struct
//			ndr.getBuffer().setIndex(8);
//			int len = ndr.readUnsignedLong();
			struct = _getInstantiationInfoData();
			struct.removeMember(7);
			try {
				struct.addMember(7, new Integer(len)); //will push COMVERSION to last place now.
			} catch (JIException e) {
				e.printStackTrace();
			}
			
			return struct;
		}
		
		private JIStruct getSpecialPropertyData()
		{
			/**
			 * typedef struct tagSpecialPropertiesData {
unsigned long dwSessionId;
long fRemoteThisSessionId;
long fClientImpersonating;
long fPartitionIDPresent;
DWORD dwDefaultAuthnLvl;
GUID guidPartition;
DWORD dwPRTFlags;
DWORD dwOrigClsctx;
DWORD dwFlags;
DWORD Reserved1;
unsigned __int64 Reserved2;
DWORD Reserved3[5];
} SpecialPropertiesData;
			 */
			
			JIStruct struct = new JIStruct();
			try {
				struct.addMember(new Integer(0xFFFFFFFF));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000001)); //auth level none ? Why ?
				struct.addMember(new UUID(UUID.NIL_UUID));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x14));
				struct.addMember(new Integer(0x2));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Long(0x0000000000000000));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000000));
				struct.addMember(new Integer(0x00000000));
			} catch (JIException e) {
				e.printStackTrace();
			} //
			
			return struct;
		}
		
		
		//discard this struct after use and create a new one
		private int getLengthOfStruct(JIStruct struct)
		{
			NetworkDataRepresentation ndr = new NetworkDataRepresentation();
			ndr.setBuffer(new NdrBuffer(new byte[512], 0));
			int startI = ndr.getBuffer().getIndex();
			
			int x = 0;
			List listOfDefferedPointers = new ArrayList();
			struct.encode(ndr, listOfDefferedPointers, JIFlags.FLAG_NULL);
			while (x < listOfDefferedPointers.size())
			{
				ArrayList newList = new ArrayList();
				Object referent = ((JIPointer)listOfDefferedPointers.get(x)).getReferent();
				if (referent instanceof JIStruct)
				{
					JIMarshalUnMarshalHelper.serialize(ndr,JIStruct.class,referent,newList, JIFlags.FLAG_NULL);
				}
				else
				if (referent instanceof JIString)
				{
					JIMarshalUnMarshalHelper.serialize(ndr,JIString.class,referent,newList, JIFlags.FLAG_NULL);
				}
				else
				{
					JIMarshalUnMarshalHelper.serialize(ndr,JIArray.class,referent,newList, JIFlags.FLAG_NULL);
				}
				x++; //incrementing index
				listOfDefferedPointers.addAll(x,newList);
			}
			
			return ndr.getBuffer().getIndex() - startI;
		}
		
		
		//Skip common header and return total length of the object buffer inside. We will need to skip the
		//padded bytes as well once we have analyzed the complete objectBuffer.
		private int skipCommonHeader(NetworkDataRepresentation ndr)
		{
			 ndr.readUnsignedSmall();//version
			 ndr.readUnsignedSmall();//endianness
			 ndr.readUnsignedShort();//common header length
			 ndr.readUnsignedLong(); //Filler
			 int retlength = ndr.readUnsignedLong();
			 ndr.readUnsignedLong();//reserved
			 return retlength;
		}
		
		private void skipBytes(int objectBufferLength, int startIndex, NetworkDataRepresentation ndr)
		{
			int bytesRead = ndr.getBuffer().getIndex() - startIndex;
			if (objectBufferLength > bytesRead)
			{
				ndr.readOctetArray(new byte[objectBufferLength - bytesRead], 0, objectBufferLength - bytesRead);
			}
		}
		
		
		
		public void read(NetworkDataRepresentation ndr) 
		{
			
			JIOrpcThat.decode(ndr);
			
			//MInterfacePointer** ppActProperties
			
			ArrayList listOfDefferedPointers = new ArrayList();
			JIInterfacePointer ppActProperties = (JIInterfacePointer)JIMarshalUnMarshalHelper.deSerialize(ndr,JIInterfacePointer.class,listOfDefferedPointers ,JIFlags.FLAG_NULL,new HashMap());
			
			//Class not registered or any other exception probably.
			if (ppActProperties == null)
			{
				int hResult = ndr.readUnsignedLong();
				throw new JIRuntimeException(hResult);
			}
			
			// we should now be standing at the Activation Properties Blob right now. 	
			int totalLength = ndr.readUnsignedLong();
			ndr.readUnsignedLong();//reserved
			
			//Custom Header begins
			//lets check what all has been returned back to us. We are only interested in two Properties (ScmReply and PropsOut)
			//Must contain the following properties
//			ScmReplyInfoData 2.2.22.2.8 Required
//			PropsOutInfo 2.2.22.2.9 Required

			/**
			 * typedef struct tagCustomHeader {
DWORD totalSize;
DWORD headerSize;
DWORD dwReserved;
DWORD destCtx;
[range(MIN_ACTPROP_LIMIT, MAX_ACTPROP_LIMIT)]
DWORD cIfs;
CLSID classInfoClsid;
[size_is(cIfs)] CLSID* pclsid;
[size_is(cIfs)] DWORD* pSizes;
DWORD* pdwReserved;
} CustomHeader;
			 */
			
			int objectBufferLength = skipCommonHeader(ndr);
			int startIndex = ndr.getBuffer().getIndex();
			JIStruct struct = new JIStruct();
			try {
				struct.addMember(Integer.class);
				struct.addMember(Integer.class);
				struct.addMember(Integer.class);
				struct.addMember(Integer.class);
				struct.addMember(Integer.class);//cIfs
				struct.addMember(UUID.class);
				struct.addMember(new JIPointer(new JIArray(UUID.class,null,1,true)));
				struct.addMember(new JIPointer(new JIArray(Integer.class,null,1,true)));
				struct.addMember(Integer.class);
			} catch (JIException e) {
				e.printStackTrace();
			}
			
			struct = decodeStruct(struct, ndr);
			
			skipBytes(objectBufferLength, startIndex, ndr);
			
			//now we need to check for the indexes of our relevant Properties
			
			UUID[] clsidProps = (UUID[])((JIArray)((JIPointer)struct.getMember(6)).getReferent()).getArrayInstance();
			
			Integer[] clsidPropsLengths = (Integer[])((JIArray)((JIPointer)struct.getMember(7)).getReferent()).getArrayInstance();
			
			//using the clsidPropsLengths we can skip the NDR buffer of the properties not needed.
			List<String> requiredProps = new ArrayList<String>();
			requiredProps.add("000001b6-0000-0000-c000-000000000046".toUpperCase());
			requiredProps.add("00000339-0000-0000-c000-000000000046".toUpperCase());
			//we will go sequentially so if a property is not found we skip that many bytes ahead
			for (int i = 0; i < clsidProps.length; i++)
			{
				if (requiredProps.contains(clsidProps[i].toString().toUpperCase()))
				{
					//its present so analyse
					objectBufferLength = skipCommonHeader(ndr);
					startIndex = ndr.getBuffer().getIndex();
					struct = new JIStruct();
					
					if (clsidProps[i].toString().equalsIgnoreCase("000001b6-0000-0000-c000-000000000046"))
					{
						try { //ScmReplyInfo
							
							/**
							 * typedef struct tagScmReplyInfoData {
DWORD* pdwReserved;
customREMOTE_REPLY_SCM_INFO* remoteReply;
} ScmReplyInfoData;
							 */
							
							struct.addMember(Integer.class);
							
							JIStruct remoteReplyStruct = new JIStruct();
							
							/**
							 * typedef struct _customREMOTE_REPLY_SCM_INFO {
OXID Oxid;
DUALSTRINGARRAY* pdsaOxidBindings;
IPID ipidRemUnknown;
DWORD authnHint;
COMVERSION serverVersion;
} customREMOTE_REPLY_SCM_INFO;
							 */
							//we need to take out oxid only way to do it is byte by byte
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							remoteReplyStruct.addMember(Byte.class);
							//8 bytes (4 + 4 LE) = OXID
							remoteReplyStruct.addMember(new JIPointer(JIDualStringArray.class,false));
							remoteReplyStruct.addMember(UUID.class);
							remoteReplyStruct.addMember(Integer.class);
							//COM Version can be taken as two shorts.
							//if this COM version is less than 5.6 than we throw an exception
							remoteReplyStruct.addMember(Short.class);
							remoteReplyStruct.addMember(Short.class);
							
							struct.addMember(new JIPointer(remoteReplyStruct));
							
						} catch (JIException e) {
							e.printStackTrace();
						}
						
						struct = decodeStruct(struct, ndr);
						struct = (JIStruct)(((JIPointer)struct.getMember(1)).getReferent());
						
						//now we need to get the IPID and Dual String Array.
						NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
						NdrBuffer buffer = new NdrBuffer(new byte[8], 0);
						buffer.buf[0] = (byte)(((Byte)struct.getMember(0)) & 0xFF);
						buffer.buf[1] = (byte)(((Byte)struct.getMember(1)) & 0xFF);
						buffer.buf[2] = (byte)(((Byte)struct.getMember(2)) & 0xFF);
						buffer.buf[3] = (byte)(((Byte)struct.getMember(3)) & 0xFF);
						buffer.buf[4] = (byte)(((Byte)struct.getMember(4)) & 0xFF);
						buffer.buf[5] = (byte)(((Byte)struct.getMember(5)) & 0xFF);
						buffer.buf[6] = (byte)(((Byte)struct.getMember(6)) & 0xFF);
						buffer.buf[7] = (byte)(((Byte)struct.getMember(7)) & 0xFF);
						ndr2.setBuffer(buffer);
						
						oxid = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr2,8);
						dualStringArrayForOxid = (JIDualStringArray)(((JIPointer)struct.getMember(8)).getReferent());
						ipid = ((UUID)struct.getMember(9)).toString();
						authenticationHint = (Integer)struct.getMember(10);
						comVersion = new JIComVersion((Short)struct.getMember(11), (Short)struct.getMember(12));
					}
					else
					if (clsidProps[i].toString().equalsIgnoreCase("00000339-0000-0000-c000-000000000046"))
					{
						try { //PropsOutInfo
							
							/**
							 * typedef struct tagPropsOutInfo {
[range(1, MAX_REQUESTED_INTERFACES)]
DWORD cIfs;
[size_is(cIfs)] IID* piid;
[size_is(cIfs)] HRESULT* phresults;
[size_is(cIfs)] MInterfacePointer** ppIntfData;
} PropsOutInfo;
							 */
							
							struct.addMember(Integer.class);
							struct.addMember(new JIPointer(new JIArray(UUID.class,null,1,true)));
							struct.addMember(new JIPointer(new JIArray(Integer.class,null,1,true))); //Hresult, 
							//0 is good anything else is bad and corresponding MInterfacePointer will not exist. 
							struct.addMember(new JIPointer(new JIArray(JIInterfacePointer.class,null,1,true)));
							
						} catch (JIException e) {
							e.printStackTrace();
						}
						
						struct = decodeStruct(struct, ndr);
						
						JIInterfacePointer[] marshalledIp = (JIInterfacePointer[])((JIArray)(((JIPointer)struct.getMember(3)).getReferent())).getArrayInstance();
						
						UUID[] iids = (UUID[])((JIArray)(((JIPointer)struct.getMember(1)).getReferent())).getArrayInstance();
						
						//now get the hresults and only those IIDs are supported which have 0x00000000
						//in our case IUnknown will always be supported (naturally) where as IDispatch may or may not be.
						Integer[] hresults = (Integer[])((JIArray)(((JIPointer)struct.getMember(2)).getReferent())).getArrayInstance();
						for (int j = 0; j < hresults.length; j++)
						{
							if (hresults[j] == 0x00000000)
							{
								//pointer exists
								//if it is Disp IID then set dual stuff else it has to be IUnknown, save it.
								if (iids[j].toString().equalsIgnoreCase("00000000-0000-0000-c000-000000000046"))
								{
									//IUnknown
									mInterfacePointer = marshalledIp[j];
								}
								else if (iids[j].toString().equalsIgnoreCase(""))
								{
									//dual is supported since the IDispatch was obtained
									isDual = true;
									//eat this keeping only the IPID for cleanup , let the user perform another queryInterface for this.
									JIInterfacePointer ptr = marshalledIp[j];
									dispIpid = ptr.getIPID();
									dispOid = ptr.getOID();
									dispRefs = ((JIStdObjRef)ptr.getObjectReference(JIInterfacePointer.OBJREF_STANDARD)).getPublicRefs();
								}
							}
						}
						
					}	
					
					skipBytes(objectBufferLength, startIndex, ndr);
					
					
				}
				else
				{
					byte[] skip = new byte[clsidPropsLengths[i]];
					ndr.readOctetArray(skip, 0, skip.length);
				}
			}
			
			isActivationSuccessful = true;
		}
		
		private JIStruct decodeStruct(JIStruct struct, NetworkDataRepresentation ndr)
		{
			List listOfDefferedPointers = new ArrayList();
			Map additionalData = new HashMap();
			struct = struct.decode(ndr, listOfDefferedPointers, JIFlags.FLAG_NULL, additionalData);
			int x = 0;
			while (x < listOfDefferedPointers.size())
			{
				ArrayList newList = new ArrayList();
				JIPointer replacement = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr, ((JIPointer)listOfDefferedPointers.get(x)), newList, JIFlags.FLAG_NULL, additionalData);
				((JIPointer)listOfDefferedPointers.get(x)).replaceSelfWithNewPointer(replacement); 
				x++; //incrementing index
				listOfDefferedPointers.addAll(x,newList);
			}
			
			return struct;
		}

		public boolean isActivationSuccessful() {
			return isActivationSuccessful;
		}

		public JIDualStringArray getDualStringArrayForOxid() {
			return dualStringArrayForOxid;
		}

		public JIInterfacePointer getMInterfacePointer() {
			return mInterfacePointer;
		}

		public String getIPID() {
			return ipid;
		}

		public boolean isDual() {
			return isDual;
		}

		public String getDispIpid() {
			return dispIpid;
		}

		public int getDispRefs() {
			return dispRefs;
		}

		public void setDispIpid(String dispIpid) {
			this.dispIpid = dispIpid;			
		}
		
	}
	
	
}
