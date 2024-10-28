package com.example.cow_cow.controllers

import android.os.Parcel
import android.os.Parcelable

data class ItemCallDetail(
    val itemName: String,
    val timestamp: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemName)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ItemCallDetail> {
        override fun createFromParcel(parcel: Parcel): ItemCallDetail = ItemCallDetail(parcel)
        override fun newArray(size: Int): Array<ItemCallDetail?> = arrayOfNulls(size)
    }
}