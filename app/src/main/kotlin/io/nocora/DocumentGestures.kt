package io.nocora

import android.util.Log
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.documentGestures(
    onOnePointerTap: (Offset) -> Unit,
    onOnePointerDoubleTap: (Offset) -> Unit,
    onOnePointerLongPress: (Offset) -> Unit,
    onTwoPointerTap: () -> Unit,
    onTwoPointerDoubleTap: () -> Unit,
    onTwoPointerLongPress: () -> Unit,
    onThreePointerTap: () -> Unit,
    onThreePointerDoubleTap: () -> Unit,
    onThreePointerLongPress: () -> Unit,
) =
    pointerInput(Unit) {
        coroutineScope {
            var lastTapTime = 0L
            var lastTapPointers = 0

            while (true) {
                awaitPointerEventScope {
                    val down = awaitFirstDown()
                    val position = down.position
                    val activePointers = mutableSetOf(down.id)
                    var isLongPress = false
                    var pointerMoved = false

                    val job = launch {
                        delay(250L)
                        isLongPress = true
                        if (!pointerMoved) {
                            when (activePointers.size) {
                                1 -> {
                                    onOnePointerLongPress(position)
                                    Log.d("Gesture", "1-finger LONG PRESS at $position")
                                }

                                2 -> {
                                    onTwoPointerLongPress()
                                    Log.d("Gesture", "2-finger LONG PRESS")
                                }

                                3 -> {
                                    onThreePointerLongPress()
                                    Log.d("Gesture", "3-finger LONG PRESS")
                                }
                            }
                        }
                    }

                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { if (it.positionChange() != Offset.Zero) pointerMoved = true }
                        activePointers.addAll(event.changes.map { it.id })
                        if (event.changes.all { it.changedToUp() }) break
                    }

                    job.cancel()

                    if (!isLongPress && !pointerMoved) {
                        val tapTime = System.currentTimeMillis()
                        val tapPointers = activePointers.size

                        if (tapTime < lastTapTime + 250L && tapPointers == lastTapPointers) {
                            when (activePointers.size) {
                                1 -> {
                                    onOnePointerDoubleTap(position)
                                    Log.d("Gesture", "1-finger DOUBLE TAP at $position")
                                }

                                2 -> {
                                    onTwoPointerDoubleTap()
                                    Log.d("Gesture", "2-finger DOUBLE TAP")
                                }

                                3 -> {
                                    onThreePointerDoubleTap()
                                    Log.d("Gesture", "3-finger DOUBLE TAP")
                                }
                            }

                            lastTapTime = 0L
                            lastTapPointers = 0
                        } else {
                            when (activePointers.size) {
                                1 -> {
                                    onOnePointerTap(position)
                                    Log.d("Gesture", "1-finger TAP at $position")
                                }

                                2 -> {
                                    onTwoPointerTap()
                                    Log.d("Gesture", "2-finger TAP")
                                }

                                3 -> {
                                    onThreePointerTap()
                                    Log.d("Gesture", "3-finger TAP")
                                }
                            }

                            lastTapTime = tapTime
                            lastTapPointers = tapPointers
                        }
                    }
                }
            }
        }
    }
