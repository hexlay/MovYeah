package hexlay.movyeah.helpers

import android.app.Activity
import android.app.DownloadManager
import android.widget.ProgressBar
import java.lang.ref.WeakReference

class DownloadProgress(
    activity: Activity,
    downloadId: Long,
    progressBar: ProgressBar,
    downloadManager: DownloadManager?,
    private val callback: ((isDone: Boolean) -> Unit)? = null
) : Thread() {

    private val weakProgressBar = WeakReference(progressBar)
    private val weakDownloadManager = WeakReference(downloadManager)
    private val weakActivity = WeakReference(activity)
    private val query = DownloadManager.Query()

    init {
        setDownloadId(downloadId)
    }

    fun setDownloadId(downloadId: Long) {
        query.setFilterById(downloadId)
    }

    override fun run() {
        while (true) {
            try {
                sleep(300)
                val cursor = weakDownloadManager.get()?.query(query)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                callback?.invoke(true)
                                interrupt()
                            }
                            DownloadManager.STATUS_FAILED -> {
                                callback?.invoke(false)
                                interrupt()
                            }
                        }
                        if (bytesTotal > 0) {
                            val progress = (bytesDownloaded * 100L / bytesTotal).toInt()
                            weakActivity.get()?.runOnUiThread {
                                weakProgressBar.get()?.progress = progress
                            }
                        }
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                interrupt()
                return
            }
        }
    }

}