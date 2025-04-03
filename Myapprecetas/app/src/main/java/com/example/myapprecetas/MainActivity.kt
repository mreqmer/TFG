package com.example.myapprecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapprecetas.ui.theme.MyapprecetasTheme
import com.example.myapprecetas.views.PaginaEnConstruccionConBotonAtras
import com.example.myapprecetas.views.viewlogin.ViewLogin
import com.example.myapprecetas.vm.VMLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MyapprecetasTheme {
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable(route = "login") {
                        val vm: VMLogin = hiltViewModel()
                        ViewLogin(vm, navController)
                    }
                    composable("construccion") {
                        PaginaEnConstruccionConBotonAtras(navController)
                    }

                }
            }
        }
    }
}
