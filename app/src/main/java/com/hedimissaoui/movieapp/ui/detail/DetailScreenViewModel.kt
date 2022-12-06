package com.hedimissaoui.movieapp.ui.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hedimissaoui.movieapp.network.entities.movie_list.Movie
import com.hedimissaoui.movieapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    var movie by mutableStateOf<Movie?>(null)
    var errorLoading by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    fun getMovieDetails(id:Int){
        errorLoading = false

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.i("cooltag", "Error getting movie details ${throwable.message}")
            errorLoading = true
            isLoading = false
        }

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isLoading = true
            val result = repository.getMovie(movieId = id)
            if (result.isSuccessful){
                result.body()?.let {
                    movie = it
                }
            }else{
                errorLoading = true
            }
            isLoading = false
        }
    }

}