package com.example.grocerylist.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grocerylist.data.repositories.FirestoreUserRepo
import com.example.grocerylist.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repo: UserRepository,
    private val firestoreRepo: FirestoreUserRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repo, firestoreRepo) as T
    }
}