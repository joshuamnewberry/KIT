package edu.gvsu.cis.kit

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gvsu.cis.kit.ui.CalendarScreen
import edu.gvsu.cis.kit.ui.ContactListScreen
import org.koin.compose.viewmodel.koinViewModel
import edu.gvsu.cis.kit.ui.HomeScreen
import edu.gvsu.cis.kit.ui.IndividualContactScreen
import edu.gvsu.cis.kit.ui.ManageRemindersScreen
import edu.gvsu.cis.kit.ui.SettingsScreen
import edu.gvsu.cis.kit.viewModels.CalendarViewModel
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import edu.gvsu.cis.kit.viewModels.HomeViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToContactList = { navController.navigate("contact list") },
                onNavigateToCalendar = { navController.navigate("calendar") }
            )
        }

        composable("settings") {
            val viewModel = koinViewModel<HomeViewModel>()
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.navigate("home") }
            )
        }

        composable("contact list") {
            val viewModel = koinViewModel<ContactsViewModel>()
            ContactListScreen(
                viewModel = viewModel,
                onNavigateToIndividualContact = { navController.navigate("individual contact") },
                onBack = { navController.navigate("home") }
            )
        }

        composable("individual contact") {
            val viewModel = koinViewModel<ContactsViewModel>()
            IndividualContactScreen(
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate("home") },
                onBack = { navController.navigate("contact list") }
            )
        }

        composable("calendar") {
            val viewModel = koinViewModel<CalendarViewModel>()
            CalendarScreen(
                viewModel = viewModel,
                onBack = { navController.navigate("home") }
            )
        }

        composable("contact list") {
            val viewModel = koinViewModel<CalendarViewModel>()
            ManageRemindersScreen(
                viewModel = viewModel,
                onBack = { navController.navigate("home") }
            )
        }
    }
}