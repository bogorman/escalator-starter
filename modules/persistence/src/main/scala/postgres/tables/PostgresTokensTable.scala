package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:11:595

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

import com.escalatorstarter.persistence.database.tables.TokensTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresTokensTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends TokensTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(t: Token): Future[Token] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = t.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[Token]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => TokenCreated(m, id = t.id, cid, time))
        }
    }

  private def insert(tl: List[Token]): Future[List[Token]] =
    monitored("insert") {
      Future.sequence(tl.map { t => insert(t) })
    }

  override def upsertOnAddres(t: Token): Future[Token] =
    monitored("upsert-on-address") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = t.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[Token]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.address)(
                _.chainIdent -> _.chainIdent,
                _.symbol -> _.symbol,
                _.name -> _.name,
                _.totalSupply -> _.totalSupply,
                _.maxSupply -> _.maxSupply,
                _.icon -> _.icon,
                _.color -> _.color,
                _.active -> _.active,
                _.disabledAt -> _.disabledAt,
                _.tags -> _.tags,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[Token]
              .filter(_.id == lift(id))
              .map(r => (r.createdAt, r.updatedAt))
          )

          _ <-
            if (tuples.isEmpty) { Task.raiseError(new NoSuchElementException("No row returned after upsert")) }
            else Task.unit

          createdAt = tuples.head._1
          updatedAt = tuples.head._2
          wasInserted = createdAt == updatedAt

          result = toUpsert.copy(id = id, createdAt = createdAt, updatedAt = updatedAt)

          _ <- Task.deferFuture {
            if (wasInserted)
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingCreated((cur, cid, time) => TokenCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, prev, id = cur.id, cid, time))
          }
        } yield result

      }.runToFuture
    }

  override def existsOnAddres(t: Token): Future[Boolean] =
    monitored("exists-address") {
      read {
        ctx
          .run(
            query[Token]
              .filter(row => row.address == lift(t.address))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def updateChainIdentById(id: TokenId, chainIdent: Option[String]): Future[Token] =
    monitored("update-chain_ident-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(chainIdent = chainIdent, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.chainIdent -> lift(chainIdent),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateSymbolById(id: TokenId, symbol: String): Future[Token] =
    monitored("update-symbol-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(symbol = symbol, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.symbol -> lift(symbol),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateNameById(id: TokenId, name: String): Future[Token] =
    monitored("update-name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(name = name, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.name -> lift(name),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateTotalSupplyById(id: TokenId, totalSupply: Option[Double]): Future[Token] =
    monitored("update-total_supply-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(totalSupply = totalSupply, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.totalSupply -> lift(totalSupply),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateMaxSupplyById(id: TokenId, maxSupply: Option[Double]): Future[Token] =
    monitored("update-max_supply-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(maxSupply = maxSupply, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.maxSupply -> lift(maxSupply),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateIconById(id: TokenId, icon: Option[String]): Future[Token] =
    monitored("update-icon-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(icon = icon, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.icon -> lift(icon),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateColorById(id: TokenId, color: Option[String]): Future[Token] =
    monitored("update-color-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(color = color, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.color -> lift(color),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateActiveById(id: TokenId, active: Boolean): Future[Token] =
    monitored("update-active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(active = active, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.active -> lift(active),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateDisabledAtById(id: TokenId, disabledAt: Option[escalator.util.Timestamp]): Future[Token] =
    monitored("update-disabled_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(disabledAt = disabledAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.disabledAt -> lift(disabledAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateTagById(id: TokenId, tags: Option[List[String]]): Future[Token] =
    monitored("update-tags-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(tags = tags, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(_.id == lift(id))
                  .update(
                    _.tags -> lift(tags),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with id $id"))
      }
    }

  override def updateChainIdentByAddres(address: TokenAddress, chainIdent: Option[String]): Future[Token] =
    monitored("update-chain_ident-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(chainIdent = chainIdent, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.chainIdent -> lift(chainIdent),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateSymbolByAddres(address: TokenAddress, symbol: String): Future[Token] =
    monitored("update-symbol-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(symbol = symbol, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.symbol -> lift(symbol),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateNameByAddres(address: TokenAddress, name: String): Future[Token] =
    monitored("update-name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(name = name, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.name -> lift(name),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateTotalSupplyByAddres(address: TokenAddress, totalSupply: Option[Double]): Future[Token] =
    monitored("update-total_supply-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(totalSupply = totalSupply, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.totalSupply -> lift(totalSupply),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateMaxSupplyByAddres(address: TokenAddress, maxSupply: Option[Double]): Future[Token] =
    monitored("update-max_supply-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(maxSupply = maxSupply, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.maxSupply -> lift(maxSupply),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateIconByAddres(address: TokenAddress, icon: Option[String]): Future[Token] =
    monitored("update-icon-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(icon = icon, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.icon -> lift(icon),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateColorByAddres(address: TokenAddress, color: Option[String]): Future[Token] =
    monitored("update-color-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(color = color, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.color -> lift(color),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateActiveByAddres(address: TokenAddress, active: Boolean): Future[Token] =
    monitored("update-active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(active = active, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.active -> lift(active),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateDisabledAtByAddres(
      address: TokenAddress,
      disabledAt: Option[escalator.util.Timestamp]
  ): Future[Token] =
    monitored("update-disabled_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(disabledAt = disabledAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.disabledAt -> lift(disabledAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def updateTagByAddres(address: TokenAddress, tags: Option[List[String]]): Future[Token] =
    monitored("update-tags-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAddres(address: TokenAddress).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(tags = tags, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Token]
                  .filter(t => t.address == lift(address))
                  .update(
                    _.tags -> lift(tags),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Token found with address: TokenAddress"))
      }
    }

  override def getById(t: TokenId): Future[Option[Token]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[Token]
              .filter(_.id == lift(t))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def update(t: Token): Future[Token] =
    monitored("update") {
      if (t.id == TokenId(0L)) {
        insert(t)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = t.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[Token]
                .filter(_.id == lift(t.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => TokenUpdated(cur, prev, id = t.id, cid, time))
      }
    }

  override def upsert(t: Token): Future[Token] =
    monitored("upsert") {
      if (t.id == TokenId(0L)) {
        insert(t)
      } else {
        update(t)
      }
    }

  override def upsert(tl: List[Token]): Future[List[Token]] = {
    Future.sequence(tl.map { t => upsert(t) })
  }

  override def delete(t: Token): Future[Token] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(t) {
        ctx
          .run(
            query[Token]
              .filter(_.id == lift(t.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => TokenDeleted(m, id = t.id, cid, time))
    }

  override def getByAddres(address: TokenAddress): Future[Option[Token]] =
    monitored("get-by-address") {
      read {
        ctx
          .run(
            query[Token]
              .filter(t => t.address == lift(address))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[Token].size).runToFuture
      }
    }

  override def getAll(): Future[List[Token]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[Token]
          )
          .runToFuture
      }
    }

}
