package com.hw15.database

import android.app.Application
import androidx.room.Room

class App : Application() {
    lateinit var db: WordsDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            WordsDatabase::class.java,
            "db"
        )
//            .fallbackToDestructiveMigration()
            .build()
    }
}