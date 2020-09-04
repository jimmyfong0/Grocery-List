package com.example.grocerylist.models

data class User(
    var fullname: String? = "",
    var email: String? = "",
    var belongShoppingListID: String? = ""
)

// belongShoppingList will use the document ID of shopping list user belongs to in order
//  to locate and load shopping list items