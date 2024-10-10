package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import com.example.cow_cow.enums.RuleConditionType
import com.example.cow_cow.enums.RuleEffectType

data class CustomRule(
    val ruleId: String,                      // Unique ID for the custom rule
    val playerId: String?,                   // Player ID this rule belongs to (optional for global rules)
    val ruleName: String,                    // Rule name (e.g., "Double Points for Low Score")
    val ruleDescription: String,             // Description of the rule effect and condition
    val ruleEffect: RuleEffectType,          // The effect the rule has (e.g., add points, deduct points, etc.)
    val value: Int = 0,                      // Value associated with the effect (e.g., points to add/deduct)
    val duration: Long = 0L,                 // Duration of the effect, in milliseconds (for time-based effects)
    val conditionType: RuleConditionType,    // The condition that triggers the rule (e.g., player has less than X points)
    val conditionValue: Int = 0,             // Value related to the condition (e.g., X points in "less than X points")
    val isGlobal: Boolean = false,           // Whether this rule applies globally to all players or to a specific player
    val isOneTimeUse: Boolean = false,       // Whether this rule can only be used once in the game
    var isActive: Boolean = true             // Whether the rule is currently active
) : Parcelable {

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        ruleId = parcel.readString() ?: "",
        playerId = parcel.readString(),
        ruleName = parcel.readString() ?: "",
        ruleDescription = parcel.readString() ?: "",
        ruleEffect = RuleEffectType.valueOf(parcel.readString() ?: RuleEffectType.ADD_POINTS.name),
        value = parcel.readInt(),
        duration = parcel.readLong(),
        conditionType = RuleConditionType.valueOf(parcel.readString() ?: RuleConditionType.ALWAYS.name),
        conditionValue = parcel.readInt(),
        isGlobal = parcel.readByte() != 0.toByte(),
        isOneTimeUse = parcel.readByte() != 0.toByte(),
        isActive = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ruleId)
        parcel.writeString(playerId)
        parcel.writeString(ruleName)
        parcel.writeString(ruleDescription)
        parcel.writeString(ruleEffect.name)
        parcel.writeInt(value)
        parcel.writeLong(duration)
        parcel.writeString(conditionType.name)
        parcel.writeInt(conditionValue)
        parcel.writeByte(if (isGlobal) 1 else 0)
        parcel.writeByte(if (isOneTimeUse) 1 else 0)
        parcel.writeByte(if (isActive) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CustomRule> {
        override fun createFromParcel(parcel: Parcel): CustomRule = CustomRule(parcel)
        override fun newArray(size: Int): Array<CustomRule?> = arrayOfNulls(size)
    }
}
