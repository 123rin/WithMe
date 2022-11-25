package com.example.withme

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class editprofileActivity : AppCompatActivity() {
    val client = OkHttpClient()
    val myApp = myApplication.getInstance()
    private val REQUEST_GALLERY_TAKE = 2

    private lateinit var userImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        userImage = findViewById<ImageView>(R.id.userImage)
        var nameEdit = findViewById<EditText>(R.id.nameEdit)
        var profileEdit = findViewById<EditText>(R.id.profileEdit)
        var saveButton = findViewById<Button>(R.id.saveButton)

        //データ保存処理
        saveButton.setOnClickListener {
            var apiUrl = myApp.apiUrl+"userUpd.php?userId="+myApp.loginMyId+"&userName="+nameEdit.text.toString()+"&profile="+profileEdit.text.toString()
            val request = Request.Builder().url(apiUrl).build()
            val errorText = "エラー"
            var countList = mutableListOf<timelinedata>()
            // Log.v("blockurl",apiUrl)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    this@editprofileActivity.runOnUiThread {
                        Toast.makeText(applicationContext, errorText, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResponse(call: Call, response: Response) {
                    val csvStr = response.body!!.string()
                    val resultError = JSONObject(csvStr)
                    if(resultError.getString("result") == "error") {
                        this@editprofileActivity.runOnUiThread {
                            Toast.makeText(applicationContext, errorText, Toast.LENGTH_SHORT).show()
                        }
                    }else if(resultError.getString("result") == "success"){
                        this@editprofileActivity.runOnUiThread {
                            Toast.makeText(applicationContext, "保存しました", Toast.LENGTH_SHORT).show()
                            //マイページ画面へ遷移
                            var intent = Intent(applicationContext, mypageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            })
        }

        userImage.setOnClickListener {
            //ギャラリーを開きイメージに定義
            openGalleryForImage()
        }
    }

    //ギャラリーを開くためのメソッド
    private fun openGalleryForImage() {
        //ギャラリーに画面を遷移するためのIntent
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_TAKE)
    }


    // onActivityResultにイメージ設定
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            2 -> {
                if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY_TAKE){
                    //選択された写真にImageViewを変更
                    userImage.setImageURI(data?.data) // handle chosen image
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val timeline = Intent(this, timelineActivity::class.java)
        val chat = Intent(this, chatlistActivity::class.java)
        val notification = Intent(this, notificationActivity::class.java)
        val mypage = Intent(this, mypageActivity::class.java)
        val settingsettinglist = Intent(this, settinglistActivity::class.java)
        val login = Intent(this, loginActivity::class.java)

        //連携処理を実施
        when (item.itemId) {
            R.id.timeline -> startActivity(timeline)
            R.id.chat -> startActivity(chat)
            R.id.notification -> startActivity(notification)
            R.id.mypage -> startActivity(mypage)
            R.id.setting -> startActivity(settingsettinglist)
            else -> startActivity(login)//ログアウト処理を書く
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

//    fun convertImageToBitmap():{
//
//    }
}