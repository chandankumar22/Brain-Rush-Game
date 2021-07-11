package com.ck.dev.tiptap.helpers

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceHelper {
    private const val USER_SP_NAME = "UserDetails"
    private const val MODE = Context.MODE_PRIVATE
    lateinit var userDetailsPreferences: SharedPreferences

    private val USER_NAME = Pair("userName", null)
    private val PROFILE_PIC = Pair("profilePic", null)
    private val IS_LOGGED_IN = Pair("isLoggedIn", false)
    private val COINS_LEFT = Pair("coins", 0)
    private val CURRENT_USER_RATING = Pair("ratings", 0f)
    private val IS_SOUNDS_ON = Pair("isSoundOn", true)
    private val IS_DARK_MODE = Pair("isDarkMode", true)
    private val IS_FIRST_LAUNCH = Pair("isFirstLaunch",null)

    fun init(context: Context) {
        userDetailsPreferences = context.getSharedPreferences(
            USER_SP_NAME,
            MODE
        )
    }


    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var userName: String?
        get() = userDetailsPreferences.getString(
            USER_NAME.first, USER_NAME.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putString(USER_NAME.first, value)
        }

    var isFirstLaunch: String?
        get() = userDetailsPreferences.getString(
                IS_FIRST_LAUNCH.first, IS_FIRST_LAUNCH.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putString(IS_FIRST_LAUNCH.first, value)
        }

    var profilePic: String?
        get() = userDetailsPreferences.getString(
            PROFILE_PIC.first, PROFILE_PIC.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putString(PROFILE_PIC.first, value)
        }

    var coins: Int
        get() = userDetailsPreferences.getInt(
            COINS_LEFT.first, COINS_LEFT.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putInt(COINS_LEFT.first, value)
        }

    var currentUserRating: Float
        get() = userDetailsPreferences.getFloat(
            CURRENT_USER_RATING.first, CURRENT_USER_RATING.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putFloat(CURRENT_USER_RATING.first, value)
        }


    var isLoggedIn: Boolean
        get() = userDetailsPreferences.getBoolean(
            IS_LOGGED_IN.first, IS_LOGGED_IN.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putBoolean(IS_LOGGED_IN.first, value)
        }

    var isSoundsOn: Boolean
        get() = userDetailsPreferences.getBoolean(
                IS_SOUNDS_ON.first, IS_SOUNDS_ON.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putBoolean(IS_SOUNDS_ON.first, value)
        }

    var isDarkMode: Boolean
        get() = userDetailsPreferences.getBoolean(
                IS_DARK_MODE.first, IS_DARK_MODE.second
        )
        set(value) = userDetailsPreferences.edit {
            it.putBoolean(IS_DARK_MODE.first, value)
        }


    fun deletePreference() {
        userDetailsPreferences.edit {
            it.clear().apply()
        }
    }

}