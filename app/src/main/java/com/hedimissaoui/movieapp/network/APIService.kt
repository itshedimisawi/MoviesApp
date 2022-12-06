package com.hedimissaoui.movieapp.network

import com.hedimissaoui.movieapp.network.entities.movie_list.Movie
import com.hedimissaoui.movieapp.network.entities.movie_list.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("/3/discover/movie")
    suspend fun getAllMovies(@Query("api_key") apiKey: String,
                          @Query("page") page: Int): Response<MoviesResponse>

    @GET("/3/movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,): Response<Movie>
}