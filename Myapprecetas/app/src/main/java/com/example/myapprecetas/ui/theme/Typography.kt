package com.example.myapprecetas.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.myapprecetas.R


object FamilyBaloo{

    val baloo = FontFamily(
        Font(R.font.balooregular, FontWeight.Normal),
        Font(R.font.baloobold, FontWeight.Bold),
    )
}

object FamilyQuicksand{

    val quicksand = FontFamily(
        Font(R.font.quicksandregular, FontWeight.Normal),
        Font(R.font.quicksandbold, FontWeight.Bold),
        Font(R.font.quicksandsemibold, FontWeight.SemiBold),
        Font(R.font.quicksandlight, FontWeight.Light),
    )
}

//object FamilyBaloo {
//    val baloo = FontFamily(
//        Font(R.font.balooregular, FontWeight.Normal), // Regular
//        Font(R.font.baloo_bold, FontWeight.Bold),    // Bold
//        Font(R.font.baloo_italic, FontWeight.Normal) // Italic
//    )
//}