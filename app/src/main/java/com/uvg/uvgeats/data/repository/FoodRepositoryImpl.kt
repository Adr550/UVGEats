package com.uvg.uvgeats.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.uvg.uvgeats.data.local.AppDatabase
import com.uvg.uvgeats.data.local.LocalFoodItem
import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.data.model.Result
import com.uvg.uvgeats.data.model.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FoodRepositoryImpl(
    private val context: Context
) : FoodRepository {

    private val realtimeDb = FirebaseDatabase.getInstance().getReference("food_items")
    private val database = AppDatabase.getInstance(context)
    private val foodDao = database.foodDao()

    override suspend fun getFoodItems(): Result<List<FoodItem>> {
        return try {
            Log.d("FoodRepository", "Obteniendo datos de Realtime Database...")
            val snapshot = realtimeDb.get().await()
            val firebaseItems = snapshot.children.mapNotNull { item ->
                try {
                    val name = item.child("name").getValue(String::class.java) ?: ""
                    val brand = item.child("brand").getValue(String::class.java) ?: ""
                    val location = item.child("location").getValue(String::class.java) ?: ""
                    val price = item.child("price").getValue(Int::class.java) ?: 0

                    FoodItem(
                        name = name,
                        brand = brand,
                        imageRes = getImageResource(name),
                        price = price,
                        location = location
                    )
                } catch (e: Exception) {
                    Log.e("FoodRepository", "Error parseando item: ${e.message}")
                    null
                }
            }

            Log.d("FoodRepository", "Realtime retornó ${firebaseItems.size} items")

            if (firebaseItems.isNotEmpty()) {
                foodDao.insertFoodItems(firebaseItems.map {
                    LocalFoodItem(
                        id = it.name.hashCode().toString(),
                        name = it.name,
                        brand = it.brand,
                        price = it.price,
                        location = it.location
                    )
                })
            }

            Result.Success(firebaseItems)

        } catch (firebaseException: Exception) {
            Log.e("FoodRepository", "Error de Realtime Database: ${firebaseException.message}")
            try {
                val localItems = foodDao.getAllFoodItems()
                var foodItems = emptyList<FoodItem>()
                localItems.collect { localList ->
                    foodItems = localList.map { localItem ->
                        FoodItem(
                            name = localItem.name,
                            brand = localItem.brand,
                            imageRes = android.R.drawable.ic_menu_camera,
                            price = localItem.price,
                            location = localItem.location
                        )
                    }
                }
                Result.Success(foodItems)
            } catch (roomException: Exception) {
                Result.Error(AppException.NetworkException("Sin conexión y sin cache disponible"))
            }
        }
    }

    override fun getFoodItemsFlow(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Loading)
        try {
            val snapshot = realtimeDb.get().await()
            val firebaseItems = snapshot.children.mapNotNull { item ->
                try {
                    val name = item.child("name").getValue(String::class.java) ?: ""
                    val brand = item.child("brand").getValue(String::class.java) ?: ""
                    val location = item.child("location").getValue(String::class.java) ?: ""
                    val price = item.child("price").getValue(Int::class.java) ?: 0

                    FoodItem(
                        name = name,
                        brand = brand,
                        imageRes = getImageResource(name),
                        price = price,
                        location = location
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (firebaseItems.isNotEmpty()) {
                foodDao.insertFoodItems(firebaseItems.map {
                    LocalFoodItem(
                        id = it.name.hashCode().toString(),
                        name = it.name,
                        brand = it.brand,
                        price = it.price,
                        location = it.location
                    )
                })
            }

            emit(Result.Success(firebaseItems))
        } catch (e: Exception) {
            try {
                val localItems = foodDao.getAllFoodItems()
                localItems.collect { localList ->
                    val foodItems = localList.map { localItem ->
                        FoodItem(
                            name = localItem.name,
                            brand = localItem.brand,
                            imageRes = android.R.drawable.ic_menu_camera,
                            price = localItem.price,
                            location = localItem.location
                        )
                    }
                    emit(Result.Success(foodItems))
                }
            } catch (localEx: Exception) {
                emit(Result.Error(AppException.NetworkException("Error de conexión")))
            }
        }
    }

    override suspend fun searchFoodItems(query: String): Result<List<FoodItem>> {
        return try {
            val snapshot = realtimeDb.get().await()
            val items = snapshot.children.mapNotNull { item ->
                val name = item.child("name").getValue(String::class.java) ?: ""
                val brand = item.child("brand").getValue(String::class.java) ?: ""
                val location = item.child("location").getValue(String::class.java) ?: ""
                val price = item.child("price").getValue(Int::class.java) ?: 0

                if (name.contains(query, ignoreCase = true) || brand.contains(query, ignoreCase = true)) {
                    FoodItem(
                        name = name,
                        brand = brand,
                        imageRes = getImageResource(name),
                        price = price,
                        location = location
                    )
                } else null
            }
            Result.Success(items)
        } catch (e: Exception) {
            try {
                val localItems = foodDao.getAllFoodItems()
                var filteredItems = emptyList<LocalFoodItem>()
                localItems.collect { localList ->
                    filteredItems = localList.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.brand.contains(query, ignoreCase = true)
                    }
                }
                val foodItems = filteredItems.map { localItem ->
                    FoodItem(
                        name = localItem.name,
                        brand = localItem.brand,
                        imageRes = android.R.drawable.ic_menu_camera,
                        price = localItem.price,
                        location = localItem.location
                    )
                }
                Result.Success(foodItems)
            } catch (localEx: Exception) {
                Result.Error(e)
            }
        }
    }

    private fun getImageResource(imageName: String): Int {
        return when (imageName.lowercase()) {
            "hamburguesa", "burger" -> android.R.drawable.ic_menu_camera
            "pizza" -> android.R.drawable.ic_menu_gallery
            "sushi" -> android.R.drawable.ic_menu_slideshow
            "crepa", "crepe" -> android.R.drawable.ic_menu_edit
            "camarones", "shrimp" -> android.R.drawable.ic_menu_report_image
            else -> android.R.drawable.ic_menu_camera
        }
    }

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
