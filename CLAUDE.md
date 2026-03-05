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

- **Presentation**: Jetpack Compose screens + `@HiltViewModel` ViewModels. UI state is exposed via `StateFlow` — non-paginated screens use `data class` state with `isLoading/error` fields; paginated screens collect `PagingData` directly via `collectAsLazyPagingItems()`.
- **Domain**: Use cases (callable via `operator fun invoke()`), repository interfaces, and domain models. `Result<T>` sealed class and `safeCall {}` (in `domain/util/Result.kt`) wrap suspend API calls. Use cases are always the only entry point from ViewModels into the domain.
- **Data**: Retrofit implementation of `MovieRepository`, DTOs mapped to domain models via extension functions in `MovieMapper.kt`, and two `PagingSource` classes.

### Navigation

Single flat nav graph in `TMDBNavGraph`. All routes are `data object`s in the `Screen` sealed class. **Important naming rule**: sealed class objects use a `Nav` suffix (e.g., `Screen.HomeNav`, `Screen.MovieListingNav`) to avoid shadowing composable function imports of the same name.

```
HomeScreen (start) ──► MovieListingScreen ──► MovieDetailsScreen ──► FullCastCrewScreen
     │                                                │
     └── (View All) ─────────────────────            └──────────────► MovieReviewsScreen
```

Routes:
- `home` → `HomeScreen`
- `search` → `SearchScreen` (placeholder)
- `profile` → `ProfileScreen` (placeholder)
- `movies?listType={listType}` → `MovieListingScreen` — `listType` resolved from `SavedStateHandle` via `MovieListType.fromRouteKey()`
- `movie/{movieId}` → `MovieDetailsScreen`
- `movie/{movieId}/cast?movieTitle={movieTitle}` → `FullCastCrewScreen`
- `movie/{movieId}/reviews?movieTitle={movieTitle}` → `MovieReviewsScreen`

Movie title is passed as a query param (not path segment) to avoid URL-encoding issues with special characters.

### Bottom Navigation

`MainActivity` holds a single `Scaffold` with a `NavigationBar` (Home / Search / Profile). The bar is shown only when `currentRoute in setOf("home", "search", "profile")`. The outer `Scaffold` uses `contentWindowInsets = WindowInsets(0.dp, ...)` to avoid double-applying system insets on top of what inner Scaffolds handle themselves. Top-level screens (Home, Search, Profile) mirror this with their own `contentWindowInsets = WindowInsets(0.dp, ...)` so the system nav bar inset is counted only once (via the `NavigationBar`'s own insets). Detail screens keep default Scaffold inset handling since the bottom bar is hidden on those routes.

### Pagination

`Paging 3` with `PagingConfig(pageSize = 20, prefetchDistance = 5)`. Two paging sources:
- `MovieListPagingSource` — used by `MovieListingScreen`. Routes to `discover/movie` when `MovieFilterState.needsDiscoverApi` is true (any filter or non-default sort is active), otherwise calls the natural endpoint (`movie/popular`, etc.).
- `MovieReviewsPagingSource` — used by `MovieReviewsScreen`.

`PagingData` flows are cached with `.cachedIn(viewModelScope)`. `MovieListingViewModel` uses `flatMapLatest` on `filterState` so the paging stream automatically restarts when filters change.

### HomeScreen Carousel Architecture

`HomeViewModel` fires 3 independent `viewModelScope.launch` coroutines in `loadAll()` to load Popular, Now Playing, and Upcoming carousels in parallel. Each updates its own slice of `HomeUiState` via `copy()`. `GetMoviePreviewUseCase` fetches only page 1 via `repository.getMoviesPreview(type)`.

### Dependency Injection

All dependencies wired in `AppModule.kt` (`@InstallIn(SingletonComponent::class)`):
`OkHttpClient` → `Retrofit` → `TMDBApiService` → `MovieRepositoryImpl` (bound to `MovieRepository` interface).

### Image URLs

Constructed in `MovieMapper.kt`:
```kotlin
"https://image.tmdb.org/t/p/" + size + path
// Sizes: w185 (profile), w500 (poster), w780 (backdrop)
```

### API Endpoints

- `GET movie/popular|now_playing|top_rated|upcoming` → `MovieListPagedResponse`
- `GET discover/movie?sort_by&with_genres&vote_average.gte&primary_release_year` → `MovieListPagedResponse`
- `GET movie/{id}` → `MovieDetailsResponse`
- `GET movie/{id}/credits` → `MovieCreditsResponse`
- `GET movie/{id}/reviews` → `MovieReviewsPagedResponse`

## Key Versions

- Kotlin: 2.3.10 | AGP: 9.0.0 | Compose BOM: 2026.02.00
- Min SDK: 24 | Target SDK: 36 | Java: 17
- Hilt: 2.59.1 | Retrofit: 3.0.0 | Paging: 3.4.1 | Coil: 2.7.0
