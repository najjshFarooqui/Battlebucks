package com.battlebucks.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.battlebucks.R
import com.battlebucks.domain.model.LeaderboardItem
import kotlinx.coroutines.delay

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val list by viewModel.leaderboard.collectAsState()
    val lastUpdatedPlayerId by viewModel.lastUpdatedPlayerId.collectAsState()
    val listState = rememberLazyListState()
    val expandedHeight = 220.dp
    val collapsedHeight = 100.dp
    val collapseRangePx = 120f

    val collapseFraction by remember(listState) {
        derivedStateOf {
            val totalOffset = if (listState.firstVisibleItemIndex > 0) {
                collapseRangePx
            } else {
                listState.firstVisibleItemScrollOffset.toFloat()
            }

            (totalOffset / collapseRangePx).coerceIn(0f, 1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(top = expandedHeight)
        ) {
            items(list, key = { it.playerId }) { item ->
                LeaderboardRow(
                    item = item,
                    isUpdated = item.playerId == lastUpdatedPlayerId
                )
            }
        }

        LeaderboardHeader(
            collapsedFraction = collapseFraction,
            expandedHeight = expandedHeight,
            collapsedHeight = collapsedHeight,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun LeaderboardHeader(
    collapsedFraction: Float,
    expandedHeight: androidx.compose.ui.unit.Dp,
    collapsedHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val expandedAlpha = 1f - collapsedFraction
    val collapsedAlpha = collapsedFraction
    val headerHeight = lerp(expandedHeight, collapsedHeight, collapsedFraction)
    val expandedAvatarSize = 100.dp
    val collapsedAvatarSize = 36.dp

    Box(
        modifier = modifier
            .height(headerHeight)
            .background(Color.Black)
            .statusBarsPadding()
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, end = 22.dp, bottom = 22.dp)
                .alpha(expandedAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_img),
                contentDescription = null,
                modifier = Modifier.size(expandedAvatarSize),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = "Najish",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Score: 2100",
                    color = Color.Yellow,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, end = 22.dp, bottom = 10.dp)
                .alpha(collapsedAlpha),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_img),
                    contentDescription = null,
                    modifier = Modifier.size(collapsedAvatarSize),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Najish",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text = "2100",
                color = Color.Yellow,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}


/*@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel = hiltViewModel()) {

    val list by viewModel.leaderboard.collectAsState()
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier

                .systemBarsPadding()
        ) {

            items(list, key = { it.playerId }) { item ->
                LeaderboardRow(item)
            }
        }
    }


}*/


@Composable
fun LeaderboardRow(
    item: LeaderboardItem,
    isUpdated: Boolean
) {
    var showHighlight by remember(item.playerId) { mutableStateOf(false) }
    val rowBackgroundColor by animateColorAsState(
        targetValue = if (showHighlight) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        } else {
            Color.Transparent
        },
        label = "leaderboard_row_highlight"
    )

    LaunchedEffect(isUpdated) {
        if (isUpdated) {
            showHighlight = true
            delay(700)
            showHighlight = false
        } else {
            showHighlight = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackgroundColor)
            .padding(12.dp),

        horizontalArrangement = Arrangement.Center
    ) {


        Image(
            painter = painterResource(id = R.drawable.ic_img),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            "${item.rank}. ${item.name}",
            modifier = Modifier
                .align(Alignment.CenterVertically)

                .padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.weight(1f))


        Row() {

            if (item.rankChange > 0) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Rank Up",
                    tint = Color.Green
                )
            } else if (item.rankChange < 0) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Rank Down",
                    tint = Color.Red
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                "${item.score}",
                color = if (showHighlight) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 4.dp)
            )
        }
    }
    HorizontalDivider()
}
