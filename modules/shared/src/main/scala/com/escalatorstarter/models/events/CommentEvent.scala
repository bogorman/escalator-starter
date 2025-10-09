package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:12:338

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for Comment aggregate
  */
sealed trait CommentEvent extends Event with PersistentEvent {
  def comment: Comment
  def id: CommentId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class CommentCreated(
    comment: Comment,
    id: CommentId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends CommentEvent

case class CommentUpdated(
    comment: Comment,
    previousComment: Option[Comment] = None,
    id: CommentId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends CommentEvent

case class CommentDeleted(
    comment: Comment,
    id: CommentId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends CommentEvent
