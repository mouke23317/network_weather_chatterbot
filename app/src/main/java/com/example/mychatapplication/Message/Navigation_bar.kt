package com.example.mychatapplication.Message

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.Message.Contacts_Dir.Contact
import com.example.mychatapplication.Message.Contacts_Dir.ContactsMainActivity
import com.example.mychatapplication.Message.ListViewContacts_Dir.ListViewTest
import com.example.mychatapplication.Message.Msg_Dir.Msg
import com.example.mychatapplication.Message.Msg_Dir.MsgMainActivity
import com.example.mychatapplication.Message.Store_Dir.AccountDatabaseHelper
import com.example.mychatapplication.Message.Store_Dir.UserDatabaseHelper
import com.example.mychatapplication.Message.Weather_Dir.City_Name
import com.example.mychatapplication.Message.Weather_Dir.Weather_Dir
import com.example.mychatapplication.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.lang.Exception
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread
import kotlin.math.log

class Navigation_bar : BaseActivity() {
    private val data = listOf("Apple", "Banana", "Orange", "Watermelon",
        "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango",
        "Apple", "Banana", "Orange", "Watermelon", "Pear", "Grape",
        "Pineapple", "Strawberry", "Cherry", "Mango")

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

    private val pinyinCities = listOf(
        "广州",
        "深圳",
        "佛山",
        "东莞",
        "中山",
        "珠海",
        "江门",
        "惠州",
        "汕头",
        "潮州",
        "揭阳",
        "茂名",
        "阳江",
        "清远",
        "湛江",
        "云浮",
        "韶关",
        "河源",
        "梅州",
        "汕尾"
        )

    private val guangdongCities = listOf(
        City_Name("广州", "Guangzhou"),
        City_Name("深圳", "Shenzhen"),
        City_Name("佛山", "Foshan"), // 注意：通常佛山的拼音是 Foshan (佛) 或 Fuoshan (佛山)，这里使用了 Foshan
        City_Name("东莞", "Dongguan"),
        City_Name("中山", "Zhongshan"),
        City_Name("珠海", "Zhuhai"),
        City_Name("江门", "Jiangmen"),
        City_Name("惠州", "Huizhou"),
        City_Name("汕头", "Shantou"),
        City_Name("潮州", "Chaozhou"),
        City_Name("揭阳", "Jieyang"),
        City_Name("茂名", "Maoming"),
        City_Name("阳江", "Yangjiang"),
        City_Name("清远", "Qingyuan"),
        City_Name("湛江", "Zhanjiang"),
        City_Name("云浮", "Yunfu"),
        City_Name("韶关", "Shaoguan"),
        City_Name("河源", "Heyuan"),
        City_Name("梅州", "Meizhou"),
        City_Name("汕尾", "Shanwei")
    )

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation_bar)
        supportActionBar?.hide()

        val contacts: Button = findViewById(R.id.contact)
        contacts.setOnClickListener {
            val account = intent.getStringExtra("username_login")
            val intent = Intent(this, ContactsMainActivity::class.java)
            intent.putExtra("username_ContactsMainActivity", account)
            startActivity(intent)
        }

        val information:Button = findViewById(R.id.information)
        information.setOnClickListener {

            val account = intent.getStringExtra("username_login")
            val intent = Intent(this, ListViewTest::class.java)
            if (account != null) {
                Log.d("ListView111", account)
            }
            intent.putExtra("username_Navigation", account)
            startActivity(intent)
        }

        val logoutBT :Button = findViewById(R.id.logout)
        logoutBT.setOnClickListener {
            val intent = Intent("com.example.broadcastbestpractice.FORCE_OFFLINE")
            sendBroadcast(intent)
        }


        val Citynamespinner: Spinner = findViewById(R.id.Citynamespinner)

        // 创建一个 ArrayAdapter，并将字符串数组和它绑定
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pinyinCities)
        // 设置下拉列表的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 将适配器设置给 Spinner
        Citynamespinner.setAdapter(adapter)
        Citynamespinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 获取当前选中的项（拼音）
                val selectedItem = parent?.getItemAtPosition(position) as? String
                if (selectedItem != null) {
                    // 使用选中的拼音值
                    Log.d("Spinner", "Selected City Pinyin: $selectedItem")
                    val matchedCity = guangdongCities.find { it.chineseName == selectedItem }
                    if (matchedCity != null) {
                        Log.d("Spinner", "Selected City Chinese Name: ${matchedCity}")
                        Cityname_pinyin = matchedCity.pinyin
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 如果没有选中任何项，这里会被调用
            }
        }

        //同步数据库的固定联系人
        val synchronizeBtn: Button =findViewById(R.id.Chat_Online)
        synchronizeBtn.setOnClickListener {
            val dbHelper = AccountDatabaseHelper(this, "Accont_Info_Store.db", 2)
            val db = dbHelper.writableDatabase

            for (item in data) {
                val whereClause = "contact = ?"
                val whereArgs = arrayOf(item)
                val cursor = db.query("Contact_List_Store", null, whereClause, whereArgs, null, null, null)
                if (cursor.moveToFirst()) {
                    // 如果找到联系人，关闭游标并继续循环
                    cursor.close()
                    Toast.makeText(this, "联系人已存入", Toast.LENGTH_SHORT).show()
                    break
                } else {
                    val Contact_values = ContentValues().apply {
                        put("contact", item)
                    }
                    db.insert("Contact_List_Store", null, Contact_values)
                }
            }

            for (item in guangdongCities) {
                val whereClause = "cityname = ?"
                val whereArgs = arrayOf(item.chineseName)
                val cursor = db.query("Cityname_List_Store", null, whereClause, whereArgs, null, null, null)
                if (cursor.moveToFirst()) {
                    // 如果找到联系人，关闭游标并继续循环
                    cursor.close()
                    break
                } else {
                    val City_Name_values = ContentValues().apply {
                        put("cityname", item.chineseName)
                        Log.d("navigat111", "Selected City Chinese Name: ${item.chineseName}")
                        put("cityname_pinyin", item.pinyin)
                        Log.d("navigat111", "Selected City pinyin Name: ${item.pinyin}")
                    }
                    db.insert("Cityname_List_Store", null, City_Name_values)
                }
            }
        }


        val sendRequestBtn: Button = findViewById(R.id.sendRequest)
        sendRequestBtn.setOnClickListener {
            sendRequestWithOkHttp()
        }
    }

    private fun sendRequestWithOkHttp() {
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
                    showWeather()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun showRespons(response: String){
        runOnUiThread(){
            //val responseText: TextView = findViewById(R.id.responseText)
            //responseText.text = response
        }
    }

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


            /*Log.d("Navigation_bar1", "id is $Cityname")
            Log.d("Navigation_bar1", "id is $Country")
            Log.d("Navigation_bar1", "id is $path")
            Log.d("Navigation_bar1", "id is $timezone")
            Log.d("Navigation_bar1", "id is $timezone_offset")

            Log.d("Navigation_bar1", "name is $Weather")
            Log.d("Navigation_bar1", "name is $Temperature")*/
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showWeather(){
        Handler(Looper.getMainLooper()).post{
            val CountryText: TextView = findViewById(R.id.Country)
            val CitynameText: TextView = findViewById(R.id.Cityname)
            val WeathrerText: TextView = findViewById(R.id.Weather)
            val TemperatureText: TextView = findViewById(R.id.Temperature)

            Handler(Looper.getMainLooper()).post{
                CountryText.text = Country
                CitynameText.text = Cityname
                WeathrerText.text = Weather
                TemperatureText.text = Temperature
            }
        }
    }
}