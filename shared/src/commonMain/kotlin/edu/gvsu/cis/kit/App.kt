package edu.gvsu.cis.kit

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gvsu.cis.kit.ui.CalendarScreen
import edu.gvsu.cis.kit.ui.ContactListScreen
import edu.gvsu.cis.kit.ui.HomeScreen
import edu.gvsu.cis.kit.ui.IndividualContactScreen
import edu.gvsu.cis.kit.ui.ManageRemindersScreen
import edu.gvsu.cis.kit.ui.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = koinViewModel(),
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToContactList = { navController.navigate("contactList") },
                onNavigateToCalendar = { navController.navigate("calendar") }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = koinViewModel(),
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
