package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:12:389

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for User aggregate
  */
sealed trait UserEvent extends Event with PersistentEvent {
  def user: User
  def id: UserId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class UserCreated(
    user: User,
    id: UserId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends UserEvent

case class UserUpdated(
    user: User,
    previousUser: Option[User] = None,
    id: UserId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends UserEvent

case class UserDeleted(
    user: User,
    id: UserId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends UserEvent
