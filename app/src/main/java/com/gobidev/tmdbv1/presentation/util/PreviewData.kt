package com.gobidev.tmdbv1.presentation.util

import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.model.Genre
import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieBelongsToCollection
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.MovieImage
import com.gobidev.tmdbv1.domain.model.MovieVideo
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.model.Season
import com.gobidev.tmdbv1.domain.model.MovieCollectionDetails
import com.gobidev.tmdbv1.domain.model.Person
import com.gobidev.tmdbv1.domain.model.PersonCastCredit
import com.gobidev.tmdbv1.domain.model.PersonDetails
import com.gobidev.tmdbv1.domain.model.SearchResult
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.model.UserAccount

/**
 * Sample data for Compose previews.
 * Provides realistic mock data for testing UI components.
 */
object PreviewData {

    val sampleGenres = listOf(
        Genre(id = 28, name = "Action"),
        Genre(id = 12, name = "Adventure"),
        Genre(id = 878, name = "Science Fiction")
    )

    val sampleMovie = Movie(
        id = 550,
        title = "Fight Club",
        overview = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy.",
        posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
        releaseDate = "1999-10-15",
        rating = 8.4,
        voteCount = 26280
    )

    val sampleMovieDetails = MovieDetails(
        id = 550,
        title = "Fight Club",
        overview = "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
        posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
        releaseDate = "1999-10-15",
        rating = 8.4,
        voteCount = 26280,
        runtime = 139,
        genres = sampleGenres,
        tagline = "Mischief. Mayhem. Soap.",
        status = "Released",
        belongsToCollection = MovieBelongsToCollection(
            id = 9485,
            name = "The Fast & Furious Collection",
            posterUrl = "https://image.tmdb.org/t/p/w500/uv63yAGg1zETas7AFf1QHEJ8Zy.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w780/gLiuFAosSIS7JjW8tdFnmDILOsS.jpg"
        )
    )

    val sampleKeywords = listOf(
        Keyword(id = 825, name = "support group"),
        Keyword(id = 851, name = "dual identity"),
        Keyword(id = 4565, name = "consumerism"),
        Keyword(id = 158234, name = "based on novel"),
        Keyword(id = 10929, name = "nihilism"),
        Keyword(id = 1299, name = "underground")
    )

    val sampleVideos = listOf(
        MovieVideo(
            id = "533ec654c3a36854480003eb",
            key = "SUXWAEX2jlg",
            name = "Fight Club - Theatrical Trailer",
            type = "Trailer",
            site = "YouTube"
        ),
        MovieVideo(
            id = "533ec654c3a36854480003ec",
            key = "BdATeHQJEBQ",
            name = "Fight Club - Official Clip",
            type = "Clip",
            site = "YouTube"
        )
    )

    val sampleImages = listOf(
        MovieImage(
            url = "https://image.tmdb.org/t/p/w780/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
            aspectRatio = 1.778,
            width = 1280,
            height = 720
        ),
        MovieImage(
            url = "https://image.tmdb.org/t/p/w780/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg",
            aspectRatio = 1.778,
            width = 1280,
            height = 720
        )
    )

    val samplePosters = listOf(
        MovieImage(
            url = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            aspectRatio = 0.667,
            width = 500,
            height = 750
        )
    )

    val sampleExternalIds = ExternalIds(
        imdbId = "tt0137523",
        wikidataId = "Q190340",
        facebookId = "FightClubMovie",
        instagramId = "fightclubmovie",
        twitterId = "FightClub"
    )

