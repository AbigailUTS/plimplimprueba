package com.example.plimplimprueba

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import okhttp3.OkHttpClient

class PlimplimApplication : Application(), ImageLoaderFactory {
    
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(GifDecoder.Factory())
                add(ImageDecoderDecoder.Factory())
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .build()
            }
            .build()
    }
} 