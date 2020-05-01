package com.example.fillfeel

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher

class CreditCardDateTextFormatter : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        if (s!!.isNotEmpty() && (s.length % 3) == 0) {
            val c = s[s.length-1]
            if (c == '/') {
                s.delete(s.length-1, s.length)
            }
        }
        if (s.isNotEmpty() && (s.length % 3) == 0) {
            val c = s[s.length-1]
            if (Character.isDigit(c) && TextUtils.split(s.toString(), "/").size <= 2) {
                s.insert(s.length-1, "/")
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {}
}