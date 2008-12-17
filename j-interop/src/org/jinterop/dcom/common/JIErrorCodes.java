/**j-Interop (Pure Java implementation of DCOM protocol)  
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jinterop.dcom.common;

//TODO look at winerror.h
/**
 * All errorcodes. ErrorCodes begining with "JI" are j-Interop error codes.  <br>
 * 
 * @since 1.0
 */
public final class JIErrorCodes {
 private JIErrorCodes(){}
 
 /**
  * Incorrect function.
  */
 public static final int  ERROR_INVALID_FUNCTION = 0x00000001;

 /**
  * The system cannot find the file specified. 
  */
  
 public static final int ERROR_FILE_NOT_FOUND = 0x00000002;
 
  
 /**
  *The system cannot find the path specified. 
  */
 public static final int ERROR_PATH_NOT_FOUND    = 0x00000003;
 
 /**
  * The filename, directory name, or volume label syntax is incorrect.
  */
 public static final int ERROR_INVALID_NAME = 0x0000007B;
 
 /**
  * File already exists.
  */
 public static final int ERROR_ALREADY_EXISTS = 0x000000B7;
 
 /**
  * No more data is available.
  */
 public static final int ERROR_NO_MORE_ITEMS = 0x00000103;
 /**
  * Class not registered
  */
 public static final int REGDB_E_CLASSNOTREG = 0x80040154;
 
 /**
  * Interface not registered 
  */
 public static final int REGDB_E_IIDNOTREG = 0x80040155;
 
 /**
  * Access is denied. 
  */
 public static final int ERROR_ACCESS_DENIED = 0x00000005;
 
 /**
  * Catastrophic failure.
  */
 public static final int E_UNEXPECTED = 0x8000FFFF;
 
 /**
  * Not implemented.
  */
 public static final int E_NOTIMPL = 0x80004001;
 
 /**
  * Not enough storage is available to complete this operation. 
  */
 public static final int E_OUTOFMEMORY = 0x8007000E;
 
 
 /**
  * The parameter is incorrect. 
  */
 public static final int E_INVALIDARG = 0x80070057;
 
 /**
  * The RPC server is unavailable. 
  */
 public static final int RPC_SERVER_UNAVAILABLE = 0x800706BA;
 
 /**
  * No such interface supported.
  */
 public static final int E_NOINTERFACE = 0x80004002;
 
 /**
  * Access is denied. 
  */
 public static final int E_ACCESSDENIED = 0x80070005;
 
 /**
  * A Remote activation was necessary but the server name provided was invalid.
  */
 public static final int CO_E_BAD_SERVER_NAME = 0x80004014;
 
 /**
  * The server process could not be started.  The pathname may be incorrect. 
  */
 public static final int CO_E_CREATEPROCESS_FAILURE = 0x80004018;
 
 /**
  * The server process could not be started as the configured identity.  The pathname may be incorrect or unavailable. 
  */
 public static final int CO_E_RUNAS_CREATEPROCESS_FAILURE  = 0x80004019;
 
 /**
  * The server process could not be started because the configured identity is incorrect.  Check the username and password. 
  */
 public static final int CO_E_RUNAS_LOGON_FAILURE = 0x8000401A;
 
 /**
  * The client is not allowed to launch this server. 
  */
 public static final int CO_E_LAUNCH_PERMSSION_DENIED = 0x8000401B;
 
 /**
  * Server execution failed.
  */
 public static final int CO_E_SERVER_EXEC_FAILURE = 0x80080005;
 
 /**
  * System call failed. You might need to restart the server machine.
  */
 public static final int RPC_E_SYS_CALL_FAILED = 0x80010100;
 
 /**
  * Unknown interface. 
  */
 public static final int DISP_E_UNKNOWNINTERFACE = 0x80020001;
 
 /**
  * Member not found. 
  */
 public static final int DISP_E_MEMBERNOTFOUND = 0x80020003;
 
