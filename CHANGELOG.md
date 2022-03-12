# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
- Replace custom `MD4` and `MD5` implementation by java integrated one.
- Replace iwombat `UUID` implementation by java integrated one.
- Update maven plugins dependencies.

## [3.3.0] - 2022-01-03
### Added
- Force minimal maven version to `3.6.0`.

### Changed
- Update `jcifs-ng` from version `2.1.6` to `2.1.7`.
- Update maven plugins dependencies.

## [3.2.0] - 2021-09-14
### Added
- Add multiples missing errors codes ([#3]).
- Preregister `WbemScripting.SWbemLocator` ClsidDB to avoid registry query when using WMI.

### Changed
- Update `jcifs-ng` from version `2.1.5` to `2.1.6`.
- Update maven plugins dependencies.

### Fixed
- Fix crash when password contains modulo (`%`) character and program ID is not registered ([#5]).

[#3]: https://github.com/skyghis/j-interop-ng/issues/3
[#5]: https://github.com/skyghis/j-interop-ng/issues/5

## [3.1.0] - 2021-03-05
### Added
- Recognize response codes `0xc0000008` and `0xc0000034`. Imported from Jeff Gehlbach work at [sourceforce patches#15](https://sourceforge.net/p/j-interop/patches/15/).

### Changed
- Remove new line prefix on logs.
- Update `jcifs-ng` from version `2.1.3` to `2.1.5`.
- Update maven plugins dependencies.

## [3.0.0] - 2019-11-15
### Added
- Add dependency to `jcifs-ng` version `2.1.3`.
- Add LICENSE, README and CHANGELOG.

### Changed
- Update minimal java version from 6 to 7.
- Auto-format code using Netbeans formatter.
- Update line ending to `lf` for all files.
- Update project structure to maven default.
- Update and tidy poms.

### Removed
- Remove dependency to `jcifs` version `1.2.19`.

### Fixed
- Socket connection timeout not used on connect.

[Unreleased]: https://github.com/skyghis/j-interop-ng/compare/3.3.0...HEAD
[3.3.0]: https://github.com/skyghis/j-interop-ng/compare/3.2.0...3.3.0
[3.2.0]: https://github.com/skyghis/j-interop-ng/compare/3.1.0...3.2.0
[3.1.0]: https://github.com/skyghis/j-interop-ng/compare/3.0.0...3.1.0
[3.0.0]: https://github.com/skyghis/j-interop-ng/releases/tag/3.0.0
