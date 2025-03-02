package com.example.memoir.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.memoir.databinding.FragmentJournalEntryBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class JournalEntryFragment : Fragment() {

    private var _binding: FragmentJournalEntryBinding? = null
    private val binding get() = _binding!!

    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Journal entry to edit (if any)
    private var journalEntry: JournalEntryModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if Firebase is initialized
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            Toast.makeText(requireContext(), "Firebase not initialized", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Firebase connected!", Toast.LENGTH_SHORT).show()
        }

        // Set up save button click listener
        binding.saveEntryButton.setOnClickListener {
            val title = binding.entryTitle.text.toString().trim()
            val content = binding.entryContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                saveEntry(title, content)
            }
        }
    }

    private fun saveEntry(title: String, content: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Create or update a journal entry
        val journalEntry = JournalEntryModel(
            userId = userId,
            title = title,
            content = content,
            date = Date().toString() // Use current date as a string
        )

        if (this.journalEntry != null) {
            // Update existing entry
            val entryId = arguments?.getString("entryId") // Get the Firestore document ID
            if (entryId != null) {
                firestore.collection("journals").document(entryId)
                    .set(journalEntry)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Entry updated successfully!", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to update entry: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // Save new entry
            firestore.collection("journals")
                .add(journalEntry)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Entry saved successfully!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to save entry: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun clearFields() {
        binding.entryTitle.text.clear()
        binding.entryContent.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_JOURNAL_ENTRY = "journalEntry"
        private const val ARG_ENTRY_ID = "entryId"

        fun newInstance(): JournalEntryFragment {
            return JournalEntryFragment()
        }

        fun newInstance(journalEntry: JournalEntryModel, entryId: String): JournalEntryFragment {
            val fragment = JournalEntryFragment()
            val args = Bundle().apply {
                putParcelable(ARG_JOURNAL_ENTRY, journalEntry)
                putString(ARG_ENTRY_ID, entryId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}