package io.nocora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentCard(viewModel: DocumentViewModel, width: Int, height: Int, density: Float) {
    @Composable
    fun Cell(name: String, value: String) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            Text(text = "$name: ")
            Text(text = value.ifBlank { "-" }, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }
    }

    AnimatedVisibility(
        visible = viewModel.isCardVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        content = {
            BasicAlertDialog(
                onDismissRequest = { viewModel.isCardVisible = false },
                content = {
                    Card(
                        content = {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                content = {
                                    Cell(name = "File Name", value = viewModel.fileName)
                                    Cell(name = "File Size", value = viewModel.fileSize.toString())
                                    HorizontalDivider()
                                    Cell(name = "Title", value = viewModel.title)
                                    Cell(name = "Author", value = viewModel.author)
                                    Cell(name = "Subject", value = viewModel.subject)
                                    Cell(name = "Keywords", value = viewModel.keywords)
                                    Cell(name = "Creation Date", value = viewModel.creationDate)
                                    Cell(name = "Modification Date", value = viewModel.modificationDate)
                                    Cell(name = "Creator", value = viewModel.creator)
                                    Cell(name = "Producer", value = viewModel.producer)
                                },
                            )
                        }
                    )
                },
            )
        },
    )
}
