package com.example.mychatapplication.Message.Store_Dir

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class UserDatabaseHelper (val context: Context, name: String, version: Int) :
                        SQLiteOpenHelper(context, name,null, version){

    private val createUserChatInfo = "create table User_Chat_Info (" +
            "id integer primary key autoincrement," +
            "User_contact_name text," +
            "Image_id text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createUserChatInfo)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion <= 1){db.execSQL(createUserChatInfo)}
        if (oldVersion <= 2){db.execSQL("alter table Contact add column category_id integer")}
    }

    fun createTable(tableName: String, columns: String) {
        val db = this.writableDatabase
        val sql = "create table $tableName ($columns)"
        db.execSQL(sql)
        db.close()
    }
}