package com.escalatorstarter.core.repositories.postgres

import scala.concurrent.Future

import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

import escalator.util.postgres.PostgresDatabase
import escalator.util.events.EventBus
import com.escalatorstarter.common.persistence.postgres.PostgresMappedEncoder

import com.escalatorstarter.models._
import com.escalatorstarter.persistence.database.tables.UserSessionsTable
import com.escalatorstarter.persistence.postgres.tables.PostgresUserSessionsTable

/**
 * Extended UserSessionsRepository with methods required by DBSessionCache.
 * Extends the auto-generated PostgresUserSessionsTable with additional functionality.
 */
class UserSessionsRepository(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends PostgresUserSessionsTable(database) {

  // Override methods here to add custom repository logic
  // This class inherits all standard CRUD operations from PostgresUserSessionsTable

  import PostgresMappedEncoder._
  import monix.execution.Scheduler.Implicits.global
  import ctx._
  import escalator.util.postgres.MonitoredPostgresOperation
  import escalator.util.{TimeUtil, Timestamp}
  import io.getquill._

  override def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  /**
   * Get session by session_id string (required by DBSessionCache)
   */
  def getBySessionIdString(sessionId: String): Future[Option[UserSession]] = {
    getBySessionId(UserSessionSessionId(sessionId))
  }

  /**
   * Update last accessed time for a session_id string (required by DBSessionCache)
   */
  def updateLastAccessedBySessionIdString(sessionId: String): Future[Unit] = {
    val now = TimeUtil.nowTimestamp()
    updateLastAccessedBySessionId(UserSessionSessionId(sessionId), now).map(_ => ())
  }

  /**
   * Get all sessions for an email (required by DBSessionCache)
   */
  def getByEmail(email: String): Future[List[UserSession]] = {
    monitored("get-by-email") {
      read {
        ctx.run(
          query[UserSession]
            .filter(session => session.email == lift(email))
        ).runToFuture
      }
    }
  }

  /**
   * Get recently active sessions for warm-up (used by HybridSessionCache)
   */
  def getRecentActive(limit: Int): Future[List[UserSession]] = {
    monitored("get-recent-active") {
      read {
        val now = TimeUtil.nowTimestamp()
        ctx.run(
          query[UserSession]
            .filter(session => session.expiresAt > lift(now))
            .sortBy(_.lastAccessed)(Ord.desc)
            .take(lift(limit))
        ).runToFuture
      }
    }
  }

  /**
   * Delete expired sessions (required by DBSessionCache)
   */
  def deleteExpired(): Future[Long] = {
    monitored("delete-expired") {
      val now = TimeUtil.nowTimestamp()
      // Get all expired sessions and delete them individually to publish events
      read {
        ctx.run(
          query[UserSession]
            .filter(session => session.expiresAt < lift(now))
        ).runToFuture
      }.flatMap { expiredSessions =>
        Future.sequence(expiredSessions.map(session => delete(session)))
          .map(_.length.toLong)
      }
    }
  }

  /**
   * Delete session by session_id string (required by DBSessionCache)
   */
  def deleteBySessionId(sessionId: String): Future[Boolean] = {
    monitored("delete-by-session-id") {
      getBySessionId(UserSessionSessionId(sessionId)).flatMap {
        case Some(session) => delete(session).map(_ => true)
        case None => Future.successful(false)
      }
    }
  }

  /**
   * Delete all sessions for an email (required by DBSessionCache)
   */
  def deleteByEmail(email: String): Future[Long] = {
    monitored("delete-by-email") {
      // Get all sessions for email and delete them individually to publish events
      getByEmail(email).flatMap { userSessions =>
        Future.sequence(userSessions.map(session => delete(session)))
          .map(_.length.toLong)
      }
    }
  }

}
