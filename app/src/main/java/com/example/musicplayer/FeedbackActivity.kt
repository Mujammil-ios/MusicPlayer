package com.example.musicplayer

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityFeedbackBinding
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedbackActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)

        supportActionBar?.title = "Feedback"

//        val feedbackMsg = binding.feedbackMsgFA.text.toString()+"\n"+binding.emailFA.text.toString()
//        val subject = binding.topicFA.text.toString()
        binding.sentBtnFA.setOnClickListener {
            val feedbackMsg = binding.feedbackMsgFA.text.toString()+"\n"+binding.emailFA.text.toString()
            val subject = binding.topicFA.text.toString()
            val userName  = "vv5106448@gmail.com"
            val pass = "vishal145236"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (feedbackMsg.isNotEmpty() && subject.isNotEmpty() && cm.activeNetworkInfo?.isConnectedOrConnecting == true)
            {
                Thread{
                    try {

                        val properties = Properties()
                        properties["main.smtp.auth"] = "true"
                        properties["main.smtp.starttls.enable"] = "true"
                        properties["main.smtp.host"] = "smtp.gmail.com"
                        properties["main.smtp.port"] = "587"

                        val session = Session.getInstance(properties,object: Authenticator()
                        {

                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(userName,pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject = subject
                        mail.setText(feedbackMsg)
                        mail.setFrom(InternetAddress(userName))
                      //  mail.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(userName))
                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName))
                        Transport.send(mail)



                    }catch (e: Exception){
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()

                    }
                }.start()
                Toast.makeText(this,"Thanks for Feedback!!",Toast.LENGTH_LONG).show()
                finish()

            }
            else {
                Toast.makeText(this, "Went Something Wrong!!!", Toast.LENGTH_LONG).show()
            }
        }

    }
}