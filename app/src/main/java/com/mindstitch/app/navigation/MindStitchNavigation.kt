package com.mindstitch.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mindstitch.app.ui.screens.CaptureScreen
import com.mindstitch.app.ui.screens.DetailScreen
import com.mindstitch.app.ui.screens.StatsScreen
import com.mindstitch.app.ui.screens.StreamScreen

sealed class Screen(val route: String) {
    data object Stream : Screen("stream")
    data object Capture : Screen("capture?editId={editId}") {
        fun createRoute(editId: Long? = null) = if (editId != null) "capture?editId=$editId" else "capture"
    }
    data object Detail : Screen("detail/{ideaId}") {
        fun createRoute(ideaId: String) = "detail/$ideaId"
    }
    data object Stats : Screen("stats")
    data object Profile : Screen("profile")
    data object Stitch : Screen("stitch")
}

@Composable
fun MindStitchNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Stream.route
    ) {
        composable(Screen.Stream.route) {
            StreamScreen(
                onNavigateToCapture = { navController.navigate(Screen.Capture.createRoute()) },
                onNavigateToDetail = { ideaId -> navController.navigate(Screen.Detail.createRoute(ideaId)) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onNavigateToProfile = { /* TODO */ },
                onNavigateToStitch = { /* TODO */ }
            )
        }
        composable(
            route = Screen.Capture.route,
            arguments = listOf(
                navArgument("editId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val editIdStr = backStackEntry.arguments?.getString("editId")
            val editId = editIdStr?.toLongOrNull()
            CaptureScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveAndStitch = { navController.popBackStack() },
                editIdeaId = editId
            )
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val ideaId = backStackEntry.arguments?.getString("ideaId") ?: ""
            DetailScreen(
                ideaId = ideaId,
                onNavigateBack = { navController.popBackStack() },
                onIterate = {
                    // 跳转到编辑模式
                    navController.navigate(Screen.Capture.createRoute(ideaId.toLongOrNull()))
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStream = { 
                    navController.navigate(Screen.Stream.route) {
                        popUpTo(Screen.Stream.route) { inclusive = true }
                    }
                },
                onNavigateToCapture = { navController.navigate(Screen.Capture.createRoute()) },
                onNavigateToProfile = { /* TODO */ },
                onNavigateToDetail = { ideaId -> 
                    navController.navigate(Screen.Detail.createRoute(ideaId.toString()))
                }
            )
        }
    }
}
