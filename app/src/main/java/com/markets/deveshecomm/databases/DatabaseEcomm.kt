package com.markets.deveshecomm.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.markets.deveshecomm.dao.DaoItems
import com.markets.deveshecomm.dao.DaoUsers
import com.markets.deveshecomm.models.ModelItems
import com.markets.deveshecomm.models.ModelUsers

@Database(entities = [ModelUsers::class, ModelItems::class], version = 3)
abstract class DatabaseEcomm : RoomDatabase(){
    abstract fun daoUsers(): DaoUsers
    abstract fun daoItems(): DaoItems
}