package com.citoyen.jeparticipe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

import com.citoyen.jeparticipe.data.local.SessionManager
import com.citoyen.jeparticipe.data.repository.AuthRepository
import com.citoyen.jeparticipe.data.repository.SignalementRepository
import com.citoyen.jeparticipe.ui.agent.AgentHomeScreen
import com.citoyen.jeparticipe.ui.agent.AgentSignalementViewModel
import com.citoyen.jeparticipe.ui.agent.AgentSignalementViewModelFactory
import com.citoyen.jeparticipe.ui.admin.AdminHomeScreen
import com.citoyen.jeparticipe.ui.admin.AdminViewModel
import com.citoyen.jeparticipe.ui.admin.AdminViewModelFactory
import com.citoyen.jeparticipe.ui.register.RegisterScreen
import com.citoyen.jeparticipe.ui.register.RegisterViewModel
import com.citoyen.jeparticipe.ui.register.RegisterViewModelFactory
import com.citoyen.jeparticipe.ui.login.LoginScreen
import com.citoyen.jeparticipe.ui.login.LoginViewModel
import com.citoyen.jeparticipe.ui.login.LoginViewModelFactory
import com.citoyen.jeparticipe.ui.citoyen.CitoyenHomeScreen
import com.citoyen.jeparticipe.ui.citoyen.CreateSignalementScreen
import com.citoyen.jeparticipe.ui.citoyen.SignalementViewModel
import com.citoyen.jeparticipe.ui.citoyen.SignalementViewModelFactory
import com.citoyen.jeparticipe.ui.citoyen.MesSignalementsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var screen by remember { mutableStateOf("LOGIN") }

            val sessionManager = remember {
                SessionManager(this)
            }

            // ============ REPOSITORIES ============
            val authRepository = remember { AuthRepository(sessionManager) }          // ✅ Déclaré ici
            val signalementRepository = remember { SignalementRepository(sessionManager) }

            // ============ VIEWMODELS ============
            val loginFactory = remember {
                LoginViewModelFactory(
                    authRepository,
                    sessionManager
                )
            }
            val loginViewModel: LoginViewModel = viewModel(
                factory = loginFactory
            )

            val registerFactory = remember {
                RegisterViewModelFactory(
                    authRepository
                )
            }
            val registerViewModel: RegisterViewModel = viewModel(
                factory = registerFactory
            )

            // ✅ Pour le citoyen (SignalementViewModel)
            val signalementFactory = remember {
                SignalementViewModelFactory(
                    signalementRepository,
                    authRepository
                )
            }
            val signalementViewModel: SignalementViewModel = viewModel(
                factory = signalementFactory
            )

            // ✅ Pour l'agent (AgentSignalementViewModel)
            val agentFactory = remember {
                AgentSignalementViewModelFactory(
                    signalementRepository,
                    authRepository
                )
            }
            val agentViewModel: AgentSignalementViewModel = viewModel(
                factory = agentFactory
            )

            // ✅ Pour l'admin (AdminViewModel)
            val adminFactory = remember {
                AdminViewModelFactory(
                    signalementRepository,
                    authRepository
                )
            }
            val adminViewModel: AdminViewModel = viewModel(
                factory = adminFactory
            )

            // ✅ Fonction de déconnexion
            fun logout() {
                sessionManager.clearSession()
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            when (screen) {
                "LOGIN" -> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { role ->
                            screen = role
                        },
                        onRegisterClick = { screen = "REGISTER" }
                    )
                }

                "REGISTER" -> {
                    RegisterScreen(
                        viewModel = registerViewModel,
                        onRegisterSuccess = { screen = "LOGIN" },
                        onLoginClick = { screen = "LOGIN" }
                    )
                }

                "CITOYEN" -> {
                    CitoyenHomeScreen(
                        viewModel = signalementViewModel,
                        onCreateSignalement = { screen = "CREATE_SIGNALEMENT" },
                        onVoirSignalements = { screen = "MES_SIGNALEMENTS" },
                        onLogout = { logout() }
                    )
                }

                "MES_SIGNALEMENTS" -> {
                    MesSignalementsScreen(
                        viewModel = signalementViewModel,
                        onBack = { screen = "CITOYEN" }
                    )
                }

                "CREATE_SIGNALEMENT" -> {
                    CreateSignalementScreen(
                        viewModel = signalementViewModel,
                        onBack = { screen = "CITOYEN" }
                    )
                }

                "AGENT", "SERVICE_PUBLIC" -> {
                    AgentHomeScreen(
                        viewModel = agentViewModel,
                        onLogout = { logout() }
                    )
                }

                "ADMIN" -> {
                    AdminHomeScreen(
                        viewModel = adminViewModel,
                        onLogout = { logout() }
                    )
                }

                else -> {
                    screen = "LOGIN"
                }
            }
        }
    }
}