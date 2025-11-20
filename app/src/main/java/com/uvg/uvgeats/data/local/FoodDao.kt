package com.uvg.uvgeats.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query("SELECT * FROM food_items")
    fun getAllFoodItems(): Flow<List<LocalFoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<LocalFoodItem>)

    @Query("UPDATE food_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM food_items WHERE isFavorite = 1")
    fun getFavoriteItems(): Flow<List<LocalFoodItem>>
}