package com.example.memoir.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CartModel(
    var cartId: String = "",
    var userId: String = "",
    var productId: String = "",
    var quantity: Int = 1,
    var price: Double = 0.0,
    var stability: Int = 1 // Changed to Int to match Firebase data
) : Parcelable {
    constructor() : this("", "", "", 1, 0.0, 1)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt() // Read as Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cartId)
        parcel.writeString(userId)
        parcel.writeString(productId)
        parcel.writeInt(quantity)
        parcel.writeDouble(price)
        parcel.writeInt(stability) // Write as Int
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartModel> {
        override fun createFromParcel(parcel: Parcel): CartModel {
            return CartModel(parcel)
        }

        override fun newArray(size: Int): Array<CartModel?> {
            return arrayOfNulls(size)
        }
    }
}