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
- **Data**: Five repository implementations (`Movie`, `Tv`, `Search`, `Auth`, `Account`), DTOs mapped to domain models via extension functions in `*Mapper.kt` files, and five `PagingSource` classes. `SessionManager` (in `data/local/`) persists auth state in `SharedPreferences`.

### Navigation

Single flat nav graph in `TMDBNavGraph`. All routes are `data object`s in the `Screen` sealed class. **Important naming rule**: sealed class objects use a `Nav` suffix (e.g., `Screen.HomeNav`, `Screen.TvListingNav`) to avoid shadowing composable function imports of the same name.

Movie/TV title is passed as a query param (not path segment) to avoid URL-encoding issues with special characters.

Routes:
- `home` → `HomeScreen` (start destination)
- `search` → `SearchScreen`
- `profile` → `ProfileScreen`
- `login` → `LoginScreen`
- `movies?listType={listType}` → `MovieListingScreen`
- `movie/{movieId}` → `MovieDetailsScreen`
- `movie/{movieId}/cast?movieTitle={movieTitle}` → `FullCastCrewScreen`
- `movie/{movieId}/reviews?movieTitle={movieTitle}` → `MovieReviewsScreen`
- `tv?listType={listType}` → `TvListingScreen`
- `tv/{tvId}` → `TvDetailsScreen`
- `tv/{tvId}/cast?tvName={tvName}` → `TvFullCastCrewScreen`
- `person/{personId}` → `PersonDetailsScreen`
- `collection/{collectionId}?collectionName={collectionName}` → `CollectionDetailsScreen`
- `movies/keyword/{keywordId}?keywordName={keywordName}` → reuses `MovieListingScreen` (keyword mode)
- `tv/keyword/{keywordId}?keywordName={keywordName}` → reuses `TvListingScreen` (keyword mode)
- `movies/genre/{genreId}?genreName={genreName}` → reuses `MovieListingScreen` (genre mode)
- `tv/genre/{genreId}?genreName={genreName}` → reuses `TvListingScreen` (genre mode)

### Bottom Navigation

`MainActivity` holds a single `Scaffold` with a `NavigationBar` (Home / Search / Profile). The bar is shown only when `currentRoute in setOf("home", "search", "profile")`. The outer `Scaffold` uses `contentWindowInsets = WindowInsets(0.dp, ...)` to avoid double-applying system insets. Top-level screens (Home, Profile) use `contentWindowInsets = WindowInsets(0.dp, ...)` in their own Scaffold so the nav bar inset is counted only once. **Exception**: `SearchScreen` has no `TopAppBar`, so it applies `.statusBarsPadding()` directly on its root `Column` instead. Detail screens keep default Scaffold inset handling since the bottom bar is hidden on those routes.

### Pagination & Filter/Sort

`Paging 3` with `PagingConfig(pageSize = 20, prefetchDistance = 5)`. Five paging sources:
- `MovieListPagingSource` — routes to `discover/movie` when `MovieFilterState.needsDiscoverApi` is true (any filter/non-default sort), otherwise calls the natural endpoint.
- `TvListPagingSource` — same pattern with `TvFilterState` routing to `discover/tv`; also has `TvListType.defaultSortApiValue()` for fallback sort.
- `MovieReviewsPagingSource` — for `MovieReviewsScreen`.
- `SearchPagingSource` — calls `search/multi`, maps results with `mapNotNull { it.toSearchResult() }`.
- `AccountMoviesPagingSource` — parameterised by `AccountMovieListType` (FAVORITES / WATCHLIST).

`PagingData` flows are cached with `.cachedIn(viewModelScope)`. Both `MovieListingViewModel` and `TvListingViewModel` use `flatMapLatest` on their respective `filterState` so the paging stream auto-restarts on filter changes.

**Filter state models** are in `domain/model/MovieListConfig.kt`:
- `MovieFilterState` — `sortBy`, `selectedGenreIds`, `minRating`, `releaseYear`, `withKeywordId`
- `TvFilterState` — same shape but `firstAirYear` instead of `releaseYear`; uses `TvGenreItem.ALL_GENRES`
- Both have a `needsDiscoverApi` computed property that controls discover vs natural endpoint routing

**Listing screen modes** — both `MovieListingViewModel` and `TvListingViewModel` support three modes derived from `SavedStateHandle`:
- Normal: standard listing with full filter/sort UI
- `isKeywordMode` (`keywordId != null`): filter state pre-seeded with `withKeywordId`; filter/sort controls hidden
- `isGenreMode` (`genreId != null`): filter state pre-seeded with `selectedGenreIds = setOf(genreId)`; filter/sort controls hidden

### HomeScreen Carousel Architecture

`HomeViewModel` fires 6 independent `viewModelScope.launch` coroutines in `loadAll()` to load 3 movie and 3 TV carousels in parallel. Each updates its own slice of `HomeUiState` via `update { it.copy(...) }`. The home screen interleaves movie and TV carousels: Popular Movies → Popular TV → Now Playing → On The Air → Upcoming → Top Rated TV.

`MovieCategoryState(movies, isLoading, error)` and `TvCategoryState(shows, isLoading, error)` are the per-carousel state holders. `PosterCard` (120×180dp) is the shared composable for both movie and TV posters.

### Search

`SearchViewModel` exposes `results` via:
```kotlin
_query.debounce(300L).flatMapLatest { q ->
    if (q.isBlank()) flow { emit(PagingData.empty()) }
    else searchMultiUseCase(q)
}.cachedIn(viewModelScope)
```
Blank query skips the API and shows an empty state. Results are `sealed class SearchResult` — `MovieResult`, `TvResult`, or `PersonResult`.

