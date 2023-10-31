package frontend.app.components.login

import akka.actor.ActorSystem
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.components.Component
import frontend.app.components.forms.SimpleForm
import frontend.app.components.helpers.forms.InputString
import escalator.frontend.app.router.Router
import escalator.frontend.app.utils.ActorSystemContainer
import escalator.frontend.utils.http.DefaultHttp.{boilerplate, path, _}
import io.circe.generic.auto._
import escalator.errors.BackendError
import models.users.LoginUser
import escalator.validators.FieldsValidator
import org.scalajs.dom.html.Form
import sttp.client3.Response
import escalator.syntax.WithUnit

import scala.util.{Failure, Success}

private[login] final class LoginForm(val validator: FieldsValidator[LoginUser, BackendError])(
    implicit val actorSystem: ActorSystem,
    val formDataWithUnit: WithUnit[LoginUser]
) extends Component[Form]
    with SimpleForm[LoginUser] {

  // val wrongCredentials: Var[Boolean] = Var[Boolean](false)
  val showErrors: EventBus[Boolean] = new EventBus[Boolean]()

  val element: ReactiveHtmlElement[Form] = {

    val $changeEmail = createFormDataChanger((email: String) => _.copy(email = email))
    val $changePW = createFormDataChanger((password: String) => _.copy(password = password))

    run()

    def submit(): Unit = {
      println("Submit!")

      val errorsNow = validator(formData.now)

      if (errorsNow.isEmpty) {
        boilerplate
          .post(path("login"))
          .body(formData.now)
          .response(asErrorOnly)
          .send()
          .onComplete {
            // case Success(Response(Right(_),code,statusText,headers,history,request)) =>
              // Router.router.moveTo("/home")       
            // case Success(m: Response[_]) if m.isSuccess =>
            //   Router.router.moveTo("/home")       
            // case Success(Response(Left(Right(backendErrors)),code,statusText,headers,history,request)) =>
            //   wrongCredentials.update(_ => true)
            //   println("FAILED.")
            //   println(backendErrors)
            //   errorsWriter.onNext(backendErrors) 
  
            case Success(m: Response[_]) if m.isSuccess =>
              // Router.router.moveTo("/after-registration")
              println("SUCCESSFUL LOGIN - MoveTo / home")
              Router.router.moveTo("/home")
            case Success(Response(Left(Right(backendErrors)), code, statusText, headers, history, request)) =>
              println("FAILED.")
              println(backendErrors)
              showErrors.writer.onNext(true)
              errorsWriter.onNext(backendErrors)
            case Failure(exception) =>
              throw exception
            case _ =>
              throw new Exception("LoginForm: Failure during de-serialization")
          }

      }
    }

    // form(
    //   onSubmit.preventDefault --> (_ => submit()),
    //   cls := "form-valide-with-icon needs-validation",
    //   noValidate := true,
    //   // InputString("Name ", formData.signal.map(_.name), $changeName),
    //   // InputPassword("Password ", $changePW, $errors),
      // InputText(
      //   "validationCustomUsername",
      //   "Username",
      //   "Enter your username..",
      //   "fa-user",
      //   formData.signal.map(_.name),
      //   $changeName,
      //    firstMatchingCheckedError(showErrors.events,List("name","backend.error.nameNotFound"))
      // ),
    //   InputPassword(
    //     "dlab-password",
    //     "Password",
    //     "Enter your password..",
    //     "fa-lock",
    //     formData.signal.map(_.password),
    //     $changePW,
    //     firstMatchingCheckedError(showErrors.events,List("password","backend.error.passwordIncorrect"))
    //   )
    //   ,      
    //   // child <-- wrongCredentials.signal.map(if (_) div("Incorrect username and/or password") else div()),
    //   // input(tpe := "submit", value := "Log in")
    //   div(cls := "text-center mt-4", button(tpe := "submit", cls := "btn btn-primary btn-block", "Log in"))

            form(
                onSubmit.preventDefault --> (_ => submit()),
                cls := "w-100",
                div(
                  cls := "row",
                  div(
                    cls := "col-lg-5 col-md-7 col-sm-10 mx-auto",
                    div(
                      cls := "text-center mb-7",
                      a(
                        cls := "navbar-brand me-0",
                        href := "index.html",
                        img(
                          cls := "brand-img d-inline-block",
                          src := "img/Logo_SWOT_web_green.png",
                          alt := "brand"
                        )
                      )
                    ),
                    div(
                      cls := "card card-lg card-border",
                      div(
                        cls := "card-body",
                        h4(
                          cls := "mb-4 text-center",
                          "Sign in to your account"),
                        div(
                          cls := "row gx-3",
                          // div(
                          //   cls := "form-group col-lg-12",
                          //   div(
                          //     cls := "form-label-group",
                          //     label("User Name")),
                          //   input(
                          //     cls := "form-control",
                          //     placeholder := "Enter username or email ID",
                          //     defaultValue := "",
                          //     tpe := "text")
                          // ),
                          InputText(
                            "validationCustomEmail",
                            "Email",
                            "Enter email..",
                            "fa-user",
                            formData.signal.map(_.email),
                            $changeEmail,
                             firstMatchingCheckedError(showErrors.events,List("email","backend.error.emailNotFound"))
                          ),                          
                          // div(
                          //   cls := "form-group col-lg-12",
                          //   div(
                          //     cls := "form-label-group",
                          //     label("Password"),
                          //     a(
                          //       href := "#",
                          //       cls := "fs-7 fw-medium",
                          //       "Forgot Password ?")),
                          //   div(
                          //     cls := "input-group password-check",
                          //     span(
                          //       cls := "input-affix-wrapper",
                          //       input(
                          //         cls := "form-control",
                          //         placeholder := "Enter your password",
                          //         defaultValue := "",
                          //         tpe := "password"),
                          //       a(
                          //         href := "#",
                          //         cls := "input-suffix text-muted",
                          //         span(
                          //           cls := "feather-icon",
                          //           i(
                          //             cls := "form-icon",
                          //             dataAttr("feather") := "eye")),
                          //         span(
                          //           cls := "feather-icon d-none",
                          //           i(
                          //             cls := "form-icon",
                          //             dataAttr("feather") := "eye-off"))))))
                          InputPassword(
                            "dlab-password",
                            "Password",
                            "Enter your password..",
                            "fa-lock",
                            formData.signal.map(_.password),
                            $changePW,
                            firstMatchingCheckedError(showErrors.events,List("password","backend.error.passwordIncorrect"))
                          )                          
                        ),
                        div(
                          cls := "d-flex justify-content-center",
                          div(
                            cls := "form-check form-check-sm mb-3",
                            input(
                              tpe := "checkbox",
                              cls := "form-check-input",
                              idAttr := "logged_in",
                              defaultChecked := true),
                            label(
                              cls := "form-check-label text-muted fs-7",
                              forId := "logged_in",
                              "Keep me logged in"))),
                        // a(
                        button(tpe := "submit",
                          // href := "#",
                          cls := "btn btn-primary btn-uppercase btn-block",
                          "Login"),
                        // p(
                        //   cls := "p-xs mt-2 text-center",
                        //   "New to EscalatorStarter? ",
                        //   a(
                        //     href := "#",
                        //     u("Create new account")))
                      )
                    )
                  )
                )
              )

    // )
  }

}

object LoginForm {

  def apply(
      validator: FieldsValidator[LoginUser, BackendError] = LoginUser.validator
  )(implicit actorSystemContainer: ActorSystemContainer, formDataWithUnit: WithUnit[LoginUser]): LoginForm = {
    import actorSystemContainer.actorSystem
    new LoginForm(validator)
  }

}
