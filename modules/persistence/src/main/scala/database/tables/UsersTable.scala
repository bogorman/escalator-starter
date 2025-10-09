package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:11:773

import scala.concurrent.Future

import com.escalatorstarter.models._

trait UsersTable {
  def tableName = "users"

  //def store(u: User): Future[User]

  //def store(ul: List[User]): Future[List[User]]

  //def insert(u: User): Future[_]

  def upsertOnEmail(u: User): Future[User]

  def upsertOnConfirmationToken(u: User): Future[User]

  def upsertOnResetPasswordToken(u: User): Future[User]

  def upsertOnUsername(u: User): Future[User]

  def existsOnEmail(u: User): Future[Boolean]

  def existsOnUsername(u: User): Future[Boolean]

  def existsOnResetPasswordToken(u: User): Future[Boolean]

  def existsOnConfirmationToken(u: User): Future[Boolean]

  def updateEncryptedPasswordById(id: UserId, encryptedPassword: String): Future[User]

  def updateRememberTokenById(id: UserId, rememberToken: Option[String]): Future[User]

  def updateRememberCreatedAtById(id: UserId, rememberCreatedAt: Option[escalator.util.Timestamp]): Future[User]

  def updateSignInCountById(id: UserId, signInCount: Int): Future[User]

  def updateCurrentSignInAtById(id: UserId, currentSignInAt: Option[escalator.util.Timestamp]): Future[User]

  def updateLastSignInAtById(id: UserId, lastSignInAt: Option[escalator.util.Timestamp]): Future[User]

  def updateCurrentSignInIpById(id: UserId, currentSignInIp: Option[String]): Future[User]

  def updateLastSignInIpById(id: UserId, lastSignInIp: Option[String]): Future[User]

  def updateConfirmationAtById(id: UserId, confirmationAt: Option[escalator.util.Timestamp]): Future[User]

  def updateConfirmationSentAtById(id: UserId, confirmationSentAt: Option[escalator.util.Timestamp]): Future[User]

  def updatePasswordSaltById(id: UserId, passwordSalt: Option[String]): Future[User]

  def updateFullNameById(id: UserId, fullName: String): Future[User]

  def updateInitialById(id: UserId, initials: String): Future[User]

  def updateTwoFactorAuthActiveById(id: UserId, twoFactorAuthActive: Option[Boolean]): Future[User]

  def updateTwoFactorAuthSecretById(id: UserId, twoFactorAuthSecret: Option[String]): Future[User]

  def updateRoleById(id: UserId, role: Option[String]): Future[User]

  def updateStatuById(id: UserId, status: String): Future[User]

  def updateEncryptedPasswordByEmail(email: UserEmail, encryptedPassword: String): Future[_]

  def updateRememberTokenByEmail(email: UserEmail, rememberToken: Option[String]): Future[_]

  def updateRememberCreatedAtByEmail(email: UserEmail, rememberCreatedAt: Option[escalator.util.Timestamp]): Future[_]

  def updateSignInCountByEmail(email: UserEmail, signInCount: Int): Future[_]

  def updateCurrentSignInAtByEmail(email: UserEmail, currentSignInAt: Option[escalator.util.Timestamp]): Future[_]

  def updateLastSignInAtByEmail(email: UserEmail, lastSignInAt: Option[escalator.util.Timestamp]): Future[_]

  def updateCurrentSignInIpByEmail(email: UserEmail, currentSignInIp: Option[String]): Future[_]

  def updateLastSignInIpByEmail(email: UserEmail, lastSignInIp: Option[String]): Future[_]

  def updateConfirmationAtByEmail(email: UserEmail, confirmationAt: Option[escalator.util.Timestamp]): Future[_]

  def updateConfirmationSentAtByEmail(email: UserEmail, confirmationSentAt: Option[escalator.util.Timestamp]): Future[_]

  def updatePasswordSaltByEmail(email: UserEmail, passwordSalt: Option[String]): Future[_]

  def updateFullNameByEmail(email: UserEmail, fullName: String): Future[_]

  def updateInitialByEmail(email: UserEmail, initials: String): Future[_]

  def updateTwoFactorAuthActiveByEmail(email: UserEmail, twoFactorAuthActive: Option[Boolean]): Future[_]

  def updateTwoFactorAuthSecretByEmail(email: UserEmail, twoFactorAuthSecret: Option[String]): Future[_]

  def updateRoleByEmail(email: UserEmail, role: Option[String]): Future[_]

  def updateStatuByEmail(email: UserEmail, status: String): Future[_]

