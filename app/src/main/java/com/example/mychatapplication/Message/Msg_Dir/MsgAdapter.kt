package com.example.mychatapplication.Message.Msg_Dir

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapplication.R

class MsgAdapter(val msgList: List<Msg> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class LeftViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val leftMsg :TextView = view.findViewById(R.id.leftMsg)
    }

    inner class RightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rightMsg: TextView = view.findViewById(R.id.rightMsg)
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if(viewType == Msg.TYPE_RECRIVED){
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.msg_left_item, parent, false)
        LeftViewHolder(view)
    }else{
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.msg_right_item, parent, false)
        RightViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder){
            is LeftViewHolder -> holder.leftMsg.text = msg.context
            is RightViewHolder -> holder.rightMsg.text = msg.context
            else -> throw  IllegalAccessException()
        }
    }

    override fun getItemCount() = msgList.size
}