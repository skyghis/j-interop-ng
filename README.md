# J-Interop-ng

Implementation of DCOM wire protocol (MSRPC) to enable development of Pure Bi-Directional, Non-Native Java applications which can interoperate with any COM component.
The implementation is itself purely in Java and does **not** use JNI to provide COM access.

This is a cleaned-up and improved version of the j-Interop library.
Based on [j-interop]([https://sourceforge.net/projects/j-interop/) and [kohsuke](https://github.com/kohsuke/j-interop) work, and modified to use [jcifs-ng](https://github.com/AgNO3/jcifs-ng).

## Notes

The goal is to support `SMB2` authentication method and new version of Windows.

I only use this library to perform `WMI` call. Best effort is done to keep other functionalities alive.

Old _deprecated_ documentation have been transformed to Markdown and exist at [`j-interop/README.md`](j-interop/README.md)

## Requirements and dependencies

- Java Standard Edition Runtime Environment (JRE) version 7 or up.
- External dependency to [jcifs-ng](https://github.com/AgNO3/jcifs-ng).
- All others dependencies are compiled and linked from project [`j-interopdeps-ng`](j-interopdeps)

## Quick start

**TODO**

<!--
Add this dependency to your project's POM (or equivalent for non-Maven users):

```xml
<dependency>
    <groupId>com.github.skyghis</groupId>
    <artifactId>j-interop-ng</artifactId>
    <version>{version}</version>
</dependency>
```

**TODO**: Simple use case.

-->

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[GNU LGPLv3](https://choosealicense.com/licenses/lgpl-3.0/)