### Auth & Session

**`SessionManager`** (`data/local/`) is a `@Singleton` SharedPreferences wrapper storing `sessionId` (String?) and `accountId` (Int). `isLoggedIn` is derived from `sessionId != null`.

**Login flow** (inside `AuthRepositoryImpl.login()`):
1. `GET authentication/token/new` → request token
2. `POST authentication/token/validate_with_login` (username, password, token) → validated token
3. `POST authentication/session/new` (token) → session ID → stored in `SessionManager`
4. `GET account?session_id=…` → account ID → stored in `SessionManager`

**ProfileViewModel** initialises to `LoggedOut` if no session exists. After login, `ProfileScreen` uses `LifecycleResumeEffect` to call `loadAccount()` when the screen resumes and the session is active but UI state is still `LoggedOut` — this bridges the gap since the VM is retained across back-stack navigation.

### Shared UI Components

- **`SectionTitle`** (`presentation/moviedetails/MovieDetailsScreen.kt`) — public composable with a 4dp primary-colored left accent bar + bold `titleMedium` text. Imported by `TvDetailsScreen` and `ExternalIdsSection` — do not duplicate.
- **`MovieFilterSortBottomSheet`** (`presentation/movielisting/`) — used by `MovieListingScreen`. TV equivalent is `TvFilterSortBottomSheet` (`presentation/tvlisting/`). Both open full-screen (`skipPartiallyExpanded = true`).
- **`KeywordsSection`** (`MovieDetailsScreen.kt`) — public composable reused in `TvDetailsScreen` via import.
- **`CastSection`, `VideosSection`, `ImagesSection`, `InfoRow`, `ReviewCard`** — all defined in `MovieDetailsScreen.kt` and imported into `TvDetailsScreen`.
- **`ExternalIdsSection`** — in `presentation/components/`, shared between movie and TV detail screens.
- Shimmer composables (`CastCarouselShimmer`, `MediaListShimmer`, `DetailsMainShimmer`, `ReviewCardShimmer`) live in `presentation/util/`.

### Dependency Injection

All dependencies wired in `AppModule.kt` (`@InstallIn(SingletonComponent::class)`):
`OkHttpClient` → `Retrofit` → `TMDBApiService` → five repository impls bound to their interfaces. `SessionManager` is provided directly (constructor injection via Hilt).

### Image URLs

Constructed in `MovieMapper.kt` (same pattern reused in `TvMapper.kt`, `AccountMapper.kt`):
```kotlin
"https://image.tmdb.org/t/p/" + size + path
// Sizes: w185 (profile/avatar), w500 (poster), w780 (backdrop)
```

### API Endpoints

**Movies**
- `GET movie/popular|now_playing|top_rated|upcoming` → `MovieListPagedResponse`
- `GET discover/movie?sort_by&with_genres&vote_average.gte&primary_release_year&with_keywords` → `MovieListPagedResponse`
- `GET movie/{id}` → `MovieDetailsResponse`
- `GET movie/{id}/credits` → `MovieCreditsResponse`
- `GET movie/{id}/reviews` → `MovieReviewsPagedResponse`
- `GET movie/{id}/keywords` → `MovieKeywordsResponse`
- `GET movie/{id}/external_ids` → `ExternalIdsResponse`
- `GET movie/{id}/images` → `MovieImagesResponse`
- `GET movie/{id}/videos` → `MovieVideosResponse`
- `GET movie/{id}/recommendations` → `MovieListPagedResponse`
- `GET collection/{id}` → `CollectionDetailsResponse`

**TV**
- `GET tv/popular|top_rated|on_the_air|airing_today` → `TvListPagedResponse`
- `GET discover/tv?sort_by&with_genres&vote_average.gte&first_air_date_year&with_keywords` → `TvListPagedResponse`
- `GET tv/{id}` → `TvDetailsResponse`
- `GET tv/{id}/credits` → `TvCreditsResponse`
- `GET tv/{id}/keywords` → `TvKeywordsResponse` (field is `results`, not `keywords`)
- `GET tv/{id}/external_ids` → `ExternalIdsResponse`
- `GET tv/{id}/images` → `MovieImagesResponse`
- `GET tv/{id}/videos` → `MovieVideosResponse`
- `GET tv/{id}/recommendations` → `TvListPagedResponse`
- `GET tv/{id}/season/{season_number}` → `SeasonDetailsResponse`

**Trending / Person**
- `GET trending/all/{time_window}` → `TrendingResponseDto`
- `GET person/popular` → `PopularPersonListResponse`
- `GET person/{id}` → `PersonDetailsResponse`
- `GET person/{id}/combined_credits` → `PersonCombinedCreditsResponse`

**Search**
- `GET search/multi?query&page` → `SearchResultPagedResponse`

**Auth**
- `GET authentication/token/new` → `RequestTokenResponse`
- `POST authentication/token/validate_with_login` → `RequestTokenResponse`
- `POST authentication/session/new` → `SessionResponse`
- `DELETE authentication/session` → (void)

**Account**
- `GET account?session_id` → `AccountResponse`
- `GET account/{id}/favorite/movies?session_id&page` → `MovieListPagedResponse`
- `GET account/{id}/watchlist/movies?session_id&page` → `MovieListPagedResponse`

## Key Versions

- Kotlin: 2.3.10 | AGP: 9.0.0 | Compose BOM: 2026.02.00
- Min SDK: 24 | Target SDK: 36 | Java: 17
- Hilt: 2.59.1 | Retrofit: 3.0.0 | Paging: 3.4.1 | Coil: 2.7.0
