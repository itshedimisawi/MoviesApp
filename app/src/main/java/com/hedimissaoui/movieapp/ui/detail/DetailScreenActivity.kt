package com.hedimissaoui.movieapp.ui.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.hedimissaoui.movieapp.ui.theme.MoviesAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[DetailScreenViewModel::class.java]
        var movieId:Int? = null
        if (intent.hasExtra("movie_id")){
            movieId = intent.getIntExtra("movie_id",0)
        }
        setContent {
            MoviesAppTheme {
                LaunchedEffect(true){
                    movieId?.let {
                        viewModel.getMovieDetails(it)
                    }
                }
                Scaffold(topBar = {
                    TopAppBar(title = {
                        Text(
                            text = when (viewModel.movie != null && viewModel.movie?.title != null) {
                                true -> viewModel.movie?.title.toString().trim()
                                else -> "Movie details"
                            }, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }, navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.Default.Close, null)
                        }
                    })
                }) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        } else {
                            if (viewModel.errorLoading) {
                                Text(
                                    "Error getting movie details",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(horizontal = 16.dp),
                                )
                            } else {
                                viewModel.movie?.let { movie ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Image(
                                            painter = rememberImagePainter(data =
                                            when (movie.backdropPath.isNullOrBlank()){
                                                true -> "https://via.placeholder.com/500x300?text=Image+not+available"
                                                    false -> "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
                                            }),
                                            contentScale = ContentScale.FillWidth,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            movie.title?.let {
                                                Text(
                                                    text = it,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(top = 8.dp),
                                                    style = MaterialTheme.typography.h4
                                                )
                                            }
                                            movie.releaseDate?.let {
                                                Text(
                                                    text = it,
                                                    color = MaterialTheme.colors.onBackground.copy(0.6f),
                                                    modifier = Modifier.padding(top = 8.dp)
                                                )
                                            }

                                            movie.overview?.let {
                                                Text(
                                                    text = it,
                                                    modifier = Modifier.padding(top = 12.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}