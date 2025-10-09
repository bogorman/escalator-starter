package com.escalatorstarter.persistence.postgres.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:11:000

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

import com.escalatorstarter.persistence.database.tables.EntitiesTable

import monix.eval.Task
import escalator.util.monix.TaskSyntax._

abstract class PostgresEntitiesTable(database: PostgresDatabase)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends EntitiesTable
    with PostgresCustomEncoder
    with RepositoryHelpers {

  def db: PostgresDatabase = database

  import PostgresMappedEncoder._

  import monix.execution.Scheduler.Implicits.global
  import ctx._

  def monitored(name: String) = MonitoredPostgresOperation(name, tableName)

  private def insert(e: Entity): Future[Entity] =
    monitored("insert") {
      val ts = TimeUtil.nowTimestamp()
      val toInsert = e.copy(createdAt = ts, updatedAt = ts)

      ctx
        .run(
          query[Entity]
            .insert(lift(toInsert))
            .returningGenerated(_.id)
        )
        .runToFuture
        .map { newId =>
          toInsert.copy(id = newId)
        }
        .flatMap { result =>
          writeWithTimestamp(result, ts)(Future.successful(()))
            .publishingCreated((m, cid, time) => EntityCreated(m, id = e.id, cid, time))
        }
    }

  private def insert(el: List[Entity]): Future[List[Entity]] =
    monitored("insert") {
      Future.sequence(el.map { e => insert(e) })
    }

  override def upsertOnTag(e: Entity): Future[Entity] =
    monitored("upsert-on-tag") {
      val ts = TimeUtil.nowTimestamp()
      val toUpsert = e.copy(createdAt = ts, updatedAt = ts)

      ctx.transaction {
        for {
          // Step 1: perform upsert, return only the generated ID
          id <- ctx.run(
            query[Entity]
              .insert(lift(toUpsert))
              .onConflictUpdate(_.tag)(
                _.keywords -> _.keywords,
                _.entityType -> _.entityType,
                _.entityObjectId -> _.entityObjectId,
                _.userId -> _.userId,
                _.url -> _.url,
                _.content -> _.content,
                _.domains -> _.domains,
                _.originalImage -> _.originalImage,
                _.croppedImage -> _.croppedImage,
                _.miniImage -> _.miniImage,
                _.croppedCoverImage -> _.croppedCoverImage,
                _.blacklisted -> _.blacklisted,
                _.weighting -> _.weighting,
                _.parentEntityId -> _.parentEntityId,
                _.wdu -> _.wdu,
                _.externalEntityId -> _.externalEntityId,
                _.precreated -> _.precreated,
                _.verified -> _.verified,
                _.claimed -> _.claimed,
                _.claimedAt -> _.claimedAt,
                _.active -> _.active,
                _.settings -> _.settings,
                _.personal -> _.personal,
                _.professional -> _.professional,
                _.subline -> _.subline,
                _.countryCode -> _.countryCode,
                _.hashTags -> _.hashTags,
                _.searchKeywords -> _.searchKeywords,
                _.originalCoverImage -> _.originalCoverImage,
                _.createdBy -> _.createdBy,
                _.validationKeywords -> _.validationKeywords,
                _.newsTopics -> _.newsTopics,
                _.newsVerificationRequired -> _.newsVerificationRequired,
                _.rejectionKeywords -> _.rejectionKeywords,
                _.refEntityType -> _.refEntityType,
                _.updatedAt -> _.updatedAt
              )
              .returningGenerated(_.id)
          )

          tuples: List[(Timestamp, Timestamp)] <- ctx.run(
            query[Entity]
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
                .publishingCreated((cur, cid, time) => EntityCreated(cur, id = cur.id, cid, time))
            else
              writeWithTimestamp(result, ts)(Future.successful(()))
                .publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, prev, id = cur.id, cid, time))
          }
        } yield result

      }.runToFuture
    }

  override def existsOnTag(e: Entity): Future[Boolean] =
    monitored("exists-tag") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(row => row.tag == lift(e.tag))
              .nonEmpty
          )
          .runToFuture
      }
    }

  override def updateKeywordById(id: EntityId, keywords: Option[List[String]]): Future[Entity] =
    monitored("update-keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(keywords = keywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.keywords -> lift(keywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateEntityTypeById(id: EntityId, entityType: EntityType): Future[Entity] =
    monitored("update-entity_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(entityType = entityType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.entityType -> lift(entityType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateEntityObjectIdById(id: EntityId, entityObjectId: Option[Long]): Future[Entity] =
    monitored("update-entity_object_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(entityObjectId = entityObjectId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.entityObjectId -> lift(entityObjectId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateUserIdById(id: EntityId, userId: UserId): Future[Entity] =
    monitored("update-user_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userId = userId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.userId -> lift(userId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateUrlById(id: EntityId, url: Option[String]): Future[Entity] =
    monitored("update-url-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(url = url, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.url -> lift(url),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateContentById(id: EntityId, content: Option[String]): Future[Entity] =
    monitored("update-content-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(content = content, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.content -> lift(content),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateDomainById(id: EntityId, domains: Option[List[String]]): Future[Entity] =
    monitored("update-domains-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(domains = domains, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.domains -> lift(domains),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateOriginalImageById(id: EntityId, originalImage: Option[String]): Future[Entity] =
    monitored("update-original_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(originalImage = originalImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.originalImage -> lift(originalImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateCroppedImageById(id: EntityId, croppedImage: Option[String]): Future[Entity] =
    monitored("update-cropped_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(croppedImage = croppedImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.croppedImage -> lift(croppedImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateMiniImageById(id: EntityId, miniImage: Option[String]): Future[Entity] =
    monitored("update-mini_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(miniImage = miniImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.miniImage -> lift(miniImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateCroppedCoverImageById(id: EntityId, croppedCoverImage: Option[String]): Future[Entity] =
    monitored("update-cropped_cover_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(croppedCoverImage = croppedCoverImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.croppedCoverImage -> lift(croppedCoverImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateBlacklistedById(id: EntityId, blacklisted: Option[Boolean]): Future[Entity] =
    monitored("update-blacklisted-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(blacklisted = blacklisted, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.blacklisted -> lift(blacklisted),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateWeightingById(id: EntityId, weighting: Option[Double]): Future[Entity] =
    monitored("update-weighting-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(weighting = weighting, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.weighting -> lift(weighting),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateParentEntityIdById(id: EntityId, parentEntityId: Option[EntityId]): Future[Entity] =
    monitored("update-parent_entity_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(parentEntityId = parentEntityId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.parentEntityId -> lift(parentEntityId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateWduById(id: EntityId, wdu: Option[String]): Future[Entity] =
    monitored("update-wdu-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(wdu = wdu, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.wdu -> lift(wdu),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateExternalEntityIdById(id: EntityId, externalEntityId: Option[String]): Future[Entity] =
    monitored("update-external_entity_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(externalEntityId = externalEntityId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.externalEntityId -> lift(externalEntityId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updatePrecreatedById(id: EntityId, precreated: Option[Boolean]): Future[Entity] =
    monitored("update-precreated-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(precreated = precreated, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.precreated -> lift(precreated),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateVerifiedById(id: EntityId, verified: Option[Boolean]): Future[Entity] =
    monitored("update-verified-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(verified = verified, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.verified -> lift(verified),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateClaimedById(id: EntityId, claimed: Option[Boolean]): Future[Entity] =
    monitored("update-claimed-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(claimed = claimed, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.claimed -> lift(claimed),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateClaimedAtById(id: EntityId, claimedAt: Option[escalator.util.Timestamp]): Future[Entity] =
    monitored("update-claimed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(claimedAt = claimedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.claimedAt -> lift(claimedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateActiveById(id: EntityId, active: Option[Boolean]): Future[Entity] =
    monitored("update-active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(active = active, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.active -> lift(active),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateSettingById(id: EntityId, settings: io.circe.Json): Future[Entity] =
    monitored("update-settings-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(settings = settings, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.settings -> lift(settings),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updatePersonalById(id: EntityId, personal: io.circe.Json): Future[Entity] =
    monitored("update-personal-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(personal = personal, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.personal -> lift(personal),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateProfessionalById(id: EntityId, professional: io.circe.Json): Future[Entity] =
    monitored("update-professional-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(professional = professional, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.professional -> lift(professional),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateSublineById(id: EntityId, subline: Option[String]): Future[Entity] =
    monitored("update-subline-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(subline = subline, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.subline -> lift(subline),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateCountryCodeById(id: EntityId, countryCode: Option[String]): Future[Entity] =
    monitored("update-country_code-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(countryCode = countryCode, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.countryCode -> lift(countryCode),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateHashTagById(id: EntityId, hashTags: Option[List[String]]): Future[Entity] =
    monitored("update-hash_tags-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(hashTags = hashTags, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.hashTags -> lift(hashTags),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateSearchKeywordById(id: EntityId, searchKeywords: Option[List[String]]): Future[Entity] =
    monitored("update-search_keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(searchKeywords = searchKeywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.searchKeywords -> lift(searchKeywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateOriginalCoverImageById(id: EntityId, originalCoverImage: Option[String]): Future[Entity] =
    monitored("update-original_cover_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(originalCoverImage = originalCoverImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.originalCoverImage -> lift(originalCoverImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateCreatedByById(id: EntityId, createdBy: Option[Long]): Future[Entity] =
    monitored("update-created_by-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(createdBy = createdBy, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.createdBy -> lift(createdBy),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateValidationKeywordById(id: EntityId, validationKeywords: Option[List[String]]): Future[Entity] =
    monitored("update-validation_keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(validationKeywords = validationKeywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.validationKeywords -> lift(validationKeywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateNewsTopicById(id: EntityId, newsTopics: Option[List[String]]): Future[Entity] =
    monitored("update-news_topics-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(newsTopics = newsTopics, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.newsTopics -> lift(newsTopics),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateNewsVerificationRequiredById(
      id: EntityId,
      newsVerificationRequired: Option[Boolean]
  ): Future[Entity] =
    monitored("update-news_verification_required-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(newsVerificationRequired = newsVerificationRequired, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.newsVerificationRequired -> lift(newsVerificationRequired),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateRejectionKeywordById(id: EntityId, rejectionKeywords: Option[List[String]]): Future[Entity] =
    monitored("update-rejection_keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rejectionKeywords = rejectionKeywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.rejectionKeywords -> lift(rejectionKeywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateRefEntityTypeById(id: EntityId, refEntityType: Option[EntityType]): Future[Entity] =
    monitored("update-ref_entity_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getById(id).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(refEntityType = refEntityType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(_.id == lift(id))
                  .update(
                    _.refEntityType -> lift(refEntityType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with id $id"))
      }
    }

  override def updateKeywordByTag(tag: Option[EntityTag], keywords: Option[List[String]]): Future[Entity] =
    monitored("update-keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(keywords = keywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.keywords -> lift(keywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateEntityTypeByTag(tag: Option[EntityTag], entityType: EntityType): Future[Entity] =
    monitored("update-entity_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(entityType = entityType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.entityType -> lift(entityType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateEntityObjectIdByTag(tag: Option[EntityTag], entityObjectId: Option[Long]): Future[Entity] =
    monitored("update-entity_object_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(entityObjectId = entityObjectId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.entityObjectId -> lift(entityObjectId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateUserIdByTag(tag: Option[EntityTag], userId: UserId): Future[Entity] =
    monitored("update-user_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(userId = userId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.userId -> lift(userId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateUrlByTag(tag: Option[EntityTag], url: Option[String]): Future[Entity] =
    monitored("update-url-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(url = url, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.url -> lift(url),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateContentByTag(tag: Option[EntityTag], content: Option[String]): Future[Entity] =
    monitored("update-content-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(content = content, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.content -> lift(content),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateDomainByTag(tag: Option[EntityTag], domains: Option[List[String]]): Future[Entity] =
    monitored("update-domains-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(domains = domains, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.domains -> lift(domains),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateOriginalImageByTag(tag: Option[EntityTag], originalImage: Option[String]): Future[Entity] =
    monitored("update-original_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(originalImage = originalImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.originalImage -> lift(originalImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateCroppedImageByTag(tag: Option[EntityTag], croppedImage: Option[String]): Future[Entity] =
    monitored("update-cropped_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(croppedImage = croppedImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.croppedImage -> lift(croppedImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateMiniImageByTag(tag: Option[EntityTag], miniImage: Option[String]): Future[Entity] =
    monitored("update-mini_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(miniImage = miniImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.miniImage -> lift(miniImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateCroppedCoverImageByTag(tag: Option[EntityTag], croppedCoverImage: Option[String]): Future[Entity] =
    monitored("update-cropped_cover_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(croppedCoverImage = croppedCoverImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.croppedCoverImage -> lift(croppedCoverImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateBlacklistedByTag(tag: Option[EntityTag], blacklisted: Option[Boolean]): Future[Entity] =
    monitored("update-blacklisted-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(blacklisted = blacklisted, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.blacklisted -> lift(blacklisted),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateWeightingByTag(tag: Option[EntityTag], weighting: Option[Double]): Future[Entity] =
    monitored("update-weighting-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(weighting = weighting, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.weighting -> lift(weighting),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateParentEntityIdByTag(tag: Option[EntityTag], parentEntityId: Option[EntityId]): Future[Entity] =
    monitored("update-parent_entity_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(parentEntityId = parentEntityId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.parentEntityId -> lift(parentEntityId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateWduByTag(tag: Option[EntityTag], wdu: Option[String]): Future[Entity] =
    monitored("update-wdu-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(wdu = wdu, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.wdu -> lift(wdu),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateExternalEntityIdByTag(tag: Option[EntityTag], externalEntityId: Option[String]): Future[Entity] =
    monitored("update-external_entity_id-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(externalEntityId = externalEntityId, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.externalEntityId -> lift(externalEntityId),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updatePrecreatedByTag(tag: Option[EntityTag], precreated: Option[Boolean]): Future[Entity] =
    monitored("update-precreated-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(precreated = precreated, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.precreated -> lift(precreated),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateVerifiedByTag(tag: Option[EntityTag], verified: Option[Boolean]): Future[Entity] =
    monitored("update-verified-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(verified = verified, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.verified -> lift(verified),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateClaimedByTag(tag: Option[EntityTag], claimed: Option[Boolean]): Future[Entity] =
    monitored("update-claimed-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(claimed = claimed, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.claimed -> lift(claimed),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateClaimedAtByTag(
      tag: Option[EntityTag],
      claimedAt: Option[escalator.util.Timestamp]
  ): Future[Entity] =
    monitored("update-claimed_at-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(claimedAt = claimedAt, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.claimedAt -> lift(claimedAt),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateActiveByTag(tag: Option[EntityTag], active: Option[Boolean]): Future[Entity] =
    monitored("update-active-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(active = active, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.active -> lift(active),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateSettingByTag(tag: Option[EntityTag], settings: io.circe.Json): Future[Entity] =
    monitored("update-settings-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(settings = settings, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.settings -> lift(settings),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updatePersonalByTag(tag: Option[EntityTag], personal: io.circe.Json): Future[Entity] =
    monitored("update-personal-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(personal = personal, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.personal -> lift(personal),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateProfessionalByTag(tag: Option[EntityTag], professional: io.circe.Json): Future[Entity] =
    monitored("update-professional-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(professional = professional, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.professional -> lift(professional),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateSublineByTag(tag: Option[EntityTag], subline: Option[String]): Future[Entity] =
    monitored("update-subline-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(subline = subline, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.subline -> lift(subline),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateCountryCodeByTag(tag: Option[EntityTag], countryCode: Option[String]): Future[Entity] =
    monitored("update-country_code-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(countryCode = countryCode, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.countryCode -> lift(countryCode),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateHashTagByTag(tag: Option[EntityTag], hashTags: Option[List[String]]): Future[Entity] =
    monitored("update-hash_tags-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(hashTags = hashTags, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.hashTags -> lift(hashTags),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateSearchKeywordByTag(tag: Option[EntityTag], searchKeywords: Option[List[String]]): Future[Entity] =
    monitored("update-search_keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(searchKeywords = searchKeywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.searchKeywords -> lift(searchKeywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateOriginalCoverImageByTag(
      tag: Option[EntityTag],
      originalCoverImage: Option[String]
  ): Future[Entity] =
    monitored("update-original_cover_image-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(originalCoverImage = originalCoverImage, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.originalCoverImage -> lift(originalCoverImage),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateCreatedByByTag(tag: Option[EntityTag], createdBy: Option[Long]): Future[Entity] =
    monitored("update-created_by-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(createdBy = createdBy, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.createdBy -> lift(createdBy),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateValidationKeywordByTag(
      tag: Option[EntityTag],
      validationKeywords: Option[List[String]]
  ): Future[Entity] =
    monitored("update-validation_keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(validationKeywords = validationKeywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.validationKeywords -> lift(validationKeywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateNewsTopicByTag(tag: Option[EntityTag], newsTopics: Option[List[String]]): Future[Entity] =
    monitored("update-news_topics-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(newsTopics = newsTopics, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.newsTopics -> lift(newsTopics),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateNewsVerificationRequiredByTag(
      tag: Option[EntityTag],
      newsVerificationRequired: Option[Boolean]
  ): Future[Entity] =
    monitored("update-news_verification_required-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(newsVerificationRequired = newsVerificationRequired, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.newsVerificationRequired -> lift(newsVerificationRequired),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateRejectionKeywordByTag(
      tag: Option[EntityTag],
      rejectionKeywords: Option[List[String]]
  ): Future[Entity] =
    monitored("update-rejection_keywords-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(rejectionKeywords = rejectionKeywords, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.rejectionKeywords -> lift(rejectionKeywords),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def updateRefEntityTypeByTag(tag: Option[EntityTag], refEntityType: Option[EntityType]): Future[Entity] =
    monitored("update-ref_entity_type-by-id") {
      val ts = TimeUtil.nowTimestamp()

      getByTag(tag: Option[EntityTag]).flatMap {
        case Some(currentModel) =>
          val updatedModel = currentModel.copy(refEntityType = refEntityType, updatedAt = ts)

          write(updatedModel) {
            ctx
              .run(
                query[Entity]
                  .filter(e => e.tag == lift(tag))
                  .update(
                    _.refEntityType -> lift(refEntityType),
                    _.updatedAt -> lift(ts)
                  )
              )
              .runToFuture
          }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, Some(updatedModel), id = cur.id, cid, time))

        case None =>
          Future.failed(new NoSuchElementException(s"No Entity found with tag: Option[EntityTag]"))
      }
    }

  override def getById(e: EntityId): Future[Option[Entity]] =
    monitored("getById") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(_.id == lift(e))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def update(e: Entity): Future[Entity] =
    monitored("update") {
      if (e.id == EntityId(0L)) {
        insert(e)
      } else {
        val ts = TimeUtil.nowTimestamp()
        val updatedModel = e.copy(updatedAt = ts)

        write(updatedModel) {
          ctx
            .run(
              query[Entity]
                .filter(_.id == lift(e.id))
                .update(lift(updatedModel))
            )
            .runToFuture
        }.publishingUpdated((cur, prev, cid, time) => EntityUpdated(cur, prev, id = e.id, cid, time))
      }
    }

  override def upsert(e: Entity): Future[Entity] =
    monitored("upsert") {
      if (e.id == EntityId(0L)) {
        insert(e)
      } else {
        update(e)
      }
    }

  override def upsert(el: List[Entity]): Future[List[Entity]] = {
    Future.sequence(el.map { e => upsert(e) })
  }

  override def delete(e: Entity): Future[Entity] =
    monitored("delete") {
      val ts = TimeUtil.nowTimestamp()

      write(e) {
        ctx
          .run(
            query[Entity]
              .filter(_.id == lift(e.id))
              .delete
          )
          .runToFuture
      }.publishingDeleted((m, cid, time) => EntityDeleted(m, id = e.id, cid, time))
    }

  override def getByTag(tag: Option[EntityTag]): Future[Option[Entity]] =
    monitored("get-by-tag") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(e => e.tag == lift(tag))
              .take(1)
          )
          .runToFuture
          .map(_.headOption)
      }
    }

  override def getByEntityType(entityType: EntityType): Future[List[Entity]] =
    monitored("get-by-entity-type") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => r.entityType == lift(entityType))
          )
          .runToFuture
      }
    }

  override def getByEntityTypes(entityTypes: List[EntityType]): Future[List[Entity]] =
    monitored("get-by-entity-types") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => liftQuery(entityTypes).contains(r.entityType))
          )
          .runToFuture
      }
    }

  override def getByUserId(userId: UserId): Future[List[Entity]] =
    monitored("get-by-user-id") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => r.userId == lift(userId))
          )
          .runToFuture
      }
    }

  override def getByUserIds(userIds: List[UserId]): Future[List[Entity]] =
    monitored("get-by-user-ids") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => liftQuery(userIds).contains(r.userId))
          )
          .runToFuture
      }
    }

  override def getByParentEntityId(parentEntityId: EntityId): Future[List[Entity]] =
    monitored("get-by-parent-entity-id") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => r.parentEntityId.contains(lift(parentEntityId)))
          )
          .runToFuture
      }
    }

  override def getByParentEntityIds(parentEntityIds: List[EntityId]): Future[List[Entity]] =
    monitored("get-by-parent-entity-ids") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => r.parentEntityId.exists(v => liftQuery(parentEntityIds).contains(v)))
          )
          .runToFuture
      }
    }

  override def getByRefEntityType(refEntityType: Option[EntityType]): Future[List[Entity]] =
    monitored("get-by-ref-entity-type") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => r.refEntityType.contains(lift(refEntityType)))
          )
          .runToFuture
      }
    }

  override def getByRefEntityTypes(refEntityTypes: List[Option[EntityType]]): Future[List[Entity]] =
    monitored("get-by-ref-entity-types") {
      read {
        ctx
          .run(
            query[Entity]
              .filter(r => r.refEntityType.exists(v => liftQuery(refEntityTypes).contains(v)))
          )
          .runToFuture
      }
    }

  override def count: Future[Long] =
    monitored("count") {
      read {
        ctx.run(query[Entity].size).runToFuture
      }
    }

  override def getAll(): Future[List[Entity]] =
    monitored("get_all") {
      read {
        ctx
          .run(
            query[Entity]
          )
          .runToFuture
      }
    }

}
