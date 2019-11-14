package com.ankineri.gwwcompanion

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

internal const val PERMISSIONS_KEY = "permissions"
internal const val ACTION_PERMISSIONS_GRANTED = "GetPermissionsActivity.permissions_granted"
internal const val ACTION_PERMISSIONS_DENIED = "GetPermissionsActivity.permissions_denied"

class GetPermissionsActivity: AppCompatActivity() {

    private val permissionRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reqPerms = intent.getStringArrayExtra(PERMISSIONS_KEY)
        if (reqPerms == null) {
            val theIntent = Intent(this, LocationProviderService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(theIntent)
            } else {
                this.startService(theIntent)
            }
            finish()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    reqPerms,
                    permissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == permissionRequestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sendBroadcast(Intent(ACTION_PERMISSIONS_GRANTED))
            } else {
                sendBroadcast(Intent(ACTION_PERMISSIONS_DENIED))
            }
            finish()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
