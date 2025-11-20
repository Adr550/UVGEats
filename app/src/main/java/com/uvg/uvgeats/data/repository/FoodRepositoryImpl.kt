package com.uvg.uvgeats.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uvg.uvgeats.data.local.AppDatabase
import com.uvg.uvgeats.data.local.LocalFoodItem
import com.uvg.uvgeats.data.model.AppException
import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FoodRepositoryImpl(
    private val context: Context
) : FoodRepository {

    private val realtimeDb = FirebaseDatabase.getInstance().getReference("food_items")
    private val favoritesDb = FirebaseDatabase.getInstance().getReference("user_favorites")
    private val auth = FirebaseAuth.getInstance()

    private val database = AppDatabase.getInstance(context)
    private val foodDao = database.foodDao()


    private fun generateId(name: String, brand: String): String =
        "${brand}_${name}".hashCode().toString()


    override suspend fun getFoodItems(): Result<List<FoodItem>> {
        return try {
            Log.d("FoodRepository", "Obteniendo datos de Realtime Database...")

            val uid = auth.currentUser?.uid
            val favoritesIds = if (uid != null) {
                val favSnapshot = favoritesDb.child(uid).get().await()
                favSnapshot.children.mapNotNull { it.key }.toSet()
            } else emptySet()

            val snapshot = realtimeDb.get().await()
            val firebaseItems = snapshot.children.mapNotNull { item ->
                try {
                    val name = item.child("name").getValue(String::class.java) ?: ""
                    val brand = item.child("brand").getValue(String::class.java) ?: ""
                    val location = item.child("location").getValue(String::class.java) ?: ""
                    val price = item.child("price").getValue(Int::class.java) ?: 0
                    val imageUrl = item.child("image").getValue(String::class.java)
                        ?: item.child("imageUrl").getValue(String::class.java)

                    val id = generateId(name, brand)
                    val isFavorite = favoritesIds.contains(id)

                    FoodItem(
                        name = name,
                        brand = brand,
                        price = price,
                        location = location,
                        imageUrl = imageUrl,
                        isFavorite = isFavorite
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
                        id = generateId(it.name, it.brand),
                        name = it.name,
                        brand = it.brand,
                        price = it.price,
                        location = it.location,
                        imageUrl = it.imageUrl,
                        isFavorite = it.isFavorite
                    )
                })
            }

            Result.Success(firebaseItems)

        } catch (firebaseException: Exception) {
            Log.e("FoodRepository", "Error de Realtime Database: ${firebaseException.message}")
            try {
                val localItemsFlow = foodDao.getAllFoodItems()
                var foodItems = emptyList<FoodItem>()
                localItemsFlow.collect { localList ->
                    foodItems = localList.map { localItem ->
                        FoodItem(
                            name = localItem.name,
                            brand = localItem.brand,
                            price = localItem.price,
                            location = localItem.location,
                            imageUrl = localItem.imageUrl,
                            isFavorite = localItem.isFavorite
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
            val uid = auth.currentUser?.uid
            val favoritesIds = if (uid != null) {
                val favSnapshot = favoritesDb.child(uid).get().await()
                favSnapshot.children.mapNotNull { it.key }.toSet()
            } else emptySet()

            val snapshot = realtimeDb.get().await()
            val firebaseItems = snapshot.children.mapNotNull { item ->
                try {
                    val name = item.child("name").getValue(String::class.java) ?: ""
                    val brand = item.child("brand").getValue(String::class.java) ?: ""
                    val location = item.child("location").getValue(String::class.java) ?: ""
                    val price = item.child("price").getValue(Int::class.java) ?: 0
                    val imageUrl = item.child("image").getValue(String::class.java)
                        ?: item.child("imageUrl").getValue(String::class.java)

                    val id = generateId(name, brand)
                    val isFavorite = favoritesIds.contains(id)

                    FoodItem(
                        name = name,
                        brand = brand,
                        price = price,
                        location = location,
                        imageUrl = imageUrl,
                        isFavorite = isFavorite
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (firebaseItems.isNotEmpty()) {
                foodDao.insertFoodItems(firebaseItems.map {
                    LocalFoodItem(
                        id = generateId(it.name, it.brand),
                        name = it.name,
                        brand = it.brand,
                        price = it.price,
                        location = it.location,
                        imageUrl = it.imageUrl,
                        isFavorite = it.isFavorite
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
                            price = localItem.price,
                            location = localItem.location,
                            imageUrl = localItem.imageUrl,
                            isFavorite = localItem.isFavorite
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
                val imageUrl = item.child("image").getValue(String::class.java)
                    ?: item.child("imageUrl").getValue(String::class.java)

                if (name.contains(query, ignoreCase = true) ||
                    brand.contains(query, ignoreCase = true)
                ) {
                    FoodItem(
                        name = name,
                        brand = brand,
                        price = price,
                        location = location,
                        imageUrl = imageUrl
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
                        price = localItem.price,
                        location = localItem.location,
                        imageUrl = localItem.imageUrl,
                        isFavorite = localItem.isFavorite
                    )
                }
                Result.Success(foodItems)
            } catch (localEx: Exception) {
                Result.Error(e)
            }
        }
    }


    override suspend fun getFoodItemById(id: String): Result<FoodItem> {
        return Result.Error(AppException.UnknownException("getFoodItemById no implementado"))
    }


    override suspend fun addToFavorites(foodItem: FoodItem): Result<Boolean> {
        val uid = auth.currentUser?.uid
            ?: return Result.Error(AppException.UnauthorizedException("Usuario no autenticado"))

        return try {
            val id = generateId(foodItem.name, foodItem.brand)
            val favRef = favoritesDb.child(uid).child(id)

            val data = mapOf(
                "name" to foodItem.name,
                "brand" to foodItem.brand,
                "location" to foodItem.location,
                "price" to foodItem.price,
                "image" to foodItem.imageUrl
            )

            favRef.setValue(data).await()
            // Actualizamos cache local
            foodDao.updateFavorite(id, true)

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeFromFavorites(foodItem: FoodItem): Result<Boolean> {
        val uid = auth.currentUser?.uid
            ?: return Result.Error(AppException.UnauthorizedException("Usuario no autenticado"))

        return try {
            val id = generateId(foodItem.name, foodItem.brand)
            favoritesDb.child(uid).child(id).removeValue().await()
            foodDao.updateFavorite(id, false)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getFavorites(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Loading)

        val uid = auth.currentUser?.uid
        if (uid == null) {
            emit(Result.Success(emptyList()))
            return@flow
        }

        try {
            val snapshot = favoritesDb.child(uid).get().await()
            val favorites = snapshot.children.mapNotNull { item ->
                try {
                    val name = item.child("name").getValue(String::class.java) ?: ""
                    val brand = item.child("brand").getValue(String::class.java) ?: ""
                    val location = item.child("location").getValue(String::class.java) ?: ""
                    val price = item.child("price").getValue(Int::class.java) ?: 0
                    val imageUrl = item.child("image").getValue(String::class.java)
                        ?: item.child("imageUrl").getValue(String::class.java)

                    FoodItem(
                        name = name,
                        brand = brand,
                        price = price,
                        location = location,
                        imageUrl = imageUrl,
                        isFavorite = true
                    )
                } catch (e: Exception) {
                    null
                }
            }

            emit(Result.Success(favorites))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
