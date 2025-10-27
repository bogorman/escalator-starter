package com.escalatorstarter.state

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.escalatorstarter._
import com.escalatorstarter.models.User
/**
  * Global application state management using Airstream Var/Signal pattern.
  *
  * This is a simplified state management example for the Escalator Starter template.
  * Expand this based on your application's needs.
  */
object AppState {

  // =============================================
  // Authentication & User State
  // =============================================

  /**
    * Currently logged-in user (None if not authenticated)
    */
  val currentUser: Var[Option[User]] = Var(None)

  /**
    * Reactive signal indicating if user is authenticated
    */
  val isAuthenticated: Signal[Boolean] = currentUser.signal.map(_.isDefined)

  /**
    * Log in a user
    */
  def login(user: User): Unit = {
    currentUser.set(Some(user))
    dom.console.log(s"User logged in: ${user.email}")
  }

  /**
    * Log out the current user
    */
  def logout(): Unit = {
    currentUser.set(None)
    // Clear any cookies or localStorage items
    dom.window.localStorage.removeItem("session_token")
    dom.console.log("User logged out")
  }

  // =============================================
  // UI State
  // =============================================

  /**
    * Dark mode toggle (default: dark mode enabled)
    */
  val isDarkMode: Var[Boolean] = Var(true)

  /**
    * Set dark mode and persist to localStorage
    */
  def setDarkMode(enabled: Boolean): Unit = {
    isDarkMode.set(enabled)
    if (enabled) {
      dom.window.localStorage.setItem("theme", "dark")
      dom.document.documentElement.classList.add("dark")
    } else {
      dom.window.localStorage.setItem("theme", "light")
      dom.document.documentElement.classList.remove("dark")
    }
  }

  /**
    * Toggle dark mode
    */
  def toggleDarkMode(): Unit = {
    setDarkMode(!isDarkMode.now())
  }

  // =============================================
  // Helper functions
  // =============================================

  /**
    * Load a value from localStorage
    */
  private def loadFromLocalStorage(key: String): Option[String] = {
    Option(dom.window.localStorage.getItem(key))
  }

  /**
    * Save a value to localStorage
    */
  private def saveToLocalStorage(key: String, value: String): Unit = {
    dom.window.localStorage.setItem(key, value)
  }
}
