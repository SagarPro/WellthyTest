package sagar.wellthytest.utils

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import sagar.wellthytest.interfaces.AlertDialogListener

object ViewUtils {

    fun ProgressBar.show(){
        this.visibility = View.VISIBLE
    }

    fun ProgressBar.hide(){
        this.visibility = View.GONE
    }

    fun Context.showCustomMessage(title: String, message: String, listener: AlertDialogListener){
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    listener.userRequestedToGetCityName()
                }
                .show()
    }

}