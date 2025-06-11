package com.example.myapprecetas.ui.theme.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.FamilyQuicksand

@Composable
fun BottomBar(navController: NavHostController) {
    //Elementos que aparecen en el menú de nevegación inferior
    val items = listOf(
        BottomNavItem.ListadoRecetas,
        BottomNavItem.Favoritas,
        BottomNavItem.Construccion
    )
    // Obtenemos la ruta actual para saber que ítem está seleccionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Barra de navegación
    NavigationBar(
        containerColor = Colores.VerdeClaro,
        tonalElevation = 0.dp,
        modifier = Modifier.height(120.dp)
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
    ) {
        items.forEach { item ->
            //Comprueba que está seleccionado
            val selected = currentRoute == item.route

            // Cada ítem de la barra de navegación
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) item.selectedIcon else item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(ConstanteIcono.IconoNormal)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontFamily = FamilyQuicksand.quicksand,
                        fontSize = ConstanteTexto.TextoMuyPequeno,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                selected = selected,
                onClick = {
                    // Solo navega si no está ya en esa ruta
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo("lista_recetas") { inclusive = true }
                            launchSingleTop = false
                        }
                    }
                },
                //Colores
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Colores.Negro,
                    unselectedIconColor = Colores.Gris,
                    selectedTextColor = Colores.Negro,
                    unselectedTextColor = Colores.Gris,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
