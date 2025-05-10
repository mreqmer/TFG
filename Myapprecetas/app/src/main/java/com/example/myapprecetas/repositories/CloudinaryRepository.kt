package com.example.myapprecetas.repositories

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myapprecetas.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CloudinaryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        try {
            MediaManager.get()
        } catch (e: IllegalStateException) {
            val config = mapOf(
                "cloud_name" to context.getString(R.string.cloud_name),
                "api_key" to context.getString(R.string.apikey),
                "api_secret" to context.getString(R.string.apisecret)
            )
            MediaManager.init(context, config)
        }
    }

    fun uploadImage(
        imageUri: Uri,
        onLoading: () -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            MediaManager.get()
        } catch (e: IllegalStateException) {
            val config = mapOf(
                "cloud_name" to context.getString(R.string.cloud_name),
                "api_key" to context.getString(R.string.apikey),
                "api_secret" to context.getString(R.string.apisecret)
            )
            MediaManager.init(context, config)
        }

        onLoading()

        MediaManager.get().upload(imageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(
                    requestId: String?,
                    resultData: MutableMap<Any?, Any?>?
                ) {
                    val url = resultData?.get("secure_url") as? String
                    if (url != null) onSuccess(url)
                    else onError("URL vacía")
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Error desconocido")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

}
