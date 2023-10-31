package frontend.app.components.helpers.forms

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.html

import scala.util.Try

object InputInt {

  def apply(title: String, values: Observable[Int], valuesWriter: Observer[Int]): ReactiveHtmlElement[html.Span] = span(
    title,
    input(
      tpe := "number",
      value <-- values.map(_.toString),
      inContext(
        thisNode =>
          onInput.mapTo(Try(thisNode.ref.value.toInt).toOption).filter(_.isDefined).map(_.get) --> valuesWriter
      )
    )
  )

}
