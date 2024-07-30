package com.example.mychatapplication.Message.Contacts_Dir

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.ListViewContacts_Dir.ListViewTest
import com.example.mychatapplication.Message.Msg_Dir.MsgMainActivity
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.R
import kotlin.math.log

class ContactsMainActivity : BaseActivity() {
    private val contactListRec = ArrayList<Contact>()
    private val finalMsgListRec = ArrayList<String>()
    private val imageMap = HashMap<String, Int>()
    init {
        // 在初始化时填充映射
        imageMap["Apple_pic"] = R.drawable.apple_pic
        imageMap["Banana_pic"] = R.drawable.banana_pic
        imageMap["Orange_pic"] = R.drawable.orange_pic
        imageMap["Watermelon_pic"] = R.drawable.watermelon_pic
        imageMap["Pear_pic"] = R.drawable.pear_pic
        imageMap["Grape_pic"] = R.drawable.grape_pic
        imageMap["Pineapple_pic"] = R.drawable.pineapple_pic
        imageMap["Strawberry_pic"] = R.drawable.strawberry_pic
        imageMap["Cherry_pic"] = R.drawable.cherry_pic
        imageMap["Mango_pic"] = R.drawable.mango_pic
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        supportActionBar?.hide()

        val account = intent.getStringExtra("username_ContactsMainActivity")
        if (account != null) {
            Log.d("ContactsMainActivity11", account)
        }


        val dbHelper = UserDatabaseHelper(this, "${account}_Chat_Info.db", 2)
        val db = dbHelper.writableDatabase
        val cursor = db.query("User_Chat_Info", null, null, null, null, null, null)
        if (cursor.moveToFirst()){
            do {
                val username_value = cursor.getColumnIndex("User_contact_name")
                val imageid_value = cursor.getColumnIndex("Image_id")
                val contactName = cursor.getString(username_value)
                val imageId = cursor.getString(imageid_value)
                Log.d("ContactsMainActivity11111", "Contact name is $imageId")

                initContactRec(imageId, contactName)
            } while (cursor.moveToNext())
        }
        cursor.close()


        //日志点
        for (contact in contactListRec){
            Log.d("ContactsMainActivity1111", "Contact name is ${contact.name}")
            Log.d("ContactsMainActivity1111", "ImageId is ${contact.imageId}")
        }

        for (contact in contactListRec){
            initFinalMsg(contact.name, account!!)
        }


        val recyclerView: RecyclerView =findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = ContactAdapterRec(contactListRec, finalMsgListRec)
        recyclerView.adapter = adapter
        }


    override fun onResume() {
        setContentView(R.layout.activity_contacts)
        supportActionBar?.hide()
        finalMsgListRec.clear()

        val sharedPreferences = getSharedPreferences("Account_display", Context.MODE_PRIVATE)
        // 使用之前保存的键来获取字符串
        val account = sharedPreferences.getString("account_now", "")
        Log.d("ContactMainActivity", "account_now is $account")

        for (contact in contactListRec){
            initFinalMsg(contact.name, account!!)
        }


        for (msg in finalMsgListRec) {
            Log.d("ContactMainActivity", "finalMsgListRec item: $msg")
        }
        val recyclerView: RecyclerView =findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = ContactAdapterRec(contactListRec, finalMsgListRec)
        recyclerView.adapter = adapter
        super.onResume()
    }
    private fun initContactRec(image: String, contactName: String){
        val imageResId = imageMap[image]
        Log.d("ContactsMainActivity11111", "Contact name is $imageMap[image]")
        contactListRec.add(Contact(contactName, imageResId))
    }

    private  fun initFinalMsg(contactName: String, account: String){
        val editor = getSharedPreferences("${account}_final_msg_display", Context.MODE_PRIVATE)
        Log.d("ContactMainActivity", "Contact name is $contactName")

        val test = editor.getString(contactName, "")
        Log.d("ContactMainActivity", test!!)

        finalMsgListRec.add((editor.getString(contactName, "").toString()))
    }
}