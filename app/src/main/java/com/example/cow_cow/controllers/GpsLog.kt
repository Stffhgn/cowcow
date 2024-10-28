package com.example.cow_cow.controllers

import android.os.Parcel
import android.os.Parcelable

data class GpsLog(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GpsLog> {
        override fun createFromParcel(parcel: Parcel): GpsLog = GpsLog(parcel)
        override fun newArray(size: Int): Array<GpsLog?> = arrayOfNulls(size)
    }
}