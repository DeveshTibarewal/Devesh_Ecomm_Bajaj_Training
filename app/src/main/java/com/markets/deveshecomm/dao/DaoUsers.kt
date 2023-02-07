package com.markets.deveshecomm.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.markets.deveshecomm.models.ModelUsers

@Dao
interface DaoUsers {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(users: ModelUsers)

    @Query("select * from users")
    fun readUser() : List<ModelUsers>

    @Query("select * from users WHERE email=:email AND password=:password")
    fun authenticateUser(email: String, password: String): LiveData<ModelUsers>

}