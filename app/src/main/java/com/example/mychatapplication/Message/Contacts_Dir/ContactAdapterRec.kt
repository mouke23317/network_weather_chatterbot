package com.example.mychatapplication.Message.Contacts_Dir

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapplication.Message.Msg_Dir.MsgMainActivity
import com.example.mychatapplication.R

class ContactAdapterRec(val contactListRec: List<Contact>, val finalMsgListRec: List<String>) :
    RecyclerView.Adapter<ContactAdapterRec.ViewHolderRec>(){

            inner class ViewHolderRec(view: View) : RecyclerView.ViewHolder(view){
                val contactImage: ImageView = view.findViewById(R.id.contactImage)
                val contactName: TextView = view.findViewById(R.id.contactName)
                val finalMsg: TextView = view.findViewById(R.id.final_msg_display)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRec {
                val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_item, parent, false)
                val viewHoldertest = ViewHolderRec(view)


                viewHoldertest.itemView.setOnClickListener {
                    val position = viewHoldertest.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val contact = contactListRec[position]
                        val intent = Intent(it.context, MsgMainActivity::class.java)
                        val contact_name = contact.name
                        intent.putExtra("contact_name", contact_name)
                        it.context.startActivity(intent)
                    }
                }

                viewHoldertest.contactImage.setOnClickListener {
                    val position = viewHoldertest.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val contact = contactListRec[position]
                        val intent = Intent(it.context, MsgMainActivity::class.java)
                        val contact_name = contact.name
                        intent.putExtra("contact_name", contact_name)

                        it.context.startActivity(intent)
                    }
                }

                return  viewHoldertest
            }

            override fun onBindViewHolder(holder: ViewHolderRec, position: Int) {
                val contact = contactListRec[position]
                val finaMsgPos = finalMsgListRec[position]
                holder.contactImage.setImageResource(contact.imageId!!)
                holder.contactName.text = (contact.name)
                holder.finalMsg.text = (finaMsgPos)
            }

            override fun getItemCount() = contactListRec.size
}
