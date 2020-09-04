package com.example.grocerylist

import android.app.Application
import com.example.grocerylist.auth.AuthViewModelFactory
import com.example.grocerylist.data.implementations.FirestoreUserRepoImpl
import com.example.grocerylist.data.implementations.UserRepoImpl
import com.example.grocerylist.data.repositories.FirestoreUserRepo
import com.example.grocerylist.data.repositories.UserRepository
import com.example.grocerylist.ui.main.MainViewModelFactory
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule

class FirebaseApplication: Application(), DIAware {

    override val di by DI.lazy {
        import(androidXModule(this@FirebaseApplication))

        bind()  from singleton { UserRepoImpl() }
        bind() from singleton { FirestoreUserRepoImpl() }
        bind() from singleton { AuthViewModelFactory(instance(), instance()) }
        bind() from singleton { MainViewModelFactory(instance(), instance()) }
    }
}