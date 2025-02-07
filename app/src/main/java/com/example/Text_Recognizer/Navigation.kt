package com.example.Text_Recognizer

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Newspaper
import com.example.Text_Recognizer.textrecognition.CameraTextRecognitionScreen
import com.example.Text_Recognizer.textrecognition.DocumentEditingScreen
import com.example.Text_Recognizer.textrecognition.DocumentSharedViewModel
import com.example.Text_Recognizer.textrecognition.LandingScreen
import com.example.Text_Recognizer.textrecognition.ThemeViewModel
import com.example.settingsscreen.SettingsScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Scan : Screen("scan")
    object PDF : Screen("document")
    object Settings : Screen("settings")
}

private val screenOrder = listOf(
    Screen.Landing.route,
    Screen.Scan.route,
    Screen.PDF.route
)

private fun getScreenIndex(route: String?): Int =
    screenOrder.indexOf(route).takeIf { it >= 0 } ?: 0

private fun customEnterTransition(
    initialRoute: String?,
    targetRoute: String?
): EnterTransition {
    val initialIndex = getScreenIndex(initialRoute)
    val targetIndex = getScreenIndex(targetRoute)
    return if (targetIndex > initialIndex) {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        )
    } else {
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(300)
        )
    }
}

private fun customExitTransition(
    initialRoute: String?,
    targetRoute: String?
): ExitTransition {
    val initialIndex = getScreenIndex(initialRoute)
    val targetIndex = getScreenIndex(targetRoute)
    return if (targetIndex > initialIndex) {

        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    } else {

        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(themeViewModel: ThemeViewModel) {
    val documentSharedViewModel: DocumentSharedViewModel = viewModel()

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerEnabled = (currentRoute == Screen.Landing.route || currentRoute == Screen.Settings.route)

    // Bottom nav visas bara i Landing och  Document screns
    val bottomNavRoutes = listOf(Screen.Landing.route, Screen.PDF.route)

    fun navigateTo(route: String) {
        scope.launch {
            drawerState.close()
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    // olika topp bars till varje skärm
    @Composable
    fun MyTopBar() {
        when (currentRoute) {
            Screen.Scan.route -> {
                CenterAlignedTopAppBar(
                    title = { Text("Scan text") },
                    navigationIcon = {
                        TextButton(onClick = { navigateTo(Screen.Landing.route) }) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Home icon")
                            Spacer(Modifier.width(4.dp))
                            Text("Home")
                        }
                    },
                    actions = {
                        TextButton(onClick = { navigateTo(Screen.PDF.route) }) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next icon")
                            Spacer(Modifier.width(4.dp))
                            Text("Next")
                        }
                    }
                )
            }
            Screen.Landing.route -> {
                TopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Icon")
                        }
                    }
                )
            }
            Screen.PDF.route -> {
                TopAppBar(title = { Text("Export to PDF") })
            }
            // settings tppappbar
            Screen.Settings.route -> {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Icon")
                        }
                    }
                )
            }
            else -> {
                TopAppBar(title = { Text("TextScan") })
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerEnabled,
        drawerContent = {
            // Visar endast navdrawer i landingpage och settings
            if (drawerEnabled) {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Drawer",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider()

                        NavigationDrawerItem(
                            label = { Text("Home") },
                            selected = (currentRoute == Screen.Landing.route),
                            icon = {
                                Icon(Icons.Default.AccountBox, contentDescription = "Landing")
                            },
                            onClick = { navigateTo(Screen.Landing.route) }
                        )

                        // Use a Settings icon here
                        NavigationDrawerItem(
                            label = { Text("Settings") },
                            selected = (currentRoute == Screen.Settings.route),
                            icon = {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            },
                            onClick = { navigateTo(Screen.Settings.route) }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = { MyTopBar() },
            bottomBar = {
                if (currentRoute in bottomNavRoutes) {
                    MyBottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = ::navigateTo
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Landing.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // home skärnm, enter exit animationer baserat på vilket håll man navigerar
                composable(Screen.Landing.route,
                    enterTransition = {
                        customEnterTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    exitTransition = {
                        customExitTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    popEnterTransition = {
                        customEnterTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    popExitTransition = {
                        customExitTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    }
                ) {
                    LandingScreen()
                }
                // kamera skärm, enter exit animationer baserat på vilket håll man navigerar
                composable(Screen.Scan.route,
                    enterTransition = {
                        customEnterTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    exitTransition = {
                        customExitTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    popEnterTransition = {
                        customEnterTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    popExitTransition = {
                        customExitTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    }
                ) {
                    CameraTextRecognitionScreen(documentSharedViewModel)
                }
                // PDF skärm, enter exit animationer baserat på vilket håll man navigerar
                composable(Screen.PDF.route,
                    enterTransition = {
                        customEnterTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    exitTransition = {
                        customExitTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    popEnterTransition = {
                        customEnterTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    },
                    popExitTransition = {
                        customExitTransition(
                            initialState.destination.route,
                            targetState.destination.route
                        )
                    }
                ) {
                    DocumentEditingScreen(documentSharedViewModel)
                }

                // Settings screen med default animation
                composable(Screen.Settings.route) {
                    SettingsScreen(themeViewModel = themeViewModel)
                }
            }
        }
    }
}

@Composable
fun MyBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        // to the left landing page
        NavigationBarItem(
            selected = (currentRoute == Screen.Landing.route),
            onClick = { onNavigate(Screen.Landing.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        // in the middle camera text recognition
        NavigationBarItem(
            selected = (currentRoute == Screen.Scan.route),
            onClick = { onNavigate(Screen.Scan.route) },
            icon = { Icon(Lucide.Camera, contentDescription = "Scan") },
            label = { Text("TextScan") }
        )
        // to the right Edit document screen
        NavigationBarItem(
            selected = (currentRoute == Screen.PDF.route),
            onClick = { onNavigate(Screen.PDF.route) },
            icon = { Icon(Lucide.Newspaper, contentDescription = "PDF") },
            label = { Text("Export to PDF") }
        )
    }
}
