package com.escalatorstarter.persistence.postgres

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:30:983

import com.escalatorstarter.models._
import com.escalatorstarter.common.persistence.postgres.PostgresCommonEncoder

trait PostgresDbTypesEncoder extends PostgresCommonEncoder {

  protected lazy implicit val entityDecoder: ctx.Decoder[EntityType] = createAttributeTypeDecoder(EntityType.apply)
  protected lazy implicit val entityEncoder: ctx.Encoder[EntityType] = createAttributeTypeEncoder[EntityType]

  protected lazy implicit val userroleDecoder: ctx.Decoder[UserRoleType] = createAttributeTypeDecoder(
    UserRoleType.apply
  )

  protected lazy implicit val userroleEncoder: ctx.Encoder[UserRoleType] = createAttributeTypeEncoder[UserRoleType]

  protected lazy implicit val userstatusDecoder: ctx.Decoder[UserStatusType] = createAttributeTypeDecoder(
    UserStatusType.apply
  )

  protected lazy implicit val userstatusEncoder: ctx.Encoder[UserStatusType] =
    createAttributeTypeEncoder[UserStatusType]

  protected lazy implicit val usersessionDecoder: ctx.Decoder[UserSessionType] = createAttributeTypeDecoder(
    UserSessionType.apply
  )

  protected lazy implicit val usersessionEncoder: ctx.Encoder[UserSessionType] =
    createAttributeTypeEncoder[UserSessionType]

  protected lazy implicit val workDecoder: ctx.Decoder[WorkType] = createAttributeTypeDecoder(WorkType.apply)
  protected lazy implicit val workEncoder: ctx.Encoder[WorkType] = createAttributeTypeEncoder[WorkType]

  protected lazy implicit val workstatusDecoder: ctx.Decoder[WorkStatusType] = createAttributeTypeDecoder(
    WorkStatusType.apply
  )

  protected lazy implicit val workstatusEncoder: ctx.Encoder[WorkStatusType] =
    createAttributeTypeEncoder[WorkStatusType]

}
