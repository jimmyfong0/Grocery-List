package com.example.grocerylist.auth

interface AuthListener {
    fun onSuccess(message: String?)
    fun onFailure(message: String)
}