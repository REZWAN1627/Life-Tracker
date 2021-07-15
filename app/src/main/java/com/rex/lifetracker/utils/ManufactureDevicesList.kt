package com.rex.lifetracker.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.rex.lifetracker.BuildConfig

object ManufactureDevicesList {

    fun Huawei(context: Context){

        val intent: Intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.systemmanager.optimize.process.ProtectActivity"
        )
        intent.component = comp
        context.startActivity(intent)
    }
    fun Meizu(context: Context){
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        context.startActivity(intent)
    }
    fun Millet(context: Context){
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        val componentName = ComponentName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
        )
        intent.component = componentName
        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID)
        context.startActivity(intent)
    }
    fun Sony(context: Context){
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
        intent.component = comp
        context.startActivity(intent)
    }
    fun OPPO(context: Context){
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName(
            "com.color.safecenter",
            "com.color.safecenter.permission.PermissionManagerActivity"
        )
        intent.component = comp
        context.startActivity(intent)
    }
    fun LG(context: Context){
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$AccessLockSummaryActivity"
        )
        intent.component = comp
        context.startActivity(intent)


    }
    fun Vivo(context: Context){
        val intent: Intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName(
            "com.vivo.permissionmanager",
            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
        )
        intent.component = comp
        context.startActivity(intent)
    }
    fun Asus(context: Context){
        val intent: Intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName(
            "com.asus.mobilemanager",
            "com.asus.mobilemanager.autostart.AutoStartActivity"
        )
        intent.component = comp
        context.startActivity(intent)

    }
    fun Lenovo(context: Context){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        context.startActivity(intent)

    }
    fun Xiaomi(context: Context){
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID)
        context.startActivity(intent)
    }
}