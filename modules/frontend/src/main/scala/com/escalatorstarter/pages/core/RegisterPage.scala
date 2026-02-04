package com.escalatorstarter.pages.core

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.{Button, Input, PasswordInput, FormField, Card}
import escalator.frontend.components.layout.Container
import com.escalatorstarter.routes.Router
import com.escalatorstarter.api.ApiClient
import com.escalatorstarter.users.NewUser
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object RegisterPage {

  def apply(): HtmlElement = {
    Container.centered(
      Card(
        // Header
        div(
          className := "text-center mb-8",
          h2(
            className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
            "Create your account"
          ),
          p(
            className := "text-gray-600 dark:text-gray-400",
            "Join Escalator Starter today"
          )
        ),

        // Register Form
        registerForm(),

        // Footer
        div(
          className := "mt-6 text-center",
          p(
            className := "text-sm text-gray-600 dark:text-gray-400",
            "Already have an account? ",
            Router.link(
              Router.Page.Login,
              "Log in",
              "text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 font-medium"
            )
          )
        )
      )
    )
  }

  private def registerForm(): HtmlElement = {
    val fullName = Var("")
    val email = Var("")
    val password = Var("")
    val confirmPassword = Var("")
    val nameError = Var[Option[String]](None)
    val emailError = Var[Option[String]](None)
    val passwordError = Var[Option[String]](None)
    val confirmPasswordError = Var[Option[String]](None)
    val isSubmitting = Var(false)

    def handleSubmit(): Unit = {
      // Reset errors
      nameError.set(None)
      emailError.set(None)
      passwordError.set(None)
      confirmPasswordError.set(None)

      // Basic validation
      var hasErrors = false

      if (fullName.now().trim.isEmpty) {
        nameError.set(Some("Name is required"))
        hasErrors = true
      } else if (fullName.now().trim.length < 3) {
        nameError.set(Some("Name must be at least 3 characters"))
        hasErrors = true
      }

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
      } else if (password.now().length < 8) {
        passwordError.set(Some("Password must be at least 8 characters"))
        hasErrors = true
      }

      if (confirmPassword.now() != password.now()) {
        confirmPasswordError.set(Some("Passwords do not match"))
        hasErrors = true
      }

      if (!hasErrors) {
        isSubmitting.set(true)

        // Create NewUser object
        val newUser = NewUser(
          name = fullName.now().trim,
          password = password.now(),
          confirmPassword = confirmPassword.now(),
          email = email.now().trim
        )

        // Call API
        ApiClient.register(newUser).onComplete {
          case Success(Right(user)) =>
            // Registration successful
            isSubmitting.set(false)
            dom.console.log(s"Registration successful: ${user.fullName.getOrElse(user.email.email)}")
            Router.navigateTo(Router.Page.Dashboard)

          case Success(Left(serverErrors)) =>
            // Server returned validation errors
            isSubmitting.set(false)
            dom.console.log(s"Registration failed with errors: $serverErrors")

            // Map server errors to form fields
            serverErrors.get("name").flatMap(_.headOption).foreach { err =>
              nameError.set(Some(err.message))
            }
            serverErrors.get("email").flatMap(_.headOption).foreach { err =>
              emailError.set(Some(err.message))
            }
            serverErrors.get("password").flatMap(_.headOption).foreach { err =>
              passwordError.set(Some(err.message))
            }
            serverErrors.get("confirmPassword").flatMap(_.headOption).foreach { err =>
              confirmPasswordError.set(Some(err.message))
            }
            serverErrors.get("passwordMatch").flatMap(_.headOption).foreach { err =>
              confirmPasswordError.set(Some(err.message))
            }

          case Failure(exception) =>
            // Network error or exception
            isSubmitting.set(false)
            dom.console.error(s"Registration error: ${exception.getMessage}")
            nameError.set(Some("Network error. Please try again."))
        }
      }
    }

    form(
      onSubmit.preventDefault --> Observer[dom.Event](_ => handleSubmit()),

      // Full Name Field
      FormField(
        labelText = "Full Name",
        htmlFor = "fullName",
        input = Input(
          id = "fullName",
          inputType = "text",
          placeholderText = "Enter your full name",
          valueSignal = fullName.signal,
          onChange = fullName.writer,
          isRequired = true,
          autoCompleteValue = Some("name"),
          hasError = nameError.signal.map(_.isDefined)
        ),
        error = nameError.signal
      ),

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
          placeholderText = "Create a password",
          valueSignal = password.signal,
          onChange = password.writer,
          isRequired = true,
          autoCompleteValue = Some("new-password"),
          hasError = passwordError.signal.map(_.isDefined)
        ),
        error = passwordError.signal,
        helpText = Some("Must be at least 8 characters")
      ),

      // Confirm Password Field
      FormField(
        labelText = "Confirm Password",
        htmlFor = "confirmPassword",
        input = PasswordInput(
          id = "confirmPassword",
          placeholderText = "Confirm your password",
          valueSignal = confirmPassword.signal,
          onChange = confirmPassword.writer,
          isRequired = true,
          autoCompleteValue = Some("new-password"),
          hasError = confirmPasswordError.signal.map(_.isDefined)
        ),
        error = confirmPasswordError.signal
      ),

      // Terms and Conditions
      div(
        className := "mb-6",
        label(
          className := "flex items-start",
          input(
            typ := "checkbox",
            className := "h-4 w-4 mt-1 text-primary-600 focus:ring-primary-500 border-gray-300 rounded",
            required := true
          ),
          span(
            className := "ml-2 text-sm text-gray-700 dark:text-gray-300",
            "I agree to the ",
            a(
              href := "#",
              className := "text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300",
              "Terms and Conditions"
            ),
            " and ",
            a(
              href := "#",
              className := "text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300",
              "Privacy Policy"
            )
          )
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
        child.text <-- isSubmitting.signal.map(s => if (s) "Creating account..." else "Create account")
      )
    )
  }
}
