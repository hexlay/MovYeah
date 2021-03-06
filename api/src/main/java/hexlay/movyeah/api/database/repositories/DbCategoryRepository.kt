package hexlay.movyeah.api.database.repositories

import android.content.Context
import hexlay.movyeah.api.database.AdjaraDatabase
import hexlay.movyeah.api.database.dao.CategoriesDao
import hexlay.movyeah.api.models.attributes.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbCategoryRepository(context: Context) : CoroutineScope {

    override val coroutineContext
        get() = Dispatchers.Main

    private var dao: CategoriesDao? = null

    init {
        dao = AdjaraDatabase.getInstance(context).categoriesDao()
    }

    fun getCategories() = dao?.getAll()

    fun insertCategory(category: Category) {
        launch {
            insert(category)
        }
    }

    fun clearAll() {
        launch {
            clear()
        }
    }

    private suspend fun insert(category: Category) {
        withContext(Dispatchers.IO) {
            dao?.insert(category)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            dao?.clear()
        }
    }

}