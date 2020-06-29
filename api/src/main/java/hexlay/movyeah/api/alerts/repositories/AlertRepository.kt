package hexlay.movyeah.api.alerts.repositories

import hexlay.movyeah.api.alerts.AlertAPI
import hexlay.movyeah.api.models.alert.BasicAlert
import hexlay.movyeah.api.network.repositories.base.AbsAdjaraRepository

class AlertRepository(private val api: AlertAPI) : AbsAdjaraRepository() {

    suspend fun getBasicAlerts(): List<BasicAlert>? {
        return safeApiCall { api.getBasicAlertsAsync() }
    }

}