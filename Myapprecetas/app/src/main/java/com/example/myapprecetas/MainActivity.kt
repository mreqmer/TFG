package com.example.myapprecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapprecetas.ui.theme.MyapprecetasTheme
import com.example.myapprecetas.views.ViewLogin
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
                    // Pantalla Login (con Hilt)
                    composable(route = "login") {
                        val vm: VMLogin = hiltViewModel() // ViewModel con inyección
                        ViewLogin(vm, navController)
                    }

                }
            }
        }
    }
}
