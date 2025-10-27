package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:33:289

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for Token aggregate
  */
sealed trait TokenEvent extends Event with PersistentEvent {
  def token: Token
  def id: TokenId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class TokenCreated(
    token: Token,
    id: TokenId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends TokenEvent

case class TokenUpdated(
    token: Token,
    previousToken: Option[Token] = None,
    id: TokenId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends TokenEvent

case class TokenDeleted(
    token: Token,
    id: TokenId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends TokenEvent
