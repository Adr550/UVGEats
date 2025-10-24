package com.uvg.uvgeats.data.repository

import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.data.model.Result
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun getFoodItems(): Result<List<FoodItem>>
    fun getFoodItemsFlow(): Flow<Result<List<FoodItem>>>
    suspend fun getFoodItemById(id: String): Result<FoodItem>
    suspend fun searchFoodItems(query: String): Result<List<FoodItem>>
    suspend fun addToFavorites(foodItem: FoodItem): Result<Boolean>
    suspend fun removeFromFavorites(foodItem: FoodItem): Result<Boolean>
    fun getFavorites(): Flow<Result<List<FoodItem>>>
}