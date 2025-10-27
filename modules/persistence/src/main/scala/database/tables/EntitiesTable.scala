package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:854

import scala.concurrent.Future

import com.escalatorstarter.models._

trait EntitiesTable {
  def tableName = "entities"

  //def store(e: Entity): Future[Entity]

  //def store(el: List[Entity]): Future[List[Entity]]

  //def insert(e: Entity): Future[_]

  def upsertOnTag(e: Entity): Future[Entity]

  def existsOnTag(e: Entity): Future[Boolean]

  def updateKeywordById(id: EntityId, keywords: Option[List[String]]): Future[Entity]

  def updateEntityTypeById(id: EntityId, entityType: EntityType): Future[Entity]

  def updateEntityObjectIdById(id: EntityId, entityObjectId: Option[Long]): Future[Entity]

  def updateUserIdById(id: EntityId, userId: UserId): Future[Entity]

  def updateUrlById(id: EntityId, url: Option[String]): Future[Entity]

  def updateContentById(id: EntityId, content: Option[String]): Future[Entity]

  def updateDomainById(id: EntityId, domains: Option[List[String]]): Future[Entity]

  def updateOriginalImageById(id: EntityId, originalImage: Option[String]): Future[Entity]

  def updateCroppedImageById(id: EntityId, croppedImage: Option[String]): Future[Entity]

  def updateMiniImageById(id: EntityId, miniImage: Option[String]): Future[Entity]

  def updateCroppedCoverImageById(id: EntityId, croppedCoverImage: Option[String]): Future[Entity]

  def updateBlacklistedById(id: EntityId, blacklisted: Option[Boolean]): Future[Entity]

  def updateWeightingById(id: EntityId, weighting: Option[Double]): Future[Entity]

  def updateParentEntityIdById(id: EntityId, parentEntityId: Option[EntityId]): Future[Entity]

  def updateWduById(id: EntityId, wdu: Option[String]): Future[Entity]

  def updateExternalEntityIdById(id: EntityId, externalEntityId: Option[String]): Future[Entity]

  def updatePrecreatedById(id: EntityId, precreated: Option[Boolean]): Future[Entity]

  def updateVerifiedById(id: EntityId, verified: Option[Boolean]): Future[Entity]

  def updateClaimedById(id: EntityId, claimed: Option[Boolean]): Future[Entity]

  def updateClaimedAtById(id: EntityId, claimedAt: Option[escalator.util.Timestamp]): Future[Entity]

  def updateActiveById(id: EntityId, active: Option[Boolean]): Future[Entity]

  def updateSettingById(id: EntityId, settings: io.circe.Json): Future[Entity]

  def updatePersonalById(id: EntityId, personal: io.circe.Json): Future[Entity]

  def updateProfessionalById(id: EntityId, professional: io.circe.Json): Future[Entity]

  def updateSublineById(id: EntityId, subline: Option[String]): Future[Entity]

  def updateCountryCodeById(id: EntityId, countryCode: Option[String]): Future[Entity]

  def updateHashTagById(id: EntityId, hashTags: Option[List[String]]): Future[Entity]

  def updateSearchKeywordById(id: EntityId, searchKeywords: Option[List[String]]): Future[Entity]

  def updateOriginalCoverImageById(id: EntityId, originalCoverImage: Option[String]): Future[Entity]

  def updateCreatedByById(id: EntityId, createdBy: Option[Long]): Future[Entity]

  def updateValidationKeywordById(id: EntityId, validationKeywords: Option[List[String]]): Future[Entity]

  def updateNewsTopicById(id: EntityId, newsTopics: Option[List[String]]): Future[Entity]

  def updateNewsVerificationRequiredById(id: EntityId, newsVerificationRequired: Option[Boolean]): Future[Entity]

  def updateRejectionKeywordById(id: EntityId, rejectionKeywords: Option[List[String]]): Future[Entity]

  def updateRefEntityTypeById(id: EntityId, refEntityType: Option[EntityType]): Future[Entity]

  def updateKeywordByTag(tag: Option[EntityTag], keywords: Option[List[String]]): Future[_]

  def updateEntityTypeByTag(tag: Option[EntityTag], entityType: EntityType): Future[_]

  def updateEntityObjectIdByTag(tag: Option[EntityTag], entityObjectId: Option[Long]): Future[_]

  def updateUserIdByTag(tag: Option[EntityTag], userId: UserId): Future[_]

  def updateUrlByTag(tag: Option[EntityTag], url: Option[String]): Future[_]

  def updateContentByTag(tag: Option[EntityTag], content: Option[String]): Future[_]

