package com.uvg.uvgeats.data.repository

import com.uvg.uvgeats.data.model.AppException
import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.data.model.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeFoodRepository(
    private val shouldSimulateError: Boolean = false,
    private val errorType: ErrorType = ErrorType.NETWORK
) : FoodRepository {

    enum class ErrorType {
        NETWORK,
        SERVER,
        NOT_FOUND,
        UNAUTHORIZED,
        UNKNOWN
    }

    private val favorites = mutableSetOf<FoodItem>()

    private val mockFoodList = listOf(
        FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera, 30, "Cafetería CIT"),
        FoodItem("Crepa", "Saúl", android.R.drawable.ic_menu_gallery, 25, "Cafetería CIT"),
        FoodItem("Camarones", "Gitane", android.R.drawable.ic_menu_report_image, 45, "Cafetería CIT"),
        FoodItem("Lays", "Gitane", android.R.drawable.ic_menu_slideshow, 15, "Máquina expendedora"),
        FoodItem("Pizza", "Gitane", android.R.drawable.ic_menu_gallery, 35, "Cafetería CIT"),
        FoodItem("Tacos", "Gitane", android.R.drawable.ic_menu_camera, 28, "Cafetería CIT"),
        FoodItem("Ensalada", "Gitane", android.R.drawable.ic_menu_report_image, 22, "Cafetería CIT"),
        FoodItem("Sushi", "Gitane", android.R.drawable.ic_menu_slideshow, 40, "Cafetería CIT"),
    )

    override suspend fun getFoodItems(): Result<List<FoodItem>> {
        return try {
            delay(1500) // Simular llamada de red

            if (shouldSimulateError && Random.nextFloat() > 0.7f) {
                throw getExceptionForType()
            }

            Result.Success(mockFoodList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getFoodItemsFlow(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Loading)

        delay(1000) // Simular latencia inicial

        if (shouldSimulateError && Random.nextFloat() > 0.6f) {
            emit(Result.Error(getExceptionForType()))
            return@flow
        }

        // Emitir datos progresivamente
        val chunks = mockFoodList.chunked(3)
        chunks.forEachIndexed { index, chunk ->
            delay(500) // Simular carga progresiva

            if (shouldSimulateError && index == 1 && Random.nextFloat() > 0.8f) {
                emit(Result.Error(AppException.NetworkException("Conexión perdida durante la carga")))
                return@flow
            }

            emit(Result.Success(mockFoodList.take((index + 1) * 3)))
        }
    }

    override suspend fun getFoodItemById(id: String): Result<FoodItem> {
        return try {
            delay(800)

            if (shouldSimulateError && Random.nextFloat() > 0.7f) {
                throw getExceptionForType()
            }

            val item = mockFoodList.firstOrNull { it.name == id }

            if (item != null) {
                Result.Success(item)
            } else {
                Result.Error(AppException.NotFoundException("Producto no encontrado"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchFoodItems(query: String): Result<List<FoodItem>> {
        return try {
            delay(600)

            if (shouldSimulateError && query.length > 5 && Random.nextFloat() > 0.8f) {
                throw AppException.NetworkException("Tiempo de espera agotado")
            }

            val filtered = mockFoodList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.brand.contains(query, ignoreCase = true)
            }

            Result.Success(filtered)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addToFavorites(foodItem: FoodItem): Result<Boolean> {
        return try {
            delay(300)

            if (shouldSimulateError && Random.nextFloat() > 0.85f) {
                throw AppException.ServerException("Error al guardar favorito")
            }

            favorites.add(foodItem)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeFromFavorites(foodItem: FoodItem): Result<Boolean> {
        return try {
            delay(300)

            if (shouldSimulateError && Random.nextFloat() > 0.9f) {
                throw AppException.ServerException("Error al eliminar favorito")
            }

            favorites.remove(foodItem)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getFavorites(): Flow<Result<List<FoodItem>>> = flow {
        emit(Result.Loading)
        delay(500)

        if (shouldSimulateError && Random.nextFloat() > 0.8f) {
            emit(Result.Error(AppException.NetworkException("Error al cargar favoritos")))
            return@flow
        }

        emit(Result.Success(favorites.toList()))
    }

    private fun getExceptionForType(): Exception {
        return when (errorType) {
            ErrorType.NETWORK -> AppException.NetworkException()
            ErrorType.SERVER -> AppException.ServerException()
            ErrorType.NOT_FOUND -> AppException.NotFoundException()
            ErrorType.UNAUTHORIZED -> AppException.UnauthorizedException()
            ErrorType.UNKNOWN -> AppException.UnknownException()
        }
    }
}