 /**
  * Parameter not found. 
  */
 public static final int DISP_E_PARAMNOTFOUND = 0x80020004;
 
 /**
  * Type mismatch.  
  */
 public static final int DISP_E_TYPEMISMATCH = 0x80020005;
 
 /**
  * No named arguments. 
  */
 public static final int DISP_E_NONAMEDARGS = 0x80020007;
 
 /**
  * Bad variable type. 
  */
 public static final int DISP_E_BADVARTYPE = 0x80020008;
 
 /**
  * Exception occurred. 
  */
 public static final int DISP_E_EXCEPTION = 0x80020009;
 
 /**
  * Invalid index. 
  */
 public static final int DISP_E_BADINDEX = 0x8002000B;
 
 /**
  * Invalid number of parameters. 
  */
 public static final int DISP_E_BADPARAMCOUNT = 0x8002000E;
 
 /**
  * Parameter not optional. 
  */
 public static final int DISP_E_PARAMNOTOPTIONAL = 0x8002000F;
 
 /**
  * The requested object or interface does not exist. 
  */
 public static final int RPC_E_INVALID_IPID = 0x80010113;
 
 /**
  * The requested object does not exist. 
  */
 public static final int RPC_E_INVALID_OBJECT = 0x80010114;
 
 /**
  * The marshaled interface data packet (OBJREF) has an invalid or unknown format. 
  */
 public static final int RPC_E_INVALID_OBJREF = 0x8001011D;
 
 /**
  * An internal error occurred. 
  */
 public static final int RPC_E_UNEXPECTED = 0x8001FFFF;
 
 /**
  * Call was rejected by callee. 
  */
 public static final int RPC_E_CALL_REJECTED = 0x80010001;
 
 /**
  * Unknown name.
  */
 public static final int DISP_E_UNKNOWNNAME = 0x80020006;
 
 /**
  * Wrong module kind for the operation.
  */
 public static final int TYPE_E_BADMODULEKIND  = 0x800288BD;
 
 /**
  * Element not found.
  */
 public static final int TYPE_E_ELEMENTNOTFOUND  = 0x8002802B;

 /**
  * COM server could not establish call back connection. 
  */
 public static final int E_NOINTERFACE_CALLBACK = 0x80040202;
 
 /**
  * The object exporter was not found. 
  */
 public static final int RPC_E_INVALID_OXID = 0x80070776;
 
 /**
  * The stub recieved bad data. . Please check whether the API has been called in the right way, with correct parameter formation.
  */
 public static final int RPC_E_INVALID_DATA = 0x800706F7;
 
 /**
  * The procedure number is out of range.
  */
 public static final int RPC_S_PROCNUM_OUT_OF_RANGE2 = 0x800706D1;
 
 /**
  * The procedure number is out of range.
  */
 public static final int RPC_S_PROCNUM_OUT_OF_RANGE = 0xC002002E;
 
 /**
  * Access Violation.
  */
 public static final int RPC_S_ACCESS_VIOLATION = 0xC0000005;
 
 /**
  * The server threw an exception. 
  */
 public static final int RPC_E_SERVERFAULT  = 0x80010105;
  
 /**
  * Invalid Callee.
  */
 public static final int DISP_E_BADCALLEE = 0x80020010;
 
 /**
  *  The object invoked has disconnected from its clients.
  */
 public static final int RPC_E_DISCONNECTED = 0x80010108;

 /**
  * The version of OLE on the client and server machines does not match. 
  */
 public static final int RPC_E_VERSION_MISMATCH = 0x80010110;
 
 /**
  * Space for tools is not available.
  */
 public static final int INPLACE_E_NOTOOLSPACE  = 0x800401A1;
 
 /**
  * The attempted logon is invalid. This is either due to a bad username or authentication information.
  */
 public static final int WIN_AUTH_FAILURE  = 0xC000006D;
 
 
 /**
  * Unspecified Error.
  */
 public static final int E_FAIL = 0x80004005; 
 
 
 
/////System's Own ...start from 0x00001001 to 0x00002001

