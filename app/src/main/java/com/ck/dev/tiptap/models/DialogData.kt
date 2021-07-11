package com.ck.dev.tiptap.models

import android.os.Parcel
import android.os.Parcelable

data class DialogData(
    val title: String,
    val content: String,
    val posBtnText: String,
    val negBtnText: String,
    val posListener: () -> Unit,
    val megListener: () -> Unit,
    val extraCoinsText:String = "",
    val coinsToTake:Int = 0,
    val extraCoinsListener: () -> Unit = {},
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        {},
        {}
    )


    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    override fun describeContents(): Int {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR
    }

    companion object CREATOR : Parcelable.Creator<DialogData> {
        override fun createFromParcel(parcel: Parcel): DialogData {
            return DialogData(parcel)
        }

        override fun newArray(size: Int): Array<DialogData?> {
            return arrayOfNulls(size)
        }
    }
}