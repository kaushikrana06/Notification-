package com.appnotification.notificationhistorylog.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnotification.notificationhistorylog.FAQActivity
import com.appnotification.notificationhistorylog.R

class IssueActivity : AppCompatActivity() {
    var logs = 0
    var buttonAllow: Button? = null
    var buttonfaqs: Button? = null
    var buttonhelp: Button? = null
    var button_check: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)
        val buttonAllow:Button = findViewById(R.id.button_allow)
        buttonAllow.setOnClickListener(View.OnClickListener { view: View? -> startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) })
        val buttonfaqs:Button = findViewById(R.id.button_faqs)
        buttonfaqs.setOnClickListener(View.OnClickListener {
            val startIntent = Intent(this@IssueActivity, FAQActivity::class.java)
            startActivity(startIntent)
        })
        val button_check:Button = findViewById(R.id.button_check)
        button_check.setOnClickListener(View.OnClickListener { checklogs() })
        val buttonhelp:Button = findViewById(R.id.button_help)
        buttonhelp.setOnClickListener(View.OnClickListener {
            val toast =
                Toast.makeText(this@IssueActivity, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG)
            val view1 = toast.view
            view1?.background?.setColorFilter(Color.parseColor("#FF104162"), PorterDuff.Mode.SRC_IN)
            val text = view1?.findViewById<TextView>(android.R.id.message)
            text?.setTextColor(Color.WHITE)
            toast.show()
            val send = Intent(Intent.ACTION_SENDTO)
            val uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                    "?subject=" + Uri.encode("Notification Log App ") +
                    "&body=" + Uri.encode("Write Your Query Here")
            val uri = Uri.parse(uriText)
            send.data = uri
            startActivity(Intent.createChooser(send, "Send Mail Via : "))
        })
    }

    private fun checklogs() {
        val adapter = BrowseAdapter(this)
        val count = adapter.itemCount.toString()
        //Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
        logs = count.toInt()
        if (logs >= 1) {
            val startIntent = Intent(this@IssueActivity, NewMainActivity::class.java)
            startActivity(startIntent)
        } else if (logs == 0) {
            Toast.makeText(this, "No Notifications Logged Yet !", Toast.LENGTH_SHORT).show()
        }
    }
}