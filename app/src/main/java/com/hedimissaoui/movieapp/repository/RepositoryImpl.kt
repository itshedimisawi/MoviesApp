package com.hedimissaoui.movieapp.repository

import com.hedimissaoui.movieapp.network.APIService
import com.hedimissaoui.movieapp.network.entities.movie_list.Movie
import com.hedimissaoui.movieapp.network.entities.movie_list.MoviesResponse
import retrofit2.Response

class RepositoryImpl (private val apiService: APIService): Repository{

    private val API_KEY = "c9856d0cb57c3f14bf75bdc6c063b8f3"

    override suspend fun getAllMovies(page: Int): Response<MoviesResponse> {
        return apiService.getAllMovies(apiKey = API_KEY, page= page)
    }

    override suspend fun getMovie(movieId: Int): Response<Movie> {
        return apiService.getMovie(apiKey = API_KEY, movieId = movieId)
    }


}