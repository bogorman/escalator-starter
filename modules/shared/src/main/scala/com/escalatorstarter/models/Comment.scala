package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:169

case class CommentId(id: Long) extends AnyVal

case class Comment(
    id: CommentId,
    postId: PostId,
    active: Option[Boolean],
    content: Option[String],
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object Comment {

  def apply(postId: PostId, active: Option[Boolean], content: Option[String]): Comment = {
    Comment(
      CommentId(0L),
      postId,
      active,
      content,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
