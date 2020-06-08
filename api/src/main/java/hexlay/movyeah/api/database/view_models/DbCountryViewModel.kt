package hexlay.movyeah.api.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.database.repositories.DbCountryRepository
import hexlay.movyeah.api.models.attributes.Country

class DbCountryViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbCountryRepository(application.applicationContext)

    fun getCountries() = repository.getCountries()

    fun insertCountry(country: Country) = repository.insertCountry(country)

    fun clearCategories() = repository.clearAll()

}