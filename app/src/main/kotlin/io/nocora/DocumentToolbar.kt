package io.nocora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DocumentToolbar(viewModel: DocumentViewModel, sliderState: SliderState, textFieldState: TextFieldState) {
    AnimatedVisibility(
        visible = viewModel.isToolbarVisible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        content = {
            BottomAppBar(
                actions = {
                    Crossfade(
                        targetState = viewModel.toolbarState,
                        content = {
                            when (it) {
                                R.string.key_page -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Slider(
                                            state = sliderState,
                                            modifier = Modifier.weight(1f),
                                            colors =
                                                SliderDefaults.colors(
                                                    activeTickColor = Color.Transparent,
                                                    inactiveTickColor = Color.Transparent,
                                                ),
                                        )
                                        Text(
                                            text =
                                                "%%0%dd / %%d"
                                                    .format(viewModel.pageCount.toString().length)
                                                    .format(sliderState.value.roundToInt() + 1, viewModel.pageCount)
                                        )
                                    }
                                }

                                R.string.key_search -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        OutlinedTextField(
                                            state = textFieldState,
                                            modifier = Modifier.weight(1f),
                                            lineLimits = TextFieldLineLimits.SingleLine,
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        )
                                        SplitButtonLayout(
                                            leadingButton = {
                                                SplitButtonDefaults.LeadingButton(onClick = {}) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.chevron_left_24px),
                                                        contentDescription = null,
                                                    )
                                                }
                                            },
                                            trailingButton = {
                                                SplitButtonDefaults.TrailingButton(onClick = {}) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.chevron_right_24px),
                                                        contentDescription = null,
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        },
                    )
                },
                contentPadding = PaddingValues(24.dp),
            )
        },
    )
}
