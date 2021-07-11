package com.ck.dev.tiptap.models

import android.os.Parcel
import android.os.Parcelable

data class UnJumbledWord(
    val endPos: String,
    val startPos: String,
    val word: String
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(endPos)
        parcel.writeString(startPos)
        parcel.writeString(word)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UnJumbledWord> {
        override fun createFromParcel(parcel: Parcel): UnJumbledWord {
            return UnJumbledWord(parcel)
        }

        override fun newArray(size: Int): Array<UnJumbledWord?> {
            return arrayOfNulls(size)
        }
    }
}