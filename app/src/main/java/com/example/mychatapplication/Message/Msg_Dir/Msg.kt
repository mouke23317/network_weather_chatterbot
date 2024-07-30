package com.example.mychatapplication.Message.Msg_Dir

class Msg (val context: String, val type:Int){
    companion object{
        const val TYPE_RECRIVED = 0
        const val TYPE_SENT = 1
    }
}