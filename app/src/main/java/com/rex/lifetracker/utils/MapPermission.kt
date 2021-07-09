package com.rex.lifetracker.utils

import android.Manifest
import android.content.Context
import android.os.Build
import com.vmadalin.easypermissions.EasyPermissions

object MapPermission {
     fun hasPermission(context: Context) =
         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
             EasyPermissions.hasPermissions(
                 context,
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
             )
         }else{
             EasyPermissions.hasPermissions(
                 context,
                 Manifest.permission.ACCESS_COARSE_LOCATION,
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.ACCESS_BACKGROUND_LOCATION
             )
         }
}