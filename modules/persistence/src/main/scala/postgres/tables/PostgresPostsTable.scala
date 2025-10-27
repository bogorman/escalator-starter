package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:32:363

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
// import com.escalatorstarter.models.events._

import com.escalatorstarter.persistence.database.tables.PostsTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresPostsTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends PostsTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(p: Post): Future[Post] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = p.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[Post]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => events.PostCreated(m, id = p.id, cid, time))
        }
    }

  private def insert(pl: List[Post]): Future[List[Post]] =
    monitored("insert") {
      Future.sequence(pl.map { p => insert(p) })
    }

  override def updateUserIdById(id: PostId, userId: UserId): Future[Post] =
    monitored("update-user_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userId = userId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Post]
                  .filter(_.id == lift(id))
                  .update(
                    _.userId -> lift(userId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.PostUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No Post found with id $id"))
      }
    }

  override def updateActiveById(id: PostId, active: Option[Boolean]): Future[Post] =
    monitored("update-active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(active = active, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Post]
                  .filter(_.id == lift(id))
                  .update(
                    _.active -> lift(active),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.PostUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No Post found with id $id"))
      }
    }

  override def updateContentById(id: PostId, content: Option[String]): Future[Post] =
    monitored("update-content-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(content = content, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Post]
                  .filter(_.id == lift(id))
                  .update(
                    _.content -> lift(content),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.PostUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No Post found with id $id"))
      }
    }

  override def getById(p: PostId): Future[Option[Post]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[Post]
              .filter(_.id == lift(p))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByIds(p: List[PostId]): Future[List[Post]] =
    monitored("getByIds") {
      read {
        ctx
          .run(
            query[Post]
              .filter(obj => liftQuery(p).contains(obj.id))
          )
          .runToFuture
      }
    }

  override def update(p: Post): Future[Post] =
    monitored("update") {
      if (p.id == PostId(0L)) {
        insert(p)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = p.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[Post]
                .filter(_.id == lift(p.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => events.PostUpdated(cur, prev, id = p.id, cid, time))
      }
    }

  override def upsert(p: Post): Future[Post] =
    monitored("upsert") {
      if (p.id == PostId(0L)) {
        insert(p)
      } else {
        update(p)
      }
    }

  override def upsert(pl: List[Post]): Future[List[Post]] = {
    Future.sequence(pl.map { p => upsert(p) })
  }

  override def delete(p: Post): Future[Post] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(p) {
        ctx
          .run(
            query[Post]
              .filter(_.id == lift(p.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => events.PostDeleted(m, id = p.id, cid, time))
    }

  override def getListByUserId(userId: UserId): Future[List[Post]] =
    monitored("get-by-user-id") {
      read {
        ctx
          .run(
            query[Post]
              .filter(r => r.userId == lift(userId))
          )
          .runToFuture
      }
    }

  override def getListByUserIds(userIds: List[UserId]): Future[List[Post]] =
    monitored("get-by-user-ids") {
      read {
        ctx
          .run(
            query[Post]
              .filter(r => liftQuery(userIds).contains(r.userId))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[Post].size).runToFuture
      }
    }

  override def getAll(): Future[List[Post]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[Post]
          )
          .runToFuture
      }
    }

}
