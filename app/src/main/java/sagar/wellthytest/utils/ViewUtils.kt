package sagar.wellthytest.utils

import android.view.View
import android.widget.ProgressBar

object ViewUtils {

    fun ProgressBar.show(){
        this.visibility = View.VISIBLE
    }

    fun ProgressBar.hide(){
        this.visibility = View.GONE
    }

}