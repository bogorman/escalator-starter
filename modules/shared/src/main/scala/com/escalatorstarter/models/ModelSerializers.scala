package com.escalatorstarter.models


// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:332
		        

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object ModelSerializers {

  implicit val codecTimestamp: Codec.AsObject[escalator.util.Timestamp] = deriveCodec[escalator.util.Timestamp]
  implicit val codecCorrelationId: Codec.AsObject[escalator.models.CorrelationId] = deriveCodec[escalator.models.CorrelationId]

  implicit val codecCandleId: Codec.AsObject[CandleId] = deriveCodec[CandleId]
  implicit val codecCommentId: Codec.AsObject[CommentId] = deriveCodec[CommentId]
  implicit val codecEntityId: Codec.AsObject[EntityId] = deriveCodec[EntityId]
  implicit val codecEntityTag: Codec.AsObject[EntityTag] = deriveCodec[EntityTag]
  implicit val codecPostId: Codec.AsObject[PostId] = deriveCodec[PostId]
  implicit val codecTokenId: Codec.AsObject[TokenId] = deriveCodec[TokenId]
  implicit val codecTokenAddress: Codec.AsObject[TokenAddress] = deriveCodec[TokenAddress]
  implicit val codecUserSessionId: Codec.AsObject[UserSessionId] = deriveCodec[UserSessionId]
  implicit val codecUserSessionSessionId: Codec.AsObject[UserSessionSessionId] = deriveCodec[UserSessionSessionId]
  implicit val codecUserId: Codec.AsObject[UserId] = deriveCodec[UserId]
  implicit val codecUserEmail: Codec.AsObject[UserEmail] = deriveCodec[UserEmail]
  implicit val codecUserResetPasswordToken: Codec.AsObject[UserResetPasswordToken] = deriveCodec[UserResetPasswordToken]
  implicit val codecUserConfirmationToken: Codec.AsObject[UserConfirmationToken] = deriveCodec[UserConfirmationToken]
  implicit val codecUserAccessToken: Codec.AsObject[UserAccessToken] = deriveCodec[UserAccessToken]
  implicit val codecWorkQueueId: Codec.AsObject[WorkQueueId] = deriveCodec[WorkQueueId]

  implicit val codecEntityType: Codec.AsObject[EntityType] = deriveCodec[EntityType]
  implicit val codecUserRoleType: Codec.AsObject[UserRoleType] = deriveCodec[UserRoleType]
  implicit val codecUserStatusType: Codec.AsObject[UserStatusType] = deriveCodec[UserStatusType]
  implicit val codecUserSessionType: Codec.AsObject[UserSessionType] = deriveCodec[UserSessionType]
  implicit val codecWorkType: Codec.AsObject[WorkType] = deriveCodec[WorkType]
  implicit val codecWorkStatusType: Codec.AsObject[WorkStatusType] = deriveCodec[WorkStatusType]
        
  implicit val codecCandle: Codec.AsObject[Candle] = deriveCodec[Candle]
  implicit val codecComment: Codec.AsObject[Comment] = deriveCodec[Comment]
  implicit val codecEntity: Codec.AsObject[Entity] = deriveCodec[Entity]
  implicit val codecPost: Codec.AsObject[Post] = deriveCodec[Post]
  implicit val codecToken: Codec.AsObject[Token] = deriveCodec[Token]
  implicit val codecUserSession: Codec.AsObject[UserSession] = deriveCodec[UserSession]
  implicit val codecUser: Codec.AsObject[User] = deriveCodec[User]
  implicit val codecWorkQueue: Codec.AsObject[WorkQueue] = deriveCodec[WorkQueue]

}
      