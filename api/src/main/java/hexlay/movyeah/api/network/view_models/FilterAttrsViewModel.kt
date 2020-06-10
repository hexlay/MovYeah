package hexlay.movyeah.api.network.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.attributes.Category
import hexlay.movyeah.api.models.attributes.Country
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class FilterAttrsViewModel(application: Application) : AbsAdjaraViewModel(application) {

    fun fetchCategories(): MutableLiveData<List<Category>> {
        val categories = MutableLiveData<List<Category>>()
        scope.launch {
            try {
                categories.postValue(repository.getCategories())
            } catch (t: Throwable) {
                Log.e("fetchCategories", t.message.toString())
            }
        }
        return categories
    }

    fun fetchCountries(): MutableLiveData<List<Country>> {
        val countries = MutableLiveData<List<Country>>()
        scope.launch {
            try {
                countries.postValue(repository.getCountries())
            } catch (t: Throwable) {
                Log.e("fetchCountries", t.message.toString())
            }
        }
        return countries
    }

}