 /**
  * Object is already instantiated.
  */
 public static final int JI_OBJECT_ALREADY_INSTANTIATED = 0x00001001;
 
 /**
  * This API cannot be invoked right now, further operations are required before the system is ready to 
  * give out results through this API.
  */
 public static final int JI_API_INCORRECTLY_CALLED = 0x00001002;
 
 /**
  * Session is already established, please initiate a new session for new Stub.
  */
 public static final int JI_SESSION_ALREADY_ESTABLISHED= 0x00001003;
 
 /**
  * Discriminant cannot be null
  */
 public static final int JI_UNION_NULL_DISCRMINANT = 0x00001004;
 
 /**
  * Discriminant class type mismatch, please provide object of the same class as discriminant.
  */
 public static final int JI_UNION_DISCRMINANT_MISMATCH = 0x00001005;
 
 /**
  * Only 1 discriminant allowed for serialization, please remove the rest or no discriminant has been added at all.
  */
 public static final int JI_UNION_DISCRMINANT_SERIALIZATION_ERROR = 0x00001006;
 
 /**
  * No discriminant value has been added at all.
  */
 public static final int JI_UNION_DISCRMINANT_DESERIALIZATION_ERROR = 0x00001007;
 
 /**
  * Incorrect Value of FLAG sent for this API. This FLAG is not valid here.
  */
 public static final int JI_UTIL_FLAG_ERROR = 0x00001008;
 
 /**
  * Internal Library Error. This method should not have been called. Please check the parameters which you have passed to JICallBuilder. 
  * They have been sent incorrectly.
  */
 public static final int JI_UTIL_INCORRECT_CALL = 0x00001009;
 
 /**
  * Outparams cannot have more than 1 parameter here. It should be a JIVariant class parameter.
  */
 public static final int JI_DISP_INCORRECT_OUTPARAM = 0x0000100A;
 
 /**
  * Parameters inparams and dispId\paramNames arrays should have same length.
  */
 public static final int JI_DISP_INCORRECT_PARAM_LENGTH = 0x0000100B;
 
 /**
  * This in parameter cannot have null or "" values.
  */
 public static final int JI_DISP_INCORRECT_VALUE_FOR_GETIDNAMES = 0x0000100C;
 
 /**
  * progId\clsid,address,session cannot be empty or null.
  */
 public static final int JI_COMSTUB_ILLEGAL_ARGUMENTS = 0x0000100D;
 
 /**
  * Could not retrieve JIClsid from JIProgId via Windows Remote Registry Service
  */
 public static final int JI_COMSTUB_RR_ERROR = 0x0000100E;
 
 /**
  *	Internal Library Error, the serializer\deserializer was not found for {0}. Please check the parameters passed to JICallBuilder.
  */
 public static final int JI_UTIL_SERDESER_NOT_FOUND = 0x0000100F;
 
 /**
  *	Authentication information was not supplied.
  */
 public static final int JI_AUTH_NOT_SUPPLIED = 0x00001010;
 
 /**
  *	Incorrect or Invalid Parameter(s) specified.
  */
 public static final int JI_COMFACTORY_ILLEGAL_ARG = 0x00001011;
 
 /**
  * The template cannot be null. 
  */
 public static final int JI_ARRAY_TEMPLATE_NULL = 0x00001012;
 
 /**
  * Only Arrays Accepted as parameter.
  */
 public static final int JI_ARRAY_PARAM_ONLY = 0x00001013;

 /**
  * Arrays of Primitive Data Types are not accepted
  */
 public static final int JI_ARRAY_PRIMITIVE_NOTACCEPT = 0x00001014;

 /**
  * Can only accept JIStruct, JIUnion, JIPointer and JIString as parameters for template.
  */
 public static final int JI_ARRAY_INCORRECT_TEMPLATE_PARAM = 0x00001015;

