package hexlay.movyeah.api.database.repositories

import android.content.Context
import hexlay.movyeah.api.database.AdjaraDatabase
import hexlay.movyeah.api.database.dao.CountriesDao
import hexlay.movyeah.api.models.attributes.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbCountryRepository(context: Context) : CoroutineScope {

    override val coroutineContext
        get() = Dispatchers.Main

    private var dao: CountriesDao? = null

    init {
        dao = AdjaraDatabase.getInstance(context).countriesDao()
    }

    fun getCountries() = dao?.getAll()

    fun insertCountry(country: Country) {
        launch {
            insert(country)
        }
    }

    fun clearAll() {
        launch {
            clear()
        }
    }

    private suspend fun insert(country: Country) {
        withContext(Dispatchers.IO) {
            dao?.insert(country)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            dao?.clear()
        }
    }

}