    val sampleCastMembers = listOf(
        CastMember(
            id = 819,
            name = "Edward Norton",
            character = "The Narrator",
            profileUrl = "https://image.tmdb.org/t/p/w185/5XBzD5WuTyVQZeS4VI25z2moMeY.jpg",
            order = 0
        ),
        CastMember(
            id = 287,
            name = "Brad Pitt",
            character = "Tyler Durden",
            profileUrl = "https://image.tmdb.org/t/p/w185/kU3B75TyRiCgE270EyZnHjfivoq.jpg",
            order = 1
        ),
        CastMember(
            id = 1283,
            name = "Helena Bonham Carter",
            character = "Marla Singer",
            profileUrl = "https://image.tmdb.org/t/p/w185/58oJPFG1wefMC0Vj7sFzHPrm67J.jpg",
            order = 2
        ),
        CastMember(
            id = 7470,
            name = "Meat Loaf",
            character = "Robert 'Bob' Paulson",
            profileUrl = null,
            order = 3
        ),
        CastMember(
            id = 7499,
            name = "Jared Leto",
            character = "Angel Face",
            profileUrl = "https://image.tmdb.org/t/p/w185/msugySeTCyCmlRWtyB6sMixTQYY.jpg",
            order = 4
        )
    )

    val sampleCrewMembers = listOf(
        CrewMember(
            id = 7467,
            name = "David Fincher",
            job = "Director",
            department = "Directing",
            profileUrl = "https://image.tmdb.org/t/p/w185/tpEczFclQZeKAiCeKZZ0adRvtfz.jpg"
        ),
        CrewMember(
            id = 7468,
            name = "Jim Uhls",
            job = "Screenplay",
            department = "Writing",
            profileUrl = null
        ),
        CrewMember(
            id = 7469,
            name = "Chuck Palahniuk",
            job = "Novel",
            department = "Writing",
            profileUrl = "https://image.tmdb.org/t/p/w185/8nOJDJ6SqwV2h7PjdLBDTvIxXvx.jpg"
        ),
        CrewMember(
            id = 7471,
            name = "Ross Grayson Bell",
            job = "Producer",
            department = "Production",
            profileUrl = null
        ),
        CrewMember(
            id = 7472,
            name = "Ceán Chaffin",
            job = "Producer",
            department = "Production",
            profileUrl = null
        ),
        CrewMember(
            id = 1254,
            name = "Art Linson",
            job = "Producer",
            department = "Production",
            profileUrl = null
        )
    )

    val sampleMovies = listOf(
        Movie(
            id = 1,
            title = "The Shawshank Redemption",
            overview = "Imprisoned in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison...",
            posterUrl = "https://image.tmdb.org/t/p/w500/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w780/kXfqcdQKsToO0OUXHcrrNCHDBzO.jpg",
            releaseDate = "1994-09-23",
            rating = 8.7,
            voteCount = 24500
        ),
        Movie(
            id = 2,
            title = "The Godfather",
            overview = "Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family...",
            posterUrl = "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w780/tmU7GeKVybMWFButWEGl2M4GeiP.jpg",
            releaseDate = "1972-03-14",
            rating = 8.7,
            voteCount = 18000
        ),
        Movie(
            id = 3,
            title = "The Dark Knight",
            overview = "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent...",
            posterUrl = "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w780/nMKdUUepR0i5zn0y1T4CsSB5chy.jpg",
            releaseDate = "2008-07-18",
            rating = 8.5,
            voteCount = 30500
        )
    )

    val sampleReview = Review(
        id = "5b3e1ba1925141144c007f17",
        author = "Brett Pascoe",
        authorUsername = "SneekyNuts",
        authorAvatarUrl = "https://secure.gravatar.com/avatar/04d45e186650672a416315dac947b3d6.jpg",
        content = "In my top 5 of all time favourite movies. Great story line and a movie you can watch over and over again.",
        rating = 9.0,
        createdAt = "2018-07-05T13:22:41.754Z",
        updatedAt = "2021-06-23T15:58:10.199Z"
    )

    // ── TV sample data ────────────────────────────────────────────────────────

    private val tvGenres = listOf(
        Genre(id = 18, name = "Drama"),
        Genre(id = 80, name = "Crime"),
        Genre(id = 10765, name = "Sci-Fi & Fantasy")
    )

