package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:32:716

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

  def upsertOnAccessToken(u: User): Future[User]

  def existsOnAccessToken(u: User): Future[Boolean]

  def existsOnEmail(u: User): Future[Boolean]

  def existsOnResetPasswordToken(u: User): Future[Boolean]

  def existsOnConfirmationToken(u: User): Future[Boolean]

  def updateEncryptedPasswordById(id: UserId, encryptedPassword: String): Future[User]

  def updateRememberTokenById(id: UserId, rememberToken: Option[String]): Future[User]

  def updateRememberCreatedAtById(id: UserId, rememberCreatedAt: Option[escalator.util.Timestamp]): Future[User]

  def updateConfirmedAtById(id: UserId, confirmedAt: Option[escalator.util.Timestamp]): Future[User]

  def updateConfirmationSentAtById(id: UserId, confirmationSentAt: Option[escalator.util.Timestamp]): Future[User]

  def updatePasswordSaltById(id: UserId, passwordSalt: Option[String]): Future[User]

  def updateFullNameById(id: UserId, fullName: Option[String]): Future[User]

  def updateInitialById(id: UserId, initials: Option[String]): Future[User]

  def updateTwoFactorAuthActiveById(id: UserId, twoFactorAuthActive: Option[Boolean]): Future[User]

  def updateTwoFactorAuthSecretById(id: UserId, twoFactorAuthSecret: Option[String]): Future[User]

  def updateSignInCountById(id: UserId, signInCount: Option[Int]): Future[User]

  def updateCurrentSignInAtById(id: UserId, currentSignInAt: Option[escalator.util.Timestamp]): Future[User]

  def updateLastSignInAtById(id: UserId, lastSignInAt: Option[escalator.util.Timestamp]): Future[User]

  def updateCurrentSignInIpById(id: UserId, currentSignInIp: Option[String]): Future[User]

  def updateLastSignInIpById(id: UserId, lastSignInIp: Option[String]): Future[User]

  def updateRoleById(id: UserId, role: UserRoleType): Future[User]

  def updateStatuById(id: UserId, status: UserStatusType): Future[User]

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

  def updateConfirmedAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      confirmedAt: Option[escalator.util.Timestamp]
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
      fullName: Option[String]
  ): Future[_]

  def updateInitialByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      initials: Option[String]
  ): Future[_]

  def updateTwoFactorAuthActiveByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      twoFactorAuthActive: Option[Boolean]
  ): Future[_]

  def updateTwoFactorAuthSecretByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      twoFactorAuthSecret: Option[String]
  ): Future[_]

  def updateSignInCountByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      signInCount: Option[Int]
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

  def updateRoleByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken], role: UserRoleType): Future[_]

  def updateStatuByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      status: UserStatusType
  ): Future[_]

  def updateEncryptedPasswordByAccessToken(accessToken: UserAccessToken, encryptedPassword: String): Future[_]

  def updateRememberTokenByAccessToken(accessToken: UserAccessToken, rememberToken: Option[String]): Future[_]

  def updateRememberCreatedAtByAccessToken(
      accessToken: UserAccessToken,
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateConfirmedAtByAccessToken(
      accessToken: UserAccessToken,
      confirmedAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateConfirmationSentAtByAccessToken(
      accessToken: UserAccessToken,
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updatePasswordSaltByAccessToken(accessToken: UserAccessToken, passwordSalt: Option[String]): Future[_]

  def updateFullNameByAccessToken(accessToken: UserAccessToken, fullName: Option[String]): Future[_]

  def updateInitialByAccessToken(accessToken: UserAccessToken, initials: Option[String]): Future[_]

  def updateTwoFactorAuthActiveByAccessToken(
      accessToken: UserAccessToken,
      twoFactorAuthActive: Option[Boolean]
  ): Future[_]

  def updateTwoFactorAuthSecretByAccessToken(
      accessToken: UserAccessToken,
      twoFactorAuthSecret: Option[String]
  ): Future[_]

  def updateSignInCountByAccessToken(accessToken: UserAccessToken, signInCount: Option[Int]): Future[_]

  def updateCurrentSignInAtByAccessToken(
      accessToken: UserAccessToken,
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateLastSignInAtByAccessToken(
      accessToken: UserAccessToken,
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateCurrentSignInIpByAccessToken(accessToken: UserAccessToken, currentSignInIp: Option[String]): Future[_]

  def updateLastSignInIpByAccessToken(accessToken: UserAccessToken, lastSignInIp: Option[String]): Future[_]

  def updateRoleByAccessToken(accessToken: UserAccessToken, role: UserRoleType): Future[_]

  def updateStatuByAccessToken(accessToken: UserAccessToken, status: UserStatusType): Future[_]

  def updateEncryptedPasswordByEmail(email: UserEmail, encryptedPassword: String): Future[_]

  def updateRememberTokenByEmail(email: UserEmail, rememberToken: Option[String]): Future[_]

  def updateRememberCreatedAtByEmail(email: UserEmail, rememberCreatedAt: Option[escalator.util.Timestamp]): Future[_]

  def updateConfirmedAtByEmail(email: UserEmail, confirmedAt: Option[escalator.util.Timestamp]): Future[_]

  def updateConfirmationSentAtByEmail(email: UserEmail, confirmationSentAt: Option[escalator.util.Timestamp]): Future[_]

  def updatePasswordSaltByEmail(email: UserEmail, passwordSalt: Option[String]): Future[_]

  def updateFullNameByEmail(email: UserEmail, fullName: Option[String]): Future[_]

  def updateInitialByEmail(email: UserEmail, initials: Option[String]): Future[_]

  def updateTwoFactorAuthActiveByEmail(email: UserEmail, twoFactorAuthActive: Option[Boolean]): Future[_]

  def updateTwoFactorAuthSecretByEmail(email: UserEmail, twoFactorAuthSecret: Option[String]): Future[_]

  def updateSignInCountByEmail(email: UserEmail, signInCount: Option[Int]): Future[_]

  def updateCurrentSignInAtByEmail(email: UserEmail, currentSignInAt: Option[escalator.util.Timestamp]): Future[_]

  def updateLastSignInAtByEmail(email: UserEmail, lastSignInAt: Option[escalator.util.Timestamp]): Future[_]

  def updateCurrentSignInIpByEmail(email: UserEmail, currentSignInIp: Option[String]): Future[_]

  def updateLastSignInIpByEmail(email: UserEmail, lastSignInIp: Option[String]): Future[_]

  def updateRoleByEmail(email: UserEmail, role: UserRoleType): Future[_]

  def updateStatuByEmail(email: UserEmail, status: UserStatusType): Future[_]

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

  def updateConfirmedAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      confirmedAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updateConfirmationSentAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[_]

  def updatePasswordSaltByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      passwordSalt: Option[String]
  ): Future[_]

  def updateFullNameByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      fullName: Option[String]
  ): Future[_]

  def updateInitialByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      initials: Option[String]
  ): Future[_]

  def updateTwoFactorAuthActiveByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      twoFactorAuthActive: Option[Boolean]
  ): Future[_]

  def updateTwoFactorAuthSecretByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      twoFactorAuthSecret: Option[String]
  ): Future[_]

  def updateSignInCountByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      signInCount: Option[Int]
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

  def updateRoleByConfirmationToken(confirmationToken: Option[UserConfirmationToken], role: UserRoleType): Future[_]

  def updateStatuByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      status: UserStatusType
  ): Future[_]

  def getById(u: UserId): Future[Option[User]]

  def getByIds(u: List[UserId]): Future[List[User]]

  def update(u: User): Future[User]

  def upsert(u: User): Future[User]

  def upsert(ul: List[User]): Future[List[User]]

  def delete(u: User): Future[User]

  def getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]): Future[Option[User]]

  def getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]): Future[Option[User]]

  def getByEmail(email: UserEmail): Future[Option[User]]

  def getByAccessToken(accessToken: UserAccessToken): Future[Option[User]]

  def getListByRole(role: UserRoleType): Future[List[User]]

  def getListByRoles(roles: List[UserRoleType]): Future[List[User]]
  def getListByStatu(status: UserStatusType): Future[List[User]]

  def getListByStatus(statuss: List[UserStatusType]): Future[List[User]]

  def count: Future[Long]

  def getAll(): Future[List[User]]
}
