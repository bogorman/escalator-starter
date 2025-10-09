package com.escalatorstarter.http.server.auth

import java.util.UUID._

import org.apache.commons.codec.binary.Base64._
import org.apache.commons.codec.binary.StringUtils._

import com.kolich.common.util.crypt.Base64Utils.encodeBase64URLSafe

import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}
import java.util.concurrent.TimeUnit

object WebAppSessionCache extends SessionCache
// [SessionData] // Singleton

trait SessionCache {

  // private lazy val sessionCache = CacheBuilder.newBuilder()
  //   .expireAfterAccess(24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS)
  //   .asInstanceOf[CacheBuilder[String, T]]
  //   .build[String, T]()
  //   .asMap() // Concurrent map, fwiw

  private val cacheBuilder = Caffeine
    .newBuilder
    .maximumSize(1000L)
    .expireAfterWrite(24, TimeUnit.HOURS)
    .build[String, Entry[SessionData]]

  private val sessionCache: Cache[SessionData] = CaffeineCache[SessionData](cacheBuilder)    

   // Option[SessionData]
  def getSession(sessionId: String) = {
    // Wrapped in Option() to handle null's
    // Option()
    sessionCache.get(sessionId) 
    // None
  }

  def setSession(sessionId: String, data: SessionData): Unit = {
    // Wrapped in Option() to handle null's
    // Option(sessionCache.put(sessionId, data)) 
    sessionCache.put(sessionId)(data)
    // None
  }

  def removeSession(sessionId: String): Unit = {
    // Option(sessionCache.remove(sessionId)) // Wrapped in Option() to handle null's
    sessionCache.remove(sessionId)
  }

  def getRandomSessionId: String = {
    // Base-64 URL safe encoding (for the cookie value) and no chunking of
    // the encoded output (important).
    encodeBase64URLSafe(randomUUID.toString)
  }

}

