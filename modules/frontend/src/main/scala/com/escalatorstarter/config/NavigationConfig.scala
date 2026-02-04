package com.escalatorstarter.config

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui.Icon
import com.escalatorstarter.routes.Router

/**
  * Navigation configuration for the application.
  *
  * Centralized definition of all navigation items, organized by section.
  * This makes it easy to add, remove, or reorder navigation items without
  * touching the Sidebar component code.
  */
object NavigationConfig {

  /**
    * Navigation item definition
    *
    * @param label Display label for the nav item
    * @param icon SVG icon element
    * @param page Router page to navigate to
    * @param path URL path for active state detection
    * @param badge Optional badge text (e.g., notification count)
    */
  case class NavItem(
    label: String,
    icon: SvgElement,
    page: Router.Page,
    path: String,
    badge: Option[String] = None
  )

  /**
    * Main navigation items
    */
  val mainNavItems: List[NavItem] = List(
    NavItem("Dashboard", Icon.home(), Router.Page.Dashboard, "/dashboard"),
    NavItem("Users", Icon.users(), Router.Page.Dashboard, "/users"),
    NavItem("Analytics", Icon.chart(), Router.Page.Dashboard, "/analytics"),
    NavItem("Settings", Icon.settings(), Router.Page.Dashboard, "/settings")
  )

  /**
    * Component examples navigation items
    */
  val componentExamplesNavItems: List[NavItem] = List(
    NavItem("DataTable", Icon.document(), Router.Page.DataTableTest, "/datatable-test"),
    NavItem("Toasts", Icon.bell(), Router.Page.ToastTest, "/toast-test"),
    NavItem("Dropdowns", Icon.settings(), Router.Page.DropdownTest, "/dropdown-test"),
    NavItem("Forms", Icon.document(), Router.Page.FormTest, "/form-test"),
    NavItem("Modals", Icon.folder(), Router.Page.ModalTest, "/modal-test"),
    NavItem("Charts", Icon.chart(), Router.Page.ChartsTest, "/charts-test")
  )

  /**
    * Resources navigation items
    */
  val resourcesNavItems: List[NavItem] = List(
    NavItem("Documentation", Icon.document(), Router.Page.Dashboard, "/documentation"),
    NavItem("Templates", Icon.folder(), Router.Page.Dashboard, "/templates")
  )

  /**
    * Check if a path matches a nav item
    */
  def isActive(item: NavItem, currentPath: String): Boolean = {
    currentPath == item.path
  }

  // ========================================================================
  // Helper methods for converting to generic component config types
  // ========================================================================

  import escalator.frontend.components.layout.dashboard.{NavItemConfig, NavSection, SidebarConfig}

  /**
    * Convert an escalator-starter NavItem to a generic NavItemConfig.
    *
    * @param item The escalator-starter nav item
    * @param currentPath Current URL path for active state
    * @return Generic NavItemConfig for use with generic Sidebar component
    */
  def toNavItemConfig(item: NavItem, currentPath: String): NavItemConfig = {
    NavItemConfig(
      label = item.label,
      icon = item.icon,
      isActive = isActive(item, currentPath),
      onClick = () => Router.navigateTo(item.page),
      badge = item.badge
    )
  }

  /**
    * Build navigation sections for the Sidebar component.
    *
    * @param currentPath Current URL path for active state highlighting
    * @return List of NavSections configured for escalator-starter
    */
  def buildNavSections(currentPath: String): List[NavSection] = {
    List(
      // Main navigation section (no title)
      NavSection(
        title = None,
        items = mainNavItems.map(item => toNavItemConfig(item, currentPath))
      ),
      // Component Examples section
      NavSection(
        title = Some("Component Examples"),
        items = componentExamplesNavItems.map(item => toNavItemConfig(item, currentPath))
      ),
      // Resources section
      NavSection(
        title = Some("Resources"),
        items = resourcesNavItems.map(item => toNavItemConfig(item, currentPath))
      )
    )
  }

  /**
    * Build complete SidebarConfig for the generic Sidebar component.
    *
    * @param currentPath Current URL path for active state highlighting
    * @return Complete SidebarConfig for escalator-starter
    */
  def buildSidebarConfig(currentPath: String): SidebarConfig = {
    // Logo element
    val logo = div(
      className := "flex items-center gap-2",
      div(
        className := "w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center",
        span(
          className := "text-white font-bold text-lg",
          "E"
        )
      ),
      h1(
        className := "text-xl font-bold text-gray-900 dark:text-white",
        "Escalator"
      )
    )

    SidebarConfig(
      logoElement = logo,
      navSections = buildNavSections(currentPath),
      footerElement = None
    )
  }
}
