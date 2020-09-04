package com.example.grocerylist.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.grocerylist.R
import com.example.grocerylist.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import org.kodein.di.*
import org.kodein.di.android.x.di

class LoginFragment : Fragment(), View.OnClickListener, DIAware, AuthListener {

    private lateinit var navController: NavController

    private lateinit var viewModel: AuthViewModel

    private lateinit var binding: FragmentLoginBinding

    override val di: DI by di()
    private val viewModelFactory: AuthViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Log.i("Main", "start")
        // Check if user is signed in and update UI accordingly
        viewModel.user?.let {
            Log.i("Main", it.uid)
            navController.navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AuthViewModel::class.java)
        binding.authViewModel = viewModel
        viewModel.authListener = this

        // set up observer relationship for errors
        viewModel.error.observe(viewLifecycleOwner, { error ->
            if (error == "email") {
                binding.email.error = "Please enter a valid email"
                binding.email.requestFocus()
            } else if (error == "password") {
                binding.password.error = "Please enter your password"
                binding.password.requestFocus()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnSignIn.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSignIn -> {
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                viewModel.login(email, password)
            }
            R.id.btnSignUp -> navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    override fun onSuccess(message: String?) {
        navController.navigate(R.id.action_loginFragment_to_mainFragment)
    }

    override fun onFailure(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}