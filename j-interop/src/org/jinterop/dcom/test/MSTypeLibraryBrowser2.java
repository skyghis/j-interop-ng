package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.win32.ElemDesc;
import org.jinterop.dcom.win32.FuncDesc;
import org.jinterop.dcom.win32.IJIDispatch;
import org.jinterop.dcom.win32.IJITypeInfo;
import org.jinterop.dcom.win32.IJITypeLib;
import org.jinterop.dcom.win32.IMPLETYPEFLAGS;
import org.jinterop.dcom.win32.JIComFactory;
import org.jinterop.dcom.win32.TYPEKIND;
import org.jinterop.dcom.win32.TypeAttr;
import org.jinterop.dcom.win32.TypeDesc;
import org.jinterop.dcom.win32.VarDesc;

public class MSTypeLibraryBrowser2 {
	
	private JIComServer comServer = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null; 
	
	public MSTypeLibraryBrowser2(String address, String args[]) throws JIException, UnknownHostException
	{
		JISession session = JISession.createSession(args[1],args[2],args[3]);
		session.useSessionSecurity(true);
		comServer = new JIComServer(JIProgId.valueOf(session,args[4]),address,session);
	}
	
	public void start() throws JIException
	{
		unknown = comServer.createInstance();
		dispatch = (IJIDispatch)JIComFactory.createCOMInstance(JIComFactory.IID_IDispatch,unknown);
		IJITypeLib typeLib = (IJITypeLib)((Object[])dispatch.getTypeInfo(0).getContainingTypeLib())[0];
		Object[] result = typeLib.getDocumentation(-1);
		System.out.println("Name: " + ((JIString)result[0]).getString());
		System.out.println("Library Name: " + ((JIString)result[1]).getString());
		System.out.println("Full path to help file: " + ((JIString)result[3]).getString());
		System.out.println("\n------------------------Library Members---------------------");
		int typeInfoCount = typeLib.getTypeInfoCount();
		String g_arrClassification[] = { "Enum","Struct","Module","Interface",
			"Dispinterface","Coclass","Typedef","Union"};
		for(int l = 0; l < typeInfoCount;l++)
		{
			System.out.println("\n\n-----------------------Member Description--------------------------");
			result = typeLib.getDocumentation(l);
			int k = typeLib.getTypeInfoType(l);
			
			
			System.out.println("Name: " + ((JIString)result[0]).getString());
			System.out.println("Type: " + g_arrClassification[k]);
			
			
			IJITypeInfo typeInfo = typeLib.getTypeInfo(l);
			TypeAttr typeAttr = typeInfo.getTypeAttr();
			IJITypeInfo ptempInfo = null;
			TypeAttr pTempAttr = null;
			if(typeAttr.typekind != TYPEKIND.TKIND_DISPATCH.intValue() && typeAttr.typekind != TYPEKIND.TKIND_COCLASS.intValue())
			{
				int p = 0;
				p++;
			}
			
			if(typeAttr.typekind == TYPEKIND.TKIND_COCLASS.intValue())
			{
				
				for (int i = 0;i<typeAttr.cImplTypes;i++)
				{
					int nFlags = -1;
					try{
						nFlags = typeInfo.getImplTypeFlags(i);	
					}catch (JIException e) {
						continue;	
					}
					
					if((nFlags & IMPLETYPEFLAGS.IMPLTYPEFLAG_FDEFAULT) == IMPLETYPEFLAGS.IMPLTYPEFLAG_FDEFAULT)
					{
						int hRefType = -1;
						try{
							hRefType = typeInfo.getRefTypeOfImplType(i);
						}catch(JIException e)
						{
							break;
						}
						
						
						try{
							ptempInfo = typeInfo.getRefTypeInfo(hRefType);
						}catch(JIException e)
						{
							break;
						}
						
						try{
							pTempAttr = ptempInfo.getTypeAttr();
						}catch(JIException e)
						{
							System.out.println("Failed to get reference type info.");
							return;
						}
					}
				}
	
			}
			
			if (pTempAttr != null)
			{
				typeInfo = ptempInfo;
				typeAttr = pTempAttr;
			}
			
			int m_nMethodCount = typeAttr.cFuncs;
			int m_nVarCount = typeAttr.cVars;
			int m_nDispInfoCount = m_nMethodCount+2*m_nVarCount;
			System.out.println("Method and variable count = " + m_nMethodCount + m_nVarCount + "\n\n");
			
			
			for(int i = 0;i < m_nMethodCount; i++)
			{
				System.out.println("************Method Seperator*****************");	
				FuncDesc pFuncDesc ;
				
				try{
					pFuncDesc = typeInfo.getFuncDesc(i);
				}catch(JIException e)
				{
					e.printStackTrace();
					return;
				}
				
				System.out.println(i + ": DispID = " + pFuncDesc.memberId);
	
				int nCount;
				try{
					Object[] ret = typeInfo.getNames(pFuncDesc.memberId ,1);
					System.out.println("MethodName = " + ((JIString)((Object[])((JIArray)ret[0]).getArrayInstance())[0]).getString());
					nCount = ((Integer)ret[1]).intValue();
				}catch(JIException e)
				{
					System.out.println("GetNames failed.");
					return;
				}
				
				switch(pFuncDesc.invokeKind)
				{
				
				case 2://INVOKEKIND.INVOKE_PROPERTYGET.intValue():
					System.out.println("PropertyGet");
					break;
				case 4://INVOKEKIND.INVOKE_PROPERTYPUT.intValue():
					System.out.println("PropertyPut");
					break;
				case 8://INVOKEKIND.INVOKE_PROPERTYPUTREF.intValue():
					System.out.println("PropertyPutRef");
					break;
				case 1://INVOKEKIND.INVOKE_FUNC.intValue():
					System.out.println("DispatchMethod");
					break;
				default:
					break;
				}
				
				System.out.println("VTable offset: " + pFuncDesc.oVft);
				System.out.println("Calling convention: " + pFuncDesc.callConv);
				//TODO need to return a string representation of this.
				System.out.println("Return type = " + pFuncDesc.elemdescFunc.typeDesc.vt);
				System.out.println("ParamCount = " + pFuncDesc.cParams);
				JIArray array = (JIArray)pFuncDesc.lprgelemdescParam.getReferent();
				ElemDesc[] types = null;
				if (array != null)
				{
					Object[] temp = (Object[])array.getArrayInstance();
					types = new ElemDesc[temp.length];
					for (int k1 = 0;k1 < temp.length;k1++)
					{
						types[k1] = new ElemDesc((JIStruct)temp[k1]);
					}
				}
				
				for(int j = 0;j < pFuncDesc.cParams; j++)
				{
					
					if(((ElemDesc)types[j]).typeDesc.vt == TypeDesc.VT_SAFEARRAY.shortValue())
					{
						System.out.println("Param(" + j + ") type = SafeArray" );
					}
					else if(((ElemDesc)types[j]).typeDesc.vt == TypeDesc.VT_PTR.shortValue())
					{
						System.out.println("Param(" + j + ") type = Pointer" );
					}
					else
					{
						System.out.println("Param(" + j + ") type = UserDefined" );
					}
				}
			}
				
			
			for(int i = m_nMethodCount; i < m_nMethodCount + m_nVarCount; i++)
			{
				System.out.println("************Variable Seperator*****************");	
				VarDesc pVarDesc;
				try{
					pVarDesc = typeInfo.getVarDesc(i - m_nMethodCount);
				}catch(JIException e)
				{
					System.out.println("GetVarDesc failed.");
					return;
				}
				
				System.out.println(i + ": DispID = " + pVarDesc.memberId);
	
				int nCount;
				try{
					Object[] ret = typeInfo.getNames(pVarDesc.memberId ,1);
					System.out.println("VarName = " + ((JIString)((Object[])((JIArray)ret[0]).getArrayInstance())[0]).getString());
					nCount = ((Integer)ret[1]).intValue();
				}catch(JIException e)
				{
					System.out.println("GetNames failed.");
					return;
				}
				
				switch(pVarDesc.varkind)
				{
				case VarDesc.VAR_DISPATCH:
					System.out.println("VarKind = VAR_DISPATCH");
					System.out.println("VarType = " + pVarDesc.elemdescVar.typeDesc.vt);
					break;
				default:
					//TODO resolve to it's string representation
					System.out.println("VarKind = " + pVarDesc.varkind);
					break;
				}
			}
		}
		
		System.out.println("########################Execution complete#########################");
		JISession.destroySession(dispatch.getAssociatedSession());
	}
	
	public static void main(String[] args) {
		try {
			if (args.length < 5)
		    {
		    	System.out.println("Please provide address domain username password progIdOfApplication");
		    	return;
		    }
			JISystem.getLogger().setLevel(Level.OFF);
			JISystem.setInBuiltLogHandler(false);
			MSTypeLibraryBrowser2 typeLibraryBrowser = new MSTypeLibraryBrowser2(args[0],args);
			typeLibraryBrowser.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
