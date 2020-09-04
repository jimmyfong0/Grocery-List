package com.example.grocerylist.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.R
import com.example.grocerylist.databinding.FragmentMainBinding
import com.example.grocerylist.models.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class MainFragment : Fragment(), View.OnClickListener, DIAware, MainListener, CoroutineScope by MainScope() {

    private lateinit var navController: NavController

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: FragmentMainBinding

    private lateinit var viewManager: RecyclerView.LayoutManager
    private var groceryList: List<Item> = listOf()

    override val di: DI by di()
    private val mainViewModelFactory: MainViewModelFactory by instance()

    private lateinit var email: String
    private lateinit var uID: String
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        binding.mainViewModel = viewModel
        viewModel.mainListener = this

        // Check whether list exists or list has any item
        viewModel.noList.observe(viewLifecycleOwner, { noList ->
            if (noList) {
                binding.noItemOrList.text = "You have not created a list yet"
                binding.noItemOrList.visibility = View.VISIBLE
            } else {
                binding.noItemOrList.visibility = View.INVISIBLE
            }
        })

        // Initialise some useful variables
        email = viewModel.user!!.email!!
        uID = viewModel.uID
        db = viewModel.db

        // Testing code for retrieving data
        binding.name.text = viewModel.user!!.email

        // Set layout manager
        viewManager = GridLayoutManager(activity, 2)
        // Get grocery list

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnSignOut.setOnClickListener(this)
        binding.test.setOnClickListener(this)
        binding.makeList.setOnClickListener(this)
        binding.addItem.setOnClickListener(this)
    }

    // Popup window for creating new shopping list
    private fun createShoppingListDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val createListPopupView: View = layoutInflater.inflate(R.layout.create_list_popup, null)
        val submit = createListPopupView.findViewById<TextView>(R.id.submit)
        val cancel = createListPopupView.findViewById<TextView>(R.id.cancel)
        val exit = createListPopupView.findViewById<ImageView>(R.id.exit)

        dialogBuilder.setView(createListPopupView)
        val dialog = dialogBuilder.create()
        dialog.show()

        submit.setOnClickListener {
            val listName = createListPopupView.findViewById<EditText>(R.id.nameInput).text.toString().trim()
            if (listName.isEmpty()) {
                Toast.makeText(context, "Please enter a name for your list", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.checkDuplicateAndSubmit(listName)
            }

            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        exit.setOnClickListener { dialog.dismiss() }

    }

    // Popup window for adding item to list
    private fun addItem() {
        val dialogBuilder = AlertDialog.Builder(context)
        val addItemPopupView: View = layoutInflater.inflate(R.layout.add_item_popup, null)
        val submit = addItemPopupView.findViewById<TextView>(R.id.submit)
        val cancel = addItemPopupView.findViewById<TextView>(R.id.cancel)
        val exit = addItemPopupView.findViewById<ImageView>(R.id.exit)

        dialogBuilder.setView(addItemPopupView)
        val dialog = dialogBuilder.create()
        dialog.show()

        submit.setOnClickListener {
            val itemName = addItemPopupView.findViewById<EditText>(R.id.name).text.toString().trim()
            val value =
                addItemPopupView.findViewById<EditText>(R.id.quantity).text.toString().trim()
            val description =
                addItemPopupView.findViewById<EditText>(R.id.desc).text.toString().trim()

            if (itemName.isEmpty() || value.isEmpty() || description.isEmpty()) {
                Toast.makeText(context, "Please ensure all fields are filled", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val quantity = Integer.parseInt(value)
            val item = Item(itemName, quantity, description)
            viewModel.addItem(item)
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        exit.setOnClickListener { dialog.dismiss() }
    }

    override fun onSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSignOut -> {
                viewModel.logout()
                navController.navigate(R.id.action_mainFragment_to_nested_login_nav)
            }
            R.id.makeList -> createShoppingListDialog()
            R.id.addItem -> addItem()
        }
    }
}