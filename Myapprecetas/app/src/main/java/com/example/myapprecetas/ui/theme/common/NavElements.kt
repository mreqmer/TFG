package com.example.myapprecetas.ui.theme.common

import com.example.myapprecetas.R

sealed class BottomNavItem(val route: String, val label: String, val icon: Int, val selectedIcon: Int) {
    object ListadoRecetas : BottomNavItem(
        "lista_recetas",
        "Recetas",
        R.drawable.pot2,
        R.drawable.pot
    )
    object Favoritas : BottomNavItem(
        "lista_favoritos",
        "Favoritas",
        R.drawable.corazonvacio2,
        R.drawable.corazonvacio2
    )
    object Construccion : BottomNavItem(
        "perfil",
        "Perfil",
        R.drawable.user,
        R.drawable.usero
    )
}
