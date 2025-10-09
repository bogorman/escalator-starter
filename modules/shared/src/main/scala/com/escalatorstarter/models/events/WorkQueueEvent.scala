package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:12:399

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for WorkQueue aggregate
  */
sealed trait WorkQueueEvent extends Event with PersistentEvent {
  def workqueue: WorkQueue
  def id: WorkQueueId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class WorkQueueCreated(
    workqueue: WorkQueue,
    id: WorkQueueId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends WorkQueueEvent

case class WorkQueueUpdated(
    workqueue: WorkQueue,
    previousWorkQueue: Option[WorkQueue] = None,
    id: WorkQueueId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends WorkQueueEvent

case class WorkQueueDeleted(
    workqueue: WorkQueue,
    id: WorkQueueId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends WorkQueueEvent
