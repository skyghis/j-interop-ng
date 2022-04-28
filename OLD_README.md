# j-Interop

----

:warning: This documentation is probably **deprecated**.
It is imported from repository at version _Release 2.01 (RC 6), 13th Jul 2008_ and converted to markdown format.
Cleanup and update is necessary to match present code and Windows OS versions.

----

## Introduction

Implementation of DCOM wire protocol (MSRPC) to enable development of **Bi-Directional, Pure and Non-Native** Java applications which can interoperate with any COM component.
The implementation is not dependent on JNI for COM interoperability.
It also allows for complete Windows Registry manipulation operations (create, read, update, delete) using the WinReg interface.
More information about j-Interop is available [here](http://www.j-interop.org).

## Contents

1. [Getting Started](#Getting-Started)
1. [Frequently Asked Questions](#Frequently-Asked-Questions)
1. [Third Party Dependencies](#Third-Party-Dependencies)
1. [Installation](#Installation)
1. [Examples](#Examples)
1. [License](#License)
1. [Third Party Licenses](#Third-Party-Licenses)
1. [Support](#Support)
1. [Acknowledgements](#Acknowledgements)

## Requirements

## Getting Started

First, please follow the [installation](#Installation) instructions. Once done, the best way to get started is by looking at the [examples](#examples).
You may want to skip the [FAQs](#Frequently-Asked-Questions) with [**Advance**] tag for the time being.
You can get back to them once you are a bit comfortable with j-Interop.

If the COM server you are trying to access has a Windows COM client (executed from a Remote machine), then it is probably already configured for DCOM access.
If it is not so, please read [FAQ (A5)](#A5)</A>.
If the COM server you are using is a `DLL`\\`OCX` (In-Proc), you may want to have a look at [FAQ (A6)](#A6).
<
Some important things, **before you start**:

- Please make sure that the _Server Service_ and _Remote Registry Service_ are running on the Workstation where the COM server resides.
Read [FAQ (A12)](#A12) for more details.
- To avoid getting **ACCESS DENIED** exceptions by COM server, it is best to create a session under the identity of currently logged in user.

For e.g. if you are logged into your local machine under username `administrator`, the session should be created as:

```java
JISession session = JISession.createSession("localhost","administrator","PASSWORD");
```

For a Domain like `MYDOMAIN`, it can be likewise:

```java
JISession session = JISession.createSession("MYDOMAIN","DOMAINUSER","USERPASSWORD");
```

Incase granting `administrators` permission is a concern, then :

- You can create a local user under "Users" group.
- Then go to Control Panel > Administrative Tools > Local Security Policy > Security Settings > Local Policies > Security Options :
  - Double-click "DCOM: Machine Access Restrictions" policy, click Edit Security, add the user created above, allow "Remote Access"
  - Double-click "DCOM: Machine Launch Restrictions" policy, click Edit Security, add the user created above, allow "Local Launch", "Remote Launch", "Local Activation", "Remote Activation"
- Go to Control Panel > Administrative Tools > Component Services > Computers > right-click My Computer > click Properties > click COM Security tab :
  - In Access Permissions section, click Edit Default > add the user created above, allow "Remote Access"
  - In Launch and Activation Permissions section > click Edit Default > add the user created above, allow "Local Launch", "Remote Launch", "Local Activation", "Remote Activation"
    (The Component Services section, to be more accurate, you can go to a specific component, and grant permission from there, instead of from "My Computer", which is a blanket grant)

Sometimes the Windows Firewall will act up if not configured properly, so please make sure that you have either configured it for DCOM protocol or turned it off.
Please note that the firewall issue will prevent _all_ DCOM Windows applications to fail as well.

Please make sure that your Windows machine (where COM server is hosted) is up to date with all the _Service packs_ and _updates_ from Microsoft.
Many a times issues are due to improper machine configuration.

[This](http://j-integra.intrinsyc.com/support/com/doc/remoteaccess.html) is a good article on how to configure DCOM using _DCOMCnfg_.

If you are not really familiar with DCOM, then [this](http://www.winehq.com/site/docs/winedev-guide/dcom-1) article provides a good overview.
The architecture document can be found [here](http://msdn.microsoft.com/library/default.asp?url=/library/en-us/dndcom/html/msdn_dcomarch.asp).

## Frequently Asked Questions

1. [What is the JRE version compatibility of j-Interop ?](#A1)
1. [How do I install it ?](#A2)
1. [What threading model do the COM servers adhere to while in use from j-Interop ?](#A3) **[Advance]**
1. [What threading model does j-Interop follow ?](#A4) **[Advance]**
1. [How do I configure my COM server for DCOM access ?](#A5)
1. [My COM Server is a `DLL`\\`OCX` (Inproc server), how do I make it work with j-Interop ?](#A6)
1. [Why could the library not do step 7 for me automatically?](#A7)
1. [What all COM Interfaces are directly supported ?](#A8)
1. [What data types are supported by j-Interop ?](#A9)
1. [COM to Java Data Type Mappings](#A10) **[Advance]**
1. [Do I have to do any reference counting,memory management etc. ?](#A11)
1. [Any configuration to be done before using j-Interop ?](#A12)
1. [Is there any logging done ?](#A13)
1. [What type of License does it follow ?](#A14)

### (A1)

Tested with JRE 1.4 and 1.6 on Windows XP(SP2), Windows Vista(SP1), Windows 2K3(SP2) and Ubuntu 8.4(desktop).
Compatible with JRE version 1.4 and above.

### (A2)

Please see the third party dependencies and installation section.

### (A3)

From MSDN:

On Windows side, Local servers (EXEs) are in full control of the kind of apartment(threading model) that COM is using for their objects.
The local server calls `CoInitializeEx` on one or more threads and registers the class factory (or multiple class factories) from the appropriate thread with the appropriate threading model.

The In-process servers (DLLs), however run in the context of their client i.e they run in the apartment the client gives them.
By client, I don't mean j-Interop here, but a Windows COM Client.
In-process components indicate the threading model they are ready to satisfy by placing a named value (`ThreadingModel`) under their `InprocServer32` key in the registry:

`[HKEY_CLASSES_ROOT\CLSID\{clsid}\InprocServer32]`

"ThreadingModel"="Both" or "ThreadingModel"="Apartment" or "ThreadingModel"="Free".

`[HKEY_CLASSES_ROOT\CLSID\{clsid}\InprocServer32]`

"ThreadingModel"="Both" or "ThreadingModel"="Apartment" or "ThreadingModel"="Free"

If ThreadingModel is not specified, the component is assumed to follow the assumptions for `STA-Main` and can only be loaded into the main STA in a process.
A value of `Both` indicates that the component can be loaded in both MTAs and STAs.
A value of `Apartment` indicates that the component can be loaded into any STA.
A value of `Free` indicates that the component can be loaded into an MTA, but not into an STA.

### (A4)

j-Interop follows the `Apartment` model i.e regardless of threading model or the component type (Local or Inproc) used by the COM Server, j-Interop synchronizes all calls to the COM Server per `org.jinterop.dcom.core.JIComServer` instance (which is the starting point for each COM Server).

For e.g. in the following piece of code :

```java
JISession session = JISession.createSession("DOMAIN", "USERNAME", "PASSWORD");
JIComServer comServer = new JIComServer(JIProgId.valueOf("Excel.Application"), "127.0.0.1", session);
IJIComObject comObject = comServer.createInstance();
IJIDispatch dispatch = (IJIDispatch)JIObjectFactory.narrowObject(comObject.queryInterface(IJIDispatch.IID));
IJITypeInfo typeInfo = dispatch.getTypeInfo(0);
FuncDesc funcDesc = typeInfo.getFuncDesc(0);
int dispId = dispatch.getIDsOfNames("Visible");
JIVariant variant = new JIVariant(Boolean.TRUE);
dispatch.put(dispId, variant);<p>
```

Calls from all interfaces (`dispatch` and `typeInfo`), even if they are running on different threads, are synchronized at the `JIComServer` (`comServer`) level.
That said, within an application, there can be more than one `JIComServer`s running at the same time and they run independent of each other.

### (A5)

Ideally if your COM server is actively being used for remote access, then it is perhaps already configured for DCOM.
If not you can configure it by following steps mentioned [here](http://support.microsoft.com/kb/268550/EN-US/) or [here](http://j-integra.intrinsyc.com/support/com/doc/remoteaccess.html).

For Windows XP (SP2), [this](http://j-integra.intrinsyc.com/support/com/doc/#remoteaccess.html#winxpsp2) is a good link.

Alternatively, [this](http://www.techvanguards.com/com/tutorials/tips.htm#Know%20the%203%20most%20important%20things%20in%20COM%20security) is also a good article.

### (A6)

Ideally if your COM server is actively being used for remote access, then it is perhaps already configured for DCOM.
If not, you have 2 ways to do this.
Both ways are recommended by Microsoft. I personally prefer the _Easiest way_.

#### Easiest Way

Let the j-Interop library do this for you. You can set the `autoRegistration` flag in the `JISystem` or the `JIClsid`, `JIProgId` classes.
When the library encounters a `Class not registered` exception, it will perform all the registry changes if the autoRegistration flag is set.
And then re-attempt loading the COM Server.

Please have a look at `MSSysInfo`, `MSWMI` examples.

#### Easy Way

From MSDN, [here](http://support.microsoft.com/kb/198891/en-us) (skip to the section titled below):

> Modify registry to force remoting of the object

- Use the OLE/COM Object viewer (`Oleview.exe`) that is shipped with Microsoft Visual C++ and locate the ProgID in the form of `OLEComponent.Object` under **All Objects**.
- Select the COM object, and then from the **Object** menu, select **CoCreateInstance Flags**. Make sure that only `CLSCTX_LOCAL_SERVER` is selected.
- Next, under the **Implementation** and **Inproc Server** tabs select **Use Surrogate Process** and leave the "Path to Custom Surrogate" blank, which allows the `Dllhost.exe` file to be loaded and the COM DLL brought within it's process space.

If you do not have Microsoft Visual C++, the OLE/COM Object Viewer utility is also available for download from the following Microsoft Web site: [http://www.microsoft.com/downloads/details.aspx?familyid=5233b70d-d9b2-4cb5-aeb6-45664be858b6&displaylang=en](http://www.microsoft.com/downloads/details.aspx?familyid=5233b70d-d9b2-4cb5-aeb6-45664be858b6&displaylang=en).

#### Hard Way

1. For each CLSID encountered in the DLL component, create an AppID value under `HKCR\CLSID\{clsid}` which contains the same value as the CLSID.
  `HKCR\CLSID\{clsid}`
  `AppID = {appid value}`
1. Using the same AppID value, then create an AppID key under `HKCR\AppID`.
  `HKCR\AppID\{appid}`
1. Under `HKCR\AppID\{appid}`, then create the following values:
    - `(Default) =`
    - `DllSurrogate =`

For example, let's say `YourDll.dll` contains one class called `MyClass` with CLSID `{6A048AAA-7DDD-4CCC-BE59-9BBB746E5C6E}`.
To host `YourDll.dll` using `dllhost`, you will need to create the following entries in the registry:

- `HKCR\CLSID\{6A048AAA-7DDD-4CCC-BE59-9BBB746E5C6E}`
  - AppID = `{6A048AAA-7DDD-4CCC-BE59-9BBB746E5C6E}`
- `HKCR\AppID\{6A048AAA-7DDD-4CCC-BE59-9BBB746E5C6E}`
  - (Default) = `Your DLL Surrogate`
  - DllSurrogate =

You would then be able to configure this surrogate by running `DCOMCNFG` and looking for the Application entry called `Your DLL Surrogate`.

Please note that the use of surrogates for accessing DLLs is **not a j-Interop specification, but a COM specification**.

In _any_ DCOM case for accessing a `DLL`\\`OCX` you would need the DLLHOST.
It is a Microsoft DCOM DLL Host Process.
If the COM server being accessed is an Exe, like MSWord or MSExcel or IE then this is not required. But for DLLs , it is required.
This is how the DCOM clients talk to In-Process Servers.
You can obtain more info from [here](http://windowssdk.msdn.microsoft.com/en-us/library/ms695225.aspx) (please open in IE for proper viewing).

Also, it would be _best to view j-Interop as a DCOM client_ when accessing COM from Java.
It would be much easier to work with it then.
Whatever configurations are required for a DCOM client, will be required for j-Interop also.

#### (A7)

j-Interop behaves as a COM client to the COM Server, changes in <A href=#a6>step 6</a> have to be done at the server machine's registry.
It is best that the user initiate those actions instead of the library doing these silently.

#### (A8)

All automation interfaces like `IDispatch`, `ITypeInfo`, `ITypeLib`, `IEnumVariant` are directly supported.
You can start using them right away.

#### (A9)

All DCOM datatypes including `VARIANT`s are supported by j-Interop.
The only limitation in the present version is that Arrays up to Maximum 2 dimensions are accepted currently.

#### (A10)

After going through some of the examples, it should be fairly simple (or so I hope) to gauge what COM data type maps to which Java type, but here are some hints anyways:

1. All primitive data types of COM map to their corresponding Java counterparts, with the exception of `long`, which maps to `int` at the Java side.
1. `IIDs` are j-Interop uuids (represented as `java.lang.String`).
1. `OLECHAR` is `JIString(LPWSTR)`.
1. <a name="iv"></a>Top level pointers are pointers that are NOT elements of arrays, NOT members of structures or unions.
    All Top level "Interface" pointers (`IUnknown*`, `IDispatch*`, `IDispatch**`, `ITypeLib**` etc.) are mapped to there referents themselves.
    For e.g : `ITypeLib** ppTLib` or `ITypeInfo* ppTInfo` maps directly to `JIInterfacePointer` and NOT to `JIPointer(JIInterfacePointer)`.
1. <a name="v"></a>All Other Top level pointers are mapped as following:
    - First level indirection is mapped directly to there referents.
        For e.g. `int*` is `int`, `double*` is `double`, `BSTR*` is `JIString(BSTR_FLAG)` ,`OLECHAR FAR *` ptr is `JIString(LPWSTR)`.
    - <a name="vb"></a>Second and subsequent level indirections are mapped to there (level - 1) indirections.
        For e.g. `int**` maps to`JIPointer(int)`, `int***` maps to `JIPointer(JIPointer(int))`, `double**` maps to `JIPointer(double)`, `double***` maps to `JIPointer(JIPointer(double))`.
    - All data types can be mapped like [rule 4](#iv) and [5(a,b)](#v).
        The exception to above rules are `BSTR**` and `VARIANT**`.
        Since `BSTR` and `VARIANT`s in COM are inherently pointers themselves, they follow [rule v(b)](#vb) only after 3rd level of indirection.
        i.e. `BSTR*` and `BSTR**` are both mapped to `JIString(BSTR)`. `VARIANT*` and `VARIANT**` are both mapped to `JIVariant(,byRef=true);`
        3rd and subsequent level indirections of `BSTR` or `VARIANT`s are mapped according to rule (level - 2).
        for e.g. the `BSTR***` mapped to `JIPointer(JIString(BSTR))` , `VARIANT***` is mapped to `JIPointer(JIVariant(,byRef=true));`
1. <a name="p6"></a>When using `IJIDispatch`, you will be required to use `JIVariants`.
    Automation in COM does not allow indirection beyond level 2.
    So simple mappings would suffice for non pointer types, and for pointer types as parameters, please use the `byRef` flag of JIVariant.

    Most of the times the MSDN documentation itself will tell you what the data type stands for, just use the corresponding type in j-Interop.

    For e.g., from MSDN:

    ```cpp
    IDispatch::GetIDsOfNames
    HRESULT GetIDsOfNames(
        REFIID  riid,
        OLECHAR FAR* FAR*  rgszNames,
        unsigned int  cNames,
        LCID   lcid,
        DISPID FAR*  rgDispId
    );
    ```

    - `riid` is Reserved for future use. Must be IID_NULL.
    - `rgszNames` is Passed-in array of names to be mapped.
    - `cNames` is Count of the names to be mapped.
    - `lcid` is The locale context in which to interpret the names
    - `rgDispId` is Caller-allocated array, each element of which contains an identifier (ID) corresponding to one of the names passed in the `rgszNames` array.
        The first element represents the member name. The subsequent elements represent each of the member's parameters.

    j-Interop definition for these would be:

    - `riid` is `uuid`.
    - `rgszNames` is `JIArray(JIPointer(JIString(LPWSTR)))`.
    - `cNames` is `int`.
    - `lcid` is `int` (for this you will have to look up MSDN, it translates to a `long`, which maps to `int` in j-Interop.
    - `rgDispId` is `JIArray(Integer)`.

1. Embedded Pointers (members of structures, unions, elements of arrays) are represented by `JIPointer(type)`, like the `OLECHAR FAR* FAR*` `rgszNames` in [Point (6)](#p6).
    It first got mapped to `JIArray` since it is a top level pointer.
    Within the array, it was supposed to return pointers to `OLECHAR`, therefore it got mapped to `JIPointer(JIString(LPWSTR))` forming the whole definition as `JIArray(JIPointer JIString(LPWSTR)))`.

    For more examples, please have a look at `JITypeInfoImpl.java (getFuncDesc API)`.
    It will show you how the mapping is done between embedded pointers and j-Interop types.
    Please keep MSDN handy for having a look at the actual C++ struct.
    Search on `ITypeInfo -->GetFuncDesc(...)` there. Also see the `FUNCDESC` structure.

1. Unless specified otherwise (like the dimensions are provided) in the documentation, all arrays are `conformant` arrays.
1. You can also look up the IDL for the component if only `size_is()` is present then Array `"isConformant" (true)`.
    If both `size_is()` and `length_is()` are present then Array `"isConformant" and "isVarying" (true,true)`.
1. When you are implementing callbacks, if the COM source interface has a single level interface pointer like `IDispatch*` then mapping to local java class will be `JIInterfacePointer` itself.
    But second level pointers to interfaces like `IDispatch**` must be declared as `JIVariants` only.

    For e.g.: `IDispatch*`can be mapped to `IJIDispatch`, but `IDispatch**` should be mapped to `JIVariant` only.
    see `MSInternetExplorer --> DWebBrowserEvents (BeforeNavigate2 and NewWindow3)` for more details.

#### (A11)

No, the library does all this on it's own (including pinging the COM server for keeping it alive).

#### (A12)

Yes, please make sure that the Server service and Remote Registry service is running on the target workstation (where the COM Server is hosted).
This is required for reading the registry to map the `ProgIds` to their `clsids`.
If you can't have this, then please use `clsid` instead of `progId`.
The `progIdVsClsidDB.properties` maintains a mapping of `progId` Vs there `clsids`, if this file is present in the _classpath_.
This file is consulted before the registry for the `progId`.

Also, if you are working with GUI components and would like to make them visible / interactive, then make sure that you read up [(A5)](#A5) and setup the COM Server for "Interactive User". By default, it is the "Launching User".
If this option is set, then the COM Server will not present it's GUI. It is best to use this for all silient operations like working with DBs, or using Excel formulas etc.

#### (A13)

Yes, j-Interop uses java logging by default (and to the console), but you can configure a handler for this logger to redirect output to logger mechanisms of your own choice.
There is also a method in `JISystem.setInBuiltLogHandler` which creates a handler to store the logs to a file in the user's temp directory as `j-Interop.log`. (e.g. for Windows systems it should be `C:\Documents and Settings\your_username\Local Settings\Temp`)

#### (A14)

Please see the license section.

## License

This library is distributed under the LGPL v3.0 (http://www.gnu.org/licenses/lgpl-3.0.html).
Please refer to the accompanying `lgpl.txt` for more details.

Those not speaking 'legal'ise can have a look [here](http://www.gnu.org/licenses/lgpl-java.html) as to what it means.
Incase you would like to get a LGPL v2.1 exception for your library, please write to us.

## Third Party Dependencies

j-Interop depends on :

- [jarapac](http://sourceforge.net/projects/jarapac) - DCE/RPC protocol
- [jcifs](http://jcifs.samba.org/) - SMB authentication
- [iwombat](http://www.iwombat.com) - Open source library for UUID generation
- [Gnu Crypto](http://www.gnu.org/software/gnu-crypto/index.html) - Cryptographic library from GNU

## Installation

Please extract all files from the `j-Interop.zip`. The `lib` folder has all the jars required to use j-Interop library.

Installation of `j-interop.jar` is similar to any standard jar file, just add it to the _classpath_.

As a performance improvement, the j-Interop comes with a `progIdVsClsidDB.properties` file, which gets updated with the clsid of each ProgId.
This prevents the library from accessing the Windows Registry for the clsid, when the same COM server is required again.
Please make sure that `progIdVsClsidDB.properties` is also included in the _classpath_.

## Examples

Commented examples are located in the `examples` folder.

## Third Party Licenses

- `jcifs` is licensed under [LGPL](http://www.opensource.org/licenses/lgpl-license.php).
- `jarapac` has forked out to jcifs implementation and follows the same licensing scheme.
- `iwombat` donated the pieces of code required by the library, Many Thanks to Bob Combs and [iwombat.com](http://www.iwombat.com).

## Support

If you have any issues, please post it at the support forum or file a bug in the bugs database maintained at project
website.

## Acknowledgements

This product includes software developed by the [iWombat.com](http://www.iWombat.com/) and all other members of the [Third Party Dependencies](#Third-Party-Dependencies).
