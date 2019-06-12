package com.trevorhalvorson.portal

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.io.File
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "MainActivity"
        private const val PERMISSIONS_REQUEST_CODE = 1
        private const val FILE_REQUEST_CODE = 2
        private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
        private const val PHOTOS = "photos"
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var output: File? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setSupportActionBar(toolbar)
        supportActionBar?.title = BuildConfig.TOOLBAR_TITLE

        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?):
                    Boolean {
                if (!BuildConfig.ALLOW_BROWSING) {
                    val originalHost = URI(BuildConfig.URL).host
                    val requestedHost = URI(request!!.url.toString()).host

                    return !originalHost!!.contentEquals(requestedHost)
                }
                return false
            }
        }

        web_view.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(webView: WebView?,
                                           callback: ValueCallback<Array<Uri>>?,
                                           fileChooserParams: FileChooserParams?): Boolean {
                filePathCallback = callback
                output = File(File(filesDir, PHOTOS),
                        "${BuildConfig.APPLICATION_ID}_${System.currentTimeMillis()}.jpg")
                if (output?.exists()!!) {
                    output?.delete()
                } else {
                    output?.parentFile?.mkdirs()
                }
                val outputUri = FileProvider
                        .getUriForFile(this@MainActivity, AUTHORITY, output!!)

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)

                val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
                fileIntent.type = "*/*"

                val chooserIntent = Intent
                        .createChooser(cameraIntent, getString(R.string.file_intent_chooser_title))
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        arrayOf(galleryIntent, fileIntent))
                startActivityForResult(chooserIntent, FILE_REQUEST_CODE)

                return true
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                if (BuildConfig.ENABLE_WEB_VIEW_LOGS && consoleMessage != null) {
                    consoleMessage.apply {
                        Log.i("$TAG-WebView", "${message()} -- From line ${lineNumber()} of ${sourceId()}")
                    }
                    return true
                }
                return false
            }
        }

        web_view.settings.javaScriptEnabled = true
        web_view.settings.builtInZoomControls = true
        web_view.settings.loadWithOverviewMode = true

        if (savedInstanceState == null) {
            web_view.loadUrl(BuildConfig.URL)
        }

        if (BuildConfig.ALLOW_FILE_UPLOAD) {
            for (permission in PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE)
                    break
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            FILE_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    val outputUri: Uri = if (intent?.data != null) {
                        // existing URI from storage
                        intent.data!!
                    } else {
                        // URI we created
                        FileProvider.getUriForFile(this, AUTHORITY, output!!)
                    }
                    filePathCallback?.onReceiveValue(arrayOf(outputUri))
                } else {
                    filePathCallback?.onReceiveValue(null)
                }
                filePathCallback = null
            }
            else -> {
                Log.d(TAG, "Activity result for $requestCode not handled")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                for (result in grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this,
                                getString(R.string.permission_denied_message),
                                Toast.LENGTH_LONG).show()
                        break
                    }
                }
            }
            else -> {
                Log.d(TAG, "Permissions result for $requestCode not handled")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        web_view.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (web_view.restoreState(savedInstanceState) == null) {
            web_view.loadUrl(BuildConfig.URL)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web_view.canGoBack()) {
            web_view.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> {
                web_view.loadUrl(BuildConfig.URL)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
