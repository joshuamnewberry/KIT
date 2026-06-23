package edu.gvsu.cis.kit

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gvsu.cis.kit.ui.CalendarScreen
import edu.gvsu.cis.kit.ui.ContactListScreen
import edu.gvsu.cis.kit.ui.HomeScreen
import edu.gvsu.cis.kit.ui.IndividualContactScreen
import edu.gvsu.cis.kit.ui.ManageRemindersScreen
import edu.gvsu.cis.kit.ui.SettingsScreen
import edu.gvsu.cis.kit.viewModels.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    val homeViewModel: HomeViewModel = koinViewModel()
    val isDarkMode by homeViewModel.isDarkMode.collectAsState()

    val colorScheme = if (isDarkMode) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToSettings = { navController.navigate("settings") },
                        onNavigateToContactList = { navController.navigate("contactList") },
                        onNavigateToCalendar = { navController.navigate("calendar") }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        viewModel = homeViewModel,
                        onBack = { navController.navigate("home") }
                    )
                }

                composable("contactList") {
                    ContactListScreen(
                        viewModel = koinViewModel(),
                        onNavigateToIndividualContact = { navController.navigate("individualContact") },
                        onBack = { navController.navigate("home") }
                    )
                }

                composable("individualContact") {
                    IndividualContactScreen(
                        viewModel = koinViewModel(),
                        onNavigateToHome = { navController.navigate("home") },
                        onBack = { navController.navigate("contactList") }
                    )
                }

                composable("calendar") {
                    CalendarScreen(
                        viewModel = koinViewModel(),
                        onBack = { navController.navigate("home") }
                    )
                }

                composable("manageReminders") {
                    ManageRemindersScreen(
                        viewModel = koinViewModel(),
                        onBack = { navController.navigate("home") }
                    )
                }
            }
        }
    }
}
