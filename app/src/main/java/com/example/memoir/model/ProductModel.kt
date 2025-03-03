package com.example.memoir.model

import android.os.Parcel
import android.os.Parcelable

data class ProductModel(
    var productId: String,
    var productName: String = "",
    var price: Double = 0.0,
    val imageRes: Int
) {}