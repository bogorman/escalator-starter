package com.escalatorstarter.pages.domain

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui._
import org.scalajs.dom

/**
  * Test page for demonstrating Modal component
  */
object ModalTestPage {

  def apply(): HtmlElement = {

    // State for different modals
    val simpleModalOpen = Var(false)
    val confirmModalOpen = Var(false)
    val dangerModalOpen = Var(false)
    val alertModalOpen = Var(false)
    val successModalOpen = Var(false)
    val errorModalOpen = Var(false)
    val formModalOpen = Var(false)
    val smallModalOpen = Var(false)
    val largeModalOpen = Var(false)
    val xlargeModalOpen = Var(false)
    val noCloseModalOpen = Var(false)
    val customModalOpen = Var(false)

    // Form state
    val nameVar = Var("")
    val emailVar = Var("")
    val messageVar = Var("")

    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900 p-8",

      // Header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          "Modal Component Test Page"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Interactive demonstration of Modal/Dialog components"
        )
      ),

      // Basic Modals Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Basic Modals"
        ),
        div(
          className := "flex flex-wrap gap-3",

          // Simple Modal
          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700",
            "Simple Modal",
            onClick --> Observer[dom.MouseEvent] { _ =>
              simpleModalOpen.set(true)
            }
          ),

          // Alert Modal
          button(
            className := "px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700",
            "Alert Dialog",
            onClick --> Observer[dom.MouseEvent] { _ =>
              alertModalOpen.set(true)
            }
          ),

          // Success Modal
          button(
            className := "px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700",
            "Success Alert",
            onClick --> Observer[dom.MouseEvent] { _ =>
              successModalOpen.set(true)
            }
          ),

          // Error Modal
          button(
            className := "px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700",
            "Error Alert",
            onClick --> Observer[dom.MouseEvent] { _ =>
              errorModalOpen.set(true)
            }
          )
        )
      ),

      // Confirmation Dialogs Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Confirmation Dialogs"
        ),
        div(
          className := "flex flex-wrap gap-3",

          // Confirm Modal
          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700",
            "Confirm Action",
            onClick --> Observer[dom.MouseEvent] { _ =>
              confirmModalOpen.set(true)
            }
          ),

          // Danger Confirm Modal
          button(
            className := "px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700",
            "Delete Item (Danger)",
            onClick --> Observer[dom.MouseEvent] { _ =>
              dangerModalOpen.set(true)
            }
          )
        )
      ),

      // Form Modal Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Form Modal"
        ),
        button(
          className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
          "Create User Form",
          onClick --> Observer[dom.MouseEvent] { _ =>
            formModalOpen.set(true)
          }
        )
      ),

      // Size Variants Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Size Variants"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "Small Modal",
            onClick --> Observer[dom.MouseEvent] { _ =>
              smallModalOpen.set(true)
            }
          ),

          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "Large Modal",
            onClick --> Observer[dom.MouseEvent] { _ =>
              largeModalOpen.set(true)
            }
          ),

          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "XLarge Modal",
            onClick --> Observer[dom.MouseEvent] { _ =>
              xlargeModalOpen.set(true)
            }
          )
        )
      ),

      // Special Behaviors Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Special Behaviors"
        ),
        div(
          className := "flex flex-wrap gap-3",

          button(
            className := "px-4 py-2 bg-orange-600 text-white rounded hover:bg-orange-700",
            "No Close Options",
            onClick --> Observer[dom.MouseEvent] { _ =>
              noCloseModalOpen.set(true)
            }
          ),

          button(
            className := "px-4 py-2 bg-pink-600 text-white rounded hover:bg-pink-700",
            "Custom Styled",
            onClick --> Observer[dom.MouseEvent] { _ =>
              customModalOpen.set(true)
            }
          )
        )
      ),

      // ========== Modal Definitions ==========

      // Simple Modal
      Modal(
        isOpen = simpleModalOpen.signal,
        onClose = () => simpleModalOpen.set(false),
        title = Some("Simple Modal"),
        content = div(
          p(className := "mb-2", "This is a simple modal with some content."),
          p(className := "mb-2", "You can close it by:"),
          ul(
            className := "list-disc list-inside text-sm text-gray-600",
            li("Clicking the X button"),
            li("Clicking outside the modal"),
            li("Pressing the ESC key")
          )
        )
      ),

      // Alert Dialog
      Modal.alert(
        isOpen = alertModalOpen.signal,
        title = "Information",
        message = "This is an informational alert dialog. Click OK to continue.",
        onClose = () => alertModalOpen.set(false)
      ),

      // Success Alert
      Modal.success(
        isOpen = successModalOpen.signal,
        title = "Success!",
        message = "Your operation completed successfully.",
        onClose = () => successModalOpen.set(false)
      ),

      // Error Alert
      Modal.error(
        isOpen = errorModalOpen.signal,
        title = "Error",
        message = "Something went wrong. Please try again later.",
        onClose = () => errorModalOpen.set(false)
      ),

      // Confirm Dialog
      Modal.confirm(
        isOpen = confirmModalOpen.signal,
        title = "Confirm Action",
        message = "Are you sure you want to proceed with this action?",
        onConfirm = () => {
          dom.console.log("Action confirmed!")
          dom.window.alert("Action confirmed!")
          confirmModalOpen.set(false)
        },
        onCancel = () => confirmModalOpen.set(false)
      ),

      // Danger Confirm Dialog
      Modal.confirmDanger(
        isOpen = dangerModalOpen.signal,
        title = "Delete Item",
        message = "Are you sure you want to delete this item? This action cannot be undone.",
        onConfirm = () => {
          dom.console.log("Item deleted!")
          dom.window.alert("Item deleted!")
          dangerModalOpen.set(false)
        },
        onCancel = () => dangerModalOpen.set(false)
      ),

      // Form Modal
      Modal(
        isOpen = formModalOpen.signal,
        onClose = () => formModalOpen.set(false),
        title = Some("Create User"),
        content = div(
          // Name field
          div(
            className := "mb-4",
            label(
              className := "block text-sm font-medium text-gray-700 mb-1",
              "Name"
            ),
            input(
              typ := "text",
              className := "w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500",
              placeholder := "Enter name",
              controlled(
                value <-- nameVar.signal,
                onInput.mapToValue --> nameVar
              )
            )
          ),

          // Email field
          div(
            className := "mb-4",
            label(
              className := "block text-sm font-medium text-gray-700 mb-1",
              "Email"
            ),
            input(
              typ := "email",
              className := "w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500",
              placeholder := "Enter email",
              controlled(
                value <-- emailVar.signal,
                onInput.mapToValue --> emailVar
              )
            )
          ),

          // Message field
          div(
            className := "mb-4",
            label(
              className := "block text-sm font-medium text-gray-700 mb-1",
              "Message"
            ),
            textArea(
              className := "w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500",
              placeholder := "Enter a message",
              rows := 3,
              controlled(
                value <-- messageVar.signal,
                onInput.mapToValue --> messageVar
              )
            )
          )
        ),
        footer = Some(
          div(
            className := "flex justify-end gap-3",
            button(
              className := "px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50",
              "Cancel",
              onClick --> Observer[dom.MouseEvent] { _ =>
                formModalOpen.set(false)
              }
            ),
            button(
              className := "px-4 py-2 text-sm font-medium text-white bg-purple-600 rounded-md hover:bg-purple-700",
              "Create",
              onClick --> Observer[dom.MouseEvent] { _ =>
                dom.console.log(s"Creating user: ${nameVar.now()}, ${emailVar.now()}, ${messageVar.now()}")
                dom.window.alert(s"User created:\nName: ${nameVar.now()}\nEmail: ${emailVar.now()}")
                formModalOpen.set(false)
                // Reset form
                nameVar.set("")
                emailVar.set("")
                messageVar.set("")
              }
            )
          )
        )
      ),

      // Small Modal
      Modal(
        isOpen = smallModalOpen.signal,
        onClose = () => smallModalOpen.set(false),
        title = Some("Small Modal"),
        content = p("This is a small modal (max-w-md)."),
        options = ModalOptions.small
      ),

      // Large Modal
      Modal(
        isOpen = largeModalOpen.signal,
        onClose = () => largeModalOpen.set(false),
        title = Some("Large Modal"),
        content = div(
          p(className := "mb-2", "This is a large modal (max-w-2xl)."),
          p("It can contain more content and is suitable for forms or detailed information.")
        ),
        options = ModalOptions.large
      ),

      // XLarge Modal
      Modal(
        isOpen = xlargeModalOpen.signal,
        onClose = () => xlargeModalOpen.set(false),
        title = Some("Extra Large Modal"),
        content = div(
          p(className := "mb-2", "This is an extra large modal (max-w-4xl)."),
          p("Perfect for complex forms, tables, or detailed content.")
        ),
        options = ModalOptions.xlarge
      ),

      // No Close Modal
      Modal(
        isOpen = noCloseModalOpen.signal,
        onClose = () => noCloseModalOpen.set(false),
        title = Some("No Close Options"),
        content = div(
          p(className := "mb-2", "This modal has no close button, backdrop click, or ESC key."),
          p("You must use the button below to close it.")
        ),
        footer = Some(
          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700",
            "Close Modal",
            onClick --> Observer[dom.MouseEvent] { _ =>
              noCloseModalOpen.set(false)
            }
          )
        ),
        options = ModalOptions.noClose
      ),

      // Custom Styled Modal
      Modal(
        isOpen = customModalOpen.signal,
        onClose = () => customModalOpen.set(false),
        title = Some("Custom Styled Modal"),
        content = div(
          p(className := "text-pink-700 font-semibold", "This modal has custom styling!"),
          p(className := "mt-2", "You can customize the panel and backdrop classes.")
        ),
        options = ModalOptions(
          panelClassName = "border-4 border-pink-500",
          backdropClassName = "bg-pink-900"
        )
      )
    )
  }
}
