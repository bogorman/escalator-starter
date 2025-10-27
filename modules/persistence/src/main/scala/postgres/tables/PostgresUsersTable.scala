package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:32:719

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

import com.escalatorstarter.persistence.database.tables.UsersTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresUsersTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends UsersTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(u: User): Future[User] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[User]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => events.UserCreated(m, id = u.id, cid, time))
        }
    }

  private def insert(ul: List[User]): Future[List[User]] =
    monitored("insert") {
      Future.sequence(ul.map { u => insert(u) })
    }

  override def upsertOnEmail(u: User): Future[User] =
    monitored("upsert-on-email") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[User]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.email)(
                _.encryptedPassword -> _.encryptedPassword,
                _.resetPasswordToken -> _.resetPasswordToken,
                _.rememberToken -> _.rememberToken,
                _.rememberCreatedAt -> _.rememberCreatedAt,
                _.confirmationToken -> _.confirmationToken,
                _.confirmedAt -> _.confirmedAt,
                _.confirmationSentAt -> _.confirmationSentAt,
                _.passwordSalt -> _.passwordSalt,
                _.fullName -> _.fullName,
                _.initials -> _.initials,
                _.twoFactorAuthActive -> _.twoFactorAuthActive,
                _.twoFactorAuthSecret -> _.twoFactorAuthSecret,
                _.signInCount -> _.signInCount,
                _.currentSignInAt -> _.currentSignInAt,
                _.lastSignInAt -> _.lastSignInAt,
                _.currentSignInIp -> _.currentSignInIp,
                _.lastSignInIp -> _.lastSignInIp,
                _.role -> _.role,
                _.status -> _.status,
                _.accessToken -> _.accessToken,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[User]
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
                .publishingCreated((cur, cid, time) => events.UserCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) => events.UserUpdated(cur, prev, id = cur.id, cid, time))
          }
        } yield result

      }.runToFuture
    }

  override def upsertOnConfirmationToken(u: User): Future[User] =
    monitored("upsert-on-confirmation_token") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[User]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.confirmationToken)(
                _.email -> _.email,
                _.encryptedPassword -> _.encryptedPassword,
                _.resetPasswordToken -> _.resetPasswordToken,
                _.rememberToken -> _.rememberToken,
                _.rememberCreatedAt -> _.rememberCreatedAt,
                _.confirmedAt -> _.confirmedAt,
                _.confirmationSentAt -> _.confirmationSentAt,
                _.passwordSalt -> _.passwordSalt,
                _.fullName -> _.fullName,
                _.initials -> _.initials,
                _.twoFactorAuthActive -> _.twoFactorAuthActive,
                _.twoFactorAuthSecret -> _.twoFactorAuthSecret,
                _.signInCount -> _.signInCount,
                _.currentSignInAt -> _.currentSignInAt,
                _.lastSignInAt -> _.lastSignInAt,
                _.currentSignInIp -> _.currentSignInIp,
                _.lastSignInIp -> _.lastSignInIp,
                _.role -> _.role,
                _.status -> _.status,
                _.accessToken -> _.accessToken,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[User]
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
                .publishingCreated((cur, cid, time) => events.UserCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) => events.UserUpdated(cur, prev, id = cur.id, cid, time))
          }
        } yield result

      }.runToFuture
    }

  override def upsertOnAccessToken(u: User): Future[User] =
    monitored("upsert-on-access_token") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[User]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.accessToken)(
                _.email -> _.email,
                _.encryptedPassword -> _.encryptedPassword,
                _.resetPasswordToken -> _.resetPasswordToken,
                _.rememberToken -> _.rememberToken,
                _.rememberCreatedAt -> _.rememberCreatedAt,
                _.confirmationToken -> _.confirmationToken,
                _.confirmedAt -> _.confirmedAt,
                _.confirmationSentAt -> _.confirmationSentAt,
                _.passwordSalt -> _.passwordSalt,
                _.fullName -> _.fullName,
                _.initials -> _.initials,
                _.twoFactorAuthActive -> _.twoFactorAuthActive,
                _.twoFactorAuthSecret -> _.twoFactorAuthSecret,
                _.signInCount -> _.signInCount,
                _.currentSignInAt -> _.currentSignInAt,
                _.lastSignInAt -> _.lastSignInAt,
                _.currentSignInIp -> _.currentSignInIp,
                _.lastSignInIp -> _.lastSignInIp,
                _.role -> _.role,
                _.status -> _.status,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[User]
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
                .publishingCreated((cur, cid, time) => events.UserCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) => events.UserUpdated(cur, prev, id = cur.id, cid, time))
          }
        } yield result

      }.runToFuture
    }

  override def upsertOnResetPasswordToken(u: User): Future[User] =
    monitored("upsert-on-reset_password_token") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = u.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[User]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.resetPasswordToken)(
                _.email -> _.email,
                _.encryptedPassword -> _.encryptedPassword,
                _.rememberToken -> _.rememberToken,
                _.rememberCreatedAt -> _.rememberCreatedAt,
                _.confirmationToken -> _.confirmationToken,
                _.confirmedAt -> _.confirmedAt,
                _.confirmationSentAt -> _.confirmationSentAt,
                _.passwordSalt -> _.passwordSalt,
                _.fullName -> _.fullName,
                _.initials -> _.initials,
                _.twoFactorAuthActive -> _.twoFactorAuthActive,
                _.twoFactorAuthSecret -> _.twoFactorAuthSecret,
                _.signInCount -> _.signInCount,
                _.currentSignInAt -> _.currentSignInAt,
                _.lastSignInAt -> _.lastSignInAt,
                _.currentSignInIp -> _.currentSignInIp,
                _.lastSignInIp -> _.lastSignInIp,
                _.role -> _.role,
                _.status -> _.status,
                _.accessToken -> _.accessToken,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[User]
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
                .publishingCreated((cur, cid, time) => events.UserCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) => events.UserUpdated(cur, prev, id = cur.id, cid, time))
          }
        } yield result

      }.runToFuture
    }

  override def existsOnAccessToken(u: User): Future[Boolean] =
    monitored("exists-access_token") {
      read {
        ctx
          .run(
            query[User]
              .filter(row => row.accessToken == lift(u.accessToken))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def existsOnConfirmationToken(u: User): Future[Boolean] =
    monitored("exists-confirmation_token") {
      read {
        ctx
          .run(
            query[User]
              .filter(row => row.confirmationToken == lift(u.confirmationToken))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def existsOnEmail(u: User): Future[Boolean] =
    monitored("exists-email") {
      read {
        ctx
          .run(
            query[User]
              .filter(row => row.email == lift(u.email))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def existsOnResetPasswordToken(u: User): Future[Boolean] =
    monitored("exists-reset_password_token") {
      read {
        ctx
          .run(
            query[User]
              .filter(row => row.resetPasswordToken == lift(u.resetPasswordToken))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def updateEncryptedPasswordById(id: UserId, encryptedPassword: String): Future[User] =
    monitored("update-encrypted_password-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(encryptedPassword = encryptedPassword, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.encryptedPassword -> lift(encryptedPassword),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateRememberTokenById(id: UserId, rememberToken: Option[String]): Future[User] =
    monitored("update-remember_token-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberToken = rememberToken, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.rememberToken -> lift(rememberToken),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateRememberCreatedAtById(
      id: UserId,
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-remember_created_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberCreatedAt = rememberCreatedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.rememberCreatedAt -> lift(rememberCreatedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateConfirmedAtById(id: UserId, confirmedAt: Option[escalator.util.Timestamp]): Future[User] =
    monitored("update-confirmed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmedAt = confirmedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.confirmedAt -> lift(confirmedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateConfirmationSentAtById(
      id: UserId,
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmation_sent_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmationSentAt = confirmationSentAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.confirmationSentAt -> lift(confirmationSentAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updatePasswordSaltById(id: UserId, passwordSalt: Option[String]): Future[User] =
    monitored("update-password_salt-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(passwordSalt = passwordSalt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.passwordSalt -> lift(passwordSalt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateFullNameById(id: UserId, fullName: Option[String]): Future[User] =
    monitored("update-full_name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(fullName = fullName, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.fullName -> lift(fullName),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateInitialById(id: UserId, initials: Option[String]): Future[User] =
    monitored("update-initials-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(initials = initials, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.initials -> lift(initials),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateTwoFactorAuthActiveById(id: UserId, twoFactorAuthActive: Option[Boolean]): Future[User] =
    monitored("update-two_factor_auth_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthActive = twoFactorAuthActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.twoFactorAuthActive -> lift(twoFactorAuthActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateTwoFactorAuthSecretById(id: UserId, twoFactorAuthSecret: Option[String]): Future[User] =
    monitored("update-two_factor_auth_secret-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthSecret = twoFactorAuthSecret, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.twoFactorAuthSecret -> lift(twoFactorAuthSecret),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateSignInCountById(id: UserId, signInCount: Option[Int]): Future[User] =
    monitored("update-sign_in_count-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(signInCount = signInCount, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.signInCount -> lift(signInCount),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateCurrentSignInAtById(id: UserId, currentSignInAt: Option[escalator.util.Timestamp]): Future[User] =
    monitored("update-current_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInAt = currentSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.currentSignInAt -> lift(currentSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateLastSignInAtById(id: UserId, lastSignInAt: Option[escalator.util.Timestamp]): Future[User] =
    monitored("update-last_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInAt = lastSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.lastSignInAt -> lift(lastSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateCurrentSignInIpById(id: UserId, currentSignInIp: Option[String]): Future[User] =
    monitored("update-current_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInIp = currentSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.currentSignInIp -> lift(currentSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateLastSignInIpById(id: UserId, lastSignInIp: Option[String]): Future[User] =
    monitored("update-last_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInIp = lastSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.lastSignInIp -> lift(lastSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateRoleById(id: UserId, role: UserRoleType): Future[User] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateStatuById(id: UserId, status: UserStatusType): Future[User] =
    monitored("update-status-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(status = status, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(_.id == lift(id))
                  .update(
                    _.status -> lift(status),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with id $id"))
      }
    }

  override def updateEncryptedPasswordByAccessToken(
      accessToken: UserAccessToken,
      encryptedPassword: String
  ): Future[User] =
    monitored("update-encrypted_password-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(encryptedPassword = encryptedPassword, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.encryptedPassword -> lift(encryptedPassword),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateRememberTokenByAccessToken(
      accessToken: UserAccessToken,
      rememberToken: Option[String]
  ): Future[User] =
    monitored("update-remember_token-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberToken = rememberToken, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.rememberToken -> lift(rememberToken),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateRememberCreatedAtByAccessToken(
      accessToken: UserAccessToken,
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-remember_created_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberCreatedAt = rememberCreatedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.rememberCreatedAt -> lift(rememberCreatedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateConfirmedAtByAccessToken(
      accessToken: UserAccessToken,
      confirmedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmedAt = confirmedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.confirmedAt -> lift(confirmedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateConfirmationSentAtByAccessToken(
      accessToken: UserAccessToken,
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmation_sent_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmationSentAt = confirmationSentAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.confirmationSentAt -> lift(confirmationSentAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updatePasswordSaltByAccessToken(
      accessToken: UserAccessToken,
      passwordSalt: Option[String]
  ): Future[User] =
    monitored("update-password_salt-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(passwordSalt = passwordSalt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.passwordSalt -> lift(passwordSalt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateFullNameByAccessToken(accessToken: UserAccessToken, fullName: Option[String]): Future[User] =
    monitored("update-full_name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(fullName = fullName, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.fullName -> lift(fullName),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateInitialByAccessToken(accessToken: UserAccessToken, initials: Option[String]): Future[User] =
    monitored("update-initials-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(initials = initials, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.initials -> lift(initials),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateTwoFactorAuthActiveByAccessToken(
      accessToken: UserAccessToken,
      twoFactorAuthActive: Option[Boolean]
  ): Future[User] =
    monitored("update-two_factor_auth_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthActive = twoFactorAuthActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.twoFactorAuthActive -> lift(twoFactorAuthActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateTwoFactorAuthSecretByAccessToken(
      accessToken: UserAccessToken,
      twoFactorAuthSecret: Option[String]
  ): Future[User] =
    monitored("update-two_factor_auth_secret-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthSecret = twoFactorAuthSecret, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.twoFactorAuthSecret -> lift(twoFactorAuthSecret),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateSignInCountByAccessToken(accessToken: UserAccessToken, signInCount: Option[Int]): Future[User] =
    monitored("update-sign_in_count-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(signInCount = signInCount, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.signInCount -> lift(signInCount),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateCurrentSignInAtByAccessToken(
      accessToken: UserAccessToken,
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-current_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInAt = currentSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.currentSignInAt -> lift(currentSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateLastSignInAtByAccessToken(
      accessToken: UserAccessToken,
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-last_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInAt = lastSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.lastSignInAt -> lift(lastSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateCurrentSignInIpByAccessToken(
      accessToken: UserAccessToken,
      currentSignInIp: Option[String]
  ): Future[User] =
    monitored("update-current_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInIp = currentSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.currentSignInIp -> lift(currentSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateLastSignInIpByAccessToken(
      accessToken: UserAccessToken,
      lastSignInIp: Option[String]
  ): Future[User] =
    monitored("update-last_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInIp = lastSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.lastSignInIp -> lift(lastSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateRoleByAccessToken(accessToken: UserAccessToken, role: UserRoleType): Future[User] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateStatuByAccessToken(accessToken: UserAccessToken, status: UserStatusType): Future[User] =
    monitored("update-status-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByAccessToken(accessToken: UserAccessToken).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(status = status, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.accessToken == lift(accessToken))
                  .update(
                    _.status -> lift(status),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with accessToken: UserAccessToken"))
      }
    }

  override def updateEncryptedPasswordByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      encryptedPassword: String
  ): Future[User] =
    monitored("update-encrypted_password-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(encryptedPassword = encryptedPassword, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.encryptedPassword -> lift(encryptedPassword),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateRememberTokenByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      rememberToken: Option[String]
  ): Future[User] =
    monitored("update-remember_token-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberToken = rememberToken, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.rememberToken -> lift(rememberToken),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateRememberCreatedAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-remember_created_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberCreatedAt = rememberCreatedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.rememberCreatedAt -> lift(rememberCreatedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateConfirmedAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      confirmedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmedAt = confirmedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.confirmedAt -> lift(confirmedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateConfirmationSentAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmation_sent_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmationSentAt = confirmationSentAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.confirmationSentAt -> lift(confirmationSentAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updatePasswordSaltByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      passwordSalt: Option[String]
  ): Future[User] =
    monitored("update-password_salt-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(passwordSalt = passwordSalt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.passwordSalt -> lift(passwordSalt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateFullNameByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      fullName: Option[String]
  ): Future[User] =
    monitored("update-full_name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(fullName = fullName, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.fullName -> lift(fullName),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateInitialByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      initials: Option[String]
  ): Future[User] =
    monitored("update-initials-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(initials = initials, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.initials -> lift(initials),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateTwoFactorAuthActiveByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      twoFactorAuthActive: Option[Boolean]
  ): Future[User] =
    monitored("update-two_factor_auth_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthActive = twoFactorAuthActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.twoFactorAuthActive -> lift(twoFactorAuthActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateTwoFactorAuthSecretByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      twoFactorAuthSecret: Option[String]
  ): Future[User] =
    monitored("update-two_factor_auth_secret-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthSecret = twoFactorAuthSecret, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.twoFactorAuthSecret -> lift(twoFactorAuthSecret),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateSignInCountByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      signInCount: Option[Int]
  ): Future[User] =
    monitored("update-sign_in_count-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(signInCount = signInCount, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.signInCount -> lift(signInCount),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateCurrentSignInAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-current_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInAt = currentSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.currentSignInAt -> lift(currentSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateLastSignInAtByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-last_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInAt = lastSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.lastSignInAt -> lift(lastSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateCurrentSignInIpByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      currentSignInIp: Option[String]
  ): Future[User] =
    monitored("update-current_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInIp = currentSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.currentSignInIp -> lift(currentSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateLastSignInIpByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      lastSignInIp: Option[String]
  ): Future[User] =
    monitored("update-last_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInIp = lastSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.lastSignInIp -> lift(lastSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateRoleByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      role: UserRoleType
  ): Future[User] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateStatuByResetPasswordToken(
      resetPasswordToken: Option[UserResetPasswordToken],
      status: UserStatusType
  ): Future[User] =
    monitored("update-status-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(status = status, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
                  .update(
                    _.status -> lift(status),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with resetPasswordToken: Option[UserResetPasswordToken]")
          )
      }
    }

  override def updateEncryptedPasswordByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      encryptedPassword: String
  ): Future[User] =
    monitored("update-encrypted_password-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(encryptedPassword = encryptedPassword, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.encryptedPassword -> lift(encryptedPassword),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateRememberTokenByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      rememberToken: Option[String]
  ): Future[User] =
    monitored("update-remember_token-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberToken = rememberToken, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.rememberToken -> lift(rememberToken),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateRememberCreatedAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-remember_created_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberCreatedAt = rememberCreatedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.rememberCreatedAt -> lift(rememberCreatedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateConfirmedAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      confirmedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmedAt = confirmedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.confirmedAt -> lift(confirmedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateConfirmationSentAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmation_sent_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmationSentAt = confirmationSentAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.confirmationSentAt -> lift(confirmationSentAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updatePasswordSaltByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      passwordSalt: Option[String]
  ): Future[User] =
    monitored("update-password_salt-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(passwordSalt = passwordSalt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.passwordSalt -> lift(passwordSalt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateFullNameByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      fullName: Option[String]
  ): Future[User] =
    monitored("update-full_name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(fullName = fullName, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.fullName -> lift(fullName),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateInitialByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      initials: Option[String]
  ): Future[User] =
    monitored("update-initials-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(initials = initials, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.initials -> lift(initials),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateTwoFactorAuthActiveByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      twoFactorAuthActive: Option[Boolean]
  ): Future[User] =
    monitored("update-two_factor_auth_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthActive = twoFactorAuthActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.twoFactorAuthActive -> lift(twoFactorAuthActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateTwoFactorAuthSecretByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      twoFactorAuthSecret: Option[String]
  ): Future[User] =
    monitored("update-two_factor_auth_secret-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthSecret = twoFactorAuthSecret, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.twoFactorAuthSecret -> lift(twoFactorAuthSecret),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateSignInCountByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      signInCount: Option[Int]
  ): Future[User] =
    monitored("update-sign_in_count-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(signInCount = signInCount, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.signInCount -> lift(signInCount),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateCurrentSignInAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-current_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInAt = currentSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.currentSignInAt -> lift(currentSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateLastSignInAtByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-last_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInAt = lastSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.lastSignInAt -> lift(lastSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateCurrentSignInIpByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      currentSignInIp: Option[String]
  ): Future[User] =
    monitored("update-current_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInIp = currentSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.currentSignInIp -> lift(currentSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateLastSignInIpByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      lastSignInIp: Option[String]
  ): Future[User] =
    monitored("update-last_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInIp = lastSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.lastSignInIp -> lift(lastSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateRoleByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      role: UserRoleType
  ): Future[User] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateStatuByConfirmationToken(
      confirmationToken: Option[UserConfirmationToken],
      status: UserStatusType
  ): Future[User] =
    monitored("update-status-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(status = status, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.confirmationToken == lift(confirmationToken))
                  .update(
                    _.status -> lift(status),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(
            new NoSuchElementException(s"No User found with confirmationToken: Option[UserConfirmationToken]")
          )
      }
    }

  override def updateEncryptedPasswordByEmail(email: UserEmail, encryptedPassword: String): Future[User] =
    monitored("update-encrypted_password-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(encryptedPassword = encryptedPassword, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.encryptedPassword -> lift(encryptedPassword),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateRememberTokenByEmail(email: UserEmail, rememberToken: Option[String]): Future[User] =
    monitored("update-remember_token-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberToken = rememberToken, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.rememberToken -> lift(rememberToken),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateRememberCreatedAtByEmail(
      email: UserEmail,
      rememberCreatedAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-remember_created_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rememberCreatedAt = rememberCreatedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.rememberCreatedAt -> lift(rememberCreatedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateConfirmedAtByEmail(email: UserEmail, confirmedAt: Option[escalator.util.Timestamp]): Future[User] =
    monitored("update-confirmed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmedAt = confirmedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.confirmedAt -> lift(confirmedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateConfirmationSentAtByEmail(
      email: UserEmail,
      confirmationSentAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-confirmation_sent_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(confirmationSentAt = confirmationSentAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.confirmationSentAt -> lift(confirmationSentAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updatePasswordSaltByEmail(email: UserEmail, passwordSalt: Option[String]): Future[User] =
    monitored("update-password_salt-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(passwordSalt = passwordSalt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.passwordSalt -> lift(passwordSalt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateFullNameByEmail(email: UserEmail, fullName: Option[String]): Future[User] =
    monitored("update-full_name-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(fullName = fullName, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.fullName -> lift(fullName),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateInitialByEmail(email: UserEmail, initials: Option[String]): Future[User] =
    monitored("update-initials-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(initials = initials, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.initials -> lift(initials),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateTwoFactorAuthActiveByEmail(email: UserEmail, twoFactorAuthActive: Option[Boolean]): Future[User] =
    monitored("update-two_factor_auth_active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthActive = twoFactorAuthActive, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.twoFactorAuthActive -> lift(twoFactorAuthActive),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateTwoFactorAuthSecretByEmail(email: UserEmail, twoFactorAuthSecret: Option[String]): Future[User] =
    monitored("update-two_factor_auth_secret-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(twoFactorAuthSecret = twoFactorAuthSecret, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.twoFactorAuthSecret -> lift(twoFactorAuthSecret),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateSignInCountByEmail(email: UserEmail, signInCount: Option[Int]): Future[User] =
    monitored("update-sign_in_count-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(signInCount = signInCount, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.signInCount -> lift(signInCount),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateCurrentSignInAtByEmail(
      email: UserEmail,
      currentSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-current_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInAt = currentSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.currentSignInAt -> lift(currentSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateLastSignInAtByEmail(
      email: UserEmail,
      lastSignInAt: Option[escalator.util.Timestamp]
  ): Future[User] =
    monitored("update-last_sign_in_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInAt = lastSignInAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.lastSignInAt -> lift(lastSignInAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateCurrentSignInIpByEmail(email: UserEmail, currentSignInIp: Option[String]): Future[User] =
    monitored("update-current_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(currentSignInIp = currentSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.currentSignInIp -> lift(currentSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateLastSignInIpByEmail(email: UserEmail, lastSignInIp: Option[String]): Future[User] =
    monitored("update-last_sign_in_ip-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(lastSignInIp = lastSignInIp, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.lastSignInIp -> lift(lastSignInIp),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateRoleByEmail(email: UserEmail, role: UserRoleType): Future[User] =
    monitored("update-role-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(role = role, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.role -> lift(role),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def updateStatuByEmail(email: UserEmail, status: UserStatusType): Future[User] =
    monitored("update-status-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByEmail(email: UserEmail).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(status = status, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[User]
                  .filter(u => u.email == lift(email))
                  .update(
                    _.status -> lift(status),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) =>
            events.UserUpdated(cur, Some(updatedModel), id = cur.id, cid, time)
          )

        case None =>
          Future.failed(new NoSuchElementException(s"No User found with email: UserEmail"))
      }
    }

  override def getById(u: UserId): Future[Option[User]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[User]
              .filter(_.id == lift(u))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByIds(u: List[UserId]): Future[List[User]] =
    monitored("getByIds") {
      read {
        ctx
          .run(
            query[User]
              .filter(obj => liftQuery(u).contains(obj.id))
          )
          .runToFuture
      }
    }

  override def update(u: User): Future[User] =
    monitored("update") {
      if (u.id == UserId(0L)) {
        insert(u)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = u.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[User]
                .filter(_.id == lift(u.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => events.UserUpdated(cur, prev, id = u.id, cid, time))
      }
    }

  override def upsert(u: User): Future[User] =
    monitored("upsert") {
      if (u.id == UserId(0L)) {
        insert(u)
      } else {
        update(u)
      }
    }

  override def upsert(ul: List[User]): Future[List[User]] = {
    Future.sequence(ul.map { u => upsert(u) })
  }

  override def delete(u: User): Future[User] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(u) {
        ctx
          .run(
            query[User]
              .filter(_.id == lift(u.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => events.UserDeleted(m, id = u.id, cid, time))
    }

  override def getByResetPasswordToken(resetPasswordToken: Option[UserResetPasswordToken]): Future[Option[User]] =
    monitored("get-by-reset_password_token") {
      read {
        ctx
          .run(
            query[User]
              .filter(u => u.resetPasswordToken == lift(resetPasswordToken))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByEmail(email: UserEmail): Future[Option[User]] =
    monitored("get-by-email") {
      read {
        ctx
          .run(
            query[User]
              .filter(u => u.email == lift(email))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByConfirmationToken(confirmationToken: Option[UserConfirmationToken]): Future[Option[User]] =
    monitored("get-by-confirmation_token") {
      read {
        ctx
          .run(
            query[User]
              .filter(u => u.confirmationToken == lift(confirmationToken))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByAccessToken(accessToken: UserAccessToken): Future[Option[User]] =
    monitored("get-by-access_token") {
      read {
        ctx
          .run(
            query[User]
              .filter(u => u.accessToken == lift(accessToken))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getListByRole(role: UserRoleType): Future[List[User]] =
    monitored("get-by-role") {
      read {
        ctx
          .run(
            query[User]
              .filter(r => r.role == lift(role))
          )
          .runToFuture
      }
    }

  override def getListByRoles(roles: List[UserRoleType]): Future[List[User]] =
    monitored("get-by-roles") {
      read {
        ctx
          .run(
            query[User]
              .filter(r => liftQuery(roles).contains(r.role))
          )
          .runToFuture
      }
    }

  override def getListByStatu(status: UserStatusType): Future[List[User]] =
    monitored("get-by-status") {
      read {
        ctx
          .run(
            query[User]
              .filter(r => r.status == lift(status))
          )
          .runToFuture
      }
    }

  override def getListByStatus(statuss: List[UserStatusType]): Future[List[User]] =
    monitored("get-by-statuss") {
      read {
        ctx
          .run(
            query[User]
              .filter(r => liftQuery(statuss).contains(r.status))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[User].size).runToFuture
      }
    }

  override def getAll(): Future[List[User]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[User]
          )
          .runToFuture
      }
    }

}
