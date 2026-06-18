package edu.gvsu.cis.kit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import edu.gvsu.cis.kit.ui.CalendarScreen
import edu.gvsu.cis.kit.ui.ContactListScreen
import edu.gvsu.cis.kit.ui.HomeScreen
import edu.gvsu.cis.kit.ui.IndividualContactScreen
import edu.gvsu.cis.kit.ui.ManageRemindersScreen
import edu.gvsu.cis.kit.ui.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

sealed class TopLevelRoute(val name: String, val baseRoute: String, val icon: ImageVector) {
    object Home : TopLevelRoute("Home", "home", Icons.Default.Home)
    object Contacts : TopLevelRoute("Contacts", "contactList", Icons.Default.Person)
    object Calendar : TopLevelRoute("Calendar", "calendar", Icons.Default.DateRange)
    object Reminders : TopLevelRoute("Reminders", "manageReminders", Icons.Default.Notifications)
}

@Composable
fun App() {
    val navController = rememberNavController()

    val bottomNavigationItems = listOf(
        TopLevelRoute.Home,
        TopLevelRoute.Contacts,
        TopLevelRoute.Calendar,
        TopLevelRoute.Reminders
    )

    val navigateTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().route!!) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

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
                            selected = currentDestination?.hierarchy?.any { it.route?.startsWith(topLevelRoute.baseRoute) == true } == true,
                            onClick = { navigateTopLevel(topLevelRoute.baseRoute) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelRoute.Home.baseRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelRoute.Home.baseRoute) {
                HomeScreen(
                    viewModel = koinViewModel(),
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToContactList = { showAdd ->
                        val route = if (showAdd) "${TopLevelRoute.Contacts.baseRoute}?showAdd=true" else TopLevelRoute.Contacts.baseRoute
                        navigateTopLevel(route)
                    },
                    onNavigateToCalendar = { showAdd ->
                        val route = if (showAdd) "${TopLevelRoute.Calendar.baseRoute}?showAdd=true" else TopLevelRoute.Calendar.baseRoute
                        navigateTopLevel(route)
                    },
                    onNavigateToReminders = { showAdd ->
                        val route = if (showAdd) "${TopLevelRoute.Reminders.baseRoute}?showAdd=true" else TopLevelRoute.Reminders.baseRoute
                        navigateTopLevel(route)
                    }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.navigateUp() }
                )
            }
            composable(
                route = "${TopLevelRoute.Contacts.baseRoute}?showAdd={showAdd}",
                arguments = listOf(navArgument("showAdd") { defaultValue = false; type = NavType.BoolType })
            ) { backStackEntry ->
                val showAdd = backStackEntry.arguments?.getBoolean("showAdd") ?: false
                ContactListScreen(
                    viewModel = koinViewModel(),
                    initialShowAdd = showAdd,
                    onNavigateToIndividualContact = { navController.navigate("individualContact") },
                    onBack = { navController.navigateUp() }
                )
            }
            composable("individualContact") {
                IndividualContactScreen(
                    viewModel = koinViewModel(),
                    onNavigateToHome = {
                        navController.navigate(TopLevelRoute.Home.baseRoute) {
                            popUpTo(TopLevelRoute.Home.baseRoute) { inclusive = true }
                        }
                    },
                    onBack = { navController.navigateUp() }
                )
            }
            composable(
                route = "${TopLevelRoute.Calendar.baseRoute}?showAdd={showAdd}",
                arguments = listOf(navArgument("showAdd") { defaultValue = false; type = NavType.BoolType })
            ) { backStackEntry ->
                val showAdd = backStackEntry.arguments?.getBoolean("showAdd") ?: false
                CalendarScreen(
                    viewModel = koinViewModel(),
                    initialShowAdd = showAdd,
                    onBack = { navController.navigateUp() }
                )
            }
            composable(
                route = "${TopLevelRoute.Reminders.baseRoute}?showAdd={showAdd}",
                arguments = listOf(navArgument("showAdd") { defaultValue = false; type = NavType.BoolType })
            ) { backStackEntry ->
                val showAdd = backStackEntry.arguments?.getBoolean("showAdd") ?: false
                ManageRemindersScreen(
                    viewModel = koinViewModel(),
                    initialShowAdd = showAdd,
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}