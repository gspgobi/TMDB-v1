package com.gobidev.tmdbv1.presentation.util

import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.domain.model.Genre
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieDetails

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
        status = "Released"
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
}
