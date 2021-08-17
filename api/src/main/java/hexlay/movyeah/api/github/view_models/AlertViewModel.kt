package hexlay.movyeah.api.github.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.github.GithubFactory
import hexlay.movyeah.api.github.repositories.AlertRepository
import hexlay.movyeah.api.models.alert.BasicAlert
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val repository = AlertRepository(GithubFactory.buildAlerts())

    fun cancelAllRequests() = coroutineContext.cancel()

    fun fetchAlerts(): MutableLiveData<List<BasicAlert>> {
        val alerts = MutableLiveData<List<BasicAlert>>()
        scope.launch {
            try {
                alerts.postValue(repository.getBasicAlerts())
            } catch (t: Throwable) {
                Log.e("fetchAlerts", t.message.toString())
            }
        }
        return alerts
    }

}