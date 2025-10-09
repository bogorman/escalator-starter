package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:414

case class UserId(id: Long) extends AnyVal
case class Username(username: String) extends AnyVal
case class UserEmail(email: String) extends AnyVal
case class UserResetPasswordToken(resetPasswordToken: Option[String]) extends AnyVal
case class UserConfirmationToken(confirmationToken: Option[String]) extends AnyVal

case class User(
    id: UserId,
    username: Username,
    email: UserEmail,
    encryptedPassword: String,
    resetPasswordToken: Option[UserResetPasswordToken],
    rememberToken: Option[String],
    rememberCreatedAt: Option[escalator.util.Timestamp],
    signInCount: Int,
    currentSignInAt: Option[escalator.util.Timestamp],
    lastSignInAt: Option[escalator.util.Timestamp],
    currentSignInIp: Option[String],
    lastSignInIp: Option[String],
    confirmationToken: Option[UserConfirmationToken],
    confirmationAt: Option[escalator.util.Timestamp],
    confirmationSentAt: Option[escalator.util.Timestamp],
    passwordSalt: Option[String],
    fullName: String,
    initials: String,
    twoFactorAuthActive: Option[Boolean],
    twoFactorAuthSecret: Option[String],
    role: Option[String],
    status: String,
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object User {

  def apply(
      username: Username,
      email: UserEmail,
      encryptedPassword: String,
      resetPasswordToken: Option[UserResetPasswordToken],
      rememberToken: Option[String],
      rememberCreatedAt: Option[escalator.util.Timestamp],
      signInCount: Int,
      currentSignInAt: Option[escalator.util.Timestamp],
      lastSignInAt: Option[escalator.util.Timestamp],
      currentSignInIp: Option[String],
      lastSignInIp: Option[String],
      confirmationToken: Option[UserConfirmationToken],
      confirmationAt: Option[escalator.util.Timestamp],
      confirmationSentAt: Option[escalator.util.Timestamp],
      passwordSalt: Option[String],
      fullName: String,
      initials: String,
      twoFactorAuthActive: Option[Boolean],
      twoFactorAuthSecret: Option[String],
      role: Option[String],
      status: String
  ): User = {
    User(
      UserId(0L),
      username,
      email,
      encryptedPassword,
      resetPasswordToken,
      rememberToken,
      rememberCreatedAt,
      signInCount,
      currentSignInAt,
      lastSignInAt,
      currentSignInIp,
      lastSignInIp,
      confirmationToken,
      confirmationAt,
      confirmationSentAt,
      passwordSalt,
      fullName,
      initials,
      twoFactorAuthActive,
      twoFactorAuthSecret,
      role,
      status,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
