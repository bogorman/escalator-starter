package frontend.app.components.login

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.utils.ActorSystemContainer
import org.scalajs.dom.html

object LoginPage {

  def apply()(implicit actorSystemContainer: ActorSystemContainer): ReactiveHtmlElement[html.Div] = {

    div(
      cls := "hk-pg-body pt-0 pb-xl-0",
      div(
        cls := "container-xxl",
        div(
          cls := "row",
          div(
            cls := "col-sm-10 position-relative mx-auto",
            div(
              cls := "auth-content py-8",
              LoginForm()
              )
            )
          )
        )
      )

  }

}