  def updateEncryptedPasswordByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      encryptedPassword: String
  ): Future[_]

  def updateRememberTokenByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      rememberToken: Option[String]
  ): Future[_]

  def updateRememberCreatedAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateSignInCountByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      signInCount: Int
  ): Future[_]

  def updateCurrentSignInAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateLastSignInAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateCurrentSignInIpByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      currentSignInIp: Option[String]
  ): Future[_]

  def updateLastSignInIpByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      lastSignInIp: Option[String]
  ): Future[_]

  def updateConfirmationAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      confirmationAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateConfirmationSentAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updatePasswordSaltByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      passwordSalt: Option[String]
  ): Future[_]

  def updateFullNameByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      fullName: String
  ): Future[_]

  def updateInitialByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken], initials: String): Future[_]

  def updateTwoFactorAuthActiveByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      twoFactorAuthActive: Option[Boolean]
  ): Future[_]

  def updateTwoFactorAuthSecretByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      twoFactorAuthSecret: Option[String]
  ): Future[_]

  def updateRoleByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      role: Option[String]
  ): Future[_]

  def updateStatuByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken], status: String): Future[_]

  def updateEncryptedPasswordByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      encryptedPassword: String
  ): Future[_]

  def updateRememberTokenByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      rememberToken: Option[String]
  ): Future[_]

  def updateRememberCreatedAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateSignInCountByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      signInCount: Int
  ): Future[_]

  def updateCurrentSignInAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateLastSignInAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateCurrentSignInIpByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      currentSignInIp: Option[String]
  ): Future[_]

  def updateLastSignInIpByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      lastSignInIp: Option[String]
  ): Future[_]

  def updateConfirmationAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      confirmationAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateConfirmationSentAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updatePasswordSaltByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      passwordSalt: Option[String]
  ): Future[_]

  def updateFullNameByConfirmationToken(confirmationToken: Option[UserConfirmationToken], fullName: String): Future[_]

  def updateInitialByConfirmationToken(confirmationToken: Option[UserConfirmationToken], initials: String): Future[_]

  def updateTwoFactorAuthActiveByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      twoFactorAuthActive: Option[Boolean]
  ): Future[_]

  def updateTwoFactorAuthSecretByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      twoFactorAuthSecret: Option[String]
  ): Future[_]

  def updateRoleByConfirmationToken(confirmationToken: Option[UserConfirmationToken], role: Option[String]): Future[_]

  def updateStatuByConfirmationToken(confirmationToken: Option[UserConfirmationToken], status: String): Future[_]

  def updateEncryptedPasswordByUsername(username: Username, encryptedPassword: String): Future[_]

  def updateRememberTokenByUsername(username: Username, rememberToken: Option[String]): Future[_]

  def updateRememberCreatedAtByUsername(
      username: Username,
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateSignInCountByUsername(username: Username, signInCount: Int): Future[_]

  def updateCurrentSignInAtByUsername(username: Username, currentSignInAt: Option[escalator.util.Timestamp]): Future[_]

  def updateLastSignInAtByUsername(username: Username, lastSignInAt: Option[escalator.util.Timestamp]): Future[_]

  def updateCurrentSignInIpByUsername(username: Username, currentSignInIp: Option[String]): Future[_]

  def updateLastSignInIpByUsername(username: Username, lastSignInIp: Option[String]): Future[_]

  def updateConfirmationAtByUsername(username: Username, confirmationAt: Option[escalator.util.Timestamp]): Future[_]

  def updateConfirmationSentAtByUsername(
      username: Username,
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updatePasswordSaltByUsername(username: Username, passwordSalt: Option[String]): Future[_]

  def updateFullNameByUsername(username: Username, fullName: String): Future[_]

  def updateInitialByUsername(username: Username, initials: String): Future[_]

  def updateTwoFactorAuthActiveByUsername(username: Username, twoFactorAuthActive: Option[Boolean]): Future[_]

  def updateTwoFactorAuthSecretByUsername(username: Username, twoFactorAuthSecret: Option[String]): Future[_]

  def updateRoleByUsername(username: Username, role: Option[String]): Future[_]

  def updateStatuByUsername(username: Username, status: String): Future[_]

  def getById(u: UserId): Future[Option[User]]

  def update(u: User): Future[User]

  def upsert(u: User): Future[User]

  def upsert(ul: List[User]): Future[List[User]]

  def delete(u: User): Future[User]

  def getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]): Future[Option[User]]

  def getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]): Future[Option[User]]

  def getByEmail(email: UserEmail): Future[Option[User]]

  def getByUsername(username: Username): Future[Option[User]]

  def count: Future[Long]

  def getAll(): Future[List[User]]
}
