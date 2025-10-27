package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:33:296

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for UserSession aggregate
  */
sealed trait UserSessionEvent extends Event with PersistentEvent {
  def usersession: UserSession
  def id: UserSessionId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class UserSessionCreated(
    usersession: UserSession,
    id: UserSessionId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends UserSessionEvent

case class UserSessionUpdated(
    usersession: UserSession,
    previousUserSession: Option[UserSession] = None,
    id: UserSessionId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends UserSessionEvent

case class UserSessionDeleted(
    usersession: UserSession,
    id: UserSessionId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends UserSessionEvent
