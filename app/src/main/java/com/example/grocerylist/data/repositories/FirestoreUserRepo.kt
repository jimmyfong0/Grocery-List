package com.example.grocerylist.data.repositories

import com.example.grocerylist.models.User

interface FirestoreUserRepo {
    suspend fun getUserDetails(): User

    fun addToDatabase(userID: String ,fullName: String, email: String)
}