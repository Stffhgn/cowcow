package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable

enum class RuleConditionType {
    ALWAYS,
    PLAYER_HAS_LESS_THAN_X_POINTS,
    PLAYER_HAS_MORE_THAN_X_POINTS,
    // Add other condition types as needed
}
enum class RuleEffectType {
    ADD_POINTS,
    DEDUCT_POINTS,
    SILENCE_PLAYER,
    CUSTOM_PENALTY
}

data class CustomRule(
    val ruleId: Int,
    val name: String,
    val description: String,
    val effect: RuleEffectType,
    val value: Int = 0,
    val duration: Long = 0L,
    val conditionType: RuleConditionType,
    val conditionValue: Int = 0  // Additional value needed for the condition
) : Parcelable {

    constructor(parcel: Parcel) : this(
        ruleId = parcel.readInt(),
        name = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        effect = RuleEffectType.valueOf(parcel.readString() ?: RuleEffectType.ADD_POINTS.name),
        value = parcel.readInt(),
        duration = parcel.readLong(),
        conditionType = RuleConditionType.valueOf(parcel.readString() ?: RuleConditionType.ALWAYS.name),
        conditionValue = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ruleId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(effect.name)
        parcel.writeInt(value)
        parcel.writeLong(duration)
        parcel.writeString(conditionType.name)
        parcel.writeInt(conditionValue)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CustomRule> {
        override fun createFromParcel(parcel: Parcel): CustomRule = CustomRule(parcel)
        override fun newArray(size: Int): Array<CustomRule?> = arrayOfNulls(size)
    }
}
