package com.example.grocerylist.models

data class sharedShoppingList(
    var listName: String? = "",
    var admin: String? = "",
    var members: List<String>? = listOf(),
    var items: List<Item>? = listOf()
)