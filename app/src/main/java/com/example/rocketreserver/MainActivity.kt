package com.example.rocketreserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import coil.request.ImageRequest
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RocketReserverApp()
        }
    }
}

@Composable
fun RocketReserverApp() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "RocketReserver") },
                )
            }, bodyContent = {
                LaunchListContent()
            }
        )
    }
}

sealed class UiState {
    object Loading : UiState()
    object Error : UiState()
    class Success(val launchList: List<LaunchListQuery.Launch>) : UiState()
}

@Composable
fun LaunchListContent() {
    val context = AmbientContext.current

    val state = remember {
        apolloClient(context).query(LaunchListQuery()).watcher().toFlow()
            .map {
                val launchList = it
                    .data
                    ?.launchConnection
                    ?.launches
                    ?.filterNotNull()
                if (launchList == null) {
                    // There were some error
                    // TODO: do something with response.errors
                    UiState.Error
                } else {
                    UiState.Success(launchList)
                }
            }
            .catch { e ->
                emit(UiState.Error)
            }
    }.collectAsState(initial = UiState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        when (val value = state.value) {
            is UiState.Loading -> Loading(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> Error(modifier = Modifier.align(Alignment.Center))
            is UiState.Success -> LaunchList(launchList = value.launchList)
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun Error(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Oops something went wrong"
    )
}

@Preview
@Composable
fun ErrorPreview() {
    Error()
}


@Composable
fun LaunchList(launchList: List<LaunchListQuery.Launch>) {
    // This could be a LazyColumn but the performance is not great at the moment
    ScrollableColumn {
        launchList.forEach { launch ->
            LaunchItem(
                modifier = Modifier.fillMaxWidth(),
                launch = launch
            )
        }
    }
}

@Composable
fun LaunchItem(launch: LaunchListQuery.Launch, modifier: Modifier = Modifier) {
    val context = AmbientContext.current

    val bookTrip = {
        // TODO: use something better than GlobalScope
        GlobalScope.launch(Dispatchers.Main) {
            if (!launch.isBooked) {
                try {
                    apolloClient(context).mutate(
                        BookTripMutation(launch.id),
                        BookTripMutation.Data(
                            bookTrips = BookTripMutation.BookTrips(
                                launches = listOf(
                                    BookTripMutation.Launch(
                                        id = launch.id,
                                        isBooked = true
                                    )
                                )
                            )
                        )
                    ).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        Unit
    }

    ConstraintLayout(
        modifier = modifier,
        constraintSet = ConstraintSet {
            val image = createRefFor("image")
            val divider = createRefFor("divider")

            val missionName = createRefFor("missionName")
            val site = createRefFor("site")
            val button = createRefFor("button")

            constrain(image) {
                start.linkTo(parent.start, 16.dp)
                top.linkTo(parent.top, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.value(80.dp)
                height = Dimension.value(80.dp)
            }

            constrain(divider) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.value(1.dp)
            }

            constrain(missionName) {
                start.linkTo(image.end, 16.dp)
                end.linkTo(button.start, 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(image.top)
                bottom.linkTo(site.top)
            }

            constrain(site) {
                start.linkTo(image.end, 16.dp)
                end.linkTo(button.start, 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                top.linkTo(missionName.bottom)
                bottom.linkTo(image.bottom)
            }

            constrain(button) {
                end.linkTo(parent.end, 8.dp)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        }) {
        CoilImage(
            modifier = Modifier.layoutId("image"),
            request = ImageRequest.Builder(AmbientContext.current)
                .placeholder(R.drawable.ic_placeholder)
                .data(launch.mission!!.missionPatch ?: R.drawable.ic_placeholder)
                .build()
        )
        Divider(
            modifier = Modifier.layoutId("divider"),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
        )
        Text(
            modifier = Modifier.layoutId("missionName"),
            fontWeight = FontWeight.Bold,
            text = launch.mission.name!!,
        )
        Text(
            modifier = Modifier.layoutId("site"),
            text = launch.site!!,
        )
        Button(
            modifier = Modifier.layoutId("button"),
            enabled = launch.isBooked.not(),
            onClick = bookTrip
        ) {
            Text(if (launch.isBooked) "BOOKED" else "BOOK")
        }
    }
}


@Preview
@Composable
fun LaunchListPreview() {
    val list = 0.until(20).map {
        LaunchListQuery.Launch(
            id = it.toString(),
            site = "site $it",
            mission = LaunchListQuery.Mission(
                missionPatch = "https://raw.githubusercontent.com/apollographql/apollo-client/master/docs/source/logo/square.png",
                name = "mission $it"
            ),
            isBooked = false

        )
    }
    LaunchList(list)
}
