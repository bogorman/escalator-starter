package com.escalatorstarter.persistence.postgres

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:073

import com.escalatorstarter.models._
import com.escalatorstarter.common.persistence.postgres.PostgresCommonEncoder

trait PostgresDbTypesEncoder extends PostgresCommonEncoder {

  protected lazy implicit val entityDecoder: ctx.Decoder[EntityType] = createAttributeTypeDecoder(EntityType.apply)
  protected lazy implicit val entityEncoder: ctx.Encoder[EntityType] = createAttributeTypeEncoder[EntityType]

  protected lazy implicit val workDecoder: ctx.Decoder[WorkType] = createAttributeTypeDecoder(WorkType.apply)
  protected lazy implicit val workEncoder: ctx.Encoder[WorkType] = createAttributeTypeEncoder[WorkType]

  protected lazy implicit val workstatusDecoder: ctx.Decoder[WorkStatusType] = createAttributeTypeDecoder(
    WorkStatusType.apply
  )

  protected lazy implicit val workstatusEncoder: ctx.Encoder[WorkStatusType] =
    createAttributeTypeEncoder[WorkStatusType]

}
