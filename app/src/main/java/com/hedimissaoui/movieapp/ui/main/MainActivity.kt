package com.hedimissaoui.movieapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hedimissaoui.movieapp.network.entities.movie_list.Movie
import com.hedimissaoui.movieapp.ui.detail.DetailScreenActivity
import com.hedimissaoui.movieapp.ui.theme.MoviesAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            LaunchedEffect(true) {
                viewModel.reloadMovies()
            }
            MoviesAppTheme {
                Scaffold(topBar = {
                    TopAppBar(title = {
                        Text(
                            text = "Movie app", maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    })
                }) {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading),
                        onRefresh = {
                            viewModel.reloadMovies()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            if (viewModel.isLoading) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    repeat(5) {
                                        MovieItemPlaceholder()
                                    }
                                }
                            } else {
                                if (viewModel.errorLoading) {
                                    ErrorMessageButton(message = "Couldn't load movies",
                                        actionMessage = "RETRY",
                                        actionIcon = Icons.Default.Refresh,
                                        onActionClick = {
                                            viewModel.reloadMovies()
                                        })
                                } else {
                                    val context = LocalContext.current

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        itemsIndexed(viewModel.movies) { index, item ->
                                            viewModel.scrollPosition = index
                                            if ((index + 1) >= (viewModel.currentPage * viewModel.RESULTS_PER_PAGE)) {
                                                viewModel.getNextPage()
                                            }
                                            MovieItem(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp), movie = item.copy(
                                                releaseDate = viewModel.dateToYear(item.releaseDate)
                                            ), onClick = {
                                                item.id?.let {
                                                    val intent = Intent(
                                                        context,
                                                        DetailScreenActivity::class.java
                                                    )
                                                    intent.putExtra("movie_id", it)
                                                    startActivity(intent)
                                                }

                                            })

                                        }
                                    }


                                }
                            }

                            if (viewModel.isLoadingNextPage) {
                                Dialog(
                                    onDismissRequest = { },
                                    DialogProperties(
                                        dismissOnBackPress = false,
                                        dismissOnClickOutside = false
                                    )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }


                    }
                }

            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MovieItem(modifier: Modifier, movie: Movie, onClick: () -> Unit) {
        Card(modifier = modifier, elevation = 4.dp, onClick = { onClick() }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberImagePainter(
                        data = when (movie.backdropPath.isNullOrBlank()) {
                            true -> "https://via.placeholder.com/500x300?text=Image+not+available"
                            false -> "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
                        }
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    movie.title?.let {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    movie.releaseDate?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colors.onBackground.copy(0.6f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MovieItemPlaceholder(
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "", modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                            )
                    )
                    Text(
                        text = "",
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(0.2f)
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                            )
                    )
                }

            }

        }
    }


    @Composable
    fun ErrorMessageButton(
        message: String,
        actionMessage: String? = null,
        actionIcon: ImageVector? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                actionIcon?.let {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .clickable { onActionClick?.invoke() }
                            .padding(16.dp)) {

                        Icon(
                            imageVector = it,
                            contentDescription = actionMessage,
                            modifier = Modifier.size(22.dp)
                        )

                        actionMessage?.let {
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}
