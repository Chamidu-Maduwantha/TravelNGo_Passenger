package com.example.travelpass

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class PasswordChangeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_changed, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ ->

            val intent = Intent(context, Profile::class.java)
            startActivity(intent)

            // Dismiss the dialog
            dialog?.dismiss()
        }

        // Create the dialog
        val dialog = builder.create()

        // Set the dialog's window animations
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        // Set the dialog's background drawable
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Initialize the ImageView and load the GIF using Glide
        val imageView: ImageView = view.findViewById(R.id.image_password_changed)
        Glide.with(this)
            .load(R.drawable.icon_check) // Replace "your_gif_image" with your actual GIF image resource
            .into(imageView)

        return dialog
    }

    companion object {
        fun newInstance(): PasswordChangeDialogFragment {
            return PasswordChangeDialogFragment()
        }
    }
}