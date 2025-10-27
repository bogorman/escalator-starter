package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.Icon
import com.escalatorstarter.routes.Router

/**
  * Dashboard sidebar navigation
  */
object SidebarNav {

  def apply(): HtmlElement = {
    aside(
      className := "fixed left-0 top-16 h-[calc(100vh-4rem)] w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 overflow-y-auto",

      nav(
        className := "p-4 space-y-2",

        // Navigation items
        navItem(Icon.home(), "Dashboard", Router.Page.Dashboard, isActive = true),
        navItem(Icon.users(), "Users", Router.Page.Dashboard, isActive = false),
        navItem(Icon.chart(), "Analytics", Router.Page.Dashboard, isActive = false),
        navItem(Icon.settings(), "Settings", Router.Page.Dashboard, isActive = false),

        // Divider
        div(className := "border-t border-gray-200 dark:border-gray-700 my-4"),

        // Component Examples section
        div(
          className := "pt-2",
          div(
            className := "px-3 py-2 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider",
            "Component Examples"
          ),
          navItem(Icon.document(), "DataTable", Router.Page.DataTableTest, isActive = false),
          navItem(Icon.bell(), "Toasts", Router.Page.ToastTest, isActive = false),
          navItem(Icon.settings(), "Dropdowns", Router.Page.DropdownTest, isActive = false),
          navItem(Icon.document(), "Forms", Router.Page.FormTest, isActive = false),
          navItem(Icon.folder(), "Modals", Router.Page.ModalTest, isActive = false),
          navItem(Icon.chart(), "Charts", Router.Page.ChartsTest, isActive = false)
        ),

        // Divider
        div(className := "border-t border-gray-200 dark:border-gray-700 my-4"),

        // Additional sections
        div(
          className := "pt-2",
          div(
            className := "px-3 py-2 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider",
            "Resources"
          ),
          navItem(Icon.document(), "Documentation", Router.Page.Dashboard, isActive = false),
          navItem(Icon.folder(), "Templates", Router.Page.Dashboard, isActive = false)
        )
      )
    )
  }

  private def navItem(icon: SvgElement, label: String, page: Router.Page, isActive: Boolean): HtmlElement = {
    val activeClasses = if (isActive) {
      "bg-primary-50 text-primary-700 dark:bg-primary-900 dark:text-primary-300"
    } else {
      "text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
    }

    button(
      typ := "button",
      className := s"w-full flex items-center gap-3 px-3 py-2 rounded-lg transition-colors $activeClasses",
      onClick --> Observer[dom.MouseEvent](_ => Router.navigateTo(page)),

      // Icon
      div(
        className := "flex-shrink-0",
        icon
      ),

      // Label
      span(
        className := "font-medium",
        label
      )
    )
  }
}
