package com.ck.dev.tiptap.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.models.DialogData
import kotlinx.android.synthetic.main.confirmation_dialog.*
import java.util.*

class ConfirmationDialog : DialogFragment() {


    companion object {
        fun newInstance(dialogData: DialogData?): ConfirmationDialog {
            val dialog = ConfirmationDialog()
            val args = Bundle()
            if (dialogData != null) {
                args.putParcelable("dialogData", dialogData)
            }
            dialog.arguments = args
            return dialog
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Objects.requireNonNull(dialog)?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.confirmation_dialog, null, false)
    }

    //dialog view is ready
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params: ViewGroup.LayoutParams = it.attributes
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = params as WindowManager.LayoutParams
        }

        val dialogData: DialogData? = arguments?.getParcelable("dialogData")
        dialogData?.let {
            dialog_title_tv.text = it.title
            dialog_content_tv.text = it.content
            dialog_positive_button.apply {
                set(it.posBtnText)
                setOnClickListener { view ->
                    it.posListener()
                    dismiss()
                }
            }
            dialog_negative_button.apply {
                set(it.negBtnText)
                setOnClickListener { view ->
                    it.megListener()
                    dismiss()
                }
            }
        }
    }
}