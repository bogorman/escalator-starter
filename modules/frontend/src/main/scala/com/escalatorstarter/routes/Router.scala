package com.escalatorstarter.routes

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.escalatorstarter.components.pages.{
  LandingPage, LoginPage, RegisterPage, DashboardPage,
  DataTableTestPage, ToastTestPage, DropdownTestPage,
  FormTestPage, ModalTestPage, ChartsTestPage
}
import com.escalatorstarter.components.layout.dashboard.DashboardLayout
import com.escalatorstarter.state.AppState
import com.escalatorstarter.models.User

/**
  * Simple URL-based routing for the Escalator Starter application
  */
object Router {

  /**
    * Page types for type-safe routing
    */
  sealed trait Page
  object Page {
    case object Landing extends Page
    case object Login extends Page
    case object Register extends Page
    case object Dashboard extends Page

    // Component test pages
    case object DataTableTest extends Page
    case object ToastTest extends Page
    case object DropdownTest extends Page
    case object FormTest extends Page
    case object ModalTest extends Page
    case object ChartsTest extends Page
  }

  /**
    * Current page signal derived from URL
    */
  val currentPage: Signal[Page] = {
    val urlVar = Var(getCurrentPage())

    // Listen to popstate events (browser back/forward)
    dom.window.addEventListener("popstate", (_: dom.Event) => {
      urlVar.set(getCurrentPage())
    })

    urlVar.signal
  }

  /**
    * Parse current URL to determine page
    */
  private def getCurrentPage(): Page = {
    val path = dom.window.location.pathname
    path match {
      case "/" | "/landing" => Page.Landing
      case "/login" => Page.Login
      case "/register" => Page.Register
      case "/dashboard" => Page.Dashboard

      // Test pages
      case "/test/datatable" => Page.DataTableTest
      case "/test/toast" => Page.ToastTest
      case "/test/dropdown" => Page.DropdownTest
      case "/test/forms" => Page.FormTest
      case "/test/modals" => Page.ModalTest
      case "/test/charts" => Page.ChartsTest

      case _ => Page.Landing // Default to landing
    }
  }

  /**
    * Navigate to a new page
    */
  def navigateTo(page: Page): Unit = {
    val url = pageToUrl(page)
    dom.window.history.pushState(null, "", url)
    dom.window.dispatchEvent(new dom.Event("popstate"))
  }

  /**
    * Convert page to URL path
    */
  private def pageToUrl(page: Page): String = page match {
    case Page.Landing => "/"
    case Page.Login => "/login"
    case Page.Register => "/register"
    case Page.Dashboard => "/dashboard"

    // Test pages
    case Page.DataTableTest => "/test/datatable"
    case Page.ToastTest => "/test/toast"
    case Page.DropdownTest => "/test/dropdown"
    case Page.FormTest => "/test/forms"
    case Page.ModalTest => "/test/modals"
    case Page.ChartsTest => "/test/charts"
  }

  /**
    * Main router view - renders the appropriate page based on current route
    */
  def view: HtmlElement = {
    div(
      child <-- currentPage.combineWith(AppState.currentUser.signal).map {
        case (page, userOpt) =>
          renderPage(page, userOpt)
      }
    )
  }

  /**
    * Render the appropriate page component
    */
  private def renderPage(page: Page, userOpt: Option[User]): HtmlElement = {
    // If user is logged in and trying to access auth pages, redirect to dashboard
    if (userOpt.isDefined && (page == Page.Landing || page == Page.Login || page == Page.Register)) {
      navigateTo(Page.Dashboard)
    }

    // If user is not logged in and trying to access dashboard, redirect to login
    if (userOpt.isEmpty && page == Page.Dashboard) {
      navigateTo(Page.Login)
    }

    page match {
      case Page.Landing => LandingPage()
      case Page.Login => LoginPage()
      case Page.Register => RegisterPage()
      case Page.Dashboard =>
        userOpt match {
          case Some(user) =>
            DashboardLayout(
              DashboardPage()
            )
          case None =>
            // Redirect to login if somehow we get here without a user
            navigateTo(Page.Login)
            LoginPage()
        }

      // Test pages - require authentication
      case Page.DataTableTest =>
        userOpt match {
          case Some(user) => DashboardLayout(DataTableTestPage())
          case None =>
            navigateTo(Page.Login)
            LoginPage()
        }

      case Page.ToastTest =>
        userOpt match {
          case Some(user) => DashboardLayout(ToastTestPage())
          case None =>
            navigateTo(Page.Login)
            LoginPage()
        }

      case Page.DropdownTest =>
        userOpt match {
          case Some(user) => DashboardLayout(DropdownTestPage())
          case None =>
            navigateTo(Page.Login)
            LoginPage()
        }

      case Page.FormTest =>
        userOpt match {
          case Some(user) => DashboardLayout(FormTestPage())
          case None =>
            navigateTo(Page.Login)
            LoginPage()
        }

      case Page.ModalTest =>
        userOpt match {
          case Some(user) => DashboardLayout(ModalTestPage())
          case None =>
            navigateTo(Page.Login)
            LoginPage()
        }

      case Page.ChartsTest =>
        userOpt match {
          case Some(user) => DashboardLayout(ChartsTestPage())
          case None =>
            navigateTo(Page.Login)
            LoginPage()
        }
    }
  }
}
