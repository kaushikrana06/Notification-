package com.appnotification.notificationhistorylog.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.appnotification.notificationhistorylog.BuildConfig
import com.appnotification.notificationhistorylog.R
import com.google.android.material.textfield.TextInputEditText

class HelpActivity : AppCompatActivity(), View.OnClickListener {
    private var editTextName: TextInputEditText? = null
    private var editTextEmail: TextInputEditText? = null
    private var editTextCountry: TextInputEditText? = null
    private var editTextQuery: TextInputEditText? = null
    private var radioGroup: RadioGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val backButton = toolbar.findViewById<ImageView>(R.id.back_image)
        val titleTextView = toolbar.findViewById<TextView>(R.id.title_text)
        val searchButton = toolbar.findViewById<ImageView>(R.id.search_image)
        val menuButton = toolbar.findViewById<ImageView>(R.id.menu_image)
        searchButton.visibility = View.GONE
        menuButton.visibility = View.INVISIBLE
        titleTextView.text = getString(R.string.title_help)
        backButton.setOnClickListener { finish() }
        editTextName = findViewById(R.id.name)
        editTextCountry = findViewById(R.id.country)
        editTextEmail = findViewById(R.id.email)
        editTextQuery = findViewById(R.id.query)
        radioGroup = findViewById(R.id.group_query)
        val buttonSend = findViewById<Button>(R.id.button_send)
        buttonSend.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val textPattern = "^[\\p{L} .'-]+$"
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val name = editTextName?.text.toString()
        val email = editTextEmail?.text.toString()
        val country = editTextCountry?.text.toString()
        val query = editTextQuery?.text.toString()
        val selectedRadio = radioGroup?.checkedRadioButtonId
        var queryType = ""
        queryType = if (selectedRadio == R.id.query_general) {
            "General"
        } else if (selectedRadio == R.id.query_technical) {
            "Technical"
        } else {
            "Other"
        }
        if (name.matches(textPattern.toRegex())) {
            if (email.matches(emailPattern.toRegex())) {
                if (country.matches(textPattern.toRegex())) {
                    if (query != "") {
                        try {
                            val send = Intent(Intent.ACTION_SENDTO)
                            val uriText = "mailto:" + Uri.encode("notificationappgp@gmail.com") +
                                    "?subject=" + Uri.encode("$email - Notification Log App") +
                                    "&body=" + Uri.encode(
                                """Name: $name Country: $country Query Type: $queryType Query: $query  ------------ 

 Version Code : ${BuildConfig.VERSION_CODE}
 Version Name : ${BuildConfig.VERSION_NAME}
 Build : ${Build.BRAND}
${Build.MODEL}
${Build.DEVICE}"""
                            )
                            val uri = Uri.parse(uriText)
                            send.data = uri
                            send.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivityForResult(
                                Intent.createChooser(send, "Send Mail Via : "),
                                121
                            )
                        } catch (ignore: Exception) {
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please add some query to assist with!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Invalid Country!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid Email!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Invalid name!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //        if (requestCode == 121) {
//            finish();
////            if (resultCode == RESULT_OK) {
////                Toast.makeText(getApplicationContext(), getString(R.string.message_query_send), Toast.LENGTH_SHORT).show();
////                finish();
////            } else {
////                Toast.makeText(getApplicationContext(), getString(R.string.message_help_error), Toast.LENGTH_SHORT).show();
////            }
//        }
    }
}