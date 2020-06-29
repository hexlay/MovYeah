package hexlay.movyeah.api.alerts.view_models.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.alerts.AlertFactory
import hexlay.movyeah.api.alerts.repositories.AlertRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class AbsAlertViewModel(application: Application) : AndroidViewModel(application) {

    private val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
    protected val scope = CoroutineScope(coroutineContext)
    protected val repository = AlertRepository(AlertFactory.build())

    fun cancelAllRequests() = coroutineContext.cancel()

}