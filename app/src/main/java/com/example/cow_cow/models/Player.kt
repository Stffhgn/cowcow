package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable

data class Player(
    val id: Int,
    var name: String,
    var cowCount: Int = 0,
    var churchCount: Int = 0,
    var waterTowerCount: Int = 0,
    var bonusPoints: Int = 0 // New property for extra points
) : Parcelable {
    val totalScore: Int
        get() = cowCount + (churchCount * 2) + (waterTowerCount * 3) + bonusPoints

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(cowCount)
        parcel.writeInt(churchCount)
        parcel.writeInt(waterTowerCount)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player = Player(parcel)
        override fun newArray(size: Int): Array<Player?> = arrayOfNulls(size)
    }
}
