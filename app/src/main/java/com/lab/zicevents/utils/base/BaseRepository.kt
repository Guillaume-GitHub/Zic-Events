package com.lab.zicevents.utils.base

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lab.zicevents.R
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

    companion object {
        const val SUCCESS_TASK = R.string.task_success
        const val FAIL_TASK = R.string.task_failure
    }
    /**
     * Task extension to transform Firebase Task<> to Kotlin suspendCoroutine and return Result<T>
     */
    suspend fun <T> Task<T>.awaitTask(): Result<T> = suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val e = exception
                if (e == null) {
                    if (isCanceled) {
                        val cancellationError = CancellationException("Task $this was cancelled normally.")
                        continuation.resumeWithException(cancellationError)
                        Log.e("Upload Task Canceled ","" ,cancellationError)
                    }
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

    /**
     * Extension to transform UploadTask to Kotlin suspendCoroutine and return Result<StorageReference>
     */
    suspend fun UploadTask.awaitUpload() : Result<StorageReference> = suspendCoroutine { continuation ->

        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val e = exception
                if (e == null) {
                    if (isCanceled) {
                        val cancellationError = CancellationException("Task $this was cancelled normally.")
                        continuation.resumeWithException(cancellationError)
                        Log.e("Upload Task Canceled ","" ,cancellationError)
                    }
                    else continuation.resume(Result.Success(result.storage))
                } else {
                    continuation.resume(Result.Error(e))
                    Log.e("Upload Task Error ","" ,e)
                }
            } else{
                continuation.resume(Result.Error(exception!!))
                Log.e("Upload Task Exception ","" , exception)
            }
        }
    }

}

