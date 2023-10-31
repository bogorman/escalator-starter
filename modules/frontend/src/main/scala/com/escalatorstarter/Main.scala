package frontend

import com.raquo.laminar.nodes.RootNode
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import cats.effect.unsafe.implicits.global

import cats.effect.IO
import cats.effect.ExitCode

import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}
import scala.scalajs.{LinkingInfo, js}

object IndexCSS extends js.Object

object Main {
  val css: IndexCSS.type = IndexCSS

  val initializeHot: IO[Unit] = IO {
    if (LinkingInfo.developmentMode) {
      //hot.initialize()
      println("hot?")
    }
  }

  val createContainer: IO[Element] = IO {
    Option(dom.document.getElementById("app-container")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "app-container"
      dom.document.body.appendChild(elem)
      elem
    }
  }

  def render(container: dom.Element): IO[RootNode] = IO {
    com.raquo.laminar.api.L.render(container, frontend.app.EscalatorStarterWebApp())
  }

  val program: IO[ExitCode] =
    for {
      _ <- initializeHot
      container <- createContainer
      _ <- render(container)
    } yield ExitCode.Success

  def main(args: Array[String]): Unit = {    
    program.start.unsafeRunAndForget()
  }
}

