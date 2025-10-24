package com.uvg.uvgeats.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para un ítem de comida
 *
 * @property name Nombre del platillo
 * @property brand Marca o restaurante que lo ofrece
 * @property imageRes ID del recurso de imagen
 * @property price Precio en quetzales
 * @property location Ubicación donde se vende
 */
@Parcelize
data class FoodItem(
    val name: String,
    val brand: String,
    val imageRes: Int,
    val price: Int = 30,
    val location: String = "Cafetería CIT"
) : Parcelable