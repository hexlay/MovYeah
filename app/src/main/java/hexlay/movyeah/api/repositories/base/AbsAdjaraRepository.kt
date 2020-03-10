package hexlay.movyeah.api.repositories.base

import android.util.Log
import hexlay.movyeah.api.ApiResult
import retrofit2.Response
import java.io.IOException

open class AbsAdjaraRepository {

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {
        val result: ApiResult<T> = safeApiResult(call, errorMessage)
        var data: T? = null

        when (result) {
            is ApiResult.Success ->
                data = result.data
            is ApiResult.Error -> {
                Log.e("safeApiCall", "$errorMessage & Exception - ${result.exception}")
            }
        }
        return data
    }

    private suspend fun <T : Any> safeApiResult(call: suspend () -> Response<T>, errorMessage: String): ApiResult<T> {
        val response = call.invoke()
        if (response.isSuccessful)
            return ApiResult.Success(response.body()!!)
        return ApiResult.Error(IOException("Error occurred during api call, Message: $errorMessage"))
    }
}