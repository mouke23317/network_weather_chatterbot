package com.example.mychatapplication.Message

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.Spinner_Dir.Register_Adapter
import com.example.mychatapplication.Message.Store_Dir.AccountDatabaseHelper
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.Message.Store_Dir.User_Info
import com.example.mychatapplication.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.nio.Buffer
import java.time.LocalDate

class MainActivity : BaseActivity() {
    private var password_value: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val login:Button = findViewById(R.id.Login)//实例化一个登录按钮
        val register:Button = findViewById(R.id.Register)//实例化一个注册按钮

        //实例化信息文本框
        val in_username:EditText = findViewById(R.id.in_username)
        val in_password:EditText = findViewById(R.id.in_password)
        val rememberPass:CheckBox = findViewById(R.id.remenberPass)


        val prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember){
            val account = prefs.getString("account", "")
            val password = prefs.getString("password", "")
            in_username.setText(account)
            in_password.setText(password)
            rememberPass.isChecked = true
        }

        login.setOnClickListener{
            val account = in_username.text.toString()
            val password = in_password.text.toString()

            // 如果账密匹配数据库就成功
            password_value = SearchUserinfo(account)
            Log.d("Register1", "password is $password_value")

            if (password_value != null && password == password_value) {
                val editor = prefs.edit()
                if (rememberPass.isChecked) {
                    editor.putBoolean("remember_password", true)
                    editor.putString("account", account)
                    editor.putString("password", password)
                } else {
                    editor.clear()
                }
                editor.apply()
                val intent = Intent(this, Navigation_bar::class.java)
                intent.putExtra("username_login", account)
                if (account != null) {
                    Log.d("ListView111", account)
                }
                val accountEditor = getSharedPreferences("Account_display", Context.MODE_PRIVATE).edit()
                accountEditor.putString("account_now", account)
                accountEditor.apply()
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "account or password is invalid", Toast.LENGTH_SHORT).show()
            }
        }

        register.setOnClickListener {
            val intent_register = Intent(this, Register_Adapter::class.java)
            startActivity(intent_register)
        }
    }


    private fun SearchUserinfo(inputUsername: String): String?{
        //查找内容
        val dbHelper = AccountDatabaseHelper(this, "Accont_Info_Store.db", 2)
        val db = dbHelper.writableDatabase
        val whereClause = "username = ?"
        val whereArgs = arrayOf(inputUsername)
        val cursor = db.query("Accont_Info_Store", null, whereClause , whereArgs, null, null, null)
        var password: String? = null
        if (cursor.moveToFirst()){
            val password_value = cursor.getColumnIndex("password")
            password = cursor.getString(password_value)
        }
        cursor.close()
        return password
    }
}