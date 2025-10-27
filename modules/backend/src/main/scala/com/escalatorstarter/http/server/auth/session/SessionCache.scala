package com.escalatorstarter.http.server.auth

import java.util.UUID._
import org.apache.commons.codec.binary.Base64._
import org.apache.commons.codec.binary.StringUtils._
import com.kolich.common.util.crypt.Base64Utils.encodeBase64URLSafe

/**
 * Base trait for session cache implementations.
 * Provides abstract methods for session management that can be
 * implemented using different storage backends (in-memory, database, hybrid).
 */
trait SessionCache {
  
  /**
   * Retrieve a session by its ID
   * @param sessionId The unique session identifier
   * @return Optional SessionData if found
   */
  def getSession(sessionId: String): Option[SessionData]
  
  /**
   * Store or update a session
   * @param sessionId The unique session identifier
   * @param data The session data to store
   */
  def setSession(sessionId: String, data: SessionData): Unit
  
  /**
   * Remove a session by its ID
   * @param sessionId The unique session identifier
   */
  def removeSession(sessionId: String): Unit
  
  /**
   * Generate a new random session ID
   * @return A URL-safe base64 encoded UUID string
   */
  def getRandomSessionId: String = {
    // Base-64 URL safe encoding (for the cookie value) and no chunking of
    // the encoded output (important).
    encodeBase64URLSafe(randomUUID.toString)
  }
  
  /**
   * Optional cleanup method for removing expired sessions
   * Implementations can override this to provide cleanup functionality
   */
  def cleanup(): Unit = {}
  
  /**
   * Optional method to check if a session exists without retrieving it
   * @param sessionId The unique session identifier
   * @return true if the session exists
   */
  def exists(sessionId: String): Boolean = getSession(sessionId).isDefined
  
  /**
   * Optional method to update the last accessed time of a session
   * Implementations can override this for more efficient updates
   * @param sessionId The unique session identifier
   */
  def touchSession(sessionId: String): Unit = {
    // Default implementation: retrieve and re-save
    getSession(sessionId).foreach(data => setSession(sessionId, data))
  }
  
  /**
   * Optional method to get all sessions for a user
   * @param username The username to search for
   * @return List of session IDs for the user
   */
  def getUserSessions(username: String): List[String] = List.empty
  
  /**
   * Optional method to remove all sessions for a user
   * @param username The username whose sessions should be removed
   */
  def removeUserSessions(username: String): Unit = {
    getUserSessions(username).foreach(removeSession)
  }
}