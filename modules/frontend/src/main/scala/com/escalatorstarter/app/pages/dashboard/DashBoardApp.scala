package frontend.app.components.main

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.frontend.app.components.Component

// import frontend.app.components.basket.BasketBoard
// import frontend.app.components.headers.{GlobalHeader, Navigation}
// import frontend.app.components.ingredients.{IngredientBoard, NewIngredient}
// import frontend.app.components.recipes.{RecipeBoard, RecipeDisplayContainer, RecipeEditorContainer}
// import frontend.app.components.users.{AcceptUser, ViewUsers}
import escalator.frontend.app.fixlaminar.Fixes
import escalator.frontend.app.router.{Route, Router, Routes}
// import frontend.utils.Recipes
import escalator.frontend.utils.http.DefaultHttp._
import io.circe.generic.auto._
import models.users.AppUser
import org.scalajs.dom
import sttp.client3._
import urldsl.language.PathSegment.dummyErrorImpl._
import urldsl.language.QueryParameters.dummyErrorImpl.{param => qParam}

// import com.raquo.airstream.ownership.Owner

import scala.concurrent.ExecutionContext.Implicits.global

import frontend.app.components.dashboard._

// import frontend.app.components.login._

// import com.raquo.laminar.api.L._
// import com.raquo.laminar.nodes.ReactiveHtmlElement
// import frontend.app.components.helpers.Logout
// import models.users.AppUser
// import org.scalajs.dom.html



final class DashBoardApp private () extends Component[dom.html.Div] {
  implicit val owner: Owner = new Owner {}

  private def me = EventStream.fromFuture(
    boilerplate
      .get(path("me"))
      .response(responseAs[AppUser])
      .send()
      .map(_.body)
      .map(_.toOption.flatMap(_.toOption))
  )

  private def amIAdmin = EventStream.fromFuture(
    boilerplate
      .get(path("am-i-admin"))
      .response(ignore)
      .send()
      .map(_.is200)
  )

  val element: ReactiveHtmlElement[dom.html.Div] = {
    val meAndAdmin = me
      .collect { case Some(user) => user }
      .combineWith(amIAdmin)

    val element = div(
      Fixes.readMountEvents,
      child <-- meAndAdmin
        .map {
          case (user, admin) =>
            div(
              Fixes.readMountEvents,
              // className := "app",

              // cls := "hk-wrapper",
              // dataAttr("layout") := "vertical",
              // dataAttr("layout-style") := "default",
              // dataAttr("menu") := "light",
              // dataAttr("footer") := "simple",              

              // GlobalHeader(user),
              // Navigation(admin),
              TopNavBar(),
              Sidebar(),
              div(idAttr := "hk_menu_backdrop",cls := "hk-menu-backdrop"),
              // NavHeader(),
              // Header(),
              // Sidebar(),
              div(
                Fixes.readMountEvents,
                className := "hk-pg-wrapper",
                child <-- Routes
                  .firstOf(
                    // Route(root / "home" / endOfSegments, () => Home())//,
                    
                    Route(root / "home" / endOfSegments, () => DashboardPage()),
                    // Route(root / "chart" / endOfSegments, () => LiveChartView())
                    
                    // Route(root / "ingredients", () => IngredientBoard()),
                    // Route(root / "new-ingredient", () => NewIngredient(None)),
                    // Route(root / "update-ingredient" / segment[Int], (id: Int) => NewIngredient(Some(id))),
                    // Route(Recipes.topLevelPath / endOfSegments, () => RecipeBoard()),
                    // Route(
                    //   Recipes.editorPath / (segment[Int] || "new"),
                    //   (recipeIdOrNew: Either[Int, Unit]) => RecipeEditorContainer(recipeIdOrNew.swap.toOption)
                    // ),
                    // Route(
                    //   Recipes.viewRecipePath / segment[Int],
                    //   (recipeId: Int) => RecipeDisplayContainer(recipeId)
                    // ),
                    // Route(root / "basket", () => BasketBoard()),
                    // Route(
                    //   (root / "handle-registration").filter(_ => admin) ?
                    //     (qParam[String]("userName").? & qParam[String]("randomKey").?),
                    //   (_: Unit, matching: (Option[String], Option[String])) => AcceptUser(matching._1, matching._2)
                    // ),
                    // Route((root / "view-users").filter(_ => admin), () => ViewUsers())
                  )
                  .map(_.getOrElse(emptyNode))
              )
            )
        },
      /** The following child is a mock up until the actual data are loaded, in order to avoid blinking. */
      // child <-- meAndAdmin.foldLeft(true)((_, _) => false).map {
        // if (_)
        //   div(
        //     className := "app",
        //     GlobalHeader(AppUser("", Option(dom.window.sessionStorage.getItem("userName")).getOrElse("..."))),
        //     Navigation(false)
        //   )
        // else 
          // emptyNode
      // }
    )

    // go back to login if not connected
    //UITODO - OK
    me.filter(_.isEmpty).foreach(_ => Router.router.moveTo("/login"))//(element)
    me.collect { case Some(user) => user }
      .foreach(user => dom.window.sessionStorage.setItem("userName", user.name))//(element)

    element
  }

}

object DashBoardApp {

  def apply() = {
    println("Create DashBoardApp")
    new DashBoardApp()
  }
}
