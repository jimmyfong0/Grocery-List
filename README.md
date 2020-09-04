Outline of program:

User model consists of:
    -> username 
    -> fullname
    -> email
    -> belongShoppingList = ID of the shopping list this user is involved in. To simplify things,
                            each user can only belong to one list at a time.
    
sharedShoppingList model consists of:
Document ID in Firestore (This will be the ID that member users will use to access the list)
    -> listName = For display purposes
    -> admin = Equal to document ID which in turn is UID of user who created list
    -> members = A list of members belonging to this list, written using each member's uID
    -> items = A list of Item that will be displayed using RecyclerView
    
Future Improvements:
    -> Prevent creation of multiple list by adding function to check database whether current
       user is already admin of a shopping list and make toast text
       
Some useful stuff:
// Test function for learning
    private fun testAddData() {
        val ref = db.collection("sharedShoppingList")
        val query = ref.whereEqualTo("admin", "NQoVLb5uwVetoBYcKZbNWlvThJb2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.i("Main", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Main", "Error getting documents: $exception")
            }
    }
    
add "Add Item" button that only appears if user has a list

refresh list when item added# Grocery-List
