package frontend.app.components

import com.raquo.laminar.api.L._
import org.scalajs.dom

object StarRatingComponent {

  private def star(isFilled: Boolean, onClickCallback: () => Unit): Span = {
    span(
      cls := (if (isFilled) "fas" else "far") + " fa-star fa-2x",
      cursor.pointer,
      onClick --> (_ => onClickCallback())
    )
  }

  def apply(ratingVar: Var[Int] = Var(0)): HtmlElement = {
    // val ratingVar = Var(initialRating)

    div(
      // text-center
      cls := "star-rating-section mt-4",
      children <-- ratingVar.signal.map { rating =>
        (1 to 5).map { i =>
          star(i <= rating, () => ratingVar.set(i))
        }.toList
      }
    )
  }
}