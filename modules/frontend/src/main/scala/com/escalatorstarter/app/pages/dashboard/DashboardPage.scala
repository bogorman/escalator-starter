package frontend.app.components.dashboard

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.utils.ActorSystemContainer
import org.scalajs.dom.html

object DashboardPage {

  def apply()(implicit actorSystemContainer: ActorSystemContainer): ReactiveHtmlElement[html.Div] = {

    import actorSystemContainer._

    div("Welcome back")

  }
}