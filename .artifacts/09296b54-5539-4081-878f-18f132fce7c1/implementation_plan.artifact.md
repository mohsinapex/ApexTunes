# Fix Unresolved reference 'BuildConfig' and 'R'

The compilation error `Unresolved reference 'BuildConfig'` (and `R`) occurs because the `namespace` defined in `app/build.gradle.kts` is `com.mohsinraza.mohsinapexmusic`, while the source code expects `BuildConfig` and `R` to be in the `com.mohsinraza.mohsinapexmusic.music` package.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/ahsan/Downloads/Metrolist-main/Metrolist-main/app/build.gradle.kts)
- Update `namespace` from `com.mohsinraza.mohsinapexmusic` to `com.mohsinraza.mohsinapexmusic.music`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileFossDebugKotlin` to verify that the unresolved references are fixed.
- Run `./gradlew :app:assembleFossDebug` as per the project rules to ensure a full successful build.
