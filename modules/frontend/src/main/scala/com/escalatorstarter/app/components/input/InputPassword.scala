package frontend.app.components.login

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import escalator.errors.BackendError
import org.scalajs.dom.html

object InputPassword {

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

    // div(
    //     cls := "mb-3",
    //     label(cls := "text-label form-label", forId := "dlab-password", "Password *"),
    //     div(
    //       cls := "input-group transparent-append",
    //       span(cls := "input-group-text", i(cls := "fa fa-lock")),
    //       input(
    //         tpe := "password",
    //         cls := "form-control",
    //         idAttr := "dlab-password",
    //         placeholder := "Choose a safe one..",
    //         required := true,
    //         value <-- formData.signal.map(_.password),
    //         inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> $changePW)                
    //       ),
    //       span(cls := "input-group-text show-pass", i(cls := "fa fa-eye-slash"), i(cls := "fa fa-eye")),
    //       div(cls := "invalid-feedback", "Please Enter a valid password.")
    //     )
    //   )      

   // val $specificError: EventStream[Option[BackendError]] = $errors.map{ m => m.get("name").flatMap(_.headOption) }

   val textType = Var(initial = "password")
   val $showPassActive = textType.signal.map{ t => if (t == "text") "active" else "inactive" }
   val $displayErrorMessage = $inputErrors.map{ e => if (e.isEmpty) "" else e.get.errorKey + " " + e.get.message }

   // s"Please ${placeholderText}"

     // div(
     //    cls := "mb-3",
     //    label(cls := "text-label form-label", forId := id, title),
     //    div(
     //      cls := "input-group transparent-append",
     //      span(cls := "input-group-text", i(cls := s"fa ${icon}")),
     //      input(
     //        tpe <-- textType,
     //        cls := "form-control",
     //        idAttr := id,
     //        placeholder := placeholderText,
     //        required := true,
     //        // value <-- formData.signal.map(_.name),
     //        value <-- values,
     //        inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> $changeFormData),
     //        cls <-- $inputErrors.map{ e => if (e.isEmpty) "is-valid" else "is-invalid" }       
     //      ),
     //      span(
     //        cls := "input-group-text show-pass", 
     //        cls <-- $showPassActive,
     //        i(cls := "fa fa-eye-slash",
     //          onClick.mapTo("text") --> textType
     //        ), 
     //        i(cls := "fa fa-eye",
     //          onClick.mapTo("password") --> textType
     //        )
     //      ),
     //      div(cls := "invalid-feedback", 
     //        child.text <-- $displayErrorMessage)
     //    )
     //  )

    div(
      cls := "form-group col-lg-12",
      div(
        cls := "form-label-group",
        label(forId := id, title),
        a(
          href := "#",
          cls := "fs-7 fw-medium",
          "Forgot Password ?")),
      div(
        cls := "input-group password-check",
        span(
          cls := "input-affix-wrapper",
          input(
            idAttr := id,
            cls := "form-control",
            placeholder := placeholderText,
            defaultValue := "",
            tpe := "password",
            required := true,
            value <-- values,
            inContext(thisNode => onInput.mapTo(thisNode.ref.value) --> $changeFormData),
            cls <-- $inputErrors.map{ e => if (e.isEmpty) "is-valid" else "is-invalid" }  
          ),
         a(
            href := "#",
            cls := "input-suffix text-muted",
            span(
              cls := "feather-icon",
              i(
                cls := "form-icon",
                dataAttr("feather") := "eye")),
            span(
              cls := "feather-icon d-none",
              i(
                cls := "form-icon",
                dataAttr("feather") := "eye-off")
              )
            ),          
          div(cls := "invalid-feedback", child.text <-- $displayErrorMessage)   
        )
      )
    )     
  }
}

