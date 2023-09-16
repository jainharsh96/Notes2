package com.harsh.notes.ui

//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//
//
//@Composable
//fun NotesNavigationGraph(
//    modifier: Modifier = Modifier,
//    navController: NavHostController,
//    startDestination: String = NotesNavigation.NotesScreen.path()
//) {
//    NavHost(navController = navController, startDestination = startDestination) {
//        composable(
//            route = NotesNavigation.NotesScreen.destination,
//            arguments = NotesNavigation.NotesScreen.arguments
//        ) {
//
//        }
//        composable(
//            route = NotesNavigation.CreateNotesScreen.destination,
//            arguments = NotesNavigation.CreateNotesScreen.arguments
//        ) {
//
//        }
//        composable(
//            route = NotesNavigation.NotesSettingScreen.destination,
//            arguments = NotesNavigation.NotesSettingScreen.arguments
//        ) {
//
//        }
//    }
//}