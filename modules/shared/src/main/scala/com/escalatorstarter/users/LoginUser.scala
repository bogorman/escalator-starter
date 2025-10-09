package models.users

import escalator.errors.BackendError
import escalator.validators.FieldsValidator
import escalator.validators.StringValidators._
import escalator.syntax.WithUnit

final case class LoginUser(email: String, password: String)

object LoginUser {

  implicit def loginUserWithUnit: WithUnit[LoginUser] = WithUnit(LoginUser("", ""))

  def validator: FieldsValidator[LoginUser, BackendError] =
    FieldsValidator(
      Map(
        "email" -> nonEmptyString.contraMap[LoginUser](_.email),
        "password" -> nonEmptyString.contraMap[LoginUser](_.password)
      )
    )

}
