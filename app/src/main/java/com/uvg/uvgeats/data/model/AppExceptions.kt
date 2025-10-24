package com.uvg.uvgeats.data.model

sealed class AppException(message: String) : Exception(message) {
    class NetworkException(message: String = "Error de conexión") : AppException(message)
    class ServerException(message: String = "Error del servidor") : AppException(message)
    class NotFoundException(message: String = "No se encontraron datos") : AppException(message)
    class UnauthorizedException(message: String = "No autorizado") : AppException(message)
    class UnknownException(message: String = "Error desconocido") : AppException(message)
}