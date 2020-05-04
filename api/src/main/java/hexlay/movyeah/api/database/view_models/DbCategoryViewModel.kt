package hexlay.movyeah.api.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.database.repositories.DbCategoryRepository
import hexlay.movyeah.api.models.attributes.Category

class DbCategoryViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbCategoryRepository(application.applicationContext)

    fun getCategories() = repository.getCategories()

    fun insertCategory(category: Category) = repository.insertCategory(category)

    fun clearCategories() = repository.clearAll()

}