package com.escalatorstarter.models.events

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:12:355

import escalator.ddd.{ Event, PersistentEvent }
import escalator.models.CorrelationId
import escalator.util.Timestamp

import com.escalatorstarter.models._

/**
  * Events for Entity aggregate
  */
sealed trait EntityEvent extends Event with PersistentEvent {
  def entity: Entity
  def id: EntityId
  def correlationId: CorrelationId
  def timestamp: Timestamp
}

case class EntityCreated(
    entity: Entity,
    id: EntityId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends EntityEvent

case class EntityUpdated(
    entity: Entity,
    previousEntity: Option[Entity] = None,
    id: EntityId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends EntityEvent

case class EntityDeleted(
    entity: Entity,
    id: EntityId,
    correlationId: CorrelationId,
    timestamp: Timestamp
) extends EntityEvent
