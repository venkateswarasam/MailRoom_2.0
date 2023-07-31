package com.xcarriermaterialdesign

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class WebActvity : AppCompatActivity() {

    internal var webview: WebView?= null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_actvity)

        supportActionBar!!.hide()


        webview = findViewById(R.id.webview)


        webview!!.settings.javaScriptEnabled = true

        webview!!.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }


        webview!!.loadUrl("https://www.elemica.com/legal/privacy")

      //  webview!!.loadUrl("https://projects.invisionapp.com/share/4J12H6CU2F3R#/screens/465029788")


    }
}