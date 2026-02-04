package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import com.escalatorstarter.config.NavigationConfig
import escalator.frontend.components.layout.dashboard.{Sidebar => GenericSidebar}

object Sidebar {

  /**
    * escalator-starter sidebar with navigation items.
    *
    * This is a thin wrapper around the generic Sidebar from escalator-frontend,
    * using NavigationConfig to build escalator-starter-specific navigation structure.
    *
    * @param currentPath Signal for the current path (for active state highlighting)
    */
  def apply(currentPath: Signal[String]): HtmlElement = {
    div(
      child <-- currentPath.map { path =>
        // Build sidebar config from NavigationConfig
        val config = NavigationConfig.buildSidebarConfig(path)

        // Use generic sidebar component
        GenericSidebar(config)
      }
    )
  }
}
