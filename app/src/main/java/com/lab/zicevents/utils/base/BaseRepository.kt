package com.lab.zicevents.utils.base

import com.google.android.gms.tasks.Task
import com.lab.zicevents.data.Result
import kotlinx.coroutines.CancellationException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
open class BaseRepository {

    /**
     * Task extension to transform Firebase Task<> to Kotlin suspendCoroutine and return Result<T>
     */
    suspend fun <T> Task<T>.awaitTask(): Result<T> = suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val e = exception
                if (e == null) {
                    if (isCanceled) continuation.resumeWithException(CancellationException("Task $this was cancelled normally."))
                    else continuation.resume(Result.Success(result as T))
                } else {
                    continuation.resume(Result.Error(e))
                }
            } else{
                continuation.resume(Result.Error(exception!!))
            }
        }
    }

    /**
     * Extension to transform Retrofit Call to Kotlin suspendCoroutine and return Result<T>
     */
    suspend fun <T> Call<T>.awaitCall(): Result<T> = suspendCoroutine { continuation ->
        enqueue(object: Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {

                if (response.isSuccessful)
                    continuation.resume(Result.Success(response.body()!!))
                else
                    continuation.resumeWithException(Exception(response.message()))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }

}

