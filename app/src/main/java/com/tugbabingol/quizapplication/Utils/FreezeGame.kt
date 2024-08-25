package com.tugbabingol.quizapplication.Utils

import android.widget.RelativeLayout

class FreezeGame() {
    fun freeze(selectedLayout: RelativeLayout){
        selectedLayout.isEnabled = false
    }
}