package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:33:281

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for Post aggregate
  */
sealed trait PostEvent extends Event with PersistentEvent {
  def post: Post
  def id: PostId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class PostCreated(
    post: Post,
    id: PostId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends PostEvent

case class PostUpdated(
    post: Post,
    previousPost: Option[Post] = None,
    id: PostId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends PostEvent

case class PostDeleted(
    post: Post,
    id: PostId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends PostEvent
