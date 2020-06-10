package com.example.downloadpdf

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE: Int = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pdfDownloadBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED
                ) {
                    requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            STORAGE_PERMISSION_CODE
                    )
                } else {
                    startDownloading()
                }
            } else {
                startDownloading()
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    startDownloading()
                    Toast.makeText(
                            this, "Permission Accepted !",
                            Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                            this, "Permission Denied !",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startDownloading() {
        val url = "https://books.goalkicker.com/AndroidBook/AndroidNotesForProfessionals.pdf"
        val request = DownloadManager.Request(Uri.parse(url))
        request.apply {
            setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
            )
            setTitle("PDF Book Download")
            setDescription("PDF Book Is Downloading...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "${System.currentTimeMillis()}"
            )
        }
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as
                DownloadManager
        val downloadId = downloadManager.enqueue(request)
        val downloadManagerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val intentDownloadId: Long? = intent?.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID,
                        -1
                )
                if (intentDownloadId == downloadId) {
                    Toast.makeText(
                            applicationContext, "PDF Book Download Completed !",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        registerReceiver(
                downloadManagerBroadcastReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }
}
