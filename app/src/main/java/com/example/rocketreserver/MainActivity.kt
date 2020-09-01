package com.example.rocketreserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import coil.request.ImageRequest
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import dev.chrisbanes.accompanist.coil.CoilImage

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
    val context = ContextAmbient.current

    val state = remember { mutableStateOf<UiState>(UiState.Loading) }
    launchInComposition {
        try {
            val launchList = apolloClient(context).query(LaunchListQuery()).toDeferred().await()
                .data
                ?.launches
                ?.launches
                ?.filterNotNull()
            if (launchList == null) {
                // There were some error
                // TODO: do something with response.errors
                state.value = UiState.Error
            } else {
                state.value = UiState.Success(launchList)
            }
        } catch (e: ApolloException) {
            // There were some network errors
            // TODO: add a retry button
            state.value = UiState.Error
        }
    }

    Stack(modifier = Modifier.fillMaxSize()) {
        when (val value = state.value) {
            is UiState.Loading -> Loading(modifier = Modifier.gravity(Alignment.Center))
            is UiState.Error -> Error(modifier = Modifier.gravity(Alignment.Center))
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
    ConstraintLayout(
        modifier = modifier,
        constraintSet = ConstraintSet {
            val image = createRefFor("image")
            val divider = createRefFor("divider")

            val missionName = createRefFor("missionName")
            val site = createRefFor("site")

            constrain(image) {
                absoluteLeft.linkTo(parent.absoluteLeft, 8.dp)
                top.linkTo(parent.top, 8.dp)
                width = Dimension.value(50.dp)
                height = Dimension.value(50.dp)
            }

            constrain(divider) {
                absoluteLeft.linkTo(parent.absoluteLeft)
                absoluteRight.linkTo(parent.absoluteRight)
                top.linkTo(site.bottom, 8.dp)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.value(1.dp)
            }

            constrain(missionName) {
                absoluteLeft.linkTo(image.absoluteRight, 16.dp)
                absoluteRight.linkTo(parent.absoluteRight)
                width = Dimension.fillToConstraints
                top.linkTo(parent.top, 8.dp)
            }

            constrain(site) {
                absoluteLeft.linkTo(image.absoluteRight, 16.dp)
                absoluteRight.linkTo(parent.absoluteRight)
                width = Dimension.fillToConstraints
                top.linkTo(missionName.bottom)
                bottom.linkTo(parent.bottom)
            }
        }) {
        CoilImage(
            modifier = Modifier.layoutId("image"),
            request = ImageRequest.Builder(ContextAmbient.current)
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
            )
        )
    }
    LaunchList(list)
}
