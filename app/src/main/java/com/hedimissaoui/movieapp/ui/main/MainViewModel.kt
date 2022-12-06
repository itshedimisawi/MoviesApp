package com.hedimissaoui.movieapp.ui.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hedimissaoui.movieapp.network.entities.movie_list.Movie
import com.hedimissaoui.movieapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var currentPage = 1
    val RESULTS_PER_PAGE = 20
    var scrollPosition = 0
    var isLoadingNextPage by mutableStateOf(false)

    var movies = mutableStateListOf<Movie>()
    var errorLoading by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    fun reloadMovies(){
        currentPage = 1
        movies.clear()
        isLoading = true
        isLoadingNextPage = false
        getAllMovies()
    }
    fun getAllMovies() {

        Log.i("cooltag", "getting movies $currentPage")
        errorLoading = false

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.i("cooltag", "Error getting movies")
            errorLoading = true
            isLoading = false
            isLoadingNextPage = false
        }

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            delay(300)
            val result = repository.getAllMovies(page = currentPage)
            if (result.isSuccessful) {
                result.body()?.let {
                    it.results?.let { fetchedMovies ->
                        movies.addAll(fetchedMovies)
                        Log.i("cooltag", "got movies ${movies.size}")
                    }
                }
            } else {
                errorLoading = true
            }
            isLoading = false
            isLoadingNextPage = false
        }
    }

    fun getNextPage() {
        Log.i("cooltag", "getNextPage $currentPage")
        if ((scrollPosition + 1) >= (currentPage * RESULTS_PER_PAGE)) {
            currentPage += 1
            Log.i("cooltag", "incrememnt $currentPage")
            if (currentPage > 1) {
                isLoadingNextPage = true
                getAllMovies()
            }
        }
    }

    var inputDateFormatter = SimpleDateFormat("yyyy-MM-dd")
    var outputDateFormatter = SimpleDateFormat("yyyy")

    fun dateToYear(date: String?): String? {
        date?.let {
            try {
                inputDateFormatter.parse(date)?.let {
                    return outputDateFormatter.format(it)
                }
            } catch (e: Exception) {
                return date
            }
        }
        return null
    }

}