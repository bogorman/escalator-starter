package com.escalatorstarter.http.server.auth

import com.escalatorstarter.models._

// object SessionTypes {
//   val UI = SessionType("UI")
//   val CLI = SessionType("CLI")
// }

case class SessionData(
  id: String,
  email: String,
  role: Option[String],
  sessionType: UserSessionType = UserSessionTypes.UI
) {

  def isAdmin: Boolean = {
    role == Some("ADMIN")
  }

  def isUI: Boolean = sessionType.ident == "UI"

}
