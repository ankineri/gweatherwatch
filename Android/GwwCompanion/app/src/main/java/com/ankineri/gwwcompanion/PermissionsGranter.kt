package com.ankineri.gwwcompanion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.Intent




class PermissionsGranter : BroadcastReceiver() {
    var havePermissions = false
    fun getPermissions(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_PERMISSIONS_GRANTED)
        intentFilter.addAction(ACTION_PERMISSIONS_DENIED)
        context.registerReceiver(this, intentFilter)
        val intent = Intent(context, GetPermissionsActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(PERMISSIONS_KEY, arrayOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"))
        context.startActivity(intent)
        havePermissions = false
    }

    override fun onReceive(context: Context, intent: Intent) {
        when {
            intent.action == ACTION_PERMISSIONS_GRANTED -> {
                context.unregisterReceiver(this)
                onPermissionsGranted()
            }
            intent.action == ACTION_PERMISSIONS_DENIED -> {
                context.unregisterReceiver(this)
                onPermissionsDenied()
            }
        }
    }

    private fun onPermissionsGranted() {
        havePermissions = true
    }

    private fun onPermissionsDenied() {
        // ...
    }
}