package com.escalatorstarter.http.server.auth

import com.typesafe.config.Config
import scala.concurrent.ExecutionContext
import com.escalatorstarter.core.repositories.EscalatorStarterRepository
import escalator.util.logging.{Logger, Slf4jLogger}

/**
 * Factory for creating SessionCache implementations based on configuration.
 * Allows switching between different cache types without code changes.
 */
object SessionCacheFactory {
  
  /**
   * Create a SessionCache instance based on configuration
   * 
   * @param config Application configuration
   * @param repository Database repository (required for database and hybrid caches)
   * @param ec Execution context for async operations
   * @param logger Logger for debugging
   * @return Configured SessionCache implementation
   */
  def create(
    config: Config, 
    repository: EscalatorStarterRepository
  )(implicit ec: ExecutionContext, logger: Logger): SessionCache = {
    
    val sessionConfig = config.getConfig("escalatorstarter.sessions")
    val cacheType = sessionConfig.getString("cache-type")

    // Read UI and CLI TTL values
    val ttlHours = if (sessionConfig.hasPath("ttl-hours")) {
      sessionConfig.getInt("ttl-hours")
    } else {
      24 // Default UI TTL: 1 day
    }

    logger.info(s"Creating session cache of type: $cacheType with TTL: $ttlHours hours")

    cacheType.toLowerCase match {
      case "memory" | "inmemory" =>
        createInMemoryCache(sessionConfig, ttlHours) // Use CLI TTL as it's longer

      case "database" | "db" =>
        createDatabaseCache(sessionConfig, repository, ttlHours)

      case "hybrid" =>
        createHybridCache(sessionConfig, repository, ttlHours)

      case other =>
        logger.error(s"Unknown session cache type: $other, falling back to in-memory")
        createInMemoryCache(sessionConfig, ttlHours)
    }
  }
  
  private def createInMemoryCache(config: Config, ttlHours: Int)(implicit logger: Logger): InMemorySessionCache = {
    val maxSize = if (config.hasPath("memory.max-size")) {
      config.getLong("memory.max-size")
    } else {
      1000L
    }
    
    new InMemorySessionCache(maxSize, ttlHours)
  }
  
  private def createDatabaseCache(
    config: Config,
    repository: EscalatorStarterRepository,
    ttlHours: Int,
  )(implicit ec: ExecutionContext, logger: Logger): DBSessionCache = {

    val l1CacheSize = if (config.hasPath("l1-cache.max-size")) {
      config.getLong("l1-cache.max-size")
    } else {
      100L
    }

    val l1CacheTtlMinutes = if (config.hasPath("l1-cache.ttl-minutes")) {
      config.getInt("l1-cache.ttl-minutes")
    } else {
      5
    }

    val maxSessionsPerUser = if (config.hasPath("max-sessions-per-user")) {
      config.getInt("max-sessions-per-user")
    } else {
      5
    }

    new DBSessionCache(
      repository,
      ttlHours,
      l1CacheSize,
      l1CacheTtlMinutes,
      maxSessionsPerUser
    )
  }
  
  private def createHybridCache(
    config: Config,
    repository: EscalatorStarterRepository,
    ttlHours: Int
  )(implicit ec: ExecutionContext, logger: Logger): HybridSessionCache = {

    val memoryCacheSize = if (config.hasPath("memory.max-size")) {
      config.getLong("memory.max-size")
    } else {
      1000L
    }

    val maxSessionsPerUser = if (config.hasPath("max-sessions-per-user")) {
      config.getInt("max-sessions-per-user")
    } else {
      5
    }

    val cache = new HybridSessionCache(
      repository,
      memoryCacheSize,
      ttlHours,
      maxSessionsPerUser
    )

    // Note: HybridSessionCache is not implemented, warmUp is not available
    // If you need hybrid caching, implement the required database repository methods first

    cache
  }
  
  /**
   * Create a default in-memory cache.
   * Used as fallback when configuration is missing or invalid.
   */
  def createDefault(): SessionCache = {
    implicit val logger: Logger = new Slf4jLogger("SessionCacheFactory")
    new InMemorySessionCache()
  }
}