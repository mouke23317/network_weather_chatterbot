package com.example.mychatapplication.Message.Msg_Dir

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.Contacts_Dir.ContactsMainActivity
import com.example.mychatapplication.Message.ListViewContacts_Dir.ListViewTest
import com.example.mychatapplication.Message.Store_Dir.AccountDatabaseHelper
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.Buffer
import kotlin.concurrent.thread

class MsgMainActivity : BaseActivity() , View.OnClickListener{
    private val msgList = ArrayList<Msg>()
    private  val adapter: MsgAdapter?= null
    private var id: String = ""
    private var Cityname: String = ""
    private var Country: String = ""
    private  var path: String = ""
    private var timezone: String = ""
    private var timezone_offset: String = ""

    //now内容
    private var Weather: String = ""
    private var Temperature: String = ""
    private var Cityname_pinyin = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg_main)
        supportActionBar?.hide()
        val titileContactName = intent.getStringExtra("contact_name")


        val titleContact: TextView = findViewById(R.id.title_Activity_msg)
        titleContact.setText(titileContactName)


        initMsg(titileContactName)
        val layoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView =findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        val adapter = MsgAdapter(msgList)
        recyclerView.adapter = adapter
        val send: Button = findViewById(R.id.send)
        send.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val send: Button = findViewById(R.id.send)
        val inputText: EditText = findViewById(R.id.inputText)
        val contactName: TextView = findViewById(R.id.title_Activity_msg)
        val recyclerView: RecyclerView =findViewById(R.id.recyclerView)
        val titileContactName = intent.getStringExtra("contact_name")

        when (v){
            send -> {
                val content = inputText.text.toString()
                val contactNameValue = contactName.text.toString()
                if (content.isNotEmpty()){
                    val msg = Msg(content, Msg.TYPE_SENT)
                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    recyclerView.scrollToPosition(msgList.size - 1)
                    inputText.setText("")

                    //向数据库加入内容
                    val sharedPreferences = getSharedPreferences("Account_display", Context.MODE_PRIVATE)
                    val account = sharedPreferences.getString("account_now", "")
                    if (account != null) {
                        Log.d("MsgMain111", "account is $account")
                    }


                    //每次的内容都查找
                    val SearchHelper = AccountDatabaseHelper(this, "Accont_Info_Store.db", 2)
                    val Searchdb = SearchHelper.writableDatabase
                    val whereClause = "cityname = ?"
                    val whereArgs = arrayOf(content)
                    val cursor = Searchdb.query("Cityname_List_Store", null,  whereClause, whereArgs, null, null, null)
                    if (cursor.moveToFirst()){
                        //如果匹配查天气数据库关键字就成组存
                        val city_value = cursor.getColumnIndex("cityname")
                        val cityName = cursor.getString(city_value)
                        Cityname_pinyin = cityName
                        Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show()
                        sendRequestWithOkHttp(content, account, titileContactName)
                    }
                    else{//不匹配就只存content
                        val dbHelper = UserDatabaseHelper(this, "${account}_Chat_Info.db", 2)
                        val db = dbHelper.writableDatabase
                        val values = ContentValues().apply {
                            put("Message", content)
                        }
                        db.insert(titileContactName, null, values)
                        // 始终记录聊天的本人最后一句话
                        val editor = getSharedPreferences("${account}_final_msg_display", Context.MODE_PRIVATE).edit()
                        editor.putString(contactNameValue, content)
                        editor.apply()
                    }
                    cursor.close()

                }
            }
        }
    }

    private fun initMsg(titileContactName: String?){
        val msg1 = Msg("你好！需要天气信息请回复下列对应城市:\n" +
                "             \"广州\",\n" +
                "             \"深圳\",\n" +
                "             \"佛山\",\n" +
                "             \"东莞\",\n" +
                "             \"中山\",\n" +
                "             \"珠海\",\n" +
                "             \"江门\",\n" +
                "             \"惠州\",\n" +
                "             \"汕头\",\n" +
                "             \"潮州\",\n" +
                "             \"揭阳\",\n" +
                "             \"茂名\",\n" +
                "             \"阳江\",\n" +
                "             \"清远\",\n" +
                "             \"湛江\",\n" +
                "             \"云浮\",\n" +
                "             \"韶关\",\n" +
                "             \"河源\",\n" +
                "             \"梅州\",\n" +
                "             \"汕尾\"", Msg.TYPE_RECRIVED)
        msgList.add(msg1)



        val sharedPreferences = getSharedPreferences("Account_display", Context.MODE_PRIVATE)
        val account = sharedPreferences.getString("account_now", "")
        val dbHelper = UserDatabaseHelper(this, "${account}_Chat_Info.db", 2)
        val db = dbHelper.writableDatabase
        val cursor = db.query(titileContactName, null, null, null, null, null, null)

        if (cursor.moveToFirst()){
            do {
                val msg_value = cursor.getColumnIndex("Message")
                val revriced_value = cursor.getColumnIndex("Revriced")
                val msg = cursor.getString(msg_value)
                val revriced = cursor.getString(revriced_value)
                Log.d("MsgMain1111", "Contact message is $msg")
                Log.d("MsgMain1111", "revriced message is $revriced")

                //回复框为空，只初始化发送端
                if (revriced == null)
                {
                    val msgRecord = Msg(msg, Msg.TYPE_SENT)
                    msgList.add(msgRecord)
                } //回复框有内容就先初始发送端，再初始回答端
                else
                {
                    val msgRecord = Msg(msg, Msg.TYPE_SENT)
                    msgList.add(msgRecord)
                    val chatBotMsg = Msg(revriced, Msg.TYPE_RECRIVED)
                    msgList.add(chatBotMsg)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun sendRequestWithOkHttp(content: String?, account: String?, titileContactName: String?) {
        // 开启线程发起网络请求
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.seniverse.com/v3/weather/now.json?key=SSTgnjdbDhOM1xwdO&location=$Cityname_pinyin&language=zh-Hans&unit=c")
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    parseJSONWithJSONObject(responseData)
                    StoreWeatherInfo(content, account, titileContactName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*private fun showRespons(response: String){
        runOnUiThread(){
            //val responseText: TextView = findViewById(R.id.responseText)
            //responseText.text = response
        }
    }*/

    private fun parseJSONWithJSONObject(jsonData: String){
        try {
            val jsonObject = JSONObject(jsonData)
            val results = jsonObject.getJSONArray("results")//json外层

            //json内层now、location
            val now = results.getJSONObject(0).getJSONObject("now")
            val location = results.getJSONObject(0).getJSONObject("location")

            //loaction内容
            id = location.getString("id")
            Cityname = location.getString("name")
            Country = location.getString("country")
            path = location.getString("path")
            timezone = "Asia/Shanghai"
            timezone_offset = "+08:00"

            //now内容
            Weather = now.getString("text")
            Temperature = now.getString("temperature")

            //日志测试
            /*Log.d("Navigation_bar1", "id is $Cityname")
            Log.d("Navigation_bar1", "id is $Country")
            Log.d("Navigation_bar1", "id is $path")
            Log.d("Navigation_bar1", "id is $timezone")
            Log.d("Navigation_bar1", "id is $timezone_offset")

            Log.d("Navigation_bar1", "name is $Weather")
            Log.d("Navigation_bar1", "name is $Temperature")*/
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun StoreWeatherInfo(content: String?, account: String?, titileContactName: String?){
        Handler(Looper.getMainLooper()).post{
            val weatherMsg = Msg(
                "地区：$path\n" +
                        "天气：$Weather\n" +
                        "今日温度：$Temperature\n", Msg.TYPE_RECRIVED)
            msgList.add(weatherMsg)
            val dbHelper = UserDatabaseHelper(this, "${account}_Chat_Info.db", 2)
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("Message", content)
                put("Revriced", "地区：$path\n" +
                                "天气：$Weather\n" +
                                "今日温度：$Temperature\n")
            }
            db.insert(titileContactName, null, values)
            val editor = getSharedPreferences("${account}_final_msg_display", Context.MODE_PRIVATE).edit()
            editor.putString(titileContactName, "地区：$path\n" +
                                                "天气：$Weather\n" +
                                                "今日温度：$Temperature\n")
            editor.apply()
        }
    }
}