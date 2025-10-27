package com.escalatorstarter.components.pages

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.{Button, Input, PasswordInput, FormField, Card, Alert}
import escalator.frontend.components.layout.Container
import com.escalatorstarter.routes.Router
import com.escalatorstarter.api.ApiClient
import com.escalatorstarter.state.AppState
import com.escalatorstarter.users.LoginUser
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Login page for user authentication
  */
object LoginPage {

  def apply(): HtmlElement = {
    // Form state
    val emailVar = Var("")
    val passwordVar = Var("")
    val isLoadingVar = Var(false)
    val errorVar = Var[Option[String]](None)

    // Validation
    val emailError = emailVar.signal.map { email =>
      if (email.isEmpty) None
      else if (!email.contains("@")) Some("Please enter a valid email")
      else None
    }

    val passwordError = passwordVar.signal.map { password =>
      if (password.isEmpty) None
      else if (password.length < 6) Some("Password must be at least 6 characters")
      else None
    }

    val isFormValid = emailVar.signal.combineWith(passwordVar.signal).map {
      case (email, password) => email.nonEmpty && password.nonEmpty && email.contains("@") && password.length >= 6
    }

    // Handle login
    def handleLogin(): Unit = {
      errorVar.set(None)
      isLoadingVar.set(true)

      val loginUser = LoginUser(
        email = emailVar.now(),
        password = passwordVar.now()
      )

      ApiClient.login(loginUser).onComplete {
        case Success(Right(user)) =>
          isLoadingVar.set(false)
          AppState.login(user)
          Router.navigateTo(Router.Page.Dashboard)

        case Success(Left(errors)) =>
          isLoadingVar.set(false)
          val errorMessage = errors.get("general")
            .flatMap(_.headOption)
            .map(_.message)
            .getOrElse("Login failed")
          errorVar.set(Some(errorMessage))

        case Failure(ex) =>
          isLoadingVar.set(false)
          errorVar.set(Some(s"Network error: ${ex.getMessage}"))
      }
    }

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

        // Error alert
        div(
          child.maybe <-- errorVar.signal.map {
            case Some(error) =>
              Some(Alert(
                message = error,
                variant = Alert.Variant.Error,
                dismissible = true,
                onDismiss = () => errorVar.set(None)
              ))
            case None => None
          }
        ),

        // Login form
        form(
          onSubmit.preventDefault --> Observer[dom.Event](_ => handleLogin()),

          FormField(
            labelText = "Email",
            htmlFor = "email",
            error = emailError.map(e => if (emailVar.now().nonEmpty) e else None),
            input = Input(
              id = "email",
              inputType = "email",
              placeholderText = "you@example.com",
              valueSignal = emailVar.signal,
              onChange = emailVar.writer,
              isRequired = true,
              autoCompleteValue = Some("email"),
              hasError = emailError.map(_.isDefined)
            )
          ),

          FormField(
            labelText = "Password",
            htmlFor = "password",
            error = passwordError.map(e => if (passwordVar.now().nonEmpty) e else None),
            input = PasswordInput(
              id = "password",
              placeholderText = "Enter your password",
              valueSignal = passwordVar.signal,
              onChange = passwordVar.writer,
              isRequired = true,
              autoCompleteValue = Some("current-password"),
              hasError = passwordError.map(_.isDefined)
            )
          ),

          div(
            className := "mt-6",
            child <-- isFormValid.combineWith(isLoadingVar.signal).map { case (valid, loading) =>
              Button.submit(
                text = "Sign In",
                variant = Button.Variant.Primary,
                size = Button.Size.Medium,
                fullWidth = true,
                isDisabled = !valid || loading
              )
            }
          )
        ),

        // Register link
        div(
          className := "mt-6 text-center",
          span(
            className := "text-gray-600 dark:text-gray-400",
            "Don't have an account? "
          ),
          button(
            typ := "button",
            className := "text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 font-medium",
            onClick --> Observer[dom.MouseEvent](_ => Router.navigateTo(Router.Page.Register)),
            "Sign up"
          )
        )
      )
    )
  }
}
