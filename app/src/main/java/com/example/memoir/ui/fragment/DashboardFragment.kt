package com.example.memoir.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memoir.R
import com.example.memoir.databinding.FragmentDashboardBinding
import com.example.memoir.model.UserModel
import com.example.memoir.ui.fragments.JournalEntryFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var journalAdapter: JournalEntryAdapter
    private val journalList = mutableListOf<JournalEntryModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView for recent journals
        setupRecyclerView()

        // Set up button click listeners
        binding.newEntryButton.setOnClickListener {
            navigateToFragment(JournalEntryFragment())
        }

        binding.analyticsButton.setOnClickListener {
            navigateToFragment(AnalyticsFragment())
        }

        binding.calendarButton.setOnClickListener {
            navigateToFragment(CalendarFragment())
        }

        fetchRecentJournals()
    }

    private fun setupRecyclerView() {
        journalAdapter = JournalEntryAdapter(journalList) {entry ->
            navigateToFragment(JournalEntryFragment.newInstance(entry, entry.userId))
        }
        binding.recyclerViewJournals.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewJournals.adapter = journalAdapter
    }

    private fun fetchRecentJournals() {
        val userId = auth.currentUser?.uid ?: return
        Log.d(TAG, "Fetching journals for user: $userId")

        firestore.collection("journals")
            .whereEqualTo("userId", userId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5) // Fetch the 5 most recent journals
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Number of documents fetched: ${documents.size()}")
                journalList.clear()
                for (document in documents) {
                    val journalEntry = document.toObject(JournalEntryModel::class.java)
                    journalList.add(journalEntry)
                    Log.d(TAG, "Fetched journal: ${journalEntry.title}")
                }
                journalAdapter.notifyDataSetChanged()
                Log.d(TAG, "Fetched ${journalList.size} recent journals")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch journals: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to fetch journals.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)  // Enables back navigation
            .commit()
    }

    companion object {
        private const val TAG = "DashboardFragment"
    }
}