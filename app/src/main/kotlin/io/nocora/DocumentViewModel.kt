package io.nocora

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.Outline
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class DocumentViewModel : ViewModel() {
    var viewerState by mutableStateOf(R.string.key_horizontal)
    var toolbarState by mutableStateOf(R.string.key_page)
    var isToolbarVisible by mutableStateOf(false)
    var isCardVisible by mutableStateOf(false)
    var isOutlineVisible by mutableStateOf(false)

    var fileName by mutableStateOf(String())
    var fileSize by mutableStateOf(0L)

    var title by mutableStateOf(String())
    var author by mutableStateOf(String())
    var subject by mutableStateOf(String())
    var keywords by mutableStateOf(String())
    var creationDate by mutableStateOf(String())
    var modificationDate by mutableStateOf(String())
    var creator by mutableStateOf(String())
    var producer by mutableStateOf(String())

    var pageCount by mutableStateOf(0)
    var currentPage by mutableStateOf(0)

    var document by mutableStateOf<Document?>(null)

    var outlineList = mutableStateListOf<Outline>()

    val bitmapList = mutableStateMapOf<Int, Bitmap>()

    var bitmapJob by mutableStateOf<Job?>(null)

    private val mutex = Mutex()

    suspend fun openDocument(context: Context, uri: Uri): Document =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val contentResolver = context.contentResolver
                val cursor = contentResolver.query(uri, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                    }
                } finally {
                    cursor?.close()
                }
                val inputStream = contentResolver.openInputStream(uri) ?: throw IllegalArgumentException()
                try {
                    val buffer = inputStream.readBytes()
                    val mimetype = contentResolver.getType(uri)
                    Document.openDocument(buffer, mimetype)
                } finally {
                    inputStream.close()
                }
            }
        }

    fun loadDocument(context: Context, uri: Uri) {
        viewModelScope.launch {
            document = openDocument(context, uri)
            document?.let {
                it.getMetaData(Document.META_INFO_TITLE).let { title = it }
                it.getMetaData(Document.META_INFO_AUTHOR).let { author = it }
                it.getMetaData(Document.META_INFO_SUBJECT).let { subject = it }
                it.getMetaData(Document.META_INFO_KEYWORDS).let { keywords = it }
                it.getMetaData(Document.META_INFO_CREATIONDATE).let { creationDate = it }
                it.getMetaData(Document.META_INFO_MODIFICATIONDATE).let { modificationDate = it }
                it.getMetaData(Document.META_INFO_CREATOR).let { creator = it }
                it.getMetaData(Document.META_INFO_PRODUCER).let { producer = it }
                it.countPages().let { pageCount = it }
                it.loadOutline()?.let {
                    outlineList.addAll(
                        buildList {
                            fun visit(outline: Outline) {
                                add(outline)
                                outline.down?.forEach(::visit)
                            }
                            it.forEach(::visit)
                        }
                    )
                }
            }
        }
    }

    fun invertBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) pixels[i] = (pixels[i] and 0xFF000000.toInt()) or (pixels[i].inv() and 0x00FFFFFF)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    suspend fun openBitmap(
        document: Document,
        index: Int,
        isDarkTheme: Boolean,
        width: Int,
        height: Int? = null,
    ): Bitmap =
        withContext(Dispatchers.Default) {
            mutex.withLock {
                val page = document.loadPage(index)
                try {
                    val matrix =
                        height?.let { AndroidDrawDevice.fitPage(page, width, it) }
                            ?: AndroidDrawDevice.fitPageWidth(page, width)
                    val bitmap = AndroidDrawDevice.drawPage(page, matrix)
                    if (isDarkTheme) invertBitmap(bitmap) else bitmap
                } finally {
                    page.destroy()
                }
            }
        }

    fun loadBitmapList(list: List<Int>, isDarkTheme: Boolean, width: Int, height: Int? = null) {
        val document = document ?: return
        bitmapJob?.cancel()
        bitmapJob =
            viewModelScope.launch {
                bitmapList.keys
                    .filter { it !in list }
                    .forEach {
                        bitmapList[it]?.recycle()
                        bitmapList.remove(it)
                    }
                for (index in list) {
                    if (index in 0 until pageCount && index !in bitmapList) {
                        bitmapList[index] = openBitmap(document, index, isDarkTheme, width, height)
                    }
                }
            }
    }
}
