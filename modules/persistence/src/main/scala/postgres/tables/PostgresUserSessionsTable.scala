package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:32:576

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

import com.escalatorstarter.persistence.database.tables.UserSessionsTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresUserSessionsTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends UserSessionsTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(u: UserSession): Future[UserSession] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[UserSession]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => events.UserSessionCreated(m, id = u.id, cid, time))
        }
    }

  private def insert(ul: List[UserSession]): Future[List[UserSession]] =
    monitored("insert") {
      Future.sequence(ul.map { u => insert(u) })
    }

  override def upsertOnSessionId(u: UserSession): Future[UserSession] =
    monitored("upsert-on-session_id") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[UserSession]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.sessionId)(
                _.userId -> _.userId,
                _.email -> _.email,
                _.role -> _.role,
                _.sessionType -> _.sessionType,
                _.sessionData -> _.sessionData,
                _.ipAddress -> _.ipAddress,
                _.userAgent -> _.userAgent,
                _.lastAccessed -> _.lastAccessed,
                _.expiresAt -> _.expiresAt,
                _.isActive -> _.isActive,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[UserSession]
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
                .publishingCreated((cur, cid, time) => events.UserSessionCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) =>
                  events.UserSessionUpdated(cur, prev, id = cur.id, cid, time)
                )
          }
        } yield result

      }.runToFuture
    }

  override def existsOnSessionId(u: UserSession): Future[Boolean] =
    monitored("exists-session_id") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(row => row.sessionId == lift(u.sessionId))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def updateUserIdById(id: UserSessionId, userId: Option[UserId]): Future[UserSession] =
    monitored("update-user_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userId = userId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.userId -> lift(userId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateEmailById(id: UserSessionId, email: String): Future[UserSession] =
    monitored("update-email-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(email = email, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.email -> lift(email),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateRoleById(id: UserSessionId, role: Option[String]): Future[UserSession] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateSessionTypeById(id: UserSessionId, sessionType: UserSessionType): Future[UserSession] =
    monitored("update-session_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(sessionType = sessionType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.sessionType -> lift(sessionType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateSessionDataById(id: UserSessionId, sessionData: Option[io.circe.Json]): Future[UserSession] =
    monitored("update-session_data-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(sessionData = sessionData, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.sessionData -> lift(sessionData),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateIpAddresById(id: UserSessionId, ipAddress: Option[String]): Future[UserSession] =
    monitored("update-ip_address-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(ipAddress = ipAddress, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.ipAddress -> lift(ipAddress),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateUserAgentById(id: UserSessionId, userAgent: Option[String]): Future[UserSession] =
    monitored("update-user_agent-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userAgent = userAgent, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.userAgent -> lift(userAgent),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateLastAccessedById(id: UserSessionId, lastAccessed: escalator.util.Timestamp): Future[UserSession] =
    monitored("update-last_accessed-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastAccessed = lastAccessed, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.lastAccessed -> lift(lastAccessed),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateExpiresAtById(id: UserSessionId, expiresAt: escalator.util.Timestamp): Future[UserSession] =
    monitored("update-expires_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(expiresAt = expiresAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.expiresAt -> lift(expiresAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateIsActiveById(id: UserSessionId, isActive: Option[Boolean]): Future[UserSession] =
    monitored("update-is_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(isActive = isActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(_.id == lift(id))
                  .update(
                    _.isActive -> lift(isActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with id $id"))
      }
    }

  override def updateUserIdBySessionId(sessionId: UserSessionSessionId, userId: Option[UserId]): Future[UserSession] =
    monitored("update-user_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userId = userId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.userId -> lift(userId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateEmailBySessionId(sessionId: UserSessionSessionId, email: String): Future[UserSession] =
    monitored("update-email-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(email = email, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.email -> lift(email),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateRoleBySessionId(sessionId: UserSessionSessionId, role: Option[String]): Future[UserSession] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateSessionTypeBySessionId(
      sessionId: UserSessionSessionId,
      sessionType: UserSessionType
  ): Future[UserSession] =
    monitored("update-session_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(sessionType = sessionType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.sessionType -> lift(sessionType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateSessionDataBySessionId(
      sessionId: UserSessionSessionId,
      sessionData: Option[io.circe.Json]
  ): Future[UserSession] =
    monitored("update-session_data-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(sessionData = sessionData, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.sessionData -> lift(sessionData),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateIpAddresBySessionId(
      sessionId: UserSessionSessionId,
      ipAddress: Option[String]
  ): Future[UserSession] =
    monitored("update-ip_address-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(ipAddress = ipAddress, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.ipAddress -> lift(ipAddress),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateUserAgentBySessionId(
      sessionId: UserSessionSessionId,
      userAgent: Option[String]
  ): Future[UserSession] =
    monitored("update-user_agent-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userAgent = userAgent, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.userAgent -> lift(userAgent),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateLastAccessedBySessionId(
      sessionId: UserSessionSessionId,
      lastAccessed: escalator.util.Timestamp
  ): Future[UserSession] =
    monitored("update-last_accessed-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastAccessed = lastAccessed, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.lastAccessed -> lift(lastAccessed),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateExpiresAtBySessionId(
      sessionId: UserSessionSessionId,
      expiresAt: escalator.util.Timestamp
  ): Future[UserSession] =
    monitored("update-expires_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(expiresAt = expiresAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.expiresAt -> lift(expiresAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def updateIsActiveBySessionId(
      sessionId: UserSessionSessionId,
      isActive: Option[Boolean]
  ): Future[UserSession] =
    monitored("update-is_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getBySessionId(sessionId: UserSessionSessionId).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(isActive = isActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[UserSession]
                  .filter(u => u.sessionId == lift(sessionId))
                  .update(
                    _.isActive -> lift(isActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserSessionUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No UserSession found with sessionId: UserSessionSessionId"))
      }
    }

  override def getById(u: UserSessionId): Future[Option[UserSession]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(_.id == lift(u))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByIds(u: List[UserSessionId]): Future[List[UserSession]] =
    monitored("getByIds") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(obj => liftQuery(u).contains(obj.id))
          )
          .runToFuture
      }
    }

  override def update(u: UserSession): Future[UserSession] =
    monitored("update") {
      if (u.id == UserSessionId(0L)) {
        insert(u)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = u.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[UserSession]
                .filter(_.id == lift(u.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => events.UserSessionUpdated(cur, prev, id = u.id, cid, time))
      }
    }

  override def upsert(u: UserSession): Future[UserSession] =
    monitored("upsert") {
      if (u.id == UserSessionId(0L)) {
        insert(u)
      } else {
        update(u)
      }
    }

  override def upsert(ul: List[UserSession]): Future[List[UserSession]] = {
    Future.sequence(ul.map { u => upsert(u) })
  }

  override def delete(u: UserSession): Future[UserSession] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(u) {
        ctx
          .run(
            query[UserSession]
              .filter(_.id == lift(u.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => events.UserSessionDeleted(m, id = u.id, cid, time))
    }

  override def getBySessionId(sessionId: UserSessionSessionId): Future[Option[UserSession]] =
    monitored("get-by-session_id") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(u => u.sessionId == lift(sessionId))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getListByUserId(userId: UserId): Future[List[UserSession]] =
    monitored("get-by-user-id") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(r => r.userId.contains(lift(userId)))
          )
          .runToFuture
      }
    }

  override def getListByUserIds(userIds: List[UserId]): Future[List[UserSession]] =
    monitored("get-by-user-ids") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(r => r.userId.exists(v => liftQuery(userIds).contains(v)))
          )
          .runToFuture
      }
    }

  override def getListBySessionType(sessionType: UserSessionType): Future[List[UserSession]] =
    monitored("get-by-session-type") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(r => r.sessionType == lift(sessionType))
          )
          .runToFuture
      }
    }

  override def getListBySessionTypes(sessionTypes: List[UserSessionType]): Future[List[UserSession]] =
    monitored("get-by-session-types") {
      read {
        ctx
          .run(
            query[UserSession]
              .filter(r => liftQuery(sessionTypes).contains(r.sessionType))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[UserSession].size).runToFuture
      }
    }

  override def getAll(): Future[List[UserSession]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[UserSession]
          )
          .runToFuture
      }
    }

}
