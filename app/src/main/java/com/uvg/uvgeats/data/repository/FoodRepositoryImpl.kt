package com.uvg.uvgeats.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.uvgeats.data.local.AppDatabase
import com.uvg.uvgeats.data.local.LocalFoodItem
import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FoodRepositoryImpl(
    private val context: Context
) : FoodRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getInstance(context)
    private val foodDao = database.foodDao()

    override suspend fun getFoodItems(): Result<List<FoodItem>> {
        return try {
            // obtener info de Firebase
            val snapshot = firestore.collection("food_items").get().await()
            val firebaseItems = snapshot.documents.map { document ->
                FoodItem(
                    name = document.getString("name") ?: "",
                    brand = document.getString("brand") ?: "",
                    imageRes = android.R.drawable.ic_menu_camera,
                    price = (document.getDouble("price") ?: 0.0).toInt(),
                    location = document.getString("location") ?: ""
                )
            }

            // guarda en Room
            foodDao.insertFoodItems(firebaseItems.map {
                LocalFoodItem(
                    id = it.name,
                    name = it.name,
                    brand = it.brand,
                    price = it.price,
                    location = it.location
                )
            })

            Result.Success(firebaseItems)
        } catch (e: Exception) {
            // fallback a Room
            try {
                val localItems = foodDao.getAllFoodItems()
                    .map { list -> list.map { localItem ->
                        FoodItem(
                            name = localItem.name,
                            brand = localItem.brand,
                            imageRes = android.R.drawable.ic_menu_camera,
                            price = localItem.price,
                            location = localItem.location
                        )
                    }}
                // primer valor de flow
                var result: List<FoodItem> = emptyList()
                localItems.collect { result = it }
                Result.Success(result)
            } catch (localEx: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getFoodItemsFlow(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Success(emptyList()))
    }

    override suspend fun searchFoodItems(query: String): Result<List<FoodItem>> {
        return try {
            val snapshot = firestore.collection("food_items")
                .whereGreaterThanOrEqualTo("name", query)
                .get()
                .await()
            val items = snapshot.documents.map { document ->
                FoodItem(
                    name = document.getString("name") ?: "",
                    brand = document.getString("brand") ?: "",
                    imageRes = android.R.drawable.ic_menu_camera,
                    price = (document.getDouble("price") ?: 0.0).toInt(),
                    location = document.getString("location") ?: ""
                )
            }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Metodos mock
    override suspend fun getFoodItemById(id: String): Result<FoodItem> {
        return Result.Success(FoodItem("Test", "Test", android.R.drawable.ic_menu_camera))
    }

    override suspend fun addToFavorites(foodItem: FoodItem): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun removeFromFavorites(foodItem: FoodItem): Result<Boolean> {
        return Result.Success(true)
    }

    override fun getFavorites(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Success(emptyList()))
    }
}