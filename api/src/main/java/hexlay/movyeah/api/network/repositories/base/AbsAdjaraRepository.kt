package hexlay.movyeah.api.network.repositories.base

import android.util.Log
import hexlay.movyeah.api.network.ApiResult
import retrofit2.Response

open class AbsAdjaraRepository {

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): T? {
        val result = safeApiResult(call)
        var data: T? = null
        when (result) {
            is ApiResult.Success -> data = result.data
            is ApiResult.Error -> Log.e("safeApiCall", result.exception.message.toString())
        }
        return data
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun <T : Any> safeApiResult(call: suspend () -> Response<T>): ApiResult<T> {
        val response = call.invoke()
        if (response.isSuccessful)
            return ApiResult.Success(response.body()!!)
        return ApiResult.Error(Exception(response.errorBody()?.string()))
    }
}