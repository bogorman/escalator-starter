package com.escalatorstarter.http.server.auth

import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._
import escalator.util.logging.{Logger, Slf4jLogger}
import scala.util.{Try, Success, Failure}

import com.github.benmanes.caffeine.cache._
// import com.github.benmanes.caffeine.cache.{Caffeine, CacheWriter, RemovalListener, RemovalCause, Cache}


/**
 * In-memory implementation of SessionCache using Caffeine.
 * Sessions are stored in memory and will be lost on application restart.
 * Suitable for development and single-instance deployments.
 * 
 * @param maxSize Maximum number of sessions to cache
 * @param ttlHours Time-to-live for sessions in hours
 */
class InMemorySessionCache(
  maxSize: Long = 1000L,
  ttlHours: Int = 24
)(implicit logger: Logger) extends SessionCache {

  // Removal listener logs key removals, including expiration and eviction
  val removalListener = new RemovalListener[String, Entry[SessionData]] {
    override def onRemoval(key: String, value: Entry[SessionData], cause: RemovalCause): Unit = {
      cause match {
        case RemovalCause.EXPIRED =>
          println(s"[EXPIRE] Key expired: $key")
        case RemovalCause.EXPLICIT =>
          println(s"[REMOVE] Key manually removed: $key")
        case cause if cause.wasEvicted() =>
          println(s"[EVICT] Key evicted: $key (cause: $cause)")
        case _ =>
          println(s"[REMOVE] Key removed: $key (cause: $cause)")
      }
    }
  }

  // Optional: CacheWriter logs when keys are written or deleted manually
  // val cacheWriter = new CacheWriter[String, SessionData] {
  //   override def write(key: String, value: SessionData): Unit = {
  //     println(s"[PUT] Key added: $key")
  //   }

  //   override def delete(key: String, value: SessionData, cause: RemovalCause): Unit = {
  //     println(s"[DELETE] Key deleted: $key, cause: $cause")
  //   }
  // }

  println("ttlHours:" + ttlHours)

  private val cacheBuilder = Caffeine
    .newBuilder
    .maximumSize(maxSize)
    .expireAfterWrite(ttlHours, TimeUnit.HOURS)
    .removalListener(removalListener)
    // .writer(cacheWriter)    
    .build[String, Entry[SessionData]]

  private val sessionCache: Cache[SessionData] = CaffeineCache[SessionData](cacheBuilder)
  
  // Track sessions by username for user session management
  private val userSessionsMap = scala.collection.concurrent.TrieMap[String, Set[String]]()

  override def getSession(sessionId: String): Option[SessionData] = {
    logger.debug(s"InMemorySessionCache.getSession: Looking up session $sessionId")

    Try(sessionCache.get(sessionId)) match {
      case Success(result) =>
        logger.debug(s"InMemorySessionCache.getSession: Cache lookup successful for $sessionId, found: ${result.isDefined}")
        result
      case Failure(ex) =>
        logger.error(ex, s"InMemorySessionCache.getSession: Failed to get session $sessionId from cache")
        None
    }
  }

  override def setSession(sessionId: String, data: SessionData): Unit = {
    logger.debug(s"InMemorySessionCache.setSession: Storing session $sessionId for user ${data.email}")

    Try(sessionCache.put(sessionId)(data)) match {
      case Success(_) =>
        logger.debug(s"InMemorySessionCache.setSession: Successfully stored session $sessionId in cache")

        // Track user sessions
        try {
          userSessionsMap.updateWith(data.email) {
            case Some(sessions) => Some(sessions + sessionId)
            case None => Some(Set(sessionId))
          }
          logger.debug(s"InMemorySessionCache.setSession: Updated user sessions map for ${data.email}")
        } catch {
          case ex: Exception =>
            logger.error(ex, s"InMemorySessionCache.setSession: Failed to update user sessions map for ${data.email}")
        }

      case Failure(ex) =>
        logger.error(ex, s"InMemorySessionCache.setSession: Failed to store session $sessionId in cache")
    }
  }

  override def removeSession(sessionId: String): Unit = {
    logger.debug(s"InMemorySessionCache.removeSession: Removing session $sessionId")

    // Get session data before removing to update user sessions map
    getSession(sessionId).foreach { data =>
      try {
        userSessionsMap.updateWith(data.email) {
          case Some(sessions) =>
            val updated = sessions - sessionId
            if (updated.isEmpty) None else Some(updated)
          case None => None
        }
        logger.debug(s"InMemorySessionCache.removeSession: Updated user sessions map for ${data.email}")
      } catch {
        case ex: Exception =>
          logger.error(ex, s"InMemorySessionCache.removeSession: Failed to update user sessions map for ${data.email}")
      }
    }

    Try(sessionCache.remove(sessionId)) match {
      case Success(_) =>
        logger.debug(s"InMemorySessionCache.removeSession: Successfully removed session $sessionId from cache")
      case Failure(ex) =>
        logger.error(ex, s"InMemorySessionCache.removeSession: Failed to remove session $sessionId from cache")
    }
  }
  
  override def cleanup(): Unit = {
    logger.debug("InMemorySessionCache.cleanup: Starting cleanup process")

    try {
      // Caffeine handles expiration automatically

      // Clean up orphaned entries in userSessionsMap
      var cleanedUsers = 0
      var cleanedSessions = 0

      userSessionsMap.foreach { case (email, sessionIds) =>
        val validSessions = sessionIds.filter(id => getSession(id).isDefined)
        if (validSessions != sessionIds) {
          val removedCount = sessionIds.size - validSessions.size
          cleanedSessions += removedCount

          if (validSessions.isEmpty) {
            userSessionsMap.remove(email)
            cleanedUsers += 1
            logger.debug(s"InMemorySessionCache.cleanup: Removed user $email with no valid sessions")
          } else {
            userSessionsMap.update(email, validSessions)
            logger.debug(s"InMemorySessionCache.cleanup: Cleaned $removedCount sessions for user $email")
          }
        }
      }

      logger.info(s"InMemorySessionCache.cleanup: Cleaned up $cleanedSessions sessions and $cleanedUsers users")
    } catch {
      case ex: Exception =>
        logger.error(ex, "InMemorySessionCache.cleanup: Failed during cleanup process")
    }
  }
  
  override def getUserSessions(username: String): List[String] = {
    logger.debug(s"InMemorySessionCache.getUserSessions: Getting sessions for user $username")

    try {
      val sessions = userSessionsMap.get(username).map(_.toList).getOrElse(List.empty)
      logger.debug(s"InMemorySessionCache.getUserSessions: Found ${sessions.length} sessions for user $username")
      sessions
    } catch {
      case ex: Exception =>
        logger.error(ex, s"InMemorySessionCache.getUserSessions: Failed to get sessions for user $username")
        List.empty
    }
  }
  
  override def removeUserSessions(username: String): Unit = {
    logger.debug(s"InMemorySessionCache.removeUserSessions: Removing all sessions for user $username")

    try {
      userSessionsMap.get(username).foreach { sessionIds =>
        logger.debug(s"InMemorySessionCache.removeUserSessions: Removing ${sessionIds.size} sessions for user $username")

        var successCount = 0
        var failureCount = 0

        sessionIds.foreach { id =>
          Try(sessionCache.remove(id)) match {
            case Success(_) => successCount += 1
            case Failure(ex) =>
              failureCount += 1
              logger.error(ex, s"InMemorySessionCache.removeUserSessions: Failed to remove session $id")
          }
        }

        logger.debug(s"InMemorySessionCache.removeUserSessions: Removed $successCount sessions, failed $failureCount for user $username")
      }

      userSessionsMap.remove(username)
      logger.debug(s"InMemorySessionCache.removeUserSessions: Removed user $username from sessions map")
    } catch {
      case ex: Exception =>
        logger.error(ex, s"InMemorySessionCache.removeUserSessions: Failed to remove sessions for user $username")
    }
  }
  
  override def touchSession(sessionId: String): Unit = {
    logger.debug(s"InMemorySessionCache.touchSession: Touching session $sessionId")

    try {
      // For in-memory cache, accessing the session refreshes its TTL
      getSession(sessionId) match {
        case Some(data) =>
          setSession(sessionId, data)
          logger.debug(s"InMemorySessionCache.touchSession: Successfully touched session $sessionId")
        case None =>
          logger.debug(s"InMemorySessionCache.touchSession: Session $sessionId not found for touching")
      }
    } catch {
      case ex: Exception =>
        logger.error(ex, s"InMemorySessionCache.touchSession: Failed to touch session $sessionId")
    }
  }
}
