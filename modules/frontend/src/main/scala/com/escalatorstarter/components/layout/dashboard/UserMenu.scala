package com.escalatorstarter.components.layout.dashboard

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.Icon
import escalator.frontend.components.layout.dashboard.{UserMenu => GenericUserMenu, MenuItem, UserMenuConfig}
import com.escalatorstarter.state.AppState
import com.escalatorstarter.routes.Router
import com.escalatorstarter.api.ApiClient
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * User menu dropdown in the dashboard header.
  *
  * This is a thin wrapper around the generic UserMenu from escalator-frontend,
  * building escalator-starter-specific user menu configuration from AppState.
  */
object UserMenu {

  // Default values for when user is not logged in
  private val defaultUserName = "User"
  private val defaultUserEmail = "user@example.com"
  private val defaultUserImage = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"

  def apply(): HtmlElement = {
    // Build signals from AppState
    val userNameSignal: Signal[String] = AppState.currentUser.signal.map {
      case Some(user) => user.fullName.getOrElse(user.email.email)
      case None => defaultUserName
    }

    val userEmailSignal: Signal[String] = AppState.currentUser.signal.map {
      case Some(user) => user.email.email
      case None => defaultUserEmail
    }

    val userImageSignal: Signal[String] = AppState.currentUser.signal.map {
      case Some(user) => defaultUserImage  // Could be user.profileUrl if available
      case None => defaultUserImage
    }

    // Build menu items
    val menuItems = List(
      MenuItem(
        label = "Profile",
        icon = Some(Icon.users()),
        onClick = () => {
          dom.console.log("Navigate to profile")
        }
      ),
      MenuItem(
        label = "Settings",
        icon = Some(Icon.settings()),
        onClick = () => {
          dom.console.log("Navigate to settings")
        }
      )
    )

    // Logout handler
    val handleSignOut = () => {
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

    // Build config
    val config = UserMenuConfig(
      userName = userNameSignal,
      userEmail = userEmailSignal,
      userAvatar = userImageSignal,
      menuItems = menuItems,
      onLogout = handleSignOut
    )

    // Use generic user menu component
    GenericUserMenu(config)
  }
}
