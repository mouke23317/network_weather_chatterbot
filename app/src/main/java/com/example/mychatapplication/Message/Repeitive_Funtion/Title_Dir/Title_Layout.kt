package com.example.mychatapplication.Message.Repeitive_Funtion.Title_Dir

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.mychatapplication.R

class Title_Layout (context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    init {
        LayoutInflater.from(context).inflate(R.layout.title,this)
        val titleBack: Button =findViewById(R.id.title_Back)
        titleBack.setOnClickListener {
            val activity = context as Activity
            activity.finish()
        }

    }
}