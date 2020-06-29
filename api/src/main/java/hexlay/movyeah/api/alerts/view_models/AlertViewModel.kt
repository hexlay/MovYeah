package hexlay.movyeah.api.alerts.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.alerts.view_models.base.AbsAlertViewModel
import hexlay.movyeah.api.models.alert.BasicAlert
import kotlinx.coroutines.launch

class AlertViewModel(application: Application) : AbsAlertViewModel(application) {


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