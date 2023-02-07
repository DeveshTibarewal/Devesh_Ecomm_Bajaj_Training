package com.markets.deveshecomm.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.markets.deveshecomm.models.ModelItems

@Dao
interface DaoItems {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: ModelItems)

    @Query("select * from items")
    fun readAllItems(): List<ModelItems>

    @Query("select * from items WHERE id = :id ")
    fun readItem(id: Int): ModelItems

    @Query("DELETE FROM items WHERE id = :id")
    fun deleteItem(id: Int)

    @Query("DELETE FROM items")
    fun deleteAllItems()
}