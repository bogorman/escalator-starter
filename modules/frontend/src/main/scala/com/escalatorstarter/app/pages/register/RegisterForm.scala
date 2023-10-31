package frontend.app.components.login

import akka.actor.ActorSystem
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.components.Component
import frontend.app.components.forms.SimpleForm
import frontend.app.components.helpers.forms.InputString
import escalator.frontend.app.router.Router
import escalator.frontend.utils.http.DefaultHttp._
import io.circe.generic.auto._
import escalator.errors.BackendError
import models.users.NewUser
import escalator.validators.FieldsValidator
import org.scalajs.dom.html.{ Form, Progress }
import sttp.client3.Response
import escalator.syntax.WithUnit
import org.scalajs.dom.html

import scala.util.{ Failure, Success }

final class RegisterForm private (
    val validator: FieldsValidator[NewUser, BackendError]
)(implicit val actorSystem: ActorSystem, val formDataWithUnit: WithUnit[NewUser])
    extends Component[Form]
    with SimpleForm[NewUser] {

  def submit(): Unit = {
    println("Submit!")

    println("FormData NOW:" + formData.now)

    val errorsNow = validator(formData.now)

    println("ERRORS:" + errorsNow)

    if (errorsNow.isEmpty) {
      boilerplate
        .post(path("register"))
        .body(formData.now)
        .response(asErrorOnly)
        .send()
        .onComplete {
          case Success(m: Response[_]) if m.isSuccess =>
            // Router.router.moveTo("/after-registration")
            println("move to Login from Register")
            Router.router.moveTo("/login")
          case Success(Response(Left(Right(backendErrors)), code, statusText, headers, history, request)) =>
            println("FAILED.")
            println(backendErrors)
            errorsWriter.onNext(backendErrors)
          case Failure(exception) =>
            throw exception
          case m =>
            println(m)
            throw new Exception("RegisterForm: Failure during de-serialization")
        }
    } else {
      println("Do nothing - ERRORS EXIST")
      // errorsWriter.onNext(errorsNow)
    }
  }

  // val $passwordStrength: Observable[Double] = formData.signal.map(_.passwordStrength)

  // var passwordProgress = Var(s"width: 0; height:10px;")

  // formData.signal.map(_.passwordStrength).map { ps =>
  //   passwordProgress.set(s"width: ${(ps * 100).toInt.toString}; height:10px;")
  // }

  // $passwordStrength --> passwordProgress

  // val $passwordProgress: Observable[String] = s"width: ${$passwordStrength.map(_ * 100).map(_.toInt).map(_.toString)}; height:10px;"

  // val passwordStrengthBar: ReactiveHtmlElement[Progress] = progress(
  //   value <-- $passwordStrength.map(_ * 100).map(_.toInt).map(_.toString),
  //   maxAttr := 100.toString,
  //   backgroundColor <-- $passwordStrength.map { // todo: style this properly
  //     case x if x <= 0.2 => "#ff0000"
  //     case x if x <= 0.6 => "#ff9900"
  //     case _             => "#00ff00"
  //   }
  // )

  //

  // val passwordStrengthBar: ReactiveHtmlElement[html.Div] = div(
  //   cls := "progress mt-3",
  //   div(
  //     cls := "progress-bar bg-success",
  //     styleAttr <-- passwordProgress, //s"width: ${$passwordStrength.map(_ * 100).map(_.toInt).map(_.toString)}; height:10px;",
  //     role := "progressbar",
  //     span(cls := "sr-only", "100% Complete")
  //   )
  // )

  val element: ReactiveHtmlElement[Form] = {
    val $changeName = createFormDataChanger((newName: String) => _.copy(name = newName))
    val $changePW = createFormDataChanger((newPW: String) => _.copy(password = newPW))
    val $changeConfirmPW = createFormDataChanger((newPW: String) => _.copy(confirmPassword = newPW))
    val $changeEmail = createFormDataChanger((email: String) => _.copy(email = email))

    // val $nameError: EventStream[Option[BackendError]] = $errors.map{ m => m.get("name").flatMap(_.headOption) }
    // val $passwordError: EventStream[Option[BackendError]] = $errors.map{ m => m.get("password").flatMap(_.headOption) }
    // val $emailError: EventStream[Option[BackendError]] = $errors.map{ m => m.get("email").flatMap(_.headOption) }


    run() // todo[think] should this be done in the ComponentDidMount? probably...

    // $nameError

    // val $nameError: EventStream[Option[BackendError]] = $errors.map{ m => m("name").headOption }
    // val $nameError: EventStream[Option[BackendError]] = $errors.map{ m => m("name").headOption }

    // form(
    //   onSubmit.preventDefault --> (_ => submit()),
    //   fieldSet(
    //     InputString("Name ", formData.signal.map(_.name), $changeName)
    //   ),
    //   fieldSet(
    //     InputPassword("Password ", $changePW, $errors),
    //     passwordStrengthBar,
    //     // details(
    //     //   summary("Why this bar?"),
    //     //   p("This bar is supposed to give you an idea of the strength of your password."),
    //     //   p("Don't worry if you do not fill it entirely, it's purely indicative and not necessarily accurate.")
    //     // ),
    //     InputPassword("Confirm password ", $changeConfirmPW, $errors)
    //   ),
    //   fieldSet(
    //     InputString("Email ", formData.signal.map(_.email), $changeEmail),
    //     // details(
    //     //   summary("Why do we need it?"),
    //     //   p("We only use your address to send you an email when your registration has been accepted."),
    //     //   p("Once we have sent that email, this piece of information is removed from our data.")
    //     // )
    //   ),
    //   input(tpe := "submit", value := "Sign up")
    // )

    form(
      onSubmit.preventDefault --> (_ => submit()),
      cls := "form-valide-with-icon needs-validation",
      noValidate := true,
      InputText(
        "validationCustomUsername",
        "Username *",
        "Enter a username..",
        "fa-user",
        formData.signal.map(_.name),
        $changeName,
         firstMatchingErrors(List("name","backend.error.nameAlreadyExists"))
      ),
      InputText(
        "validationCustomEmail",
        "Email *",
        "Enter an email..",
        "fa-envelope",
        formData.signal.map(_.email),
        $changeEmail,
        firstMatchingErrors(List("email","backend.error.emailAlreadyExists"))
      ),
      InputPassword(
        "dlab-password",
        "Password *",
        "Enter a password..",
        "fa-lock",
        formData.signal.map(_.password),
        $changePW,
        firstMatchingErrors(List("password"))
      ),
      InputPassword(
        "dlab-password-confirm",
        "Confirm Password *",
        "Confirm your password..",
        "fa-lock",
        formData.signal.map(_.confirmPassword),
        $changeConfirmPW,
        firstMatchingErrors(List("confirmPassword","passwordMatch"))
      ),
      div(cls := "text-center mt-4", button(tpe := "submit", cls := "btn btn-primary btn-block", "Sign me up"))
    )

    // ,
    // div(
    //   cls := "new-account mt-3",
    //   p("Already have an account?", a(cls := "text-primary", href := "sign-in", "Sign in"))
    // )
    // )

  }

}

object RegisterForm {

  def apply(
      validator: FieldsValidator[NewUser, BackendError] = NewUser.fieldsValidator
  )(implicit actorSystem: ActorSystem, formDataWithUnit: WithUnit[NewUser]) = new RegisterForm(validator)

}
