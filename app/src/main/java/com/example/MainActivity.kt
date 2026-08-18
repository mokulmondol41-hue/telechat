package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.repository.BotRepository
import com.example.ui.screens.addbot.AddBotScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.chat.LiveChatScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.settings.BotSettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BotViewModel
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                TeleChatApp()
            }
        }
    }
}

@Composable
fun TeleChatApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context, coroutineScope) }
    val repository = remember { BotRepository(database) }
    val navController = rememberNavController()

    val botViewModel: BotViewModel = viewModel(
        factory = BotViewModel.provideFactory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) }
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = botViewModel,
                onNavigateToAddBot = {
                    navController.navigate("add_bot")
                },
                onNavigateToChat = { botId ->
                    navController.navigate("chat/$botId")
                },
                onNavigateToSettings = { botId ->
                    navController.navigate("settings/$botId")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable("add_bot") {
            AddBotScreen(
                viewModel = botViewModel,
                onNavigateBack = { navController.popBackStack() },
                onBotCreated = { newBotId ->
                    navController.navigate("chat/$newBotId") {
                        popUpTo("dashboard")
                    }
                }
            )
        }

        composable(
            route = "chat/{botId}",
            arguments = listOf(navArgument("botId") { type = NavType.LongType })
        ) { backStackEntry ->
            val botId = backStackEntry.arguments?.getLong("botId") ?: 1L
            val chatViewModel: ChatViewModel = viewModel(
                key = "chat_$botId",
                factory = ChatViewModel.provideFactory(repository, botId)
            )

            LiveChatScreen(
                viewModel = chatViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { bId ->
                    navController.navigate("settings/$bId")
                }
            )
        }

        composable(
            route = "settings/{botId}",
            arguments = listOf(navArgument("botId") { type = NavType.LongType })
        ) { backStackEntry ->
            val botId = backStackEntry.arguments?.getLong("botId") ?: 1L
            val settingsViewModel: SettingsViewModel = viewModel(
                key = "settings_$botId",
                factory = SettingsViewModel.provideFactory(repository, botId)
            )

            BotSettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onBotDeleted = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}
