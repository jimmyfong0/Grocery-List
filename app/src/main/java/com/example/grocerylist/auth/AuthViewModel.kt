package com.example.grocerylist.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerylist.data.implementations.UserRepoImpl
import com.example.grocerylist.data.repositories.FirestoreUserRepo
import com.example.grocerylist.data.repositories.UserRepository
import com.example.grocerylist.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class AuthViewModel(
    private val repo: UserRepository,
    private val firestoreRepo: FirestoreUserRepo
): ViewModel() {

    // get user and respective ID
    val user by lazy { repo.currentUser() }
    val uID = user?.uid
    // get firestore database
    val db = Firebase.firestore

    // auth listener
    var authListener: AuthListener? = null

    // Error handler when login details are missing
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    // disposable to dispose the Completable
    private val disposables = CompositeDisposable()

    /**
    * Login logic as well as checking user input before attempting to login through auth
     */
    fun login(email: String, password: String) {

        checkLoginInput(email, password)

        val disposable = repo.login(email, password)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                authListener?.onSuccess(null)
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    // Validate login details
    private fun checkLoginInput(email: String, password: String) {
        if (email.isEmpty()) {
            _error.value = "email"
            return
        } else if (password.isEmpty()) {
            _error.value = "password"
            return
        }
    }

    /**
     * Sign up logic together with checking user input to ensure registration success
     */
    fun signUp(email: String, password: String, confirmPassword: String, fullName: String) {

        checkSignupInput(email, password, confirmPassword, fullName)

        val disposable = repo.register(email, password)
            .subscribeOn(Schedulers.io())
            .subscribe ({
                // Have to get another user instance since at the start user is null (only for signup)
                val _user = repo.currentUser()
                val _uID = _user!!.uid
                _user.sendEmailVerification().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firestoreRepo.addToDatabase(_uID, fullName, email)
                    } else {
                        authListener?.onFailure("Error caused by ${task.exception}")
                    }
                }
            }, {
                authListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    // Validate sign up details
    private fun checkSignupInput(email: String, password: String, confirmPassword: String, fullName: String) {

        if (email.isEmpty()) {
            _error.value = "email"
            return
        } else if (password.isEmpty()) {
            _error.value = "password"
            return
        } else if (confirmPassword.isEmpty()) {
            _error.value = "confirmPassword"
            return
        } else if (password != confirmPassword) {
            _error.value = "passwordNotEqual"
            return
        } else if (fullName.isEmpty()) {
            _error.value = "fullName"
            return
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}