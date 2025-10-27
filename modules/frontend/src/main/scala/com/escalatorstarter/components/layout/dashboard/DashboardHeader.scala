package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui.Icon
import com.escalatorstarter.components.layout.dashboard.UserMenu

/**
  * Dashboard header with branding, search, and user menu
  */
object DashboardHeader {

  def apply(): HtmlElement = {
    header(
      className := "bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40",

      div(
        className := "px-4 sm:px-6 lg:px-8",

        div(
          className := "flex items-center justify-between h-16",

          // Left side - Logo and brand
          div(
            className := "flex items-center gap-4",

            // Logo
            div(
              className := "flex items-center gap-2",
              div(
                className := "w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center",
                span(
                  className := "text-white font-bold text-lg",
                  "E"
                )
              ),
              h1(
                className := "text-xl font-bold text-gray-900 dark:text-white hidden sm:block",
                "Escalator Starter"
              )
            )
          ),

          // Right side - Actions and user menu
          div(
            className := "flex items-center gap-4",

            // Search (placeholder)
            div(
              className := "hidden md:block",
              div(
                className := "relative",
                input(
                  typ := "text",
                  placeholder := "Search...",
                  className := "w-64 px-4 py-2 pl-10 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary-500"
                ),
                div(
                  className := "absolute left-3 top-2.5",
                  Icon.magnifyingGlass()
                )
              )
            ),

            // Notifications (placeholder)
            button(
              typ := "button",
              className := "p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700",
              Icon.bell()
            ),

            // User menu
            UserMenu()
          )
        )
      )
    )
  }
}
