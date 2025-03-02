package com.example.memoir.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoir.databinding.FragmentViewEntriesBinding
import com.example.memoir.model.UserModel
import com.example.memoir.ui.fragments.JournalEntryFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewEntriesFragment : Fragment() {

    private var _binding: FragmentViewEntriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: JournalEntryAdapter
    private val journalList = mutableListOf<JournalEntryModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewEntriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
            .child("JournalEntries").child(auth.currentUser!!.uid)

        // Fetch and display user data
        fetchUserData()

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch journal entries
        fetchJournalEntries()
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel = snapshot.getValue(UserModel::class.java)
                if (userModel != null) {
                    // Update the UI with the user's data
                    binding.textViewUserName.text = userModel.userName
                    binding.textViewUserEmail.text = userModel.email
                } else {
                    Log.e(TAG, "User data not found")
                    Toast.makeText(requireContext(), "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch user data: ${error.message}")
                Toast.makeText(requireContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        // Pass the onItemClick lambda to handle item clicks
        adapter = JournalEntryAdapter(journalList) { entry ->
            // Handle item click (e.g., navigate to detail screen)
            navigateToFragment(JournalEntryFragment.newInstance(entry, entry.userId))
        }
        binding.recyclerViewEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewEntries.adapter = adapter
    }

    private fun fetchJournalEntries() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                journalList.clear()
                for (entrySnapshot in snapshot.children) {
                    val entry = entrySnapshot.getValue(JournalEntryModel::class.java)
                    entry?.let {
                        // Set the Firestore document ID (if needed)
                        it.userId = entrySnapshot.key ?: "" // Use the Realtime Database key as the ID
                        journalList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                Log.d(TAG, "Fetched ${journalList.size} journal entries")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch journal entries: ${error.message}")
                Toast.makeText(requireContext(), "Failed to fetch journal entries.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToFragment(fragment: Fragment) {
        // Use the parent activity's fragment manager to replace the current fragment
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment) // Replace the entire fragment
            .addToBackStack(null)  // Enables back navigation
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ViewEntriesFragment"
    }
}