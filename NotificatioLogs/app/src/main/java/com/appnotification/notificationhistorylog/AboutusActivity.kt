package com.appnotification.notificationhistorylog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class AboutusActivity : AppCompatActivity() {
    var btncontact: Button? = null
    var harshil: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        val btncontact:Button = findViewById(R.id.btncontact)
        btncontact.setOnClickListener(View.OnClickListener {
            val send = Intent(Intent.ACTION_SENDTO)
            val uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                    "?subject=" + Uri.encode("Notification Log - Developer Contact") +
                    "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here")
            val uri = Uri.parse(uriText)
            send.data = uri
            startActivity(Intent.createChooser(send, "Send Mail Via : "))
            finish()
        })
        val harshil:LinearLayout = findViewById(R.id.harshil1)
        harshil.setOnClickListener(View.OnClickListener {
            val url = "http://www.xenonstudio.in/developer"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })
    }
}