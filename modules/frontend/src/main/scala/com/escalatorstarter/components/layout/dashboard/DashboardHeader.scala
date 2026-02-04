package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui.Icon
import escalator.frontend.components.layout.dashboard.{DashboardHeader => GenericDashboardHeader, SearchBar => GenericSearchBar}
import com.escalatorstarter.components.layout.dashboard.UserMenu

/**
  * Dashboard header with branding, search, and user menu.
  *
  * This is a thin wrapper around the generic DashboardHeader from escalator-frontend,
  * building escalator-starter-specific header content sections.
  */
object DashboardHeader {

  def apply(): HtmlElement = {
    // Left content: mobile menu button + separator
    val leftContent = div(
      className := "flex items-center gap-x-4",
      button(
        typ := "button",
        className := "-m-2.5 p-2.5 text-gray-700 hover:text-gray-900 lg:hidden dark:text-gray-400 dark:hover:text-white",
        span(className := "sr-only", "Open sidebar"),
        Icon.bars3()
      ),
      div(
        aria.hidden := true,
        className := "h-6 w-px bg-gray-900/10 lg:hidden dark:bg-white/10"
      )
    )

    // Center content: search bar
    val centerContent = GenericSearchBar(
      placeholderText = "Search..."
    )

    // Right content: notifications + user menu
    val rightContent = div(
      className := "flex items-center gap-x-4 lg:gap-x-6",

      // Notifications
      button(
        typ := "button",
        className := "-m-2.5 p-2.5 text-gray-400 hover:text-gray-500 dark:text-gray-400 dark:hover:text-gray-300",
        span(className := "sr-only", "View notifications"),
        Icon.bell()
      ),

      // Separator
      div(
        aria.hidden := true,
        className := "hidden lg:block lg:h-6 lg:w-px lg:bg-gray-900/10 dark:lg:bg-gray-100/10"
      ),

      // User menu
      UserMenu()
    )

    // Use generic header component with configured sections
    GenericDashboardHeader(
      leftContent = Some(leftContent),
      centerContent = Some(centerContent),
      rightContent = Some(rightContent)
    )
  }
}
