package escalator.starter

import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.escalatorstarter.routes.Router
import com.escalatorstarter.state.AppState

/**
  * Main entry point for the Escalator Starter application
  */
object Main {
  def main(args: Array[String]): Unit = {
    // Initialize any startup logic here
    initializeApp()

    // Render the application
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      EscalatorStarterApp.appElement()
    )
  }

  /**
    * Initialize application state and perform any startup tasks
    */
  private def initializeApp(): Unit = {
    dom.console.log("Escalator Starter App initializing...")

    // Always use dark mode
    AppState.setDarkMode(true)

    dom.console.log("Escalator Starter App initialized")
  }
}

/**
  * Root application component
  */
object EscalatorStarterApp {
  def appElement(): Element = {
    div(
      className := "min-h-screen",
      className <-- AppState.isDarkMode.signal.map { isDark =>
        if (isDark) "dark bg-gray-900 text-white" else "bg-white text-gray-900"
      },
      Router.view
    )
  }
}
