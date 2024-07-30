package com.example.mychatapplication.Message.ListViewContacts_Dir

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mychatapplication.Message.ActivityCollector_Dir.ActivityCollector
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.MainActivity
import com.example.mychatapplication.Message.Navigation_bar
import com.example.mychatapplication.Message.Store_Dir.AccountDatabaseHelper
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.R

class ListViewTest : BaseActivity() {
    private val data = listOf("Apple", "Banana", "Orange", "Watermelon",
        "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango",
        "Apple", "Banana", "Orange", "Watermelon", "Pear", "Grape",
        "Pineapple", "Strawberry", "Cherry", "Mango")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listview)
        supportActionBar?.hide()

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data)
        val listView:ListView=findViewById(R.id.listview_test)
        listView.adapter = adapter

        val editButton: Button = findViewById(R.id.title_Edit)
        editButton.setOnClickListener {
            showSearchDialog()
        }
    }

    private fun showSearchDialog() {
        // 创建一个AlertDialog.Builder实例
        val builder = AlertDialog.Builder(this)
        // 设置对话框的标题
        builder.setTitle("搜索")
        // 自定义对话框的布局（这里使用EditText作为搜索框）
        val layoutInflater = getLayoutInflater()
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val searchEditText = dialogView.findViewById<EditText>(R.id.search_edit_text)

        // 设置对话框的视图
        builder.setView(dialogView)
        builder.setPositiveButton("搜索并添加") { _, _ ->

            val account = intent.getStringExtra("username_Navigation")
            if (account != null) {
                Log.d("ListView111", account)
            }

            val searchText = searchEditText.text.toString()
            val Account_dbHelper = AccountDatabaseHelper(this, "Accont_Info_Store.db", 2)
            val User_Contact_dbHelper = UserDatabaseHelper(this, "${account}_Chat_Info.db", 2)

            val Accountdb = Account_dbHelper.writableDatabase
            val Userdb = User_Contact_dbHelper.writableDatabase

            val Account_whereClause = "contact = ?"
            val User_whereClause = "User_contact_name = ?"

            val AccountwhereArgs = arrayOf(searchText)
            val User_whereArgs = arrayOf(searchText)

            val Account_cursor =
                Accountdb.query("Contact_List_Store", null, Account_whereClause, AccountwhereArgs, null, null, null)
            val User_cursor =
                Userdb.query("User_Chat_Info", null, User_whereClause, User_whereArgs, null, null, null)

            if (Account_cursor.moveToFirst()) {
                val contact_value = Account_cursor.getColumnIndex("contact")
                val contact = Account_cursor.getString(contact_value)
                if (User_cursor.moveToFirst()) {
                    Toast.makeText(this, "请不要重复添加用户", Toast.LENGTH_SHORT).show()
                }
                else {
                        val values_user = ContentValues().apply {
                        put("User_contact_name", searchText)
                        put("Image_id", "${searchText}_pic")
               }
               Userdb.insert("User_Chat_Info", null, values_user)
                    Toast.makeText(this, "用户：${contact}已加入通讯录", Toast.LENGTH_SHORT).show()
                    val columns = "id integer primary key autoincrement," +
                            "Message text," +
                            "Revriced text"
                    User_Contact_dbHelper.createTable(searchText, columns)
                }
                User_cursor.close()
            }
            else {
                Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show()
            }
            Account_cursor.close()
        }
        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        // 创建并显示对话框
        val dialog = builder.create()
        dialog.show()
    }
}