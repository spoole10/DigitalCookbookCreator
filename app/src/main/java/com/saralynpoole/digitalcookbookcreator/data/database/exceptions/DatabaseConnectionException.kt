package com.saralynpoole.digitalcookbookcreator.data.database.exceptions

/**
 * Custom exception class for database connection errors.
 */
class DatabaseConnectionException(message: String, cause: Throwable? = null) : Exception(message, cause)
