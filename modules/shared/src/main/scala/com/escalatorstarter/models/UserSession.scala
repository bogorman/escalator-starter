package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:278

case class UserSessionId(id: Long) extends AnyVal
case class UserSessionSessionId(sessionId: String) extends AnyVal

case class UserSession(
    id: UserSessionId,
    sessionId: UserSessionSessionId,
    userId: Option[UserId],
    email: String,
    role: Option[String],
    sessionType: UserSessionType,
    sessionData: Option[io.circe.Json],
    ipAddress: Option[String],
    userAgent: Option[String],
    lastAccessed: escalator.util.Timestamp,
    expiresAt: escalator.util.Timestamp,
    isActive: Option[Boolean],
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object UserSession {

  def apply(
      sessionId: UserSessionSessionId,
      userId: Option[UserId],
      email: String,
      role: Option[String],
      sessionType: UserSessionType,
      sessionData: Option[io.circe.Json],
      ipAddress: Option[String],
      userAgent: Option[String],
      lastAccessed: escalator.util.Timestamp,
      expiresAt: escalator.util.Timestamp,
      isActive: Option[Boolean]
  ): UserSession = {
    UserSession(
      UserSessionId(0L),
      sessionId,
      userId,
      email,
      role,
      sessionType,
      sessionData,
      ipAddress,
      userAgent,
      lastAccessed,
      expiresAt,
      isActive,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
