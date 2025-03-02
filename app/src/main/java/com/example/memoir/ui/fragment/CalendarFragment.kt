package com.example.memoir.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoir.databinding.FragmentCalendarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var journalAdapter: JournalEntryAdapter
    private val journalList = mutableListOf<JournalEntryModel>()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        setupRecyclerView()

        // Set up calendar date selection listener
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            fetchJournalEntriesForDate(selectedDate)
        }
    }

    private fun setupRecyclerView() {
        journalAdapter = JournalEntryAdapter(journalList) { entry ->
            // Handle journal entry click (optional)
            Toast.makeText(requireContext(), "Clicked: ${entry.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewJournals.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewJournals.adapter = journalAdapter
    }

    private fun fetchJournalEntriesForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return
        Log.d(TAG, "Fetching journal entries for date: $date")

        // Show ProgressBar
        binding.progressBar.visibility = View.VISIBLE

        firestore.collection("journals")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
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

                // Hide ProgressBar
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch journal entries: ${e.message}", e)

                // Hide ProgressBar
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val date = calendar.time
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CalendarFragment"
    }
}