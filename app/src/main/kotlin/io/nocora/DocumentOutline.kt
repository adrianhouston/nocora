package io.nocora

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DocumentOutline(viewModel: DocumentViewModel) {
    BackHandler { viewModel.isOutlineVisible = false }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp)) {
        items(viewModel.outlineList) {
            Text(
                text = it.title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier =
                    Modifier.fillMaxWidth()
                        .clickable {
                            val doc = viewModel.document
                            if (doc != null) {
                                viewModel.outlinePage = doc.resolveLink(it.uri).page
                            }
                            viewModel.isOutlineVisible = false
                        }
                        .padding(12.dp),
            )
        }
    }
}
