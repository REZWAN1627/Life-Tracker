package com.rex.lifetracker.utils

import android.app.AlertDialog
import android.content.Context
import dmax.dialog.SpotsDialog

object LoadingDialog {
    private lateinit var loadingDialog: AlertDialog

    fun loadingDialogStart(context: Context, loadingContent: Int) {
        loadingDialog = SpotsDialog.Builder().setContext(context).setTheme(loadingContent)
            .setCancelable(false)
            .build()
        loadingDialog.show()
    }

    fun loadingDialogStop() {
        loadingDialog.dismiss()
    }
}