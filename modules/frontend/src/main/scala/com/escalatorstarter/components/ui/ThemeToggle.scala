package com.escalatorstarter.components.ui

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.escalatorstarter.state.AppState

/**
  * Theme toggle button for switching between light and dark mode
  */
object ThemeToggle {

  def apply(): HtmlElement = {
    button(
      typ := "button",
      className := "p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700",
      onClick --> Observer[dom.MouseEvent](_ => AppState.toggleDarkMode()),

      // Sun/Moon icon based on current theme
      child <-- AppState.isDarkMode.signal.map { isDark =>
        if (isDark) {
          // Sun icon (for switching to light mode)
          svg.svg(
            svg.className := "w-6 h-6",
            svg.fill := "none",
            svg.stroke := "currentColor",
            svg.viewBox := "0 0 24 24",
            svg.path(
              svg.strokeLineCap := "round",
              svg.strokeLineJoin := "round",
              svg.strokeWidth := "2",
              svg.d := "M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"
            )
          )
        } else {
          // Moon icon (for switching to dark mode)
          svg.svg(
            svg.className := "w-6 h-6",
            svg.fill := "none",
            svg.stroke := "currentColor",
            svg.viewBox := "0 0 24 24",
            svg.path(
              svg.strokeLineCap := "round",
              svg.strokeLineJoin := "round",
              svg.strokeWidth := "2",
              svg.d := "M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
            )
          )
        }
      }
    )
  }
}
