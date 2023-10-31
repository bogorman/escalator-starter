package com.escalatorstarter.http.server.auth

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._

case class SessionCookie(token: String = "") {
  lazy val value = token
  override def toString = value
}

object SessionCookie {

  // The name of the "session" cookie sent down to the client,
  // usually a browser.
  private val sessionCookieName = "escalatorstarter_session"

  def cookieName = sessionCookieName

  // Incoming cookie looks like SESSION=foobar (no quotes)
  // We only want the "foobar" part
  private val sessionCookieRegex = s"""${sessionCookieName}=([^"]+)""".r

  def apply(cookie: HttpCookiePair): SessionCookie = {
    if (cookie.name == sessionCookieName) {
      apply(token = cookie.value)
    } else {
      apply()
    }
  }

  def getSessionCookieHeader(
    content: String           = "",
    expires: Option[DateTime] = None,
    maxAge:  Option[Long]     = None): `Set-Cookie` = {
    `Set-Cookie`(HttpCookie(
      name = sessionCookieName,
      value = content,
      expires = expires,
      maxAge = maxAge,
      // Set "secure = true" if you want the "Secure" flag set on
      // outgoing cookies, meaning the cookie will only be accepted
      // over HTTPS (secure) connections.
      secure = false,
      httpOnly = true))
  }

  def getUnsetSessionCookieHeader = {
    getSessionCookieHeader(maxAge = Some(0L))
  }

}