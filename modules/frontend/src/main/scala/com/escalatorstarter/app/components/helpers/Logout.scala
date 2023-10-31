package frontend.app.components.helpers

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.router.Router
import escalator.frontend.utils.http.DefaultHttp._
import org.scalajs.dom.html

import scala.concurrent.ExecutionContext.Implicits._

import sttp.client3._


object Logout {

  private def logout(): Unit = {
    boilerplate
    // basicRequest.header("Csrf-Token", maybeCsrfToken.getOrElse("none"))
      .post(path("logout"))
      .send()
      .onComplete { _ =>
        println("move to Login from Logout")
        Router.router.moveTo("/login")
      }
  }

  def apply(): ReactiveHtmlElement[html.Button] = button(onClick --> (_ => logout()), "Log out")

}
