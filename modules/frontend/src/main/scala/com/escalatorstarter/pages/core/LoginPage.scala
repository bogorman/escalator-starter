package com.escalatorstarter.pages.core

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.{Button, Input, PasswordInput, FormField, Card}
import escalator.frontend.components.layout.Container
import com.escalatorstarter.routes.Router
import com.escalatorstarter.api.ApiClient
import com.escalatorstarter.state.AppState
import com.escalatorstarter.users.LoginUser
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object LoginPage {

  def apply(): HtmlElement = {
    Container.centered(
      Card(
        // Header
        div(
          className := "text-center mb-8",
          h2(
            className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
            "Welcome back"
          ),
          p(
            className := "text-gray-600 dark:text-gray-400",
            "Log in to your account"
          )
        ),

        // Login Form
        loginForm(),

        // Footer
        div(
          className := "mt-6 text-center",
          p(
            className := "text-sm text-gray-600 dark:text-gray-400",
            "Don't have an account? ",
            Router.link(
              Router.Page.Register,
              "Sign up",
              "text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 font-medium"
            )
          )
        )
      )
    )
  }

  private def loginForm(): HtmlElement = {
    val email = Var("")
    val password = Var("")
    val emailError = Var[Option[String]](None)
    val passwordError = Var[Option[String]](None)
    val isSubmitting = Var(false)

    def handleSubmit(): Unit = {
      // Reset errors
      emailError.set(None)
      passwordError.set(None)

      // Basic validation
      var hasErrors = false

      if (email.now().trim.isEmpty) {
        emailError.set(Some("Email is required"))
        hasErrors = true
      } else if (!email.now().contains("@")) {
        emailError.set(Some("Please enter a valid email"))
        hasErrors = true
      }

      if (password.now().trim.isEmpty) {
        passwordError.set(Some("Password is required"))
        hasErrors = true
      }

      if (!hasErrors) {
        isSubmitting.set(true)

        // Create LoginUser object
        val loginUser = LoginUser(
          email = email.now().trim,
          password = password.now()
        )

        // Call API
        ApiClient.login(loginUser).onComplete {
          case Success(Right(user)) =>
            // Login successful
            isSubmitting.set(false)
            dom.console.log(s"Login successful: ${user.email.email}")

            // Update AppState with logged-in user
            AppState.login(user, "")

            Router.navigateTo(Router.Page.Dashboard)

          case Success(Left(serverErrors)) =>
            // Server returned validation errors
            isSubmitting.set(false)
            dom.console.log(s"Login failed with errors: $serverErrors")

            // Map server errors to form fields
            serverErrors.get("email").flatMap(_.headOption).foreach { err =>
              emailError.set(Some(err.message))
            }
            serverErrors.get("password").flatMap(_.headOption).foreach { err =>
              passwordError.set(Some(err.message))
            }
            serverErrors.get("backend.error.emailNotFound").flatMap(_.headOption).foreach { err =>
              emailError.set(Some("Email not found"))
            }
            serverErrors.get("backend.error.passwordIncorrect").flatMap(_.headOption).foreach { err =>
              passwordError.set(Some("Incorrect password"))
            }

          case Failure(exception) =>
            // Network error or exception
            isSubmitting.set(false)
            dom.console.error(s"Login error: ${exception.getMessage}")
            emailError.set(Some("Network error. Please try again."))
        }
      }
    }

    form(
      onSubmit.preventDefault --> Observer[dom.Event](_ => handleSubmit()),

      // Email Field
      FormField(
        labelText = "Email",
        htmlFor = "email",
        input = Input(
          id = "email",
          inputType = "email",
          placeholderText = "Enter your email",
          valueSignal = email.signal,
          onChange = email.writer,
          isRequired = true,
          autoCompleteValue = Some("email"),
          hasError = emailError.signal.map(_.isDefined)
        ),
        error = emailError.signal
      ),

      // Password Field
      FormField(
        labelText = "Password",
        htmlFor = "password",
        input = PasswordInput(
          id = "password",
          placeholderText = "Enter your password",
          valueSignal = password.signal,
          onChange = password.writer,
          isRequired = true,
          hasError = passwordError.signal.map(_.isDefined)
        ),
        error = passwordError.signal
      ),

      // Remember Me & Forgot Password
      div(
        className := "flex items-center justify-between mb-6",
        label(
          className := "flex items-center",
          input(
            typ := "checkbox",
            className := "h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          ),
          span(
            className := "ml-2 text-sm text-gray-700 dark:text-gray-300",
            "Remember me"
          )
        ),
        a(
          href := "#",
          className := "text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300",
          "Forgot password?"
        )
      ),

      // Submit Button
      button(
        typ := "submit",
        className := "inline-flex items-center justify-center font-medium rounded-lg transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2",
        className := "bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500 dark:bg-primary-500 dark:hover:bg-primary-600",
        className := "px-4 py-2 text-base",
        className := "w-full",
        disabled <-- isSubmitting.signal,
        child.text <-- isSubmitting.signal.map(s => if (s) "Logging in..." else "Log in")
      )
    )
  }
}
