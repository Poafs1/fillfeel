package com.example.fillfeel

import java.security.Timestamp
import java.util.*

class DetailsObject (
    var account: String? = null,
    var backers: Int? = 0,
    var details: String? = null,
    var donate: Double? = 0.0,
    var goal: Double? = 0.0,
    var img: String? = null,
    var overview: String? = null,
    var paletteImage: String? = null,
    var period: Long? = null,
    var plan: String? = null,
    var rate: Double? = 0.0,
    var status: Boolean? = false,
    var tag: String? = null,
    var timestamps: Long? = null,
    var title: String? = null
)