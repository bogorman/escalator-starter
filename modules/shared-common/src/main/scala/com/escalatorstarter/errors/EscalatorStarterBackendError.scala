package com.escalatorstarter.errors

import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}

// case class BackendError(errorKey: String, message: String) extends Throwable(message)

import escalator.errors.BackendError

object EscalatorStarterBackendError {
  def InvalidSessionError(message: String): BackendError = BackendError("backend.error.invalidSession", message)
  
  def UserNameAlreadyExists(userName: String): BackendError = BackendError("backend.error.nameAlreadyExists", userName)
  def UserNameNotFound(userName: String): BackendError = BackendError("backend.error.nameNotFound", userName)

  def PasswordIncorrect(userName: String): BackendError = BackendError("backend.error.passwordIncorrect", userName)

  def EmailAlreadyExists(email: String): BackendError = BackendError("backend.error.emailAlreadyExists", email)
  def EmailNotFound(email: String): BackendError = BackendError("backend.error.emailNotFound", email)

  def InvalidNewUser(reason: String): BackendError = BackendError("backend.error.invalidNewUser", reason)

  //
  def CandidateNotFound(token: String): BackendError = BackendError("backend.error.tokenNotFound", token)
  //
  
}
