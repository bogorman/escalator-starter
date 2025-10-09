package frontend.app.components.login

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.utils.ActorSystemContainer
import org.scalajs.dom.html

object RegisterPage {

  def apply()(implicit actorSystemContainer: ActorSystemContainer): ReactiveHtmlElement[html.Div] = {

    import actorSystemContainer._

    div(
      cls := "authincation h-100",
      div(
        cls := "container h-100",
        div(
          cls := "row justify-content-center h-100 align-items-center",
          div(
            cls := "col-md-6",
            div(
              cls := "authincation-content",
              div(
                cls := "row no-gutters",
                div(
                  cls := "col-xl-12",
                  div(
                    cls := "auth-form page-r-logo",
                    div(
                      cls := "text-center mb-3",
                      a(href := "#", img(src := "img/Logo_SWOT_web_green.png", alt := "", cls := "dark-logo")),
                      // a(href := "#", img(src := "./images/logo-full-light.png", alt := "", cls := "light-logo m-auto"))
                    ),
                    h4(cls := "text-center mb-4", "Sign up your account"),
                    div(
                      cls := "form-validation",
                      RegisterForm(),
                      div(
                        cls := "new-account mt-3",
                        p("Already have an account?", a(cls := "text-primary", href := "sign-in", "Sign in"))
                      )                      
                    )
                  )
                )
              )
            )
          )
        )
      )
    )

  }

}
