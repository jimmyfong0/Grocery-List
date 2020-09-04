package com.example.grocerylist.data.repositories

import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.core.Completable

interface UserRepository {
    fun login(email: String, password: String): Completable

    fun register(email: String, password: String): Completable

    fun currentUser(): FirebaseUser?

    fun logout()
}