package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:662

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

import com.escalatorstarter.persistence.database.tables.CandlesTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresCandlesTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends CandlesTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(c: Candle): Future[Candle] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = c.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[Candle]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => CandleCreated(m, id = c.id, cid, time))
        }
    }

  private def insert(cl: List[Candle]): Future[List[Candle]] =
    monitored("insert") {
      Future.sequence(cl.map { c => insert(c) })
    }

  override def updateTokenAddresById(id: CandleId, tokenAddress: TokenAddress): Future[Candle] =
    monitored("update-token_address-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(tokenAddress = tokenAddress, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.tokenAddress -> lift(tokenAddress),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateOpenTimeById(id: CandleId, openTime: Long): Future[Candle] =
    monitored("update-open_time-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(openTime = openTime, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.openTime -> lift(openTime),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateCloseTimeById(id: CandleId, closeTime: Long): Future[Candle] =
    monitored("update-close_time-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(closeTime = closeTime, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.closeTime -> lift(closeTime),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateOpenTimestampById(id: CandleId, openTimestamp: escalator.util.Timestamp): Future[Candle] =
    monitored("update-open_timestamp-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(openTimestamp = openTimestamp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.openTimestamp -> lift(openTimestamp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateCloseTimestampById(id: CandleId, closeTimestamp: escalator.util.Timestamp): Future[Candle] =
    monitored("update-close_timestamp-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(closeTimestamp = closeTimestamp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.closeTimestamp -> lift(closeTimestamp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateDurationById(id: CandleId, duration: Long): Future[Candle] =
    monitored("update-duration-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(duration = duration, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.duration -> lift(duration),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateOpenById(id: CandleId, open: Double): Future[Candle] =
    monitored("update-open-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(open = open, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.open -> lift(open),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateHighById(id: CandleId, high: Double): Future[Candle] =
    monitored("update-high-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(high = high, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.high -> lift(high),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateLowById(id: CandleId, low: Double): Future[Candle] =
    monitored("update-low-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(low = low, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.low -> lift(low),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateCloseById(id: CandleId, close: Double): Future[Candle] =
    monitored("update-close-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(close = close, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.close -> lift(close),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def updateVolumeById(id: CandleId, volume: Double): Future[Candle] =
    monitored("update-volume-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(volume = volume, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Candle]
                  .filter(_.id == lift(id))
                  .update(
                    _.volume -> lift(volume),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Candle found with id $id"))
      }
    }

  override def getById(c: CandleId): Future[Option[Candle]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[Candle]
              .filter(_.id == lift(c))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def update(c: Candle): Future[Candle] =
    monitored("update") {
      if (c.id == CandleId(0L)) {
        insert(c)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = c.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[Candle]
                .filter(_.id == lift(c.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => CandleUpdated(cur, prev, id = c.id, cid, time))
      }
    }

  override def upsert(c: Candle): Future[Candle] =
    monitored("upsert") {
      if (c.id == CandleId(0L)) {
        insert(c)
      } else {
        update(c)
      }
    }

  override def upsert(cl: List[Candle]): Future[List[Candle]] = {
    Future.sequence(cl.map { c => upsert(c) })
  }

  override def delete(c: Candle): Future[Candle] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(c) {
        ctx
          .run(
            query[Candle]
              .filter(_.id == lift(c.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => CandleDeleted(m, id = c.id, cid, time))
    }

  override def getByTokenAddres(tokenAddress: TokenAddress): Future[List[Candle]] =
    monitored("get-by-token-address") {
      read {
        ctx
          .run(
            query[Candle]
              .filter(r => r.tokenAddress == lift(tokenAddress))
          )
          .runToFuture
      }
    }

  override def getByTokenAddress(tokenAddresss: List[TokenAddress]): Future[List[Candle]] =
    monitored("get-by-token-addresss") {
      read {
        ctx
          .run(
            query[Candle]
              .filter(r => liftQuery(tokenAddresss).contains(r.tokenAddress))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[Candle].size).runToFuture
      }
    }

  override def getAll(): Future[List[Candle]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[Candle]
          )
          .runToFuture
      }
    }

}
