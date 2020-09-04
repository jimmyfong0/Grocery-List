package com.example.grocerylist.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerylist.data.repositories.FirestoreUserRepo
import com.example.grocerylist.data.repositories.UserRepository
import com.example.grocerylist.models.Item
import com.example.grocerylist.models.User
import com.example.grocerylist.models.sharedShoppingList
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

const val USER_COLLECTION_NAME = "users"
private const val LIST_COLLECTION_NAME = "sharedShoppingList"
private const val LIST_FIELD_NAME = "belongShoppingListID"
private const val FULLNAME_FIELD_NAME = "fullname"

class MainViewModel(
    private val repo: UserRepository,
    private val firestoreRepo: FirestoreUserRepo
) : ViewModel() {

    // get currentUser and respective ID
    val user by lazy { repo.currentUser() }
    val uID = user!!.uid

    // get firestore instance
    val db = Firebase.firestore

    /**
     * User profile model and assosciated variables
     */
    private lateinit var firestoreUser: User
    private lateinit var listID: String
    private lateinit var fullName: String

    // initialise listener
    var mainListener: MainListener? = null

    // Variable for checking if list exist
    private val _noList = MutableLiveData<Boolean>(false)
    val noList: LiveData<Boolean>
        get() = _noList

    init {
        viewModelScope.launch {
            firestoreUser = firestoreRepo.getUserDetails()
            setUserDetails()
            checkForList()
        }
    }

    // Submit list to firestore
    private fun submitList(shoppingList: sharedShoppingList) {
        db.collection(LIST_COLLECTION_NAME).document(uID).set(shoppingList)
            .addOnSuccessListener {
                db.collection(USER_COLLECTION_NAME).document(uID).update(LIST_FIELD_NAME, uID)
                mainListener?.onSuccess("List successfully created")
            }
            .addOnFailureListener { e -> mainListener?.onFailure("Error uploading list: ${e}") }
    }

    // Get user full name from database and do submitList()
    private fun getDetails(listName: String) {
        val listModel = sharedShoppingList(listName, fullName)
        submitList(listModel)
        _noList.value = false
    }

    // Check whether user has already created a list
    fun checkDuplicateAndSubmit(listName: String) {
        if (listID == uID) {
            mainListener?.onFailure("You have already created a list.")
        } else {
            getDetails(listName)
        }
    }

    // Check whether user belongs to or has created a list
    private fun checkForList() {
        if (listID.isEmpty()) {
            _noList.value = true
        }
    }

    private fun setUserDetails() {
        listID = firestoreUser.belongShoppingListID!!
        fullName = firestoreUser.fullname!!
    }

    fun addItem(item: Item) {
        db.collection(LIST_COLLECTION_NAME).document(listID)
            .update("items", FieldValue.arrayUnion(item))
        mainListener?.onSuccess("Item added to list")
    }

    fun logout() = repo.logout()
}