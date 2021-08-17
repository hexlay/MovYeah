package hexlay.movyeah.api.github.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.github.GithubFactory
import hexlay.movyeah.api.github.repositories.GithubRepository
import hexlay.movyeah.api.models.github.Release
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GithubViewModel(application: Application) : AndroidViewModel(application) {

    private val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val repository = GithubRepository(GithubFactory.buildApiCall())

    fun cancelAllRequests() = coroutineContext.cancel()

    fun fetchReleases(): MutableLiveData<List<Release>> {
        val releases = MutableLiveData<List<Release>>()
        scope.launch {
            try {
                releases.postValue(repository.getReleases())
            } catch (t: Throwable) {
                Log.e("fetchReleases", t.message.toString())
            }
        }
        return releases
    }

}