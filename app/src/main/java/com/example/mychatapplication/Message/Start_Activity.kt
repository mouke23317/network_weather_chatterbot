package com.example.mychatapplication.Message

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.ImageView
import com.example.mychatapplication.Message.ActivityCollector_Dir.BaseActivity
import com.example.mychatapplication.R

class Start_Activity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_page)
        supportActionBar?.hide()

        // 创建一个Handler实例
        val handler = Handler()

        // 在子线程中延迟
        Thread {
            try {
                Thread.sleep(2000) // 延迟2秒

                // 使用Handler的post方法回到主线程
                handler.post {
                    // 跳转到LoginActivity
                    val intent = Intent(this@Start_Activity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }
}

