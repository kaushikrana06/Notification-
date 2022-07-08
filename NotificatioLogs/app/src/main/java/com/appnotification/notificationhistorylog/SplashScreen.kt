package com.appnotification.notificationhistorylog

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appnotification.notificationhistorylog.ui.MainActivity
import pl.droidsonroids.gif.GifTextView

class SplashScreen : AppCompatActivity() {
    var splashinternet: GifTextView? = null
    var splash: GifTextView? = null
    var splashTread: Thread? = null
    var mprogreeinternet: ProgressDialog? = null
    var txt: TextView? = null
    var txtversion: TextView? = null
    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME
    var btnretry: ImageButton? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        doFirstRunsecond()
        splash = findViewById(R.id.splashscreen)
        val txt: TextView = findViewById(R.id.txtint)
        val txtversion: TextView = findViewById(R.id.txtversion)
        txtversion.text = "v$versionName"
        val btnretry: ImageButton = findViewById(R.id.btnretry)
        txt.visibility = View.GONE
        btnretry.visibility = View.GONE
        splashTread = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    // Splash screen pause time
                    while (waited < 3600) {
                        sleep(100)
                        waited += 100
                    }
                    val intent = Intent(
                        this@SplashScreen,
                        MainActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                    finish()
                } catch (e: InterruptedException) {
                    // do nothing
                } finally {
                    finish()
                }
            }
        }
        (splashTread as Thread).start()
    }

    private fun doFirstRunsecond() {
        val settings = getSharedPreferences("FIRSTRUNTEXT311", MODE_PRIVATE)
        if (settings.getBoolean("isFirstRunDialogBoxtext311", true)) {
            val editor = settings.edit()
            editor.putBoolean("isFirstRunDialogBoxtext311", false)
            editor.commit()

            /*    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);

            builder.setIcon(R.drawable.ic_gif_black_24dp);

            builder.setTitle("Welcome !");
            builder.setMessage("We Will Need Camera And Internal Storage Permission To Make GIFs");

            builder.setCancelable(false);

            builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


*/


            /*  AlertDialog.Builder builder = new AlertDialog.Builder(TextActivity.this);

            builder.setIcon(R.drawable.iconshandwrit52);

            builder.setTitle("Tip ");
            builder.setMessage("You Can Copy Your Response, You Can Share Your Response \n And You Can Copy Translated Text Also");

            builder.setCancelable(false);

            builder.setPositiveButton("DEMO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {



                    new FancyShowCaseView.Builder(TextActivity.this)
                            .focusOn(findViewById(R.id.action_copy_text))
                            .focusCircleRadiusFactor(1.0)
                            .title("\n\n\n Click Here To Copy Your Response")
                            .titleStyle(R.style.TextStyle, Gravity.BOTTOM| Gravity.BOTTOM)
                            .build()
                            .show();
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();*/
        }
    }
}