    val sampleSeasons = listOf(
        Season(id = 3572, name = "Season 1", overview = "Walter White is diagnosed with inoperable lung cancer and turns to manufacturing methamphetamine.", posterUrl = "https://image.tmdb.org/t/p/w500/1BP4xYv9ZG4ZVHkL7ocOziBbSYH.jpg", seasonNumber = 1, episodeCount = 7, airDate = "2008-01-20"),
        Season(id = 3573, name = "Season 2", overview = "Walter continues down his dark path, dealing with the consequences of his choices.", posterUrl = "https://image.tmdb.org/t/p/w500/e3oGYpoTUhOFK0BJfloru5ZmGV.jpg", seasonNumber = 2, episodeCount = 13, airDate = "2009-03-08"),
        Season(id = 3574, name = "Season 3", overview = "Walt and Jesse rebuild their meth operation and face new threats.", posterUrl = "https://image.tmdb.org/t/p/w500/ffpkQ3KzmTXBfm4GQKBQ0bKoJN.jpg", seasonNumber = 3, episodeCount = 13, airDate = "2010-03-21"),
        Season(id = 3575, name = "Season 4", overview = "Walt faces his most dangerous adversary yet — Gustavo Fring.", posterUrl = null, seasonNumber = 4, episodeCount = 13, airDate = "2011-07-17"),
        Season(id = 3576, name = "Season 5", overview = "The final season brings Walter White's story to its harrowing conclusion.", posterUrl = "https://image.tmdb.org/t/p/w500/r3z70vunihrAkjILQKWHX0G2xzO.jpg", seasonNumber = 5, episodeCount = 16, airDate = "2012-07-15")
    )

    val sampleTvShowDetails = TvShowDetails(
        id = 1396,
        name = "Breaking Bad",
        overview = "When Walter White, a New Mexico chemistry teacher, is diagnosed with Stage III cancer and given a prognosis of two years left to live, he decides to partner with a former student, Jesse Pinkman, to secure his family's financial future by manufacturing and distributing the highest quality methamphetamine possible.",
        posterUrl = "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
        firstAirDate = "2008-01-20",
        rating = 9.5,
        voteCount = 12820,
        numberOfSeasons = 5,
        numberOfEpisodes = 62,
        genres = tvGenres,
        tagline = "I am the one who knocks.",
        status = "Ended",
        seasons = sampleSeasons
    )

    val sampleEpisodes = listOf(
        Episode(id = 62085, name = "Pilot", overview = "Walter White learns he has terminal lung cancer and decides to manufacture methamphetamine to secure his family's future.", episodeNumber = 1, seasonNumber = 1, airDate = "2008-01-20", stillUrl = "https://image.tmdb.org/t/p/w780/ydlY3iPfeOAvu8gVqrxPoMvzNCn.jpg", rating = 7.9, runtime = 58),
        Episode(id = 62086, name = "Cat's in the Bag", overview = "Walt and Jesse try to dispose of their problem by dissolving the bodies in hydrofluoric acid.", episodeNumber = 2, seasonNumber = 1, airDate = "2008-01-27", stillUrl = null, rating = 7.7, runtime = 48),
        Episode(id = 62087, name = "...And the Bag's in the River", overview = "Walt must make a life-or-death decision about Krazy-8 while Jesse tries to handle the fallout.", episodeNumber = 3, seasonNumber = 1, airDate = "2008-02-10", stillUrl = null, rating = 8.2, runtime = 48),
        Episode(id = 62088, name = "Cancer Man", overview = "Walt reveals his diagnosis to his family while Jesse reconnects with his parents.", episodeNumber = 4, seasonNumber = 1, airDate = "2008-02-17", stillUrl = null, rating = 7.5, runtime = 48),
        Episode(id = 62089, name = "Gray Matter", overview = "Walt rejects help from his former business partners and pushes away his family.", episodeNumber = 5, seasonNumber = 1, airDate = "2008-02-24", stillUrl = null, rating = 7.7, runtime = 48)
    )

