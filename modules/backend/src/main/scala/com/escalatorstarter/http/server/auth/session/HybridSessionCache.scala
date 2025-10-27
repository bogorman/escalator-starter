package com.escalatorstarter.http.server.auth

import scala.concurrent.{ExecutionContext, Future}
import com.escalatorstarter.core.repositories.EscalatorStarterRepository
import escalator.util.logging.Logger

/**
 * Hybrid implementation that combines in-memory and database caching.
 * Uses in-memory cache as primary storage with database as fallback and persistence layer.
 * Provides the best of both worlds: speed of in-memory access with persistence across restarts.
 *
 * @param repository The database repository for session operations
 * @param memoryCacheSize Maximum number of sessions in memory cache
 * @param uiTtlHours Time-to-live for UI sessions in hours
 * @param cliTtlHours Time-to-live for CLI sessions in hours
 * @param maxSessionsPerUser Maximum concurrent sessions per user
 */
class HybridSessionCache(
  repository: EscalatorStarterRepository,
  memoryCacheSize: Long = 1000L,
  ttlHours: Int = 24,
  maxSessionsPerUser: Int = 5
)(implicit ec: ExecutionContext, logger: Logger) extends SessionCache {

  // Primary cache: in-memory for fast access
  private val memoryCache = new InMemorySessionCache(memoryCacheSize, ttlHours)

  // Secondary storage: database for persistence
  private val dbCache = new DBSessionCache(
    repository,
    ttlHours,
    0L, // Disable L1 cache in DB cache since we have memory cache
    0,  // No L1 cache TTL
    maxSessionsPerUser
  )

  override def getSession(sessionId: String): Option[SessionData] = {
    // Try memory cache first
    memoryCache.getSession(sessionId).orElse {
      // Fall back to database
      val dbSession = dbCache.getSession(sessionId)

      // If found in database, restore to memory cache
      dbSession.foreach { data =>
        memoryCache.setSession(sessionId, data)
        logger.debug(s"Restored session $sessionId from database to memory cache")
      }

      dbSession
    }
  }

  override def setSession(sessionId: String, data: SessionData): Unit = {
    // Write to both caches
    memoryCache.setSession(sessionId, data)

    // Write to database asynchronously
    Future {
      dbCache.setSession(sessionId, data)
    }.failed.foreach { ex =>
      logger.error(ex, s"Failed to persist session $sessionId to database")
    }
  }

  override def removeSession(sessionId: String): Unit = {
    // Remove from both caches
    memoryCache.removeSession(sessionId)

    // Remove from database asynchronously
    Future {
      dbCache.removeSession(sessionId)
    }.failed.foreach { ex =>
      logger.error(ex, s"Failed to remove session $sessionId from database")
    }
  }

  override def cleanup(): Unit = {
    // Clean up both caches
    memoryCache.cleanup()
    dbCache.cleanup()
  }

  override def touchSession(sessionId: String): Unit = {
    // Touch in memory cache (fast)
    memoryCache.touchSession(sessionId)

    // Touch in database asynchronously
    Future {
      dbCache.touchSession(sessionId)
    }.failed.foreach { ex =>
      logger.debug(s"Failed to touch session $sessionId in database: ${ex.getMessage}")
    }
  }

  override def getUserSessions(username: String): List[String] = {
    // Get from database as it's the authoritative source
    val dbSessions = dbCache.getUserSessions(username)

    // Warm up memory cache with user's sessions
    Future {
      dbSessions.foreach { sessionId =>
        // Check if session exists in memory cache before loading from DB
        if (memoryCache.getSession(sessionId).isEmpty) {
          dbCache.getSession(sessionId).foreach { data =>
            memoryCache.setSession(sessionId, data)
          }
        }
      }
    }

    dbSessions
  }

  override def removeUserSessions(username: String): Unit = {
    // Get all user sessions from database (using email as username in EscalatorStarter)
    val sessionIds = dbCache.getUserSessions(username)

    // Remove from memory cache
    sessionIds.foreach(memoryCache.removeSession)

    // Remove from database
    dbCache.removeUserSessions(username)
  }

  /**
   * Warm up the memory cache by loading active sessions from database.
   * Useful after application restart to pre-populate frequently used sessions.
   *
   * @param limit Maximum number of sessions to load (default: 100)
   */
  def warmUp(limit: Int = 100): Future[Unit] = {
    Future {
      try {
        logger.info(s"Warming up hybrid cache with up to $limit recent sessions")

        // Load recent active sessions from database
        val recentSessions = repository.userSessions.getRecentActive(limit).map { sessions =>
          sessions.foreach { session =>
            val sessionData = SessionData(
              id = session.sessionId.sessionId,
              email = session.email,
              role = session.role
            )
            memoryCache.setSession(session.sessionId.sessionId, sessionData)
          }
          logger.info(s"Warmed up cache with ${sessions.size} sessions")
        }

        scala.concurrent.Await.result(recentSessions, scala.concurrent.duration.Duration(30, "seconds"))
      } catch {
        case ex: Exception =>
          logger.error(ex, "Failed to warm up hybrid cache")
      }
    }
  }
}
