package com.collabnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.collabnotes.model.Note
import com.collabnotes.ui.ForgotScreen
import com.collabnotes.ui.HomeScreen
import com.collabnotes.ui.LoginScreen
import com.collabnotes.ui.RegisterScreen
import com.collabnotes.ui.SettingsScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val systemDark = isSystemInDarkTheme()
            val useDark = isDarkTheme ?: systemDark
            val brandPurple = Color(0xFF7C4DFF)

            val colorScheme = if (useDark) {
                darkColorScheme(
                    primary = brandPurple,
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onPrimary = Color.White
                )
            } else {
                lightColorScheme(
                    primary = brandPurple,
                    background = Color(0xFFFFFFFF),
                    surface = Color(0xFFF8F9FA),
                    onPrimary = Color.White
                )
            }

            MaterialTheme(colorScheme = colorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        auth = auth,
                        isDarkTheme = useDark,
                        onThemeChanged = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    auth: FirebaseAuth,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    var currentScreen by remember {
        mutableStateOf(if (auth.currentUser != null) "home" else "login")
    }
    val notesListState = remember { mutableStateOf(listOf<Note>()) }

    when (currentScreen) {
        "login" -> LoginScreen(
            auth = auth,
            onLoginSuccess = { currentScreen = "home" },
            onNavigateToRegister = { currentScreen = "register" },
            onNavigateToForgot = { currentScreen = "forgot" } // Agora navega para o ecrã de recuperação
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = { currentScreen = "login" },
            onNavigateToLogin = { currentScreen = "login" }
        )
        "home" -> HomeScreen(
            auth = auth,
            notesState = notesListState,
            onLogout = { currentScreen = "login" },
            onNavigateToSettings = { currentScreen = "settings" }
        )
        "settings" -> SettingsScreen(
            isDarkTheme = isDarkTheme,
            onThemeChanged = onThemeChanged,
            onBack = { currentScreen = "home" }
        )
        "forgot" -> ForgotScreen(
            auth = auth,
            onNavigateBack = { currentScreen = "login" } // Volta corretamente para o login
        )
    }
}