    val sampleTvShows = listOf(
        TvShow(id = 1399, name = "Game of Thrones", overview = "Seven noble families fight for control of the mythical land of Westeros.", posterUrl = "https://image.tmdb.org/t/p/w500/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg", backdropUrl = "https://image.tmdb.org/t/p/w780/2OMB0ynKlyIenMJWI2Dy9IWT4c.jpg", firstAirDate = "2011-04-17", rating = 9.3, voteCount = 22000),
        TvShow(id = 60735, name = "The Flash", overview = "After a particle accelerator causes a freak storm, CSI Investigator Barry Allen is struck by lightning.", posterUrl = "https://image.tmdb.org/t/p/w500/lJA2RCMfsWoskqlQhXPSLFQGXEJ.jpg", backdropUrl = null, firstAirDate = "2014-10-07", rating = 7.8, voteCount = 9800),
        TvShow(id = 57243, name = "Doctor Who", overview = "The Doctor is a Time Lord who travels through time and space in the TARDIS.", posterUrl = "https://image.tmdb.org/t/p/w500/4edFyasCrkH4MKs6H4mHqlrxA6b.jpg", backdropUrl = null, firstAirDate = "2005-03-26", rating = 8.4, voteCount = 7600)
    )

    val samplePerson = Person(
        id = 287,
        name = "Brad Pitt",
        profileUrl = "https://image.tmdb.org/t/p/w185/kU3B75TyRiCgE270EyZnHjfivoq.jpg",
        knownForDepartment = "Acting"
    )

    val samplePersonDetails = PersonDetails(
        id = 287,
        name = "Brad Pitt",
        biography = "William Bradley Pitt (born December 18, 1963) is an American actor and film producer. He has received multiple Golden Globe Awards and Academy Award nominations, winning an Academy Award for Best Supporting Actor for Once Upon a Time in Hollywood (2019). He is one of the most commercially successful actors in Hollywood.",
        birthday = "1963-12-18",
        deathday = null,
        placeOfBirth = "Shawnee, Oklahoma, USA",
        profileUrl = "https://image.tmdb.org/t/p/w185/kU3B75TyRiCgE270EyZnHjfivoq.jpg",
        knownForDepartment = "Acting",
        popularity = 42.5,
        gender = 2
    )

    val samplePersonCredits = listOf(
        PersonCastCredit(id = 550, title = "Fight Club", character = "Tyler Durden", mediaType = "movie", posterUrl = "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", releaseDate = "1999-10-15"),
        PersonCastCredit(id = 807, title = "Se7en", character = "Detective David Mills", mediaType = "movie", posterUrl = "https://image.tmdb.org/t/p/w500/6yoghtyTpznpBik8EngEmJskVUO.jpg", releaseDate = "1995-09-22"),
        PersonCastCredit(id = 4348, title = "Ocean's Eleven", character = "Rusty Ryan", mediaType = "movie", posterUrl = null, releaseDate = "2001-12-07"),
        PersonCastCredit(id = 361183, title = "Bullet Train", character = "Ladybug", mediaType = "movie", posterUrl = null, releaseDate = "2022-08-05")
    )

    val sampleUserAccount = UserAccount(
        id = 12345,
        username = "moviefan99",
        name = "Alex Johnson",
        avatarUrl = null
    )

    val sampleCollection = MovieCollectionDetails(
        id = 10,
        name = "Star Wars Collection",
        overview = "All nine Skywalker Saga films, from The Phantom Menace to The Rise of Skywalker.",
        posterUrl = "https://image.tmdb.org/t/p/w500/r8Ph5MYXL04Qzu4QBbq2KjqwtkQ.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/d8duYyyC9J5T825Hg7grmaabfxQ.jpg",
        parts = sampleMovies
    )

    val sampleSearchResults = listOf(
        SearchResult.MovieResult(movie = sampleMovie),
        SearchResult.TvResult(show = sampleTvShows.first()),
        SearchResult.PersonResult(person = samplePerson)
    )

    val sampleTvExternalIds = ExternalIds(
        imdbId = "tt0903747",
        wikidataId = "Q1079",
        facebookId = "BreakingBad",
        instagramId = "breakingbad",
        twitterId = "BreakingBad",
        tvdbId = "81189"
    )
}
