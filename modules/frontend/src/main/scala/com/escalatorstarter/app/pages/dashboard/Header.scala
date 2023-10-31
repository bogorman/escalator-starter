package frontend.app.components.dashboard

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.app.components.helpers.Logout
import models.users.AppUser
import org.scalajs.dom.html

object Header {

  // user: AppUser
  def apply(): ReactiveHtmlElement[html.Element] = {
    // header(span("Emplish List"), span(user.name), Logout())

    println("Header")
    div()

  }

}
