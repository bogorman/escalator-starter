package com.escalatorstarter.http.server.auth

import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
import java.time.{Instant, Duration => JavaDuration}
import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}
import java.util.concurrent.TimeUnit
import com.escalatorstarter.core.repositories.EscalatorStarterRepository
import com.escalatorstarter.models._
import escalator.util.logging.Logger
import io.circe.syntax._
import io.circe.parser._
import io.circe.generic.auto._

import escalator.util.Timestamp
import escalator.util.TimeUtil

/**
 * Database-backed implementation of SessionCache.
 * Sessions are persisted to PostgreSQL and survive application restarts.
 * Includes an optional L1 cache for performance.
 *
 * @param repository The database repository for session operations
 * @param uiTtlHours Time-to-live for UI sessions in hours
 * @param cliTtlHours Time-to-live for CLI sessions in hours
 * @param l1CacheSize Size of the L1 cache (0 to disable)
 * @param l1CacheTtlMinutes TTL for L1 cache entries in minutes
 * @param maxSessionsPerUser Maximum concurrent sessions per user
 */
class DBSessionCache(
  repository: EscalatorStarterRepository,
  ttlHours: Int = 24,
  l1CacheSize: Long = 100L,
  l1CacheTtlMinutes: Int = 5,
  maxSessionsPerUser: Int = 5
)(implicit ec: ExecutionContext, logger: Logger) extends SessionCache {

  // Optional L1 cache for frequently accessed sessions
  private val l1Cache: Option[Cache[SessionData]] = if (l1CacheSize > 0) {
    val cacheBuilder = Caffeine
      .newBuilder
      .maximumSize(l1CacheSize)
      .expireAfterWrite(l1CacheTtlMinutes, TimeUnit.MINUTES)
      .build[String, Entry[SessionData]]
    Some(CaffeineCache[SessionData](cacheBuilder))
  } else {
    None
  }

  override def getSession(sessionId: String): Option[SessionData] = {
    // Check L1 cache first
    l1Cache.flatMap(_.get(sessionId)).orElse {
      // Fall back to database
      try {
        val future = repository.userSessions.getBySessionIdString(sessionId).map { sessionOpt =>
          sessionOpt.flatMap { session =>
            // Check if session is expired
            val now = TimeUtil.nowTimestamp()

            if (session.expiresAt.after(now)) {
              // Convert database session to SessionData
              val sessionData = SessionData(
                id = session.sessionId.sessionId,
                email = session.email,
                role = session.role,
                sessionType = session.sessionType
              )

              // Update L1 cache
              l1Cache.foreach(_.put(sessionId)(sessionData))

              // Update last accessed time asynchronously
              repository.userSessions.updateLastAccessedBySessionIdString(session.sessionId.sessionId).failed.foreach { ex =>
                logger.error(ex, s"Failed to update last accessed time for session $sessionId")
              }

              Some(sessionData)
            } else {
              // Session expired, remove it
              removeSession(sessionId)
              None
            }
          }
        }

        Await.result(future, 5.seconds)
      } catch {
        case ex: Exception =>
          logger.error(ex, s"Failed to get session $sessionId from database")
          None
      }
    }
  }

  override def setSession(sessionId: String, data: SessionData): Unit = {
    println("setSession")
    // Update L1 cache
    l1Cache.foreach(_.put(sessionId)(data))

    // Persist to database
    val now = TimeUtil.nowTimestamp()

    // Calculate TTL based on session type
    val ttl = ttlHours.hours
    val expiresAt = now + ttl

    val session = UserSession(
      sessionId = UserSessionSessionId(sessionId),
      userId = None,
      email = data.email,
      role = data.role,
      sessionData = None, // Can store additional JSON data if needed
      ipAddress = None,
      userAgent = None,
      lastAccessed = now,
      expiresAt = expiresAt,
      isActive = Some(true),
      sessionType = data.sessionType
    )

    val future = repository.userSessions.upsert(session).map { stored =>
      logger.debug(s"Stored session $sessionId for user ${data.email}")

      // Enforce max sessions per user
      enforceMaxSessionsPerUser(data.email)
    }.recover {
      case ex: Exception =>
        logger.error(ex, s"Failed to store session $sessionId to database")
        // Remove from L1 cache if database store failed
        l1Cache.foreach(_.remove(sessionId))
    }

    // Wait for database operation to complete
    try {
      Await.result(future, 5.seconds)
    } catch {
      case ex: Exception =>
        logger.error(ex, s"Timeout storing session $sessionId")
    }
  }

  override def removeSession(sessionId: String): Unit = {
    // Remove from L1 cache
    l1Cache.foreach(_.remove(sessionId))

    // Remove from database
    val future = repository.userSessions.deleteBySessionId(sessionId).map { deleted =>
      if (deleted) {
        logger.debug(s"Removed session $sessionId")
      }
    }.recover {
      case ex: Exception =>
        logger.error(ex, s"Failed to remove session $sessionId from database")
    }

    try {
      Await.result(future, 5.seconds)
    } catch {
      case ex: Exception =>
        logger.error(ex, s"Timeout removing session $sessionId")
    }
  }

  override def cleanup(): Unit = {
    // Clean up L1 cache
    l1Cache.foreach { cache =>
      // Force cleanup of expired entries
      // Caffeine cache cleanup is handled automatically
      // Manual cleanup not needed with scalacache wrapper
    }

    // Remove expired sessions from database
    val future = repository.userSessions.deleteExpired().map { count =>
      logger.info(s"Cleaned up $count expired sessions from database")
    }.recover {
      case ex: Exception =>
        logger.error(ex, "Failed to clean up expired sessions")
    }

    try {
      Await.result(future, 30.seconds)
    } catch {
      case ex: Exception =>
        logger.error(ex, "Timeout cleaning up sessions")
    }
  }

  override def getUserSessions(username: String): List[String] = {
    try {
      val now = TimeUtil.nowTimestamp()

      val future = repository.userSessions.getByEmail(username).map { sessions =>
        sessions.filter(_.expiresAt.after(now))
          .map(_.sessionId.sessionId)
      }
      Await.result(future, 5.seconds)
    } catch {
      case ex: Exception =>
        logger.error(ex, s"Failed to get sessions for user $username")
        List.empty
    }
  }

  override def removeUserSessions(username: String): Unit = {
    // Remove from L1 cache
    getUserSessions(username).foreach { sessionId =>
      l1Cache.foreach(_.remove(sessionId))
    }

    // Remove from database
    val future = repository.userSessions.deleteByEmail(username).map { count =>
      logger.info(s"Removed $count sessions for user $username")
    }.recover {
      case ex: Exception =>
        logger.error(ex, s"Failed to remove sessions for user $username")
    }

    try {
      Await.result(future, 5.seconds)
    } catch {
      case ex: Exception =>
        logger.error(ex, s"Timeout removing sessions for user $username")
    }
  }

  override def touchSession(sessionId: String): Unit = {
    // Update last accessed time in database
    val future = repository.userSessions.getBySessionIdString(sessionId).flatMap {
      case Some(session) =>
        repository.userSessions.updateLastAccessedBySessionIdString(session.sessionId.sessionId)
      case None =>
        Future.successful(())
    }.recover {
      case ex: Exception =>
        logger.error(ex, s"Failed to touch session $sessionId")
    }

    // Fire and forget - don't wait for completion
    future
  }

  private def enforceMaxSessionsPerUser(email: String): Unit = {
    val future = repository.userSessions.getByEmail(email).map { sessions =>
      val now = TimeUtil.nowTimestamp()

      val activeSessions = sessions
        .filter(_.expiresAt.after(now))
        .sortBy(_.lastAccessed)(Ordering[Timestamp].reverse)

      if (activeSessions.size > maxSessionsPerUser) {
        // Remove oldest sessions
        val toRemove = activeSessions.drop(maxSessionsPerUser)
        toRemove.foreach { session =>
          removeSession(session.sessionId.sessionId)
        }
        logger.info(s"Removed ${toRemove.size} old sessions for user $email (max: $maxSessionsPerUser)")
      }
    }.recover {
      case ex: Exception =>
        logger.error(ex, s"Failed to enforce max sessions for user $email")
    }

    // Fire and forget
    future
  }
}
