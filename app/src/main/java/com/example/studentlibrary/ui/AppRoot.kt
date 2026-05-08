package com.example.studentlibrary.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studentlibrary.ui.auth.AuthSession
import com.example.studentlibrary.ui.auth.UserRole
import com.example.studentlibrary.ui.navigation.Args
import com.example.studentlibrary.ui.navigation.Destinations
import com.example.studentlibrary.ui.screens.AdminRoute
import com.example.studentlibrary.ui.screens.BookDetailsRoute
import com.example.studentlibrary.ui.screens.FavoritesRoute
import com.example.studentlibrary.ui.screens.LibraryRoute
import com.example.studentlibrary.ui.screens.LoginRoute
import com.example.studentlibrary.ui.screens.ReaderRoute
import com.example.studentlibrary.ui.viewmodel.AdminViewModel
import com.example.studentlibrary.ui.viewmodel.BookDetailsViewModel
import com.example.studentlibrary.ui.viewmodel.FavoritesViewModel
import com.example.studentlibrary.ui.viewmodel.LibraryViewModel
import com.example.studentlibrary.ui.viewmodel.ReaderViewModel

@Composable
fun AppRoot() {
    var sessionRole by rememberSaveable { mutableStateOf<String?>(null) }

    if (sessionRole == null) {
        LoginRoute(
            onLogin = { role ->
                sessionRole = role.name
            },
        )
        return
    }

    val activeRole = sessionRole ?: return
    val session = AuthSession(role = UserRole.valueOf(activeRole))
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar =
        currentDestination?.route == Destinations.Library || currentDestination?.route == Destinations.Favorites

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items =
                        listOf(
                            Destinations.Library to Pair(Icons.Default.LibraryBooks, "Библиотека"),
                            Destinations.Favorites to Pair(Icons.Default.Favorite, "Избранное"),
                        )

                    items.forEach { (route, meta) ->
                        val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(meta.first, contentDescription = meta.second) },
                            label = { Text(meta.second) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Library,
            modifier = Modifier,
        ) {
            composable(Destinations.Library) {
                val vm: LibraryViewModel = hiltViewModel()
                LibraryRoute(
                    vm = vm,
                    session = session,
                    contentPadding = innerPadding,
                    onOpenDetails = { bookId ->
                        navController.navigate("${Destinations.Details}/$bookId")
                    },
                    onOpenAdmin = {
                        navController.navigate(Destinations.Admin)
                    },
                    onLogout = {
                        sessionRole = null
                    },
                )
            }
            composable(Destinations.Favorites) {
                val vm: FavoritesViewModel = hiltViewModel()
                FavoritesRoute(
                    vm = vm,
                    onLogout = {
                        sessionRole = null
                    },
                    contentPadding = innerPadding,
                    onOpenDetails = { bookId ->
                        navController.navigate("${Destinations.Details}/$bookId")
                    },
                )
            }
            composable(Destinations.Admin) {
                val vm: AdminViewModel = hiltViewModel()
                AdminRoute(
                    vm = vm,
                    contentPadding = innerPadding,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        sessionRole = null
                    },
                )
            }
            composable("${Destinations.Details}/{${Args.BookId}}") {
                val vm: BookDetailsViewModel = hiltViewModel()
                BookDetailsRoute(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onRead = { bookId ->
                        navController.navigate("${Destinations.Reader}/$bookId")
                    },
                )
            }
            composable("${Destinations.Reader}/{${Args.BookId}}") {
                val vm: ReaderViewModel = hiltViewModel()
                ReaderRoute(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
