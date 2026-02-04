package com.escalatorstarter.pages.domain

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui._
import org.scalajs.dom

/**
  * Test page for demonstrating Toast component
  */
object ToastTestPage {

  def apply(): HtmlElement = {

    // Container position state for demo
    val positionVar = Var[ToastContainerOptions](ToastContainerOptions.topRight)

    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900 p-8",

      // Header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          "Toast Component Test Page"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Interactive demonstration of Toast/Notification components"
        )
      ),

      // Toast Container - IMPORTANT: This must be in your app layout, shown here for demo
      child <-- positionVar.signal.map { options =>
        ToastContainer(options)
      },

      // Position Selector
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Toast Position"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Change where toasts appear on screen"
        ),
        div(
          className := "grid grid-cols-2 md:grid-cols-3 gap-3",

          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Top Right",
            onClick --> Observer[dom.MouseEvent] { _ =>
              positionVar.set(ToastContainerOptions.topRight)
            }
          ),

          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Top Left",
            onClick --> Observer[dom.MouseEvent] { _ =>
              positionVar.set(ToastContainerOptions.topLeft)
            }
          ),

          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Top Center",
            onClick --> Observer[dom.MouseEvent] { _ =>
              positionVar.set(ToastContainerOptions.topCenter)
            }
          ),

          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Bottom Right",
            onClick --> Observer[dom.MouseEvent] { _ =>
              positionVar.set(ToastContainerOptions.bottomRight)
            }
          ),

          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Bottom Left",
            onClick --> Observer[dom.MouseEvent] { _ =>
              positionVar.set(ToastContainerOptions.bottomLeft)
            }
          ),

          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Bottom Center",
            onClick --> Observer[dom.MouseEvent] { _ =>
              positionVar.set(ToastContainerOptions.bottomCenter)
            }
          )
        )
      ),

      // Basic Toasts Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Basic Toast Types"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Click buttons to show different toast types with default settings (5s duration)"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700",
            "Success Toast",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.success("Operation completed successfully!")
            }
          ),

          button(
            className := "px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700",
            "Error Toast",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.error("Something went wrong. Please try again.")
            }
          ),

          button(
            className := "px-4 py-2 bg-yellow-600 text-white rounded hover:bg-yellow-700",
            "Warning Toast",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.warning("Please review your input before continuing.")
            }
          ),

          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700",
            "Info Toast",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.info("New updates are available.")
            }
          )
        )
      ),

      // Duration Variants Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Duration Variants"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Toasts with different auto-dismiss durations"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700",
            "Quick (2s)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.show("This disappears quickly!", ToastType.Info, duration = 2000)
            }
          ),

          button(
            className := "px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700",
            "Normal (5s)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.info("Default duration toast")
            }
          ),

          button(
            className := "px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700",
            "Long (10s)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.show("This stays longer", ToastType.Info, duration = 10000)
            }
          ),

          button(
            className := "px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700",
            "Persistent (No Auto-Dismiss)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.persistent("This won't auto-dismiss. Click X to close.", ToastType.Warning)
            }
          )
        )
      ),

      // Action Toasts Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Toasts with Actions"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Toasts with action buttons for user interaction"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700",
            "Success with Action",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.successWithAction(
                "File saved successfully!",
                "View",
                () => dom.window.alert("Viewing file...")
              )
            }
          ),

          button(
            className := "px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700",
            "Error with Retry",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.errorWithAction(
                "Failed to upload file",
                "Retry",
                () => {
                  dom.console.log("Retrying upload...")
                  ToastService.info("Retrying upload...")
                }
              )
            }
          ),

          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700",
            "Update Available",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.show(
                "A new version is available",
                ToastType.Info,
                duration = 0,
                action = Some(("Update Now", () => dom.window.alert("Updating...")))
              )
            }
          )
        )
      ),

      // Stress Test Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Stress Test"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Test multiple toasts and stacking behavior"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "Spam Toasts (10)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              (1 to 10).foreach { i =>
                val toastType = i % 4 match {
                  case 0 => ToastType.Success
                  case 1 => ToastType.Error
                  case 2 => ToastType.Warning
                  case _ => ToastType.Info
                }
                dom.window.setTimeout(() => {
                  ToastService.show(s"Toast #$i", toastType)
                }, (i * 300).toDouble)
              }
            }
          ),

          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "Rapid Fire (5)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              (1 to 5).foreach { i =>
                ToastService.info(s"Rapid toast #$i")
              }
            }
          ),

          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "Mixed Types",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.success("First success")
              dom.window.setTimeout(() => ToastService.error("Then error"), 500)
              dom.window.setTimeout(() => ToastService.warning("Then warning"), 1000)
              dom.window.setTimeout(() => ToastService.info("Finally info"), 1500)
            }
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
          "Common toast patterns in actual applications"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-700",
            "Save Action",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.success("Changes saved successfully")
            }
          ),

          button(
            className := "px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-700",
            "Delete Action",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.successWithAction(
                "Item deleted",
                "Undo",
                () => ToastService.info("Item restored")
              )
            }
          ),

          button(
            className := "px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-700",
            "Network Error",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.errorWithAction(
                "Connection lost. Check your network.",
                "Retry",
                () => ToastService.success("Connection restored!")
              )
            }
          ),

          button(
            className := "px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-700",
            "Form Validation",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.error("Please fill in all required fields")
            }
          ),

          button(
            className := "px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-700",
            "Background Task",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.info("Processing in background...")
              dom.window.setTimeout(() => {
                ToastService.success("Processing complete!")
              }, 3000)
            }
          ),

          button(
            className := "px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-700",
            "Permission Required",
            onClick --> Observer[dom.MouseEvent] { _ =>
              ToastService.warning("This action requires admin permissions")
            }
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
          p(className := "font-semibold", "1. Add ToastContainer to your main layout (only once):"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code("ToastContainer(ToastContainerOptions.topRight)")
          ),

          p(className := "font-semibold mt-4", "2. Use ToastService from anywhere:"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """// Simple usage
ToastService.success("Operation completed!")
ToastService.error("Something went wrong")
ToastService.warning("Please be careful")
ToastService.info("New updates available")

// With custom duration
ToastService.show("Quick message", ToastType.Info, duration = 2000)

// With action button
ToastService.successWithAction(
  "File saved",
  "View",
  () => navigateToFile()
)

// Persistent (no auto-dismiss)
ToastService.persistent("Critical alert", ToastType.Error)"""
            )
          )
        )
      )
    )
  }
}