  def updateDomainByTag(tag: Option[EntityTag], domains: Option[List[String]]): Future[_]

  def updateOriginalImageByTag(tag: Option[EntityTag], originalImage: Option[String]): Future[_]

  def updateCroppedImageByTag(tag: Option[EntityTag], croppedImage: Option[String]): Future[_]

  def updateMiniImageByTag(tag: Option[EntityTag], miniImage: Option[String]): Future[_]

  def updateCroppedCoverImageByTag(tag: Option[EntityTag], croppedCoverImage: Option[String]): Future[_]

  def updateBlacklistedByTag(tag: Option[EntityTag], blacklisted: Option[Boolean]): Future[_]

  def updateWeightingByTag(tag: Option[EntityTag], weighting: Option[Double]): Future[_]

  def updateParentEntityIdByTag(tag: Option[EntityTag], parentEntityId: Option[EntityId]): Future[_]

  def updateWduByTag(tag: Option[EntityTag], wdu: Option[String]): Future[_]

  def updateExternalEntityIdByTag(tag: Option[EntityTag], externalEntityId: Option[String]): Future[_]

  def updatePrecreatedByTag(tag: Option[EntityTag], precreated: Option[Boolean]): Future[_]

  def updateVerifiedByTag(tag: Option[EntityTag], verified: Option[Boolean]): Future[_]

  def updateClaimedByTag(tag: Option[EntityTag], claimed: Option[Boolean]): Future[_]

  def updateClaimedAtByTag(tag: Option[EntityTag], claimedAt: Option[escalator.util.Timestamp]): Future[_]

  def updateActiveByTag(tag: Option[EntityTag], active: Option[Boolean]): Future[_]

  def updateSettingByTag(tag: Option[EntityTag], settings: io.circe.Json): Future[_]

  def updatePersonalByTag(tag: Option[EntityTag], personal: io.circe.Json): Future[_]

  def updateProfessionalByTag(tag: Option[EntityTag], professional: io.circe.Json): Future[_]

  def updateSublineByTag(tag: Option[EntityTag], subline: Option[String]): Future[_]

  def updateCountryCodeByTag(tag: Option[EntityTag], countryCode: Option[String]): Future[_]

  def updateHashTagByTag(tag: Option[EntityTag], hashTags: Option[List[String]]): Future[_]

  def updateSearchKeywordByTag(tag: Option[EntityTag], searchKeywords: Option[List[String]]): Future[_]

  def updateOriginalCoverImageByTag(tag: Option[EntityTag], originalCoverImage: Option[String]): Future[_]

  def updateCreatedByByTag(tag: Option[EntityTag], createdBy: Option[Long]): Future[_]

  def updateValidationKeywordByTag(tag: Option[EntityTag], validationKeywords: Option[List[String]]): Future[_]

  def updateNewsTopicByTag(tag: Option[EntityTag], newsTopics: Option[List[String]]): Future[_]

  def updateNewsVerificationRequiredByTag(tag: Option[EntityTag], newsVerificationRequired: Option[Boolean]): Future[_]

  def updateRejectionKeywordByTag(tag: Option[EntityTag], rejectionKeywords: Option[List[String]]): Future[_]

  def updateRefEntityTypeByTag(tag: Option[EntityTag], refEntityType: Option[EntityType]): Future[_]

  def getById(e: EntityId): Future[Option[Entity]]

  def getByIds(e: List[EntityId]): Future[List[Entity]]

  def update(e: Entity): Future[Entity]

  def upsert(e: Entity): Future[Entity]

  def upsert(el: List[Entity]): Future[List[Entity]]

  def delete(e: Entity): Future[Entity]

  def getByTag(tag: Option[EntityTag]): Future[Option[Entity]]

  def getListByEntityType(entityType: EntityType): Future[List[Entity]]

  def getListByEntityTypes(entityTypes: List[EntityType]): Future[List[Entity]]
  def getListByUserId(userId: UserId): Future[List[Entity]]

  def getListByUserIds(userIds: List[UserId]): Future[List[Entity]]
  def getListByParentEntityId(parentEntityId: EntityId): Future[List[Entity]]

  def getListByParentEntityIds(parentEntityIds: List[EntityId]): Future[List[Entity]]
  def getListByRefEntityType(refEntityType: Option[EntityType]): Future[List[Entity]]

  def getListByRefEntityTypes(refEntityTypes: List[Option[EntityType]]): Future[List[Entity]]

  def count: Future[Long]

  def getAll(): Future[List[Entity]]
}
