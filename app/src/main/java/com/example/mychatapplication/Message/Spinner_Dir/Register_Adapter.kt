package com.example.mychatapplication.Message.Spinner_Dir

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.MainActivity
import com.example.mychatapplication.Message.Navigation_bar
import com.example.mychatapplication.Message.Store_Dir.AccountDatabaseHelper
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.R
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter

class Register_Adapter : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_main)
        supportActionBar?.hide()

        val genderspinner: Spinner = findViewById(R.id.Gender)
        val username: EditText = findViewById(R.id.editTextUsername)
        val password: EditText = findViewById(R.id.editTextPassword)
        // 创建一个包含可选项目的字符串数组
        val genderlist = arrayOf("男", "女")
        // 创建一个 ArrayAdapter，并将字符串数组和它绑定
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderlist)
        // 设置下拉列表的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 将适配器设置给 Spinner
        genderspinner.setAdapter(adapter)




        // 存储用户名和密码到字符串集合
        val submit: Button = findViewById(R.id.Submit)
        submit.setOnClickListener {
            val username_value = username.text.toString()
            val password_value = password.text.toString()
            val signal : Int

            if (password_value.isEmpty() && password_value.isEmpty()) {
                // 用户名和密码都为空
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            }
            else if (username_value.isEmpty()) {
                // 用户名为空
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            }
            else if (password_value.isEmpty()) {
                // 密码为空
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            }
            else {
                signal = accont_info_save(username_value, password_value)
                Log.d("Register1", "id is $password_value")
                if (signal == 1) {
                    save(username_value, password_value)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // 结束注册活动
                }


            }
        }


    }

    private fun save(inputUsername: String, inputPassword: String) {
        try {
            val outputUsername = openFileOutput("username", Context.MODE_PRIVATE)
            val outputPassword = openFileOutput("Password", Context.MODE_PRIVATE)
            val writerUsername = BufferedWriter(OutputStreamWriter(outputUsername))
            val writerPassword = BufferedWriter(OutputStreamWriter(outputPassword))
            writerUsername.use { it.write(inputUsername) }
            writerPassword.use { it.write(inputPassword) }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun accont_info_save(inputUsername: String, inputPassword: String): Int{
        //插入用户信息数据
        //向数据库加入内容
        var signal: Int
        val Account_dbHelper = AccountDatabaseHelper(this, "Accont_Info_Store.db", 2)
        val Accountdb = Account_dbHelper.writableDatabase
        val whereClause = "username = ?"
        val whereArgs = arrayOf(inputUsername)
        val cursor = Accountdb.query("Accont_Info_Store", null, whereClause , whereArgs, null, null, null)
        if (cursor.moveToFirst()){
            Toast.makeText(this, "该用户名已注册", Toast.LENGTH_SHORT).show()
            signal = 0
        } else{
            val values_user = ContentValues().apply {
                put("username", inputUsername)
                put("password", inputPassword)
            }
            Accountdb.insert("Accont_Info_Store", null, values_user)
            //创建账户数据库
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
            signal = 1
        }
        cursor.close()
        return signal
    }

}