package com.example.memoir.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.memoir.databinding.FragmentProfileBinding
import com.example.memoir.model.UserModel
import com.example.memoir.repository.UserRepositoryImpl
import com.example.memoir.ui.activity.LoginActivity
import com.example.memoir.utils.LoadingUtils
import com.example.memoir.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils
    private lateinit var currentUser: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)
        loadingUtils = LoadingUtils(requireContext())

        // Fetch user details
        fetchCurrentUserDetails()

        // Save profile updates
        binding.btnSaveProfile.setOnClickListener {
            updateUserProfile()
        }

        // Delete profile
        binding.btnDeleteProfile.setOnClickListener {
            deleteUserProfile()
        }
    }

    private fun fetchCurrentUserDetails() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            loadingUtils.show()
            userViewModel.getCurrentUserDetails(userId) { userModel ->
                loadingUtils.dismiss()
                if (userModel != null) {
                    currentUser = userModel
                    populateUserDetails(userModel)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateUserDetails(userModel: UserModel) {
        binding.etFirstName.setText(userModel.firstName)
        binding.etLastName.setText(userModel.lastName)
        binding.etEmail.setText(userModel.email)
        binding.etAddress.setText(userModel.address)
        binding.etPhone.setText(userModel.phoneNumber)
    }

    private fun updateUserProfile() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (validateInputs(firstName, lastName, address, phone)) {
            loadingUtils.show()
            val updatedUser = currentUser.copy(
                firstName = firstName,
                lastName = lastName,
                address = address,
                phoneNumber = phone
            )

            userViewModel.updateUserProfile(updatedUser) { success, message ->
                loadingUtils.dismiss()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUserProfile() {
        loadingUtils.show()
        userViewModel.deleteUserProfile { success, message ->
            loadingUtils.dismiss()
            if (success) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(firstName: String, lastName: String, address: String, phone: String): Boolean {
        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!firstName.matches("[a-zA-Z]+".toRegex()) || firstName.length <= 2) {
            Toast.makeText(requireContext(), "First name must be at least 3 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!lastName.matches("[a-zA-Z]+".toRegex())) {
            Toast.makeText(requireContext(), "Last name must contain only letters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!address.contains("[a-zA-Z]".toRegex()) || address.length <= 4) {
            Toast.makeText(requireContext(), "Address must be at least 5 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (phone.length != 10 || !phone.matches("\\d+".toRegex())) {
            Toast.makeText(requireContext(), "Phone number must be 10 digits", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
