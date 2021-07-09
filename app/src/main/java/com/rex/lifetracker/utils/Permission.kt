package com.rex.lifetracker.utils

import android.Manifest
import android.content.Context
import android.os.Build
import com.vmadalin.easypermissions.EasyPermissions

object Permission {
     fun hasPermission(context: Context) =
         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
             EasyPermissions.hasPermissions(
                 context,
                 Manifest.permission.CALL_PHONE,
                 Manifest.permission.READ_PHONE_STATE,
                 Manifest.permission.READ_CALL_LOG,
                 Manifest.permission.SEND_SMS,
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.FOREGROUND_SERVICE
             )
         }else{
             EasyPermissions.hasPermissions(
                 context,
                 Manifest.permission.CALL_PHONE,
                 Manifest.permission.READ_PHONE_STATE,
                 Manifest.permission.READ_CALL_LOG,
                 Manifest.permission.SEND_SMS,
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.FOREGROUND_SERVICE,
                 Manifest.permission.ACCESS_BACKGROUND_LOCATION
             )
         }

}