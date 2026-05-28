package io.nocora

import android.app.ActivityManager.TaskDescription
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch

class DocumentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        setContent {
            val viewModel: DocumentViewModel by viewModels()

            val uri = intent.data

            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                    if (it == null) finishAndRemoveTask() else viewModel.loadDocument(this, it)
                }

            setTaskDescription(
                viewModel.title
                    .ifBlank { viewModel.fileName }
                    .ifBlank { getString(R.string.app_name) }
                    .let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            TaskDescription.Builder().setLabel(it).build()
                        } else {
                            TaskDescription(it)
                        }
                    }
            )

            if (viewModel.document == null) {
                uri?.let { viewModel.loadDocument(this, it) }
                    ?: LaunchedEffect(Unit) {
                        launcher.launch(arrayOf("application/pdf", "application/epub+zip", "image/*"))
                    }
            } else if (viewModel.pageCount > viewModel.currentPage) {
                BackHandler {
                    viewModel.bitmapJob?.cancel()
                    viewModel.bitmapJob = null

                    viewModel.bitmapList.values.forEach { it.recycle() }
                    viewModel.bitmapList.clear()

                    viewModel.document?.destroy()
                    viewModel.document = null

                    viewModel.outlinePage = 0
                    viewModel.currentPage = 0
                    viewModel.pageCount = 0

                    viewModel.title = String()
                    viewModel.author = String()
                    viewModel.subject = String()
                    viewModel.keywords = String()
                    viewModel.creationDate = String()
                    viewModel.modificationDate = String()
                    viewModel.creator = String()
                    viewModel.producer = String()

                    viewModel.fileName = String()
                    viewModel.fileSize = 0L

                    viewModel.isCardVisible = false
                    viewModel.isToolbarVisible = false
                    viewModel.toolbarState = R.string.key_page
                    viewModel.viewerState = R.string.key_horizontal

                    if (uri != null) finishAndRemoveTask()
                }

                DocumentScreen(viewModel)
            }
        }
    }
}
