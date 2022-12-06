package com.hedimissaoui.movieapp.repository

import com.hedimissaoui.movieapp.network.entities.movie_list.Movie
import com.hedimissaoui.movieapp.network.entities.movie_list.MoviesResponse
import retrofit2.Response

interface Repository {

    suspend fun getAllMovies(page: Int): Response<MoviesResponse>

    suspend fun getMovie(movieId: Int): Response<Movie>
}