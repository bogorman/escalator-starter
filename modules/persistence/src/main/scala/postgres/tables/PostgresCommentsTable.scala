package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:752

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

import com.escalatorstarter.persistence.database.tables.CommentsTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresCommentsTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends CommentsTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(c: Comment): Future[Comment] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = c.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[Comment]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => events.CommentCreated(m, id = c.id, cid, time))
        }
    }

  private def insert(cl: List[Comment]): Future[List[Comment]] =
    monitored("insert") {
      Future.sequence(cl.map { c => insert(c) })
    }

  override def updatePostIdById(id: CommentId, postId: PostId): Future[Comment] =
    monitored("update-post_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(postId = postId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Comment]
                  .filter(_.id == lift(id))
                  .update(
                    _.postId -> lift(postId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.CommentUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No Comment found with id $id"))
      }
    }

  override def updateActiveById(id: CommentId, active: Option[Boolean]): Future[Comment] =
    monitored("update-active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(active = active, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Comment]
                  .filter(_.id == lift(id))
                  .update(
                    _.active -> lift(active),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.CommentUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No Comment found with id $id"))
      }
    }

  override def updateContentById(id: CommentId, content: Option[String]): Future[Comment] =
    monitored("update-content-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(content = content, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Comment]
                  .filter(_.id == lift(id))
                  .update(
                    _.content -> lift(content),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.CommentUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No Comment found with id $id"))
      }
    }

  override def getById(c: CommentId): Future[Option[Comment]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[Comment]
              .filter(_.id == lift(c))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByIds(c: List[CommentId]): Future[List[Comment]] =
    monitored("getByIds") {
      read {
        ctx
          .run(
            query[Comment]
              .filter(obj => liftQuery(c).contains(obj.id))
          )
          .runToFuture
      }
    }

  override def update(c: Comment): Future[Comment] =
    monitored("update") {
      if (c.id == CommentId(0L)) {
        insert(c)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = c.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[Comment]
                .filter(_.id == lift(c.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => events.CommentUpdated(cur, prev, id = c.id, cid, time))
      }
    }

  override def upsert(c: Comment): Future[Comment] =
    monitored("upsert") {
      if (c.id == CommentId(0L)) {
        insert(c)
      } else {
        update(c)
      }
    }

  override def upsert(cl: List[Comment]): Future[List[Comment]] = {
    Future.sequence(cl.map { c => upsert(c) })
  }

  override def delete(c: Comment): Future[Comment] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(c) {
        ctx
          .run(
            query[Comment]
              .filter(_.id == lift(c.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => events.CommentDeleted(m, id = c.id, cid, time))
    }

  override def getListByPostId(postId: PostId): Future[List[Comment]] =
    monitored("get-by-post-id") {
      read {
        ctx
          .run(
            query[Comment]
              .filter(r => r.postId == lift(postId))
          )
          .runToFuture
      }
    }

  override def getListByPostIds(postIds: List[PostId]): Future[List[Comment]] =
    monitored("get-by-post-ids") {
      read {
        ctx
          .run(
            query[Comment]
              .filter(r => liftQuery(postIds).contains(r.postId))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[Comment].size).runToFuture
      }
    }

  override def getAll(): Future[List[Comment]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[Comment]
          )
          .runToFuture
      }
    }

}
