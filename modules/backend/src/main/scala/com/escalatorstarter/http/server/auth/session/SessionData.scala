package com.escalatorstarter.http.server.auth

case class SessionData(id: String, username: String, role: Option[String]) {
	def isAdmin: Boolean = {
		role == Some("ADMIN")
	}
}