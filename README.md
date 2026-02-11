# 🎬 TMDB Android App  
### Jetpack Compose + Clean Architecture
A modern Android application that displays **popular movies** and **movie details** using the **TMDB API**.

Built with **Jetpack Compose**, **Clean Architecture**, **MVVM**, **Hilt**, **Paging 3**, and **Room**.


## 🧱 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose (Material 3)  
- **Architecture:** Clean Architecture + MVVM  
- **Dependency Injection:** Hilt  
- **Networking:** Retrofit + OkHttp  
- **Asynchronous:** Kotlin Coroutines + Flow  
- **Pagination:** Paging 3  
- **Local Cache:** Room  
- **Navigation:** Navigation Compose  
- **Image Loading:** Coil  


## 📐 Architecture Overview

This project follows **Android’s recommended app architecture** with a strict separation of concerns.

### 🏗 Layers

#### 🖥 UI Layer
- Jetpack Compose Screens  
- ViewModels  

#### 🧠 Domain Layer
- Business Logic  
- UseCases  
- Domain Models  
- Repository Interfaces  

#### 💾 Data Layer
- Retrofit API Services  
- Room Database  
- Repository Implementations  

> 🗂 **Note:** Room acts as the **Single Source of Truth (SSOT)** in this application.


#### 🚀 Features

- Browse Popular Movies  
- View Movie Details  
- Offline Caching  
- Pagination Support  
- Modern Material 3 UI  
- Clean & Scalable Architecture  

---

## 🏗 High-Level Architecture Diagram  
#### (Clean Architecture + MVVM)

```mermaid
flowchart TD
    UI["Jetpack Compose UI"]
    VM["ViewModel"]
    UC["Use Cases"]
    REPO["Repository Interface"]
    REPO_IMPL["Repository Implementation"]
    REMOTE["Remote Data Source (Retrofit)"]
    LOCAL["Local Data Source (Room)"]
    API["TMDB API"]
    DB[("Room Database")]

    UI -->|collects StateFlow| VM
    VM -->|calls| UC
    UC -->|depends on| REPO
    REPO_IMPL -.->|implements| REPO
    REPO_IMPL --> REMOTE
    REPO_IMPL --> LOCAL
    REMOTE --> API
    LOCAL --> DB
```

---

## 🔄 Detailed Data Flow
#### 📃 Popular Movies List

```mermaid
sequenceDiagram
    participant UI as PopularMoviesScreen
    participant VM as PopularMoviesViewModel
    participant UC as GetPopularMoviesUseCase
    participant Repo as MovieRepository
    participant Local as Room
    participant Remote as TMDB API

    UI->>VM: collect UiState
    VM->>UC: execute()
    UC->>Repo: getPopularMovies()

    Repo->>Local: query cached movies
    Local-->>Repo: PagingSource

    Repo->>Remote: fetch popular movies
    Remote-->>Repo: API response
    Repo->>Local: save movies

    Repo-->>VM: Flow<PagingData<Movie>>
    VM-->>UI: UiState.Success
```

#### 🎞 Movie Details Screen

```mermaid
sequenceDiagram
    participant UI as MovieDetailScreen
    participant VM as MovieDetailViewModel
    participant UC as GetMovieDetailUseCase
    participant Repo as MovieRepository
    participant Local as Room
    participant Remote as TMDB API

    UI->>VM: load(movieId)
    VM->>UC: execute(movieId)
    UC->>Repo: getMovieDetail(movieId)

    Repo->>Local: get cached detail
    alt Cached exists
        Local-->>Repo: MovieDetail
        Repo-->>VM: cached data
    end

    Repo->>Remote: fetch movie detail
    Remote-->>Repo: API response
    Repo->>Local: save detail
    Repo-->>VM: updated data

    VM-->>UI: UiState.Success
```

---

### 📦 Pagination + Caching Flow  
#### (Paging 3 + Room + RemoteMediator)

```mermaid
flowchart LR
    UI["LazyColumn (Paging Compose)"]
    Pager["Pager"]
    Mediator["RemoteMediator"]
    DAO["Room DAO"]
    API["TMDB API"]

    UI --> Pager
    Pager --> DAO
    Pager --> Mediator
    Mediator --> API
    API --> Mediator
    Mediator --> DAO
```

#### ✅ Why this matters

- Room is always the **Single Source of Truth**
- Network only updates the database
- Offline support works automatically
- Paging is seamless and scalable

---

### 🧭 Navigation Flow

```mermaid
flowchart TD
    Popular["Popular Movies Screen"]
    Detail["Movie Detail Screen"]

    Popular -->|onMovieClick movieId| Detail
    Detail -->|Back| Popular
```

---

### 📸 Screenshots

_Yet to add screenshots here_

---


### 📄 License

This project is for educational purposes.

