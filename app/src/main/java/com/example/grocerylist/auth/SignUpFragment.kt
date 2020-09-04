package com.example.grocerylist.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.grocerylist.R
import com.example.grocerylist.databinding.FragmentSignUpBinding
import com.example.grocerylist.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class SignUpFragment : Fragment(), DIAware, AuthListener {

    private lateinit var navController: NavController

    private lateinit var viewModel: AuthViewModel

    private lateinit var binding: FragmentSignUpBinding

    override val di: DI by di()
    private val viewModelFactory: AuthViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AuthViewModel::class.java)
        binding.authViewModel = viewModel
        viewModel.authListener = this

        // set onclicklistener
        binding.btnSignUp.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()
            val fullName = binding.fullname.text.toString().trim()
            viewModel.signUp(email, password, confirmPassword, fullName)
        }

        // set up observer relationship for errors
        viewModel.error.observe(viewLifecycleOwner, { error ->
            if (error == "email") {
                binding.email.error = "Please enter email"
                binding.email.requestFocus()
            } else if (error == "password") {
                binding.password.error = "Please enter password"
                binding.password.requestFocus()
            } else if (error == "confirmPassword") {
                binding.confirmPassword.error = "Please enter password again to confirm"
                binding.confirmPassword.requestFocus()
            } else if (error == "passwordNotEqual") {
                binding.confirmPassword.error = "Both passwords entered are not equal"
                binding.confirmPassword.requestFocus()
            } else if (error == "fullName") {
                binding.fullname.error = "Please enter your full name"
                binding.fullname.requestFocus()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onSuccess(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.action_signUpFragment_to_mainFragment)
    }

    override fun onFailure(message: String) {
        Toast.makeText(context, "Error: ${message}", Toast.LENGTH_SHORT).show()
    }
}