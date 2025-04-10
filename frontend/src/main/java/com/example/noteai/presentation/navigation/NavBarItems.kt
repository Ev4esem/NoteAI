package com.example.noteai.presentation.navigation

import com.example.noteai.R
import com.example.noteai.domain.entity.BottomNavItem
import com.example.noteai.utils.Constants

object NavBarItems {
    val BottomNavItems = listOf(
        BottomNavItem(
            title = Constants.TAB_HOME,
            image = R.drawable.subtract,
            route = Constants.SCREEN_MAIN
        ),
        BottomNavItem(
            title = Constants.TAB_FAVOURITE,
            image = R.drawable.vector,
            route = Constants.SCREEN_FAVOURITE
        ),
    )
}