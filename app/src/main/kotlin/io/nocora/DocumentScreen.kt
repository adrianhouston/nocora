package io.nocora

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.nocora.ui.theme.AppTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(viewModel: DocumentViewModel) {
    val isDarkTheme = isSystemInDarkTheme()

    val context = LocalContext.current

    val displayMetrics = context.resources.displayMetrics

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels
    val density = displayMetrics.density

    val list = viewModel.currentPage.let { listOf(it, it + 1, it - 1, it + 2, it - 2, it + 3, it - 3) }

    LaunchedEffect(viewModel.currentPage) { viewModel.loadBitmapList(list, isDarkTheme, width) }
    LaunchedEffect(isDarkTheme, width) {
        viewModel.bitmapList.values.forEach { it.recycle() }
        viewModel.bitmapList.clear()
        viewModel.loadBitmapList(list, isDarkTheme, width)
    }

    val pagerState = rememberPagerState(initialPage = viewModel.currentPage, pageCount = { viewModel.pageCount })
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.currentPage)

    LaunchedEffect(pagerState.currentPage) {
        if (viewModel.viewerState == R.string.key_horizontal) {
            viewModel.currentPage = pagerState.currentPage
            listState.scrollToItem(viewModel.currentPage)
        }
    }
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.viewerState == R.string.key_vertical) {
            viewModel.currentPage = listState.firstVisibleItemIndex
            pagerState.scrollToPage(viewModel.currentPage)
        }
    }

    val sliderState =
        rememberSliderState(
            steps = (viewModel.pageCount - 2).coerceAtLeast(0),
            value = viewModel.currentPage.toFloat(),
            valueRange = 0f..(viewModel.pageCount - 1).toFloat(),
        )
    val textFieldState = rememberTextFieldState()

    LaunchedEffect(viewModel.currentPage) { sliderState.value = viewModel.currentPage.toFloat() }

    val scope = rememberCoroutineScope()

    val modifier =
        Modifier.fillMaxSize()
            .documentGestures(
                onOnePointerTap = {
                    when {
                        it.x < width * 0.2 -> {
                            if (viewModel.currentPage > 0) {
                                when (viewModel.viewerState) {
                                    R.string.key_horizontal ->
                                        scope.launch { pagerState.animateScrollToPage(viewModel.currentPage - 1) }

                                    R.string.key_vertical ->
                                        scope.launch { listState.animateScrollToItem(viewModel.currentPage - 1) }
                                }
                            }
                        }

                        it.x > width * 0.8 -> {
                            if (viewModel.currentPage < viewModel.pageCount - 1) {
                                when (viewModel.viewerState) {
                                    R.string.key_horizontal ->
                                        scope.launch { pagerState.animateScrollToPage(viewModel.currentPage + 1) }

                                    R.string.key_vertical ->
                                        scope.launch { listState.animateScrollToItem(viewModel.currentPage + 1) }
                                }
                            }
                        }

                        else -> viewModel.isToolbarVisible = !viewModel.isToolbarVisible
                    }
                },
                onOnePointerDoubleTap = {},
                onOnePointerLongPress = {},
                onTwoPointerTap = {
                    when (viewModel.toolbarState) {
                        R.string.key_page -> viewModel.toolbarState = R.string.key_search
                        R.string.key_search -> viewModel.toolbarState = R.string.key_page
                    }
                },
                onTwoPointerDoubleTap = {},
                onTwoPointerLongPress = {
                    when (viewModel.viewerState) {
                        R.string.key_horizontal -> {
                            viewModel.viewerState = R.string.key_vertical
                            scope.launch {
                                delay(250L)
                                pagerState.scrollToPage(viewModel.currentPage)
                            }
                        }

                        R.string.key_vertical -> {
                            viewModel.viewerState = R.string.key_horizontal
                            scope.launch {
                                delay(250L)
                                listState.scrollToItem(viewModel.currentPage)
                            }
                        }
                    }
                },
                onThreePointerTap = {
                    viewModel.isToolbarVisible = false
                    viewModel.isCardVisible = true
                },
                onThreePointerDoubleTap = {},
                onThreePointerLongPress = {
                    viewModel.isToolbarVisible = false
                    viewModel.isOutlineVisible = true
                },
            )

    fun Modifier.outlineClickable(uri: String): Modifier =
        clickable(
            onClick = {
                viewModel.document?.resolveLink(uri)?.let {
                    when (viewModel.viewerState) {
                        R.string.key_horizontal -> scope.launch { pagerState.scrollToPage(it.page) }
                        R.string.key_vertical -> scope.launch { listState.scrollToItem(it.page) }
                    }
                }
                viewModel.isOutlineVisible = false
            }
        )

    sliderState.onValueChangeFinished = {
        when (viewModel.viewerState) {
            R.string.key_horizontal -> scope.launch { pagerState.scrollToPage(sliderState.value.roundToInt()) }
            R.string.key_vertical -> scope.launch { listState.scrollToItem(sliderState.value.roundToInt()) }
        }
    }

    AppTheme(
        content = {
            Crossfade(
                targetState = viewModel.isOutlineVisible,
                content = {
                    if (it) {
                        Scaffold(content = { DocumentOutline(viewModel, Modifier::outlineClickable) })
                    } else {
                        Scaffold(
                            bottomBar = { DocumentToolbar(viewModel, sliderState, textFieldState) },
                            content = {
                                DocumentViewer(viewModel, pagerState, listState, modifier, width, height, density)
                                DocumentCard(viewModel, width, height, density)
                            },
                        )
                    }
                },
            )
        }
    )
}
