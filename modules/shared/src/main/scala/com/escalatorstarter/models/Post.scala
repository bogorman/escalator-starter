package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:243

case class PostId(id: Long) extends AnyVal

case class Post(
    id: PostId,
    userId: UserId,
    active: Option[Boolean],
    content: Option[String],
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object Post {

  def apply(userId: UserId, active: Option[Boolean], content: Option[String]): Post = {
    Post(
      PostId(0L),
      userId,
      active,
      content,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
