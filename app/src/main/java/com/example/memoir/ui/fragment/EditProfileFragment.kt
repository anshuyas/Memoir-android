package com.example.memoir.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.memoir.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            showToast("User not logged in")
            return
        }

        loadUserData(currentUser.uid)

        binding.changeProfilePictureButton.setOnClickListener {
            openImagePicker()
        }

        binding.saveProfileButton.setOnClickListener {
            saveProfileChanges(currentUser.uid)
        }
    }

    private fun loadUserData(userId: String) {
        firestore.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "User data loaded successfully")
                    binding.apply {
                        editTextUsername.setText(document.getString("username"))
                        editTextEmail.setText(document.getString("email"))
                        editTextEmail.isEnabled = false

                        val profileImageUrl = document.getString("profileImageUrl")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Log.d(TAG, "Profile image URL: $profileImageUrl")
                            Glide.with(this@EditProfileFragment)
                                .load(profileImageUrl)
                                .into(binding.profileImageView)
                        }
                    }
                } else {
                    Log.e(TAG, "User data not found")
                    showToast("User data not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to load user data: ${e.message}")
                showToast("Failed to load user data: ${e.message}")
            }
    }

    private val imagePickerLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                binding.profileImageView.setImageURI(it)
            }
        }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun saveProfileChanges(userId: String) {
        val newUsername = binding.editTextUsername.text.toString().trim()
        if (newUsername.isEmpty()) {
            binding.usernameInputLayout.error = "Username cannot be empty"
            return
        }

        val updates = hashMapOf<String, Any>("username" to newUsername)

        imageUri?.let { uri ->
            binding.progressBar.visibility = View.VISIBLE // Show progress bar
            val storageRef = storage.reference.child("profile_pictures/$userId.jpg")
            storageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Image upload failed")
                    }
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->
                    updates["profileImageUrl"] = downloadUri.toString()
                    updateUserData(userId, updates)
                }
                .addOnFailureListener { e ->
                    binding.progressBar.visibility = View.GONE // Hide progress bar
                    showToast("Failed to upload image: ${e.message}")
                }
        } ?: updateUserData(userId, updates)
    }

    private fun updateUserData(userId: String, updates: HashMap<String, Any>) {
        firestore.collection("Users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                showToast("Profile updated successfully")
                binding.progressBar.visibility = View.GONE // Hide progress bar
            }
            .addOnFailureListener { e ->
                showToast("Failed to update profile: ${e.message}")
                binding.progressBar.visibility = View.GONE // Hide progress bar
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "EditProfileFragment"
    }
}