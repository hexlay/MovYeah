package hexlay.movyeah.api.view_models.base

import androidx.lifecycle.ViewModel
import hexlay.movyeah.api.AdjaraFactory
import hexlay.movyeah.api.repositories.AdjaraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class AbsAdjaraViewModel : ViewModel() {

    private val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
    protected val scope = CoroutineScope(coroutineContext)
    protected val repository = AdjaraRepository(AdjaraFactory.createService())

    fun cancelAllRequests() = coroutineContext.cancel()

}