package com.escalatorstarter.http.server.auth

/**
 * Authentication constants used across the application
 * Centralized to ensure consistency
 */
object AuthConstants {

  /**
   * The name of the session cookie sent to/from clients
   * Used by:
   * - SessionCookie (creation)
   * - LabAuthDirective (validation)
   * - Any other authentication directives
   */
  val SESSION_COOKIE_NAME = "escalatorstarter_session"

}
