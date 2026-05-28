package io.nocora

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DocumentViewer(
    viewModel: DocumentViewModel,
    pagerState: PagerState,
    listState: LazyListState,
    modifier: Modifier,
    width: Int,
    height: Int,
    density: Float,
) {
    @Composable
    fun BitmapItem(contentAlignment: Alignment, bitmap: Bitmap?) {
        Crossfade(
            targetState = bitmap,
            content = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = contentAlignment,
                    content = {
                        if (it == null) {
                            Box(
                                modifier = Modifier.height((height / density).dp),
                                contentAlignment = Alignment.Center,
                                content = { ContainedLoadingIndicator() },
                            )
                        } else {
                            bitmap?.asImageBitmap()?.let { Image(bitmap = it, contentDescription = null) }
                        }
                    },
                )
            },
        )
    }

    Crossfade(
        targetState = viewModel.viewerState,
        content = {
            when (it) {
                R.string.key_horizontal -> {
                    HorizontalPager(
                        state = pagerState,
                        modifier = modifier,
                        pageContent = {
                            BitmapItem(contentAlignment = Alignment.Center, bitmap = viewModel.bitmapList[it])
                        },
                    )
                }

                R.string.key_vertical -> {
                    LazyColumn(
                        modifier = modifier,
                        state = listState,
                        content = {
                            items(
                                count = viewModel.pageCount,
                                itemContent = {
                                    BitmapItem(
                                        contentAlignment = Alignment.TopCenter,
                                        bitmap = viewModel.bitmapList[it],
                                    )
                                },
                            )
                        },
                    )
                }
            }
        },
    )
}
