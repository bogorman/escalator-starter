package com.escalatorstarter.http.server.privatizers

import com.escalatorstarter.models._

/**
  * Sanitizes User objects by clearing sensitive fields before sending to frontend
  */
object UserPrivatizer {

  /**
    * Clear sensitive fields from User before sending to client
    *
    * Removes:
    * - encryptedPassword
    * - resetPasswordToken
    * - rememberToken
    * - confirmationToken
    * - passwordSalt
    * - twoFactorAuthSecret
    * - IP addresses (current/last sign in)
    * - stripeCustomerId
    */
  def sanitize(user: User): User = {
    user.copy(
      encryptedPassword = "",
      resetPasswordToken = None,
      rememberToken = None
    )
  }
}
