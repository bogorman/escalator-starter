package com.escalatorstarter.routes

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.escalatorstarter.pages.core.{LandingPage, LoginPage, RegisterPage, DashboardPage}
import com.escalatorstarter.pages.domain.{
  DataTableTestPage, ToastTestPage, DropdownTestPage,
  FormTestPage, ModalTestPage, ChartsTestPage
}
import com.escalatorstarter.components.layout.dashboard.DashboardLayout
import escalator.frontend.components.ui.Spinner
import com.escalatorstarter.state.AppState
import com.escalatorstarter.models.User

/**
  * URL-based routing for the Escalator Starter application.
  *
  * Uses segment-based path parsing with centralized auth guard.
  */
object Router {

  /**
    * Page types for type-safe routing
    */
  sealed trait Page
  object Page {
    // Auth pages
    case object Landing extends Page
    case object Login extends Page
    case object Register extends Page

    // Dashboard
    case object Dashboard extends Page

    // Component test pages
    case object DataTableTest extends Page
    case object ToastTest extends Page
    case object DropdownTest extends Page
    case object FormTest extends Page
    case object ModalTest extends Page
    case object ChartsTest extends Page
  }

  private val currentPage = Var[Page](Page.Landing)

  /**
    * Check if a page requires authentication
    */
  private def requiresAuth(page: Page): Boolean = page match {
    case Page.Landing | Page.Login | Page.Register => false
    case _ => true
  }

  /**
    * Apply auth guard - redirect to login if not authenticated
    */
  private def applyAuthGuard(page: Page): Page = {
    if (requiresAuth(page) && AppState.currentUser.now().isEmpty) {
      dom.console.warn(s"Access denied to ${page}. Redirecting to login.")
      Page.Login
    } else {
      page
    }
  }

  /**
    * Parse URL path to determine current page
    */
  private def parsePath(path: String): Page = {
    val segments = path.split("/").filter(_.nonEmpty).toList

    val page = segments match {
      // Auth
      case Nil => Page.Landing
      case "login" :: Nil => Page.Login
      case "register" :: Nil => Page.Register

      // Dashboard
      case "dashboard" :: Nil => Page.Dashboard

      // Test pages
      case "test" :: "datatable" :: Nil => Page.DataTableTest
      case "test" :: "toast" :: Nil => Page.ToastTest
      case "test" :: "dropdown" :: Nil => Page.DropdownTest
      case "test" :: "forms" :: Nil => Page.FormTest
      case "test" :: "modals" :: Nil => Page.ModalTest
      case "test" :: "charts" :: Nil => Page.ChartsTest

      // Default to landing
      case _ => Page.Landing
    }

    applyAuthGuard(page)
  }

  /**
    * Navigate to a new page
    */
  def navigateTo(page: Page): Unit = {
    val guardedPage = applyAuthGuard(page)
    val path = pageToPath(guardedPage)
    dom.window.history.pushState(null, "", path)
    currentPage.set(guardedPage)
  }

  /**
    * Convert page to URL path
    */
  private def pageToPath(page: Page): String = page match {
    // Auth
    case Page.Landing => "/"
    case Page.Login => "/login"
    case Page.Register => "/register"

    // Dashboard
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
    * Initialize router - read initial path and set up popstate listener
    */
  private def initialize(): Unit = {
    val initialPath = dom.window.location.pathname
    currentPage.set(parsePath(initialPath))

    // Handle browser back/forward buttons
    dom.window.addEventListener(
      "popstate",
      (_: dom.PopStateEvent) => {
        val path = dom.window.location.pathname
        currentPage.set(parsePath(path))
      }
    )
  }

  // Call initialize on first access
  initialize()

  // Re-apply auth guard when authentication state changes
  AppState.currentUser.signal.addObserver(Observer[Option[User]] { _ =>
    val currentPath = dom.window.location.pathname
    val newPage = parsePath(currentPath)
    if (newPage != currentPage.now()) {
      currentPage.set(newPage)
    }
  })(unsafeWindowOwner)

  /**
    * Main view that switches between pages
    */
  val view: HtmlElement = {
    div(
      className := "h-full",
      child <-- currentPage.signal.combineWith(AppState.sessionRestoreInProgress.signal).map {
        case (page, true) if requiresAuth(page) =>
          // Show loading spinner while session restoration is in progress for auth-required pages
          Spinner.fullPage("Restoring session...")
        case (Page.Landing, _) => LandingPage()
        case (Page.Login, _) => LoginPage()
        case (Page.Register, _) => RegisterPage()
        case (Page.Dashboard, _) => wrapInDashboardLayout(DashboardPage())

        // Test pages - all wrapped in dashboard layout
        case (Page.DataTableTest, _) => wrapInDashboardLayout(DataTableTestPage())
        case (Page.ToastTest, _) => wrapInDashboardLayout(ToastTestPage())
        case (Page.DropdownTest, _) => wrapInDashboardLayout(DropdownTestPage())
        case (Page.FormTest, _) => wrapInDashboardLayout(FormTestPage())
        case (Page.ModalTest, _) => wrapInDashboardLayout(ModalTestPage())
        case (Page.ChartsTest, _) => wrapInDashboardLayout(ChartsTestPage())
      }
    )
  }

  /**
    * Helper for creating navigation links
    */
  def link(page: Page, text: String, classes: String = ""): HtmlElement = {
    a(
      href := "#",
      className := classes,
      text,
      onClick.preventDefault --> Observer[dom.MouseEvent](_ => navigateTo(page))
    )
  }

  /**
    * Wrap content in DashboardLayout with sidebar
    * Converts current page to path string for sidebar highlighting
    * User information is retrieved from AppState automatically
    */
  private def wrapInDashboardLayout(content: HtmlElement): HtmlElement = {
    val currentPathSignal = currentPage.signal.map(pageToPath)
    DashboardLayout(currentPathSignal)(content)
  }
}
