package com.example.mychatapplication.Message.Weather_Dir

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.lang.Exception
import java.lang.NullPointerException
import kotlin.concurrent.thread

class Weather_Dir : BaseActivity() {

    private val data = listOf("Apple", "Banana", "Orange", "Watermelon",
        "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango",
        "Apple", "Banana", "Orange", "Watermelon", "Pear", "Grape",
        "Pineapple", "Strawberry", "Cherry", "Mango")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_weather_information)
        supportActionBar?.hide()


        //创建数据库
        val dbHelper = UserDatabaseHelper(this, "ContactStore.db", 2)
        val creatDatabase : Button = findViewById(R.id.createDatabase)
        creatDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }


        //向数据库加入内容
        val connect : Button = findViewById(R.id.connect)
        connect.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values1 = ContentValues().apply {
                for (item in data){
                    put("name", item)
                }
            }
            db.insert("Contact", null, values1)

            val values2 = ContentValues().apply {
                put("name", "banana")
                put("msg", "hello")
            }
            db.insert("Contact", null, values2)
        }


        //删除数据库内容
        val deleteData: Button = findViewById(R.id.deleteData)
        deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Contact", "name = ?", arrayOf("banana"))
        }


        //更新数据库
        val updata: Button = findViewById(R.id.updata)
        updata.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("msg", "hello,world")
            db.update("Contact", values, "name = ?", arrayOf("apple"))
        }

        //查找内容
        val queryData: Button = findViewById(R.id.queryData)
        queryData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val cursor = db.query("Contact", null, null, null, null, null, null)
            if (cursor.moveToFirst()){
                do {
                    val name_value = cursor.getColumnIndex("name")
                    val msg_value = cursor.getColumnIndex("msg")
                    val name = cursor.getString(name_value)
                    val msg = cursor.getString(msg_value)
                    Log.d("Navigation_bar", "Contact name is $name")
                    Log.d("Navigation_bar", "Contact message is $msg")
                } while (cursor.moveToNext())
            }
            cursor.close()
        }


        //产生并处理事件，用于此增彼涨之类的场景
        val replace: Button = findViewById(R.id.repalce)
        replace.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                db.delete("Contact", null , null)
                if (true){ throw NullPointerException() }
                val values = ContentValues().apply {
                    put("name", "apple")
                    put("msg", "hello,world")
                }
                db.insert("Contact", null, values)
                db.setTransactionSuccessful()
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                db.endTransaction()
            }
        }
    }





}