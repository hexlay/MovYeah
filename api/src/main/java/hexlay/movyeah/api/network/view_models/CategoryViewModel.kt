package hexlay.movyeah.api.network.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.attributes.Category
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AbsAdjaraViewModel(application) {

    fun fetchCategories(): MutableLiveData<List<Category>> {
        val categories = MutableLiveData<List<Category>>()
        scope.launch {
            try {
                categories.postValue(repository.getCategories())
            } catch (t: Throwable) {}
        }
        return categories
    }

}