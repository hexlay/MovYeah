package hexlay.movyeah.api.network.view_models.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.network.AdjaraFactory
import hexlay.movyeah.api.network.repositories.AdjaraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class AbsAdjaraViewModel(application: Application) : AndroidViewModel(application) {

    private val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
    protected val scope = CoroutineScope(coroutineContext)
    protected val repository = AdjaraRepository(AdjaraFactory.createService(application.applicationContext))

    fun cancelAllRequests() = coroutineContext.cancel()

}