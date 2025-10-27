package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import com.escalatorstarter.components.layout.dashboard.{DashboardHeader, SidebarNav}

/**
  * Main dashboard layout wrapper
  * Provides the standard dashboard structure with header and sidebar
  */
object DashboardLayout {

  def apply(content: HtmlElement): HtmlElement = {
    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900",

      // Dashboard Header
      DashboardHeader(),

      // Main content area with sidebar
      div(
        className := "flex",

        // Sidebar
        SidebarNav(),

        // Main content
        main(
          className := "flex-1 p-8",
          className := "ml-64", // Offset for sidebar width
          content
        )
      )
    )
  }
}
