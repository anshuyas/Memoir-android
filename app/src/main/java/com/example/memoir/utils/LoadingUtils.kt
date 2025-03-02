package com.example.memoir.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.memoir.R

class LoadingUtils(private val context: Context) {
    private lateinit var alertDialog: AlertDialog

    fun show() {
        val builder = AlertDialog.Builder(context)
        val designView = LayoutInflater.from(context).inflate(R.layout.loading, null)
        builder.setView(designView)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismiss() {
        if (::alertDialog.isInitialized && alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }
}