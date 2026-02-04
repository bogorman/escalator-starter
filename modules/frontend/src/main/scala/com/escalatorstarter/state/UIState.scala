package com.escalatorstarter.state

import com.raquo.laminar.api.L._
import org.scalajs.dom

/**
  * UI-specific state management for global UI elements.
  *
  * Manages:
  * - Global loading indicators
  * - Global error messages
  * - Toast notifications
  * - Dark mode theme
  * - Modal state (future)
  * - Sidebar collapsed state (future)
  */
object UIState {

  // =============================================
  // UI State
  // =============================================

  /**
    * Global loading state
    */
  val globalLoading: Var[Boolean] = Var(false)

  /**
    * Global error message
    */
  val globalError: Var[Option[String]] = Var(None)

  /**
    * Toast notifications
    */
  val toastNotifications: Var[List[ToastNotification]] = Var(List.empty)

  /**
    * Dark mode toggle (default: dark mode enabled)
    */
  val isDarkMode: Var[Boolean] = Var(loadDarkModePreference())

  // =============================================
  // Toast Methods
  // =============================================

  /**
    * Show toast notification
    */
  def showToast(message: String, toastType: String = "info", duration: Int = 3000): Unit = {
    val notification = ToastNotification(
      id = System.currentTimeMillis(),
      message = message,
      toastType = toastType
    )
    toastNotifications.update(notifications => notifications :+ notification)

    // Auto-dismiss after duration
    dom.window.setTimeout(() => {
      dismissToast(notification.id)
    }, duration)
  }

  /**
    * Dismiss toast notification
    */
  def dismissToast(id: Long): Unit = {
    toastNotifications.update(notifications => notifications.filterNot(_.id == id))
  }

  // =============================================
  // Dark Mode Methods
  // =============================================

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

  /**
    * Load dark mode preference from localStorage
    */
  private def loadDarkModePreference(): Boolean = {
    Option(dom.window.localStorage.getItem("theme")).exists(_ == "dark")
  }

  // =============================================
  // Helper Methods
  // =============================================

  /**
    * Clear all UI state (used on logout)
    */
  def clear(): Unit = {
    globalLoading.set(false)
    globalError.set(None)
    toastNotifications.set(List.empty)
    // Don't clear dark mode preference on logout
  }
}

/**
  * Toast notification model
  */
case class ToastNotification(
  id: Long,
  message: String,
  toastType: String  // "info", "success", "warning", "error"
)
