package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import escalator.frontend.components.layout.dashboard.{DashboardLayout => GenericDashboardLayout}

/**
  * Main dashboard layout wrapper.
  *
  * This is a thin wrapper around the generic DashboardLayout from escalator-frontend,
  * composing escalator-starter-specific Sidebar and DashboardHeader components.
  */
object DashboardLayout {

  def apply(currentPath: Signal[String])(children: HtmlElement*): HtmlElement = {
    // Build sidebar and header using escalator-starter components
    val sidebar = Sidebar(currentPath)
    val header = DashboardHeader()

    // Use generic layout component
    GenericDashboardLayout(sidebar, header)(children: _*)
  }
}
