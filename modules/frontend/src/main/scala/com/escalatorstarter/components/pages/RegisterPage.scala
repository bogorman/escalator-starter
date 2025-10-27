package com.escalatorstarter.components.pages

import com.raquo.laminar.api.L._
import org.scalajs.dom
import escalator.frontend.components.ui.{Button, Input, PasswordInput, FormField, Card, Alert}
import escalator.frontend.components.layout.Container
import com.escalatorstarter.routes.Router
import com.escalatorstarter.api.ApiClient
import com.escalatorstarter.state.AppState
import com.escalatorstarter.users.NewUser
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Registration page for new users
  */
object RegisterPage {

  def apply(): HtmlElement = {
    // Form state
    val nameVar = Var("")
    val emailVar = Var("")
    val passwordVar = Var("")
    val confirmPasswordVar = Var("")
    val isLoadingVar = Var(false)
    val errorVar = Var[Option[String]](None)

    // Validation
    val nameError = nameVar.signal.map { name =>
      if (name.isEmpty) None
      else if (name.length < 2) Some("Name must be at least 2 characters")
      else None
    }

    val emailError = emailVar.signal.map { email =>
      if (email.isEmpty) None
      else if (!email.contains("@")) Some("Please enter a valid email")
      else None
    }

    val passwordError = passwordVar.signal.map { password =>
      if (password.isEmpty) None
      else if (password.length < 8) Some("Password must be at least 8 characters")
      else None
    }

    val confirmPasswordError = confirmPasswordVar.signal.combineWith(passwordVar.signal).map {
      case (confirm, password) =>
        if (confirm.isEmpty) None
        else if (confirm != password) Some("Passwords do not match")
        else None
    }

    val isFormValid = Signal.combineSeq(
      List(nameVar.signal, emailVar.signal, passwordVar.signal, confirmPasswordVar.signal)
    ).map {
      case Seq(name: String, email: String, password: String, confirmPassword: String) =>
        name.nonEmpty && name.length >= 2 &&
        email.nonEmpty && email.contains("@") &&
        password.nonEmpty && password.length >= 8 &&
        password == confirmPassword
      case _ => false
    }

    // Handle registration
    def handleRegister(): Unit = {
      errorVar.set(None)
      isLoadingVar.set(true)

      val newUser = NewUser(
        name = nameVar.now(),
        password = passwordVar.now(),
        confirmPassword = confirmPasswordVar.now(),
        email = emailVar.now()
      )

      ApiClient.register(newUser).onComplete {
        case Success(Right(user)) =>
          isLoadingVar.set(false)
          AppState.login(user)
          Router.navigateTo(Router.Page.Dashboard)

        case Success(Left(errors)) =>
          isLoadingVar.set(false)
          val errorMessage = errors.get("general")
            .flatMap(_.headOption)
            .map(_.message)
            .getOrElse("Registration failed")
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
            "Create your account"
          ),
          p(
            className := "text-gray-600 dark:text-gray-400",
            "Get started with Escalator Starter"
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

        // Registration form
        form(
          onSubmit.preventDefault --> Observer[dom.Event](_ => handleRegister()),

          FormField(
            labelText = "Name",
            htmlFor = "name",
            error = nameError.map(e => if (nameVar.now().nonEmpty) e else None),
            input = Input(
              id = "name",
              inputType = "text",
              placeholderText = "Your name",
              valueSignal = nameVar.signal,
              onChange = nameVar.writer,
              isRequired = true,
              autoCompleteValue = Some("name"),
              hasError = nameError.map(_.isDefined)
            )
          ),

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
              placeholderText = "At least 8 characters",
              valueSignal = passwordVar.signal,
              onChange = passwordVar.writer,
              isRequired = true,
              autoCompleteValue = Some("new-password"),
              hasError = passwordError.map(_.isDefined)
            )
          ),

          FormField(
            labelText = "Confirm Password",
            htmlFor = "confirmPassword",
            error = confirmPasswordError.map(e => if (confirmPasswordVar.now().nonEmpty) e else None),
            input = PasswordInput(
              id = "confirmPassword",
              placeholderText = "Re-enter your password",
              valueSignal = confirmPasswordVar.signal,
              onChange = confirmPasswordVar.writer,
              isRequired = true,
              autoCompleteValue = Some("new-password"),
              hasError = confirmPasswordError.map(_.isDefined)
            )
          ),

          div(
            className := "mt-6",
            child <-- isFormValid.combineWith(isLoadingVar.signal).map { case (valid, loading) =>
              Button.submit(
                text = "Create Account",
                variant = Button.Variant.Primary,
                size = Button.Size.Medium,
                fullWidth = true,
                isDisabled = !valid || loading
              )
            }
          )
        ),

        // Login link
        div(
          className := "mt-6 text-center",
          span(
            className := "text-gray-600 dark:text-gray-400",
            "Already have an account? "
          ),
          button(
            typ := "button",
            className := "text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300 font-medium",
            onClick --> Observer[dom.MouseEvent](_ => Router.navigateTo(Router.Page.Login)),
            "Sign in"
          )
        )
      )
    )
  }
}
