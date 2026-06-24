package edu.gvsu.cis.kit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.gvsu.cis.kit.ui.*
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import edu.gvsu.cis.kit.viewModels.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed class TopLevelRoute(val name: String, val baseRoute: String, val icon: ImageVector) {
    object Home : TopLevelRoute("Home", "home", Icons.Default.Home)
    object Contacts : TopLevelRoute("Contacts", "contactList", Icons.Default.Person)
    object Reminders : TopLevelRoute("Reminders", "manageReminders", Icons.Default.Notifications)
}

@Composable
fun App() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = koinViewModel()
    val isDarkMode by homeViewModel.isDarkMode.collectAsState()

    val bottomNavigationItems = listOf(
        TopLevelRoute.Home,
        TopLevelRoute.Contacts,
        TopLevelRoute.Reminders
    )

    MaterialTheme(colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val showBottomBar = bottomNavigationItems.any { routeObj ->
                    currentDestination?.route?.startsWith(routeObj.baseRoute) == true
                }

                if (showBottomBar) {
                    NavigationBar {
                        bottomNavigationItems.forEach { topLevelRoute ->
                            NavigationBarItem(
                                icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                                label = { Text(topLevelRoute.name) },
                                selected = currentDestination?.hierarchy?.any {
                                    it.route?.startsWith(topLevelRoute.baseRoute) == true
                                } == true,
                                onClick = {
                                    navController.navigate(topLevelRoute.baseRoute) {
                                        popUpTo(navController.graph.findStartDestination().route!!) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = TopLevelRoute.Home.baseRoute,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                composable(TopLevelRoute.Home.baseRoute) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToSettings = { navController.navigate("settings") }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        viewModel = homeViewModel,
                        onBack = { navController.navigateUp() }
                    )
                }

                composable(
                    route = "${TopLevelRoute.Contacts.baseRoute}?showAdd={showAdd}",
                    arguments = listOf(
                        navArgument("showAdd") {
                            defaultValue = false
                            type = NavType.BoolType
                        }
                    )
                ) { backStackEntry ->
                    ContactListScreen(
                        viewModel = koinViewModel(),
                        initialShowAdd = backStackEntry.arguments?.get("showAdd") as? Boolean ?: false,
                        onNavigateToIndividualContact = { contactId ->
                            navController.navigate("individualContact/$contactId")
                        },
                        onNavigateToAddContact = { navController.navigate("addContact") },
                        onBack = { navController.navigateUp() }
                    )
                }

                composable("addContact") {
                    AddContactScreen(
                        viewModel = koinViewModel(),
                        onBack = { navController.navigateUp() }
                    )
                }

                composable(
                    route = "individualContact/{contactId}",
                    arguments = listOf(
                        navArgument("contactId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val contactId = backStackEntry.arguments?.get("contactId") as? String ?: return@composable
                    val viewModel: ContactsViewModel = koinViewModel()

                    LaunchedEffect(contactId) {
                        viewModel.selectContact(contactId)
                    }

                    IndividualContactScreen(
                        viewModel = viewModel,
                        onNavigateToHome = {
                            navController.navigate(TopLevelRoute.Home.baseRoute) {
                                popUpTo(TopLevelRoute.Home.baseRoute) { inclusive = true }
                            }
                        },
                        onBack = { navController.navigateUp() }
                    )
                }

                composable(
                    route = "${TopLevelRoute.Reminders.baseRoute}?showAdd={showAdd}",
                    arguments = listOf(
                        navArgument("showAdd") {
                            defaultValue = false
                            type = NavType.BoolType
                        }
                    )
                ) { backStackEntry ->
                    ManageRemindersScreen(
                        viewModel = koinViewModel(),
                        initialShowAdd = backStackEntry.arguments?.get("showAdd") as? Boolean ?: false,
                        onBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}