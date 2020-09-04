package com.example.grocerylist.data.implementations

import com.example.grocerylist.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepoImpl(
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
) : UserRepository {

    override fun register(email: String, password: String) = Completable.create { emitter ->
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!emitter.isDisposed) {
                if (task.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(task.exception!!)
                }
            }
        }
    }

    override fun login(email: String, password: String) = Completable.create { emitter ->
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!emitter.isDisposed) {
                if (task.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(task.exception!!)
                }
            }
        }
    }

    override fun currentUser(): FirebaseUser? = auth.currentUser

    override fun logout() = auth.signOut()
}