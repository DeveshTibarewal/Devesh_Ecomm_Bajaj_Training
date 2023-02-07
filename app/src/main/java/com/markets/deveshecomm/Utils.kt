package com.markets.deveshecomm

import android.content.Context
import android.widget.Toast

/*A class that will contain static functions, constants, variables that will be used in whole application*/
class Utils {

    companion object {
        /** A function to show toast
        @param context the context of activity/fragment from where this function is called
        @param message the message to be shown in the Toast*/
        fun toast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}