package com.example.mychatapplication.Message.Store_Dir

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class AccountDatabaseHelper (val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name,null, version){

    private val createAccontInfo = "create table Accont_Info_Store (" +
            "id integer primary key autoincrement," +
            "username text," +
            "password text)"

    private val createContact = "create table Contact_List_Store (" +
            "id integer primary key autoincrement," +
            "contact text)"

    private val createCityName = "create table Cityname_List_Store (" +
            "id integer primary key autoincrement," +
            "cityname text," +
            "cityname_pinyin text)"



    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createAccontInfo)
        db.execSQL(createContact)
        db.execSQL(createCityName)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion <= 1){db.execSQL(createAccontInfo)}
        if (oldVersion <= 2){db.execSQL("alter table Contact add column category_id integer")}
    }
}