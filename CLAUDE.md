# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Build and install on connected device
./gradlew test                   # Run unit tests
./gradlew testDebugUnitTest      # Run debug unit tests only
./gradlew connectedAndroidTest   # Run instrumentation tests (requires device)
./gradlew lint                   # Run lint checks
./gradlew lintFix                # Auto-fix lint issues
```

## API Token Setup

The TMDB API token must be added to `local.properties` (git-ignored):
```
TMDB_API_TOKEN=your_token_here
```
It is injected at build time via `BuildConfig.TMDB_API_TOKEN` and added to all requests by `AuthInterceptor`.

## Architecture

Clean Architecture with MVVM, organized in three layers:

**Presentation** (`presentation/`) → **Domain** (`domain/`) ← **Data** (`data/`)

- **Presentation**: Jetpack Compose screens + `@HiltViewModel` ViewModels. UI state is modeled as sealed classes (`Loading`, `Success`, `Error`) exposed via `StateFlow`.
- **Domain**: Use cases (callable via `operator fun invoke()`), repository interfaces, and domain models. Contains `Result<T>` sealed class and `safeCall {}` utility for wrapping exceptions.
- **Data**: Retrofit implementation of repositories, DTOs mapped to domain models via extension functions in `MovieMapper.kt`, and Paging 3 sources.

### Navigation

Navigation Compose with string-based routes defined as sealed class objects in `Screen`:
```
PopularMoviesScreen → MovieDetailsScreen → FullCastCrewScreen → MovieReviewsScreen
```
`SavedStateHandle` is used in ViewModels to retrieve navigation arguments.

### Pagination

`Paging 3` with `PagingConfig(pageSize = 20, prefetchDistance = 5)`. `PagingData` flows are cached with `.cachedIn(viewModelScope)`. Two paging sources exist: `PopularMoviesPagingSource` and `MovieReviewsPagingSource`.

### Dependency Injection

All dependencies are wired in `AppModule.kt` (`@InstallIn(SingletonComponent::class)`). The graph provides: `OkHttpClient` → `Retrofit` → `TMDBApiService` → `MovieRepositoryImpl` (bound to `MovieRepository` interface).

### Image URLs

Profile/poster/backdrop image URLs are constructed in the mapper layer:
```kotlin
"https://image.tmdb.org/t/p/" + size + path
// Sizes: w185 (profile), w500 (poster), w780 (backdrop)
```

## Key Versions

- Kotlin: 2.3.10 | AGP: 9.0.0 | Compose BOM: 2026.02.00
- Min SDK: 24 | Target SDK: 36 | Java: 17
- Hilt: 2.59.1 | Retrofit: 3.0.0 | Paging: 3.4.1 | Coil: 2.7.0
