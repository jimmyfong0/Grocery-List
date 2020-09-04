package com.example.grocerylist.data.implementations

import com.example.grocerylist.auth.AuthListener
import com.example.grocerylist.data.repositories.FirestoreUserRepo
import com.example.grocerylist.models.User
import com.example.grocerylist.ui.main.USER_COLLECTION_NAME
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreUserRepoImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = Firebase.firestore
): FirestoreUserRepo {

    var authListener: AuthListener? = null
    private val uID = auth.uid!!

    override suspend fun getUserDetails(): User {
        return withContext(Dispatchers.IO) {
            val result = Tasks.await(db.collection(USER_COLLECTION_NAME).document(uID).get())
            result.toObject(User::class.java)!!
        }
    }

    override fun addToDatabase(userID: String ,fullName: String, email: String) {
        val _user = User(fullName, email)

        db.collection("users").document(userID).set(_user)
            .addOnSuccessListener { authListener?.onSuccess("Sign up success") }
            .addOnFailureListener { authListener?.onFailure("Error uploading user details: ${it}")}
    }

}