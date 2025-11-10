package com.uvg.uvgeats.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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

    private val firestore = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getInstance(context)
    private val foodDao = database.foodDao()

    override suspend fun getFoodItems(): Result<List<FoodItem>> {
        return try {
            // obteniene datos de Firebase
            Log.d("FoodRepository", "Obteniendo datos de Firebase...")
            val snapshot = firestore
                .collection("/food_items")
                .get()
                .await()


            val firebaseItems = snapshot.documents.mapNotNull { document ->
                try {
                    val priceValue = (document.getLong("price")
                        ?: document.getDouble("price")?.toLong()
                        ?: 0L).toInt()

                    FoodItem(
                        name = document.getString("name") ?: "",
                        brand = document.getString("brand") ?: "",
                        imageRes = getImageResource(document.getString("imageName") ?: "default"),
                        price = priceValue,
                        location = document.getString("location") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("FoodRepository", "Error parsing document: ${e.message}")
                    null
                }
            }

            Log.d("FoodRepository", "Firebase retornó ${firebaseItems.size} items")

            // Guardar datos en cache local (Room)
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
                Log.d("FoodRepository", "Datos guardados en Room cache")
            }

            Result.Success(firebaseItems)

        } catch (firebaseException: Exception) {
            Log.e("FoodRepository", "Error de Firebase: ${firebaseException.message}")

            // Fallback a Room si Firebase falla (prompt)
            try {
                Log.d("FoodRepository", "Intentando con Room cache...")
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
                Log.d("FoodRepository", "Room retornó ${foodItems.size} items")
                Result.Success(foodItems)
            } catch (roomException: Exception) {
                Log.e("FoodRepository", "Error de Room: ${roomException.message}")
                Result.Error(AppException.NetworkException("Sin conexión y sin cache disponible"))
            }
        }
    }

    override fun getFoodItemsFlow(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Loading)

        try {
            val snapshot = firestore
                .collection("food_items")
                .document("IzciUli1DK6d9l7Iiew")
                .collection("food_items")
                .get()
                .await()
            Log.d("FoodRepository", "Snapshot size: ${snapshot.size()}")
            snapshot.documents.forEach { doc ->
                Log.d("FoodRepository", "Doc ID=${doc.id}, data=${doc.data}")
            }

            val firebaseItems = snapshot.documents.mapNotNull { document ->
                val priceValue = (document.getLong("price")
                    ?: document.getDouble("price")?.toLong()
                    ?: 0L).toInt()

                FoodItem(
                    name = document.getString("name") ?: "",
                    brand = document.getString("brand") ?: "",
                    imageRes = getImageResource(document.getString("imageName") ?: "default"),
                    price = priceValue,
                    location = document.getString("location") ?: ""
                )
            }

            foodDao.insertFoodItems(firebaseItems.map {
                LocalFoodItem(
                    id = it.name.hashCode().toString(),
                    name = it.name,
                    brand = it.brand,
                    price = it.price,
                    location = it.location
                )
            })

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
            val snapshot = firestore.collection("food_items")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { document ->
                val priceValue = (document.getLong("price")
                    ?: document.getDouble("price")?.toLong()
                    ?: 0L).toInt()

                FoodItem(
                    name = document.getString("name") ?: "",
                    brand = document.getString("brand") ?: "",
                    imageRes = getImageResource(document.getString("imageName") ?: "default"),
                    price = priceValue,
                    location = document.getString("location") ?: ""
                )
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
