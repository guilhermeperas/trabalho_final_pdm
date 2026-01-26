package com.example.grupo_pdm

import android.app.Application
import coil3.SingletonImageLoader
import com.example.grupo_pdm.data.createCoilImageLoader

class MovieApp : Application(){

    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe {
            createCoilImageLoader(it)
        }

    }
}