 /**
  * IPID cannot be null.
  */
 public static final int JI_OBJ_NULL_IPID = 0x00001016;

 /**
  * Discriminant can only be of the type Integer,Short,Boolean or Character.
  */
 public static final int JI_UNION_INCORRECT_DISC = 0x00001017;

 /**
  * Referent ID for <code>VARIANT</code> not found.
  */
 public static final int JI_VARIANT_NO_REFERENT_ID = 0x00001018;
 
 /**
  * This is a programming error, this API should not be called.
  */
 public static final int JI_ILLEGAL_CALL = 0x00001019;
 
 /**
  * The parameters cannot be null.
  */
 public static final int JI_COM_RUNTIME_INVALID_CONTAINER_INFO = 0x0000101A;
 
 /**
  * An array has already been added as member and it has to be the last member of this Struct. Please insert this member elsewhere.
  */
 public static final int JI_STRUCT_ARRAY_AT_END = 0x0000101B;
 
 /**
  * An array can be added only as a last member in a structure and not inbetween.
  */
 public static final int JI_STRUCT_ARRAY_ONLY_AT_END = 0x0000101C;
 
 /**
  * This struct already has an array and the member (which also happens to be a Struct) has an array too. This member can only be present in the second last position of this new Struct.
  */
 public static final int JI_STRUCT_INCORRECT_NESTED_STRUCT_POS = 0x0000101D;
 
 /**
  * Member(which happens to be a Struct) has an array and hence can only be added to the end of this Struct , not in between.
  */
 public static final int JI_STRUCT_INCORRECT_NESTED_STRUCT_POS2 = 0x0000101E;
 
 /**
  * Authentication failure for the credentials sent by the COM server for performing call back. The identity is checked via a call back to the source COM server using SMB.
  */
 public static final int JI_CALLBACK_AUTH_FAILURE = 0x0000101F;
 
 /**
  * SMB connection failure, please check whether SERVER service is running on Target machine (where COM server) is hosted.
  */
 public static final int JI_CALLBACK_SMB_FAILURE = 0x00001020;
 
 /**
  * Illegal here to invoke this API.
  */
 public static final int JI_CALLBACK_COMOBJECT_STATE_FAILURE = 0x00001021;
 
 /**
  * Variants can only take BSTR Strings and no other String Type.
  */
 public static final int JI_VARIANT_BSTR_ONLY = 0x00001022;
 
 /**
  * Overloaded APIs are not allowed.
  */
 public static final int JI_CALLBACK_OVERLOADS_NOTALLOWED = 0x00001023;
 
 /**
  * Variants cannot take object[] having Variants themselves as indices.
  */
 public static final int JI_VARIANT_VARARRAYS_NOTALLOWED = 0x00001024;
 
 /**
  * fractionalUnits cannot be negative.
  */
 public static final int JI_CURRENCY_FRAC_NEGATIVE = 0x00001025;
 
 /**
  * Variant is null.
  */
 public static final int JI_VARIANT_IS_NULL = 0x00001026;

 /**
  * Library currently accepts only upto 2 dimension for the JIVariant
  */
 public static final int JI_VARIANT_VARARRAYS_2DIMRES = 0x00001027;

 /**
  * The upperbounds is to be specified for all dimensions or not specified at all. 
  */
 public static final int JI_ARRAY_UPPERBNDS_DIM_NOTMATCH = 0x00001028;

 /**
  * Please use the JIArray to pass arrays.
  */
 public static final int JI_VARIANT_ONLY_JIARRAY_EXCEPTED = 0x00001029;

 /**
  * Unsupported type for VARIANT.
  */
 public static final int JI_VARIANT_UNSUPPORTED_TYPE = 0x00001030;

 /**
  * Unable to access Windows Registry, please check whether the SERVER service is running on the Target Workstation.
  */
 public static final int JI_WINREG_EXCEPTION = 0x00001031;

