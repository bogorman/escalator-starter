package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.Icon
import com.escalatorstarter.state.AppState
import com.escalatorstarter.routes.Router
import com.escalatorstarter.api.ApiClient
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * User menu dropdown in the dashboard header
  */
object UserMenu {

  def apply(): HtmlElement = {
    val isOpenVar = Var(false)

    div(
      className := "relative",

      // User button
      button(
        typ := "button",
        className := "flex items-center gap-2 p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700",
        onClick --> Observer[dom.MouseEvent](_ => isOpenVar.update(!_)),

        // User avatar (placeholder)
        div(
          className := "w-8 h-8 rounded-full bg-primary-600 flex items-center justify-center",
          child <-- AppState.currentUser.signal.map {
            case Some(user) =>
              span(
                className := "text-white font-medium text-sm",
                user.fullName.getOrElse(user.email.email).take(1).toUpperCase
              )
            case None =>
              span(className := "text-white font-medium text-sm", "?")
          }
        ),

        // User name (desktop only)
        span(
          className := "hidden md:block text-sm font-medium text-gray-700 dark:text-gray-300",
          child.text <-- AppState.currentUser.signal.map {
            case Some(user) => user.fullName.getOrElse(user.email.email)
            case None => "User"
          }
        ),

        // Dropdown icon
        Icon.chevronDown()
      ),

      // Dropdown menu
      div(
        className <-- isOpenVar.signal.map { isOpen =>
          if (isOpen) {
            "absolute right-0 mt-2 w-48 rounded-lg shadow-lg bg-white dark:bg-gray-800 ring-1 ring-black ring-opacity-5"
          } else {
            "hidden"
          }
        },

        div(
          className := "py-1",

          // User info
          div(
            className := "px-4 py-2 border-b border-gray-200 dark:border-gray-700",
            child.maybe <-- AppState.currentUser.signal.map {
              case Some(user) =>
                val displayName: String = user.fullName.getOrElse(user.email.email)
                Some(div(
                  p(className := "text-sm font-medium text-gray-900 dark:text-white", displayName),
                  p(className := "text-sm text-gray-500 dark:text-gray-400", user.email.email)
                ))
              case None =>
                Some(p(className := "text-sm text-gray-500", "Not logged in"))
            }
          ),

          // Menu items
          menuItem("Profile", Icon.users(), () => {
            isOpenVar.set(false)
            dom.console.log("Navigate to profile")
          }),
          menuItem("Settings", Icon.settings(), () => {
            isOpenVar.set(false)
            dom.console.log("Navigate to settings")
          }),

          // Divider
          div(className := "border-t border-gray-200 dark:border-gray-700 my-1"),

          // Logout
          menuItem("Sign out", Icon.close(), () => {
            isOpenVar.set(false)
            handleLogout()
          })
        )
      ),

      // Click outside to close
      onClick.stopPropagation.mapTo(true) --> Observer.empty
    )
  }

  private def menuItem(label: String, icon: SvgElement, onClickFn: () => Unit): HtmlElement = {
    button(
      typ := "button",
      className := "w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2",
      onClick --> Observer[dom.MouseEvent](_ => onClickFn()),

      icon,
      label
    )
  }

  private def handleLogout(): Unit = {
    ApiClient.logout().onComplete {
      case Success(_) =>
        AppState.logout()
        Router.navigateTo(Router.Page.Landing)

      case Failure(ex) =>
        dom.console.error(s"Logout error: ${ex.getMessage}")
        // Still logout locally even if server request fails
        AppState.logout()
        Router.navigateTo(Router.Page.Landing)
    }
  }
}
