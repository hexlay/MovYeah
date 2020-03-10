package hexlay.movyeah.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.database.repositories.DbCategoryRepository
import hexlay.movyeah.models.movie.attributes.Category

class DbCategoryViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: DbCategoryRepository = DbCategoryRepository(application.applicationContext)

    fun getCategories() = repository.getCategories()

    fun insertCategory(category: Category) = repository.insertCategory(category)

    fun clearCategories() = repository.clearAll()

}