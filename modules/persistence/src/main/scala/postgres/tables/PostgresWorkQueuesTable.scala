package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:12:207

import scala.concurrent.Future

import escalator.util.TimeUtil
import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

import escalator.util.postgres.MonitoredPostgresOperation
import escalator.util.postgres.PostgresDatabase
import escalator.util.postgres.PostgresDatabase.PostgresDatabaseConfiguration
import escalator.util.postgres.CustomNamingStrategy
import escalator.util.postgres.{ EventRequiredResult, RepositoryHelpers }

import escalator.util.events.EventBus
import escalator.models.CorrelationId
import java.util.UUID
import escalator.util.Timestamp

import com.escalatorstarter.persistence.postgres.PostgresCustomEncoder
import com.escalatorstarter.common.persistence.postgres.PostgresMappedEncoder

import com.escalatorstarter.models._
import com.escalatorstarter.models.events._

import com.escalatorstarter.persistence.database.tables.WorkQueuesTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresWorkQueuesTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends WorkQueuesTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(w: WorkQueue): Future[WorkQueue] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = w.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[WorkQueue]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => WorkQueueCreated(m, id = w.id, cid, time))
        }
    }

  private def insert(wl: List[WorkQueue]): Future[List[WorkQueue]] =
    monitored("insert") {
      Future.sequence(wl.map { w => insert(w) })
    }

  override def updateWorkTypeById(id: WorkQueueId, workType: WorkType): Future[WorkQueue] =
    monitored("update-work_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(workType = workType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[WorkQueue]
                  .filter(_.id == lift(id))
                  .update(
                    _.workType -> lift(workType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            WorkQueueUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No WorkQueue found with id $id"))
      }
    }

  override def updateWorkById(id: WorkQueueId, work: Option[String]): Future[WorkQueue] =
    monitored("update-work-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(work = work, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[WorkQueue]
                  .filter(_.id == lift(id))
                  .update(
                    _.work -> lift(work),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            WorkQueueUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No WorkQueue found with id $id"))
      }
    }

  override def updateWorkStatuById(id: WorkQueueId, workStatus: WorkStatusType): Future[WorkQueue] =
    monitored("update-work_status-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(workStatus = workStatus, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[WorkQueue]
                  .filter(_.id == lift(id))
                  .update(
                    _.workStatus -> lift(workStatus),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            WorkQueueUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No WorkQueue found with id $id"))
      }
    }

  override def updateWorkResultById(id: WorkQueueId, workResult: Option[String]): Future[WorkQueue] =
    monitored("update-work_result-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(workResult = workResult, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[WorkQueue]
                  .filter(_.id == lift(id))
                  .update(
                    _.workResult -> lift(workResult),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            WorkQueueUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No WorkQueue found with id $id"))
      }
    }

  override def getById(w: WorkQueueId): Future[Option[WorkQueue]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[WorkQueue]
              .filter(_.id == lift(w))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def update(w: WorkQueue): Future[WorkQueue] =
    monitored("update") {
      if (w.id == WorkQueueId(0L)) {
        insert(w)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = w.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[WorkQueue]
                .filter(_.id == lift(w.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => WorkQueueUpdated(cur, prev, id = w.id, cid, time))
      }
    }

  override def upsert(w: WorkQueue): Future[WorkQueue] =
    monitored("upsert") {
      if (w.id == WorkQueueId(0L)) {
        insert(w)
      } else {
        update(w)
      }
    }

  override def upsert(wl: List[WorkQueue]): Future[List[WorkQueue]] = {
    Future.sequence(wl.map { w => upsert(w) })
  }

  override def delete(w: WorkQueue): Future[WorkQueue] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(w) {
        ctx
          .run(
            query[WorkQueue]
              .filter(_.id == lift(w.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => WorkQueueDeleted(m, id = w.id, cid, time))
    }

  override def getByWorkType(workType: WorkType): Future[List[WorkQueue]] =
    monitored("get-by-work-type") {
      read {
        ctx
          .run(
            query[WorkQueue]
              .filter(r => r.workType == lift(workType))
          )
          .runToFuture
      }
    }

  override def getByWorkTypes(workTypes: List[WorkType]): Future[List[WorkQueue]] =
    monitored("get-by-work-types") {
      read {
        ctx
          .run(
            query[WorkQueue]
              .filter(r => liftQuery(workTypes).contains(r.workType))
          )
          .runToFuture
      }
    }

  override def getByWorkStatu(workStatus: WorkStatusType): Future[List[WorkQueue]] =
    monitored("get-by-work-status") {
      read {
        ctx
          .run(
            query[WorkQueue]
              .filter(r => r.workStatus == lift(workStatus))
          )
          .runToFuture
      }
    }

  override def getByWorkStatus(workStatuss: List[WorkStatusType]): Future[List[WorkQueue]] =
    monitored("get-by-work-statuss") {
      read {
        ctx
          .run(
            query[WorkQueue]
              .filter(r => liftQuery(workStatuss).contains(r.workStatus))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[WorkQueue].size).runToFuture
      }
    }

  override def getAll(): Future[List[WorkQueue]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[WorkQueue]
          )
          .runToFuture
      }
    }

}
