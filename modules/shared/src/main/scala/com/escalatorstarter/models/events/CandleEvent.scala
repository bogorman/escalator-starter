package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:33:242

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for Candle aggregate
  */
sealed trait CandleEvent extends Event with PersistentEvent {
  def candle: Candle
  def id: CandleId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class CandleCreated(
    candle: Candle,
    id: CandleId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends CandleEvent

case class CandleUpdated(
    candle: Candle,
    previousCandle: Option[Candle] = None,
    id: CandleId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends CandleEvent

case class CandleDeleted(
    candle: Candle,
    id: CandleId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends CandleEvent
