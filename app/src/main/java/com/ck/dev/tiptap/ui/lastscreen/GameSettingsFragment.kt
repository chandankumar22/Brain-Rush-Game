package com.ck.dev.tiptap.ui.lastscreen

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.adapters.MainScreenAvtarAdapter
import com.ck.dev.tiptap.extensions.changeStatusBarColor
import com.ck.dev.tiptap.extensions.fetchDrawable
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import com.ck.dev.tiptap.models.RememberTheCardData
import com.ck.dev.tiptap.ui.GameApp
import com.ck.dev.tiptap.ui.custom.CustomButtonView
import com.ck.dev.tiptap.ui.custom.OnItemClick
import com.ck.dev.tiptap.ui.games.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_game_settings.*
import timber.log.Timber
import java.io.ByteArrayOutputStream

class GameSettingsFragment : BaseFragment(R.layout.fragment_game_settings) {

    private lateinit var listForCurrentScreen: java.util.ArrayList<Drawable>

    companion object {
        @JvmStatic
        fun newInstance() = GameSettingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).changeStatusBarColor(R.color.primaryLightColor)
        user_name_tv.apply {
            text = SharedPreferenceHelper.userName
            setOnClickListener { showNamePopup() }
        }
        val image = Base64.decode(SharedPreferenceHelper.profilePic, Base64.DEFAULT)
        Glide.with(this).load(image).into(avtar_iv)
        avtar_iv.setOnClickListener {
            showAvtarPopup()
        }
        dark_mode_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {//dark
                 AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                Toast.makeText(requireContext(), "dark", Toast.LENGTH_SHORT).show()
            } else {//light
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "light", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNamePopup() {
        var newName = ""
        val builder = Dialog(requireContext())
        builder.setContentView(R.layout.confirmation_dialog)
        builder.findViewById<MaterialTextView>(R.id.dialog_title_tv).text = "Your's new name ?"
        builder.findViewById<MaterialTextView>(R.id.dialog_content_tv).text =
                "Enter you username below"
        builder.findViewById<TextInputLayout>(R.id.username_til).apply {
            visibility = View.VISIBLE
            editText?.doAfterTextChanged { newName = it.toString() }
        }
        builder.findViewById<CustomButtonView>(R.id.dialog_positive_button).apply {
            setBtnText("OK")
            setOnClickListener {
                if (newName.isNotEmpty()) {
                    SharedPreferenceHelper.userName = newName
                    user_name_tv.text = newName
                    GameApp.hasNameOrAvtarUpdated.postValue(true)
                    builder.dismiss()
                }
            }
        }
        builder.findViewById<CustomButtonView>(R.id.dialog_negative_button).apply {
            setBtnText("EXIT")
            setOnClickListener {
                builder.dismiss()
            }
        }
        builder.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params: ViewGroup.LayoutParams = it.attributes
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = params as WindowManager.LayoutParams
        }
        builder.show()
    }

    private fun showAvtarPopup() {
        val builder = Dialog(requireContext())
        builder.setContentView(R.layout.dialog_avtars)
        val avtarRv = builder.findViewById<RecyclerView>(R.id.dialog_avtar_rv)
        val listOfAvtarDrawables = getAvtarDrawableList()
        avtarRv.adapter = MainScreenAvtarAdapter(listOfAvtarDrawables, object : OnItemClick {
            override fun onPicSelected(position: Int) {
                //avtarPos = position
                val bos = ByteArrayOutputStream()
                listForCurrentScreen[position].toBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos)
                val profilePic = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
                SharedPreferenceHelper.profilePic = profilePic
                Glide.with(requireContext()).load(listForCurrentScreen[position]).into(avtar_iv)
                GameApp.hasNameOrAvtarUpdated.postValue(true)
                builder.dismiss()
            }

            override fun onPicSelectedEndless(
                    rememberTheCardData: RememberTheCardData,
                    position: Int
            ) {
            }

        })
        avtarRv.setHasFixedSize(true)
        val gridLayoutManager =
                GridLayoutManager(requireContext(), 4/*, LinearLayoutManager.HORIZONTAL, false*/)
        avtarRv.layoutManager = gridLayoutManager
        builder.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params: ViewGroup.LayoutParams = it.attributes
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = params as WindowManager.LayoutParams
        }
        builder.show()
    }

    private fun getAvtarDrawableList(): ArrayList<Drawable> {
        Timber.i("getAvtarDrawableList called")
        listForCurrentScreen = ArrayList<Drawable>()
        with(listForCurrentScreen) {
            with(requireActivity() as AppCompatActivity) {
                add(fetchDrawable(R.drawable.ic_avtar_boy_1))
                add(fetchDrawable(R.drawable.ic_avtar_boy_2))
                add(fetchDrawable(R.drawable.ic_avtar_boy_3))
                add(fetchDrawable(R.drawable.ic_avtar_girl_2))
                add(fetchDrawable(R.drawable.ic_avtar_girl_3))
                add(fetchDrawable(R.drawable.ic_avtar_girl_1))
            }
        }
        listForCurrentScreen.shuffled()
        return listForCurrentScreen
    }


}