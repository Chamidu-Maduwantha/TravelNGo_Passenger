package com.example.travelpass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class UserAdapter (private val users:List<users>): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.qr_item, parent, false)
        return UserViewHolder(itemView)




    }

    override fun getItemCount(): Int {
        return users.size

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val qrCodeImageView: ImageView = itemView.findViewById(R.id.image_view)
        val nameTextView: TextView = itemView.findViewById(R.id.username)


        private val emailTextView: TextView = itemView.findViewById(R.id.bio)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(User: users) {
            nameTextView.text = User.username
            emailTextView.text = User.NIC
            imageView.setImageBitmap(User.qrCodeBitmap)
        }

    }

}