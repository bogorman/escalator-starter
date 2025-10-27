package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:296

case class UserId(id: Long) extends AnyVal
case class UserEmail(email: String) extends AnyVal
case class UserResetPasswordToken(resetPasswordToken: Option[String]) extends AnyVal
case class UserConfirmationToken(confirmationToken: Option[String]) extends AnyVal
case class UserAccessToken(accessToken: java.util.UUID) extends AnyVal

case class User(
    id: UserId,
    email: UserEmail,
    encryptedPassword: String,
    resetPasswordToken: Option[UserResetPasswordToken],
    rememberToken: Option[String],
    rememberCreatedAt: Option[escalator.util.Timestamp],
    confirmationToken: Option[UserConfirmationToken],
    confirmedAt: Option[escalator.util.Timestamp],
    confirmationSentAt: Option[escalator.util.Timestamp],
    passwordSalt: Option[String],
    fullName: Option[String],
    initials: Option[String],
    twoFactorAuthActive: Option[Boolean],
    twoFactorAuthSecret: Option[String],
    signInCount: Option[Int],
    currentSignInAt: Option[escalator.util.Timestamp],
    lastSignInAt: Option[escalator.util.Timestamp],
    currentSignInIp: Option[String],
    lastSignInIp: Option[String],
    role: UserRoleType,
    status: UserStatusType,
    accessToken: UserAccessToken,
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object User {

  def apply(
      email: UserEmail,
      encryptedPassword: String,
      resetPasswordToken: Option[UserResetPasswordToken],
      rememberToken: Option[String],
      rememberCreatedAt: Option[escalator.util.Timestamp],
      confirmationToken: Option[UserConfirmationToken],
      confirmedAt: Option[escalator.util.Timestamp],
      confirmationSentAt: Option[escalator.util.Timestamp],
      passwordSalt: Option[String],
      fullName: Option[String],
      initials: Option[String],
      twoFactorAuthActive: Option[Boolean],
      twoFactorAuthSecret: Option[String],
      signInCount: Option[Int],
      currentSignInAt: Option[escalator.util.Timestamp],
      lastSignInAt: Option[escalator.util.Timestamp],
      currentSignInIp: Option[String],
      lastSignInIp: Option[String],
      role: UserRoleType,
      status: UserStatusType,
      accessToken: UserAccessToken
  ): User = {
    User(
      UserId(0L),
      email,
      encryptedPassword,
      resetPasswordToken,
      rememberToken,
      rememberCreatedAt,
      confirmationToken,
      confirmedAt,
      confirmationSentAt,
      passwordSalt,
      fullName,
      initials,
      twoFactorAuthActive,
      twoFactorAuthSecret,
      signInCount,
      currentSignInAt,
      lastSignInAt,
      currentSignInIp,
      lastSignInIp,
      role,
      status,
      accessToken,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