 /**
  * Invalid Identifier, or there is no Connection Info associated with this identifer on this comObject.
  */
 public static final int JI_CALLBACK_INVALID_ID = 0x00001032;

 /**
  * Could not set the correct encoding for password field.
  */
 public static final int JI_WINREG_EXCEPTION2 = 0x00001033;
  
 /**
  * Unknown hostname\ip was supplied for obtaining handle to WinReg
  */
 public static final int JI_WINREG_EXCEPTION3 = 0x00001034;
 
 /**
  * Type not supported for setting\getting value in\from registry.
  */
 public static final int JI_WINREG_EXCEPTION4 = 0x00001035;
  
 /**
  *Illegal values sent as parameters, please check "data". 
  */
 public static final int JI_WINREG_EXCEPTION5 = 0x00001036;
 
 /**
  * JILocalMethodDescriptor is being added to a JILocalInterfaceDefinition supporting dispInterface, but it itself does not have a 
  * dispId.
  */
 public static final int JI_METHODDESC_DISPID_MISSING = 0x00001037;
 
 /**
  * No parameters can be null or "".
  */
 public static final int JI_CALLBACK_INVALID_PARAMS = 0x00001038;
 
 /**
  * Unsupported charset supplied while encoding or decoding String.
  */
 public static final int JI_UTIL_STRING_DECODE_CHARSET = 0x00001039;
 
 /**
  * Unsigned numbers cannot be negative or null.
  */
 public static final int JI_UNSIGNED_NEGATIVE = 0x00001040;
 
 /**
  * Class not supportted for unsigned operations. Only Long,Short,Integer allowed.
  */
 public static final int JI_UNSIGNED_INCORRECT_TYPE = 0x00001041;
 
 /**
  * "Object.class" arrays are not accepted. Only properly typed arrays accepted.
  */
 public static final int JI_ARRAY_TYPE_INCORRECT = 0x00001042;
 
 
 /**
  * This JILocalCoClass has already been exported with one interface pointer, please use a new instance of this class with JIInterfacePointer.getInterfacePointer(...) api.
  */
 public static final int JI_JAVACOCLASS_ALREADY_EXPORTED = 0x00001043;
 
 /**
  * JIInterfacePointer is not a valid parameter, please use JIVariant(IJIComObject,...).   
  */
 public static final int JI_VARIANT_TYPE_INCORRECT = 0x00001044;
 
 /**
  * Direct Marshalling, UnMarshalling of Strings are not allowed, please use JIString instead.
  */
 public static final int JI_UTIL_STRING_INVALID = 0x00001045;
 
 /**
  * createInstance() cannot be called since the JIComServer(JISession, JIInterfacePointer, String) ctor was used to create this COM server instance, please use getInstance() instead.
  */
 public static final int JI_COMSTUB_WRONGCALLCREATEINSTANCE = 0x00001046;
 
 /**
  * getInstance() cannot be called since the JIComServer(JISession, JIInterfacePointer, String) ctor was NOT used to create this COM server instance, please use createInstance() instead.
  */
 public static final int JI_COMSTUB_WRONGCALLGETINSTANCE = 0x00001047;
 
 /**
  * A session is already attached with this COM object.
  */
 public static final int JI_SESSION_ALREADY_ATTACHED = 0x00001048;
 
 /**
  * This API cannot be invoked on local references.
  */
 public static final int JI_COMOBJ_LOCAL_REF = 0x00001049;
 
 /**
  * A session is not attached with this object , use JIObjectFactory.buildObject(JISession, IJIComObject) to attach a session with this object.
  */
 public static final int JI_SESSION_NOT_ATTACHED = 0x00001050;
 
 /**
  * The associated session is being destroyed. Current call to COM server has been terminated.
  */
 public static final int JI_SESSION_DESTROYED = 0x00001051;
 
 
 
 
}
