package com.example.myapprecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapprecetas.ui.theme.Colores
import com.example.myapprecetas.ui.theme.MyapprecetasTheme
import com.example.myapprecetas.ui.theme.common.BottomBar
import com.example.myapprecetas.userauth.AuthManager
import com.example.myapprecetas.views.*
import com.example.myapprecetas.views.creacionreceta.AddRecetaScreen
import com.example.myapprecetas.views.creacionreceta.AddRecetaView
import com.example.myapprecetas.views.creacionreceta.CrearRecetaView
import com.example.myapprecetas.views.detallesreceta.DetallesRecetaView
import com.example.myapprecetas.views.inicioview.InicioView
import com.example.myapprecetas.views.listadoreceta.ListadoRecetaView
import com.example.myapprecetas.views.perfil.PerfilView
import com.example.myapprecetas.views.registro.registro.RegistroView
import com.example.myapprecetas.views.registro.selector.SelectorRegistroView
import com.example.myapprecetas.views.viewlogin.ViewLogin
import com.example.myapprecetas.vm.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        // Rutas que deben usar Scaffold con barra inferior
        private val RUTAS_CON_SCAFFOLD = setOf(
            "lista_recetas",
            "detalles_receta",
            "perfil",
            "construccion",
            "construcciondos"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Listener para saber si está el usuario logeado, dependiendo de si lo está muestra
            //una pantalla u otra
            val user by AuthManager.currentUser.collectAsState()
            val startDestination = if (user != null) "lista_recetas" else "inicio"

            // Comprueba si se necesita scaffold
            val isScaffoldNeeded = currentRoute in RUTAS_CON_SCAFFOLD

            // Dependiendo de la pantalla la botonera de navegación cambia el color
            window.navigationBarColor = if (isScaffoldNeeded) Color.Black.hashCode() else Color.Transparent.hashCode()

            MyapprecetasTheme {
                val navHostContent = @Composable { modifier: Modifier ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = modifier
                    ) {
                        // Pantallas de la app

                        composable("inicio") {
                            InicioView(navController)
                        }

                        composable("login") {
                            val vm: VMLogin = hiltViewModel()
                            ViewLogin(vm, navController)
                        }

                        composable("selector_registro") {
                            val vm: VMSelectorRegistro = hiltViewModel()
                            SelectorRegistroView(vm, navController)
                        }

                        composable("registro") {
                            val vm: VMRegistro = hiltViewModel()
                            RegistroView(vm, navController)
                        }

                        composable("lista_recetas") {
                            val vm: VMListadoReceta = hiltViewModel()
                            ListadoRecetaView(vm, navController)
                        }

                        composable("detalles_receta/{idReceta}") { backStackEntry ->
                            val idReceta = backStackEntry.arguments?.getString("idReceta")?.toIntOrNull() ?: -1
                            val vm: VMDetallesReceta = hiltViewModel()
                            DetallesRecetaView(vm, navController)
                            vm.setRecetaId(idReceta)
                        }
                        composable("perfil") {
                            val vm: VMPerfil = hiltViewModel()
                            PerfilView(vm, navController)
                        }

                        composable("creacionReceta") {
                            val vm: VMCreacionReceta = hiltViewModel()
                            CrearRecetaView(vm, navController)
                        }
                        composable("addIngrediente") {
                            val vm: VMCreacionReceta = hiltViewModel()
                            AddRecetaView(vm, navController)
                        }

                        composable("construccion") {
                            PaginaEnConstruccionConBotonAtras(navController)
                        }

                        composable("construcciondos") {
                            val vm: VMConstrucciondos = hiltViewModel()
                            PaginaEnConstrucciondosConBotonAtras(vm,navController)
                        }
                    }
                }

                // Si necesita Scaffold, lo aplicamos
                if (isScaffoldNeeded) {
                    Scaffold(
                        modifier = Modifier.padding(bottom = 0.dp),
                        containerColor = Colores.Blanco,
                        bottomBar = { BottomBar(navController) }
                    ) { innerPadding ->
                        navHostContent(
                            Modifier.padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding() / 2
                            )
                        )
                    }
                } else {
                    // Si no, solo mostramos el contenido
                    navHostContent(Modifier)
                }
            }
        }
    }
}
