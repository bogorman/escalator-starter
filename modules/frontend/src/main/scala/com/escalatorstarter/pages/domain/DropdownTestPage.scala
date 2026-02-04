package com.escalatorstarter.pages.domain

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui._
import escalator.frontend.components.ui.ToastService
import org.scalajs.dom

/**
  * Test page for demonstrating Dropdown component
  */
object DropdownTestPage {

  def apply(): HtmlElement = {

    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900 p-8",

      // Header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          "Dropdown Component Test Page"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Interactive demonstration of Dropdown/Menu components"
        )
      ),

      // Basic Dropdowns Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Basic Dropdowns"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Simple dropdown menus with text items"
        ),
        div(
          className := "flex flex-wrap gap-4",

          // Simple dropdown
          Dropdown.simple(
            triggerText = "Actions",
            items = List(
              ("View Details", () => ToastService.info("Viewing details...")),
              ("Edit", () => ToastService.info("Editing...")),
              ("Delete", () => ToastService.error("Deleting..."))
            )
          ),

          // With custom trigger class
          Dropdown.simple(
            triggerText = "Settings",
            items = List(
              ("Profile", () => ToastService.info("Opening profile")),
              ("Preferences", () => ToastService.info("Opening preferences")),
              ("Logout", () => ToastService.warning("Logging out"))
            ),
            triggerClass = "px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          ),

          // Bottom left position
          Dropdown.simple(
            triggerText = "Export",
            items = List(
              ("Export as CSV", () => ToastService.success("Exporting CSV...")),
              ("Export as JSON", () => ToastService.success("Exporting JSON...")),
              ("Export as PDF", () => ToastService.success("Exporting PDF..."))
            ),
            options = DropdownOptions.bottomLeft
          )
        )
      ),

      // Icon Dropdowns Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Icon Button Dropdowns"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Dropdowns triggered by icon buttons"
        ),
        div(
          className := "flex flex-wrap gap-4",

          // Icon button with gear
          Dropdown.iconButton(
            icon = "⚙",
            items = List(
              Dropdown.item("Account Settings", () => ToastService.info("Account Settings"), icon = Some("👤")),
              Dropdown.item("Notifications", () => ToastService.info("Notifications"), icon = Some("🔔")),
              Dropdown.divider,
              Dropdown.item("Help", () => ToastService.info("Help"), icon = Some("❓")),
              Dropdown.item("About", () => ToastService.info("About"), icon = Some("ℹ"))
            )
          ),

          // Icon button with user
          Dropdown.iconButton(
            icon = "👤",
            items = List(
              Dropdown.item("Profile", () => ToastService.info("Profile"), icon = Some("📋")),
              Dropdown.item("Settings", () => ToastService.info("Settings"), icon = Some("⚙")),
              Dropdown.divider,
              Dropdown.item("Logout", () => ToastService.warning("Logging out"), icon = Some("🚪"), variant = DropdownItemVariant.Danger)
            )
          ),

          // Action menu (three dots)
          Dropdown.actionMenu(
            items = List(
              Dropdown.item("Rename", () => ToastService.info("Renaming...")),
              Dropdown.item("Duplicate", () => ToastService.info("Duplicating...")),
              Dropdown.item("Move", () => ToastService.info("Moving...")),
              Dropdown.divider,
              Dropdown.item("Delete", () => ToastService.error("Deleting..."), variant = DropdownItemVariant.Danger)
            )
          )
        )
      ),

      // Position Variants Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Position Variants"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Dropdowns can open in different positions relative to the trigger"
        ),
        div(
          className := "grid grid-cols-2 gap-4 max-w-2xl",

          // Top left
          div(
            className := "flex justify-start items-end h-32",
            Dropdown.simple(
              triggerText = "Top Left",
              items = List(
                ("Item 1", () => ToastService.info("Item 1")),
                ("Item 2", () => ToastService.info("Item 2")),
                ("Item 3", () => ToastService.info("Item 3"))
              ),
              options = DropdownOptions.topLeft
            )
          ),

          // Top right
          div(
            className := "flex justify-end items-end h-32",
            Dropdown.simple(
              triggerText = "Top Right",
              items = List(
                ("Item 1", () => ToastService.info("Item 1")),
                ("Item 2", () => ToastService.info("Item 2")),
                ("Item 3", () => ToastService.info("Item 3"))
              ),
              options = DropdownOptions.topRight
            )
          ),

          // Bottom left
          div(
            className := "flex justify-start items-start h-32",
            Dropdown.simple(
              triggerText = "Bottom Left",
              items = List(
                ("Item 1", () => ToastService.info("Item 1")),
                ("Item 2", () => ToastService.info("Item 2")),
                ("Item 3", () => ToastService.info("Item 3"))
              ),
              options = DropdownOptions.bottomLeft
            )
          ),

          // Bottom right
          div(
            className := "flex justify-end items-start h-32",
            Dropdown.simple(
              triggerText = "Bottom Right",
              items = List(
                ("Item 1", () => ToastService.info("Item 1")),
                ("Item 2", () => ToastService.info("Item 2")),
                ("Item 3", () => ToastService.info("Item 3"))
              ),
              options = DropdownOptions.bottomRight
            )
          )
        )
      ),

      // Item Variants Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Item Variants"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Different styling variants for dropdown items"
        ),
        div(
          className := "flex flex-wrap gap-4",

          Dropdown(
            trigger = button(
              className := "px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50",
              "Item Variants",
              span(className := "ml-2", "▼")
            ),
            items = List(
              Dropdown.item("Default Item", () => ToastService.info("Default"), icon = Some("📄"), variant = DropdownItemVariant.Default),
              Dropdown.item("Success Item", () => ToastService.success("Success"), icon = Some("✓"), variant = DropdownItemVariant.Success),
              Dropdown.item("Primary Item", () => ToastService.info("Primary"), icon = Some("⭐"), variant = DropdownItemVariant.Primary),
              Dropdown.divider,
              Dropdown.item("Danger Item", () => ToastService.error("Danger"), icon = Some("⚠"), variant = DropdownItemVariant.Danger)
            )
          )
        )
      ),

      // Disabled Items Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Disabled Items"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Dropdown items can be disabled"
        ),
        div(
          className := "flex flex-wrap gap-4",

          Dropdown(
            trigger = button(
              className := "px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50",
              "File Menu",
              span(className := "ml-2", "▼")
            ),
            items = List(
              Dropdown.item("New File", () => ToastService.info("New file"), icon = Some("📄")),
              Dropdown.item("Open File", () => ToastService.info("Open file"), icon = Some("📂")),
              Dropdown.item("Save File", () => ToastService.info("Save file"), icon = Some("💾"), disabled = true),
              Dropdown.item("Save As...", () => ToastService.info("Save as"), icon = Some("💾"), disabled = true),
              Dropdown.divider,
              Dropdown.item("Close", () => ToastService.info("Close"), icon = Some("✕"))
            )
          )
        )
      ),

      // Icons and Dividers Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Icons and Dividers"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Menu items with icons and logical grouping with dividers"
        ),
        div(
          className := "flex flex-wrap gap-4",

          Dropdown(
            trigger = button(
              className := "px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50",
              "User Menu",
              span(className := "ml-2", "▼")
            ),
            items = List(
              Dropdown.item("Profile", () => ToastService.info("Profile"), icon = Some("👤")),
              Dropdown.item("Dashboard", () => ToastService.info("Dashboard"), icon = Some("📊")),
              Dropdown.divider,
              Dropdown.item("Settings", () => ToastService.info("Settings"), icon = Some("⚙")),
              Dropdown.item("Preferences", () => ToastService.info("Preferences"), icon = Some("🎨")),
              Dropdown.divider,
              Dropdown.item("Help Center", () => ToastService.info("Help"), icon = Some("❓")),
              Dropdown.item("Feedback", () => ToastService.info("Feedback"), icon = Some("💬")),
              Dropdown.divider,
              Dropdown.item("Logout", () => ToastService.warning("Logout"), icon = Some("🚪"), variant = DropdownItemVariant.Danger)
            )
          ),

          Dropdown(
            trigger = button(
              className := "px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50",
              "Tools",
              span(className := "ml-2", "▼")
            ),
            items = List(
              Dropdown.item("Calculator", () => ToastService.info("Calculator"), icon = Some("🔢")),
              Dropdown.item("Calendar", () => ToastService.info("Calendar"), icon = Some("📅")),
              Dropdown.item("Notes", () => ToastService.info("Notes"), icon = Some("📝")),
              Dropdown.divider,
              Dropdown.item("Downloads", () => ToastService.info("Downloads"), icon = Some("⬇")),
              Dropdown.item("Uploads", () => ToastService.info("Uploads"), icon = Some("⬆")),
              Dropdown.divider,
              Dropdown.item("Archive", () => ToastService.info("Archive"), icon = Some("📦")),
              Dropdown.item("Trash", () => ToastService.info("Trash"), icon = Some("🗑"), variant = DropdownItemVariant.Danger)
            )
          )
        )
      ),

      // Real-world Examples Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Real-world Examples"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Common dropdown patterns in actual applications"
        ),

        // Example: Table row actions
        div(
          className := "mb-6",
          h3(className := "text-lg font-medium text-gray-900 dark:text-white mb-3", "Table Row Actions"),
          div(
            className := "border border-gray-200 rounded-lg overflow-hidden",
            table(
              className := "min-w-full divide-y divide-gray-200",
              thead(
                className := "bg-gray-50",
                tr(
                  th(className := "px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase", "Name"),
                  th(className := "px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase", "Status"),
                  th(className := "px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase", "Actions")
                )
              ),
              tbody(
                className := "bg-white divide-y divide-gray-200",
                List("Project Alpha", "Project Beta", "Project Gamma").map { projectName =>
                  tr(
                    td(className := "px-6 py-4 whitespace-nowrap text-sm text-gray-900", projectName),
                    td(className := "px-6 py-4 whitespace-nowrap text-sm text-green-600", "Active"),
                    td(
                      className := "px-6 py-4 whitespace-nowrap text-right text-sm",
                      Dropdown.actionMenu(
                        items = List(
                          Dropdown.item("View", () => ToastService.info(s"Viewing $projectName"), icon = Some("👁")),
                          Dropdown.item("Edit", () => ToastService.info(s"Editing $projectName"), icon = Some("✏")),
                          Dropdown.item("Duplicate", () => ToastService.info(s"Duplicating $projectName"), icon = Some("📋")),
                          Dropdown.divider,
                          Dropdown.item("Archive", () => ToastService.warning(s"Archiving $projectName"), icon = Some("📦")),
                          Dropdown.item("Delete", () => ToastService.error(s"Deleting $projectName"), icon = Some("🗑"), variant = DropdownItemVariant.Danger)
                        ),
                        options = DropdownOptions.bottomRight
                      )
                    )
                  )
                }
              )
            )
          )
        ),

        // Example: Filter dropdown
        div(
          className := "mb-6",
          h3(className := "text-lg font-medium text-gray-900 dark:text-white mb-3", "Filter Dropdown"),
          div(
            className := "flex items-center gap-3",
            span(className := "text-sm text-gray-600", "Filter by:"),
            Dropdown.simple(
              triggerText = "All Statuses",
              items = List(
                ("All Statuses", () => ToastService.info("Filter: All")),
                ("Active Only", () => ToastService.info("Filter: Active")),
                ("Inactive Only", () => ToastService.info("Filter: Inactive")),
                ("Pending Only", () => ToastService.info("Filter: Pending"))
              )
            ),
            Dropdown.simple(
              triggerText = "Last 30 Days",
              items = List(
                ("Today", () => ToastService.info("Filter: Today")),
                ("Last 7 Days", () => ToastService.info("Filter: 7 days")),
                ("Last 30 Days", () => ToastService.info("Filter: 30 days")),
                ("Last 90 Days", () => ToastService.info("Filter: 90 days")),
                ("All Time", () => ToastService.info("Filter: All time"))
              )
            )
          )
        ),

        // Example: Sort dropdown
        div(
          h3(className := "text-lg font-medium text-gray-900 dark:text-white mb-3", "Sort Dropdown"),
          Dropdown(
            trigger = button(
              className := "px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50",
              "Sort by: Name ↑"
            ),
            items = List(
              Dropdown.item("Name ↑", () => ToastService.info("Sort: Name ASC"), variant = DropdownItemVariant.Primary),
              Dropdown.item("Name ↓", () => ToastService.info("Sort: Name DESC")),
              Dropdown.divider,
              Dropdown.item("Date ↑", () => ToastService.info("Sort: Date ASC")),
              Dropdown.item("Date ↓", () => ToastService.info("Sort: Date DESC")),
              Dropdown.divider,
              Dropdown.item("Size ↑", () => ToastService.info("Sort: Size ASC")),
              Dropdown.item("Size ↓", () => ToastService.info("Sort: Size DESC"))
            )
          )
        )
      ),

      // Usage Instructions Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Usage Instructions"
        ),
        div(
          className := "text-sm text-gray-600 dark:text-gray-400 space-y-2",
          p(className := "font-semibold", "1. Simple text dropdown:"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """Dropdown.simple(
  triggerText = "Actions",
  items = List(
    ("View", () => viewItem()),
    ("Edit", () => editItem()),
    ("Delete", () => deleteItem())
  )
)"""
            )
          ),

          p(className := "font-semibold mt-4", "2. Icon button dropdown:"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """Dropdown.iconButton(
  icon = "⚙",
  items = List(
    Dropdown.item("Settings", () => openSettings(), icon = Some("⚙")),
    Dropdown.divider,
    Dropdown.item("Logout", () => logout(), variant = DropdownItemVariant.Danger)
  )
)"""
            )
          ),

          p(className := "font-semibold mt-4", "3. Action menu (three dots):"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """Dropdown.actionMenu(
  items = List(
    Dropdown.item("Edit", () => edit()),
    Dropdown.item("Delete", () => delete(), variant = DropdownItemVariant.Danger)
  )
)"""
            )
          ),

          p(className := "font-semibold mt-4", "4. Custom dropdown with full control:"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """Dropdown(
  trigger = button(className := "...", "Custom Trigger"),
  items = List(
    Dropdown.item("Item 1", () => action1(), icon = Some("📄")),
    Dropdown.item("Item 2", () => action2(), disabled = true),
    Dropdown.divider,
    Dropdown.item("Danger", () => action3(), variant = DropdownItemVariant.Danger)
  ),
  options = DropdownOptions(position = DropdownPosition.BottomLeft)
)"""
            )
          )
        )
      )
    )
  }
}
