package com.example.myapprecetas.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.myapprecetas.R

val fuenteTexto: FontFamily = FamilyQuicksand.quicksand

object FamilyQuicksand{

    val quicksand = FontFamily(
        Font(R.font.quicksandregular, FontWeight.Normal),
        Font(R.font.quicksandbold, FontWeight.Bold),
        Font(R.font.quicksandsemibold, FontWeight.SemiBold),
        Font(R.font.quicksandlight, FontWeight.Light),
    )
}
