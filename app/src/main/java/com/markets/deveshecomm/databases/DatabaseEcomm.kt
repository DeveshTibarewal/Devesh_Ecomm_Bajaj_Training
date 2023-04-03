package com.markets.deveshecomm.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.markets.deveshecomm.dao.DaoItems
import com.markets.deveshecomm.dao.DaoUsers
import com.markets.deveshecomm.models.ModelItems
import com.markets.deveshecomm.models.ModelUsers

@Database(entities = [ModelUsers::class, ModelItems::class], version = 4)
abstract class DatabaseEcomm : RoomDatabase() {
    abstract fun daoUsers(): DaoUsers
    abstract fun daoItems(): DaoItems

    companion object {
        private var instance: DatabaseEcomm? = null
        fun getDatabase(context: Context): DatabaseEcomm {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val newInstance = Room.databaseBuilder(context, DatabaseEcomm::class.java, "ecomm")
                    .fallbackToDestructiveMigration().build()
                instance = newInstance
                return newInstance
            }
        }
    }
}