package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:32:575

import scala.concurrent.Future

import com.escalatorstarter.models._

trait UserSessionsTable {
  def tableName = "user_sessions"

  //def store(u: UserSession): Future[UserSession]

  //def store(ul: List[UserSession]): Future[List[UserSession]]

  //def insert(u: UserSession): Future[_]

  def upsertOnSessionId(u: UserSession): Future[UserSession]

  def existsOnSessionId(u: UserSession): Future[Boolean]

  def updateUserIdById(id: UserSessionId, userId: Option[UserId]): Future[UserSession]

  def updateEmailById(id: UserSessionId, email: String): Future[UserSession]

  def updateRoleById(id: UserSessionId, role: Option[String]): Future[UserSession]

  def updateSessionTypeById(id: UserSessionId, sessionType: UserSessionType): Future[UserSession]

  def updateSessionDataById(id: UserSessionId, sessionData: Option[io.circe.Json]): Future[UserSession]

  def updateIpAddresById(id: UserSessionId, ipAddress: Option[String]): Future[UserSession]

  def updateUserAgentById(id: UserSessionId, userAgent: Option[String]): Future[UserSession]

  def updateLastAccessedById(id: UserSessionId, lastAccessed: escalator.util.Timestamp): Future[UserSession]

  def updateExpiresAtById(id: UserSessionId, expiresAt: escalator.util.Timestamp): Future[UserSession]

  def updateIsActiveById(id: UserSessionId, isActive: Option[Boolean]): Future[UserSession]

  def updateUserIdBySessionId(sessionId: UserSessionSessionId, userId: Option[UserId]): Future[_]

  def updateEmailBySessionId(sessionId: UserSessionSessionId, email: String): Future[_]

  def updateRoleBySessionId(sessionId: UserSessionSessionId, role: Option[String]): Future[_]

  def updateSessionTypeBySessionId(sessionId: UserSessionSessionId, sessionType: UserSessionType): Future[_]

  def updateSessionDataBySessionId(sessionId: UserSessionSessionId, sessionData: Option[io.circe.Json]): Future[_]

  def updateIpAddresBySessionId(sessionId: UserSessionSessionId, ipAddress: Option[String]): Future[_]

  def updateUserAgentBySessionId(sessionId: UserSessionSessionId, userAgent: Option[String]): Future[_]

  def updateLastAccessedBySessionId(sessionId: UserSessionSessionId, lastAccessed: escalator.util.Timestamp): Future[_]

  def updateExpiresAtBySessionId(sessionId: UserSessionSessionId, expiresAt: escalator.util.Timestamp): Future[_]

  def updateIsActiveBySessionId(sessionId: UserSessionSessionId, isActive: Option[Boolean]): Future[_]

  def getById(u: UserSessionId): Future[Option[UserSession]]

  def getByIds(u: List[UserSessionId]): Future[List[UserSession]]

  def update(u: UserSession): Future[UserSession]

  def upsert(u: UserSession): Future[UserSession]

  def upsert(ul: List[UserSession]): Future[List[UserSession]]

  def delete(u: UserSession): Future[UserSession]

  def getBySessionId(sessionId: UserSessionSessionId): Future[Option[UserSession]]

  def getListByUserId(userId: UserId): Future[List[UserSession]]

  def getListByUserIds(userIds: List[UserId]): Future[List[UserSession]]
  def getListBySessionType(sessionType: UserSessionType): Future[List[UserSession]]

  def getListBySessionTypes(sessionTypes: List[UserSessionType]): Future[List[UserSession]]

  def count: Future[Long]

  def getAll(): Future[List[UserSession]]
}
