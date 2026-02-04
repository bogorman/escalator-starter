package com.escalatorstarter.state

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.escalatorstarter.models.User

/**
  * Global application state management using Airstream Var/Signal pattern.
  *
  * Provides reactive state containers for:
  * - User authentication and session management
  * - Delegated UI state
  *
  * This is the main entry point for all state access.
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
    * Session token for API authentication
    */
  val sessionToken: Var[Option[String]] = Var(loadFromLocalStorage("session_token"))

  /**
    * Flag indicating if session restoration is in progress
    * Set to true on app startup, set to false once /api/me call completes
    */
  val sessionRestoreInProgress: Var[Boolean] = Var(true)

  /**
    * Mark session restoration as complete
    */
  def setSessionRestoreComplete(): Unit = {
    sessionRestoreInProgress.set(false)
  }

  /**
    * Initialize user session from local storage on app start
    */
  def initSession(): Unit = {
    val storedUser = loadFromLocalStorage("current_user")
    storedUser.foreach { userJson =>
      // Parse user JSON and set currentUser
      // This would require circe decoding in the browser
      dom.console.log(s"Loaded user session from local storage")
    }
  }

  /**
    * Login user and store session
    */
  def login(user: User, token: String): Unit = {
    currentUser.set(Some(user))
    sessionToken.set(Some(token))
    saveToLocalStorage("session_token", token)
    saveToLocalStorage("current_user", user.toString) // TODO: Use circe JSON encoding
    dom.console.log(s"User logged in: ${user.email}")
  }

  /**
    * Logout user and clear session
    */
  def logout(): Unit = {
    currentUser.set(None)
    sessionToken.set(None)
    clearLocalStorage("session_token")
    clearLocalStorage("current_user")
    // Clear all other state
    clearAllState()
    dom.console.log("User logged out")
  }

  // =============================================
  // UI State (delegated to UIState)
  // =============================================

  // Backwards-compatible accessors
  def globalLoading: Var[Boolean] = UIState.globalLoading
  def globalError: Var[Option[String]] = UIState.globalError
  def toastNotifications: Var[List[ToastNotification]] = UIState.toastNotifications
  def showToast(message: String, toastType: String = "info", duration: Int = 3000): Unit =
    UIState.showToast(message, toastType, duration)
  def dismissToast(id: Long): Unit = UIState.dismissToast(id)
  def isDarkMode: Var[Boolean] = UIState.isDarkMode
  def setDarkMode(enabled: Boolean): Unit = UIState.setDarkMode(enabled)
  def toggleDarkMode(): Unit = UIState.toggleDarkMode()

  // =============================================
  // Helper Methods
  // =============================================

  /**
    * Clear all state (used on logout)
    */
  private def clearAllState(): Unit = {
    UIState.clear()
  }

  /**
    * Load value from local storage
    */
  private def loadFromLocalStorage(key: String): Option[String] = {
    Option(dom.window.localStorage.getItem(key))
  }

  /**
    * Save value to local storage
    */
  private def saveToLocalStorage(key: String, value: String): Unit = {
    dom.window.localStorage.setItem(key, value)
  }

  /**
    * Clear value from local storage
    */
  private def clearLocalStorage(key: String): Unit = {
    dom.window.localStorage.removeItem(key)
  }
}
