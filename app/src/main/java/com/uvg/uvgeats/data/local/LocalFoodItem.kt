package com.uvg.uvgeats.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class LocalFoodItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String,
    val price: Int,
    val location: String,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)