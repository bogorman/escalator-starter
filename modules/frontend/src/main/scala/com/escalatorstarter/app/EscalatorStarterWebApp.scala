package frontend.app

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontend.AppCSS
import frontend.app.components.helpers.Redirect

import frontend.app.components.tests.TestComponent
import escalator.frontend.app.fixlaminar.Fixes
import escalator.frontend.app.router.{Route, Routes}
import org.scalajs.dom.html
import urldsl.language.PathSegment.dummyErrorImpl._


import frontend.app.components.login._
import frontend.app.components.main._

object EscalatorStarterWebApp {

  private val css = AppCSS
  println(css)

  private def r = root

  object RouteDefinitions {
    final val dashBoardRoutes = r / oneOf(
      "home",
    )

    final val loginRoute = r / "login"
    final val signUpRoute = r / "register"
    final val signOutRoute = r / "signout"

    // final val afterRegisterRoute = r / "after-registration"
    final val testRoute = (r / "test").filter(_ => scala.scalajs.LinkingInfo.developmentMode)
  }

  import RouteDefinitions._

  def apply(): ReactiveHtmlElement[html.Div] = div(
    Fixes.readMountEvents,
    child <-- Routes
      .firstOf(
        Route(r / endOfSegments, () => Redirect(dashBoardRoutes)),
        Route(dashBoardRoutes, () => DashBoardApp()),
        Route(loginRoute, () => LoginPage()),
        Route(signUpRoute, () => RegisterPage()),

        //NEW ROUTES HERE

        Route(signOutRoute, () => {
            //
            
            Redirect(dashBoardRoutes)
        }),
        // Route(afterRegisterRoute, () => AfterRegister()),
        Route(testRoute, () => TestComponent())
      )
      .map {
        case Some(elem) => elem
        case None       => div("huh?!")
      }
  )

}
