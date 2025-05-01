package com.example.myapprecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.myapprecetas.views.*
import com.example.myapprecetas.views.detallesreceta.DetallesRecetaView
import com.example.myapprecetas.views.inicioview.InicioView
import com.example.myapprecetas.views.listadoreceta.ListadoRecetaView
import com.example.myapprecetas.views.perfil.PerfilView
import com.example.myapprecetas.views.registro.registro.RegistroView
import com.example.myapprecetas.views.registro.selector.SelectorRegistroView
import com.example.myapprecetas.views.viewlogin.ViewLogin
import com.example.myapprecetas.vm.VMDetallesReceta
import com.example.myapprecetas.vm.VMListadoReceta
import com.example.myapprecetas.vm.VMLogin
import com.example.myapprecetas.vm.VMPerfil
import com.example.myapprecetas.vm.VMRegistro
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val user = FirebaseAuth.getInstance().currentUser
            val startDestination = if (user != null) "lista_recetas" else "inicio"
            window.navigationBarColor = Color.Transparent.hashCode()
            val isScaffoldNeeded = currentRoute != "inicio" && currentRoute != "login" && currentRoute != "detalles_receta" && currentRoute != "selector_registro" && currentRoute != "registro"
            if (isScaffoldNeeded) {

                window.navigationBarColor = Color.Black.hashCode()
                MyapprecetasTheme {
                    Scaffold(
                        modifier = Modifier.padding(bottom = 0.dp),
                        containerColor = Colores.Blanco,
                        bottomBar = {
                            BottomBar(navController)
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding() / 2
                            )
                        ) {
                            composable("inicio") {
                                InicioView(navController)
                            }
                            composable("login") {
                                val vm: VMLogin = hiltViewModel()
                                ViewLogin(vm, navController)
                            }
                            composable("selector_registro") {
                                SelectorRegistroView(navController)
                            }
                            composable("registro") {
                                val vm: VMRegistro = hiltViewModel()
                                RegistroView(vm, navController)
                            }
                            composable("lista_recetas") {
                                val vm: VMListadoReceta = hiltViewModel()
                                ListadoRecetaView(vm, navController)
                            }
                            composable("detalles_receta") {
                                val vm: VMDetallesReceta = hiltViewModel()
                                DetallesRecetaView(vm, navController)
                            }
                            composable("perfil") {
                                val vm: VMPerfil = hiltViewModel()
                                PerfilView(vm, navController)
                            }
                            composable("construccion") {
                                PaginaEnConstruccionConBotonAtras(navController)
                            }
                            composable("construcciondos") {
                                PaginaEnConstrucciondosConBotonAtras(navController)
                            }
                        }
                    }
                }
            } else {
                // Si no es necesario Scaffold, simplemente mostramos la vista directamente
                MyapprecetasTheme {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(
                            top = 0.dp,
                            bottom = 0.dp
                        )
                    ) {
                        composable("inicio") {
                            InicioView(navController)
                        }
                        composable("login") {
                            val vm: VMLogin = hiltViewModel()
                            ViewLogin(vm, navController)
                        }
                        composable("selector_registro") {
                            SelectorRegistroView(navController)
                        }
                        composable("registro") {
                            val vm: VMRegistro = hiltViewModel()
                            RegistroView(vm, navController)
                        }
                        composable("lista_recetas") {
                            val vm: VMListadoReceta = hiltViewModel()
                            ListadoRecetaView(vm, navController)
                        }
                        composable("detalles_receta") {
                            val vm: VMDetallesReceta = hiltViewModel()
                            DetallesRecetaView(vm, navController)
                        }
                        composable("perfil") {
                            val vm: VMPerfil = hiltViewModel()
                            PerfilView(vm, navController)
                        }
                        composable("construccion") {
                            PaginaEnConstruccionConBotonAtras(navController)
                        }
                        composable("construcciondos") {
                            PaginaEnConstrucciondosConBotonAtras(navController)
                        }
                    }
                }
            }
        }
    }
}
