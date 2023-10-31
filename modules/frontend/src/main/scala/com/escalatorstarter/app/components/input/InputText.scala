package frontend.app.components.login

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.errors.BackendError
import org.scalajs.dom.html

object InputText {

  def apply(
  	  id: String,
      title: String,
      placeholderText: String,
      icon: String,
      values: Signal[String],
      $changeFormData: Observer[String],
      // $errors: EventStream[Map[String, List[BackendError]]]
      $inputErrors: EventStream[Option[BackendError]]
  ): ReactiveHtmlElement[html.Div] = {

    val $displayErrorMessage = $inputErrors.map{ e => if (e.isEmpty) "" else e.get.errorKey + " " + e.get.message }
  
    div(
      cls := "form-group col-lg-12",
      div(
        cls := "form-label-group",
        label(forId := id, title)),
        input(
          cls := "form-control",
          placeholder := placeholderText,
          defaultValue := "",
          tpe := "text",
          required := true,
          value <-- values,
          inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> $changeFormData),
          cls <-- $inputErrors.map{ e => if (e.isEmpty) "is-valid" else "is-invalid" }  
        ),
        div(cls := "invalid-feedback",
            child.text <-- $displayErrorMessage
        )        
    )         

  }

}