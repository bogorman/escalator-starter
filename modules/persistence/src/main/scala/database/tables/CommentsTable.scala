package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:892

import scala.concurrent.Future

import com.escalatorstarter.models._

trait CommentsTable {
  def tableName = "comments"

  //def store(c: Comment): Future[Comment]

  //def store(cl: List[Comment]): Future[List[Comment]]

  //def insert(c: Comment): Future[_]

  def updatePostIdById(id: CommentId, postId: PostId): Future[Comment]

  def updateActiveById(id: CommentId, active: Option[Boolean]): Future[Comment]

  def updateContentById(id: CommentId, content: Option[String]): Future[Comment]

  def getById(c: CommentId): Future[Option[Comment]]

  def update(c: Comment): Future[Comment]

  def upsert(c: Comment): Future[Comment]

  def upsert(cl: List[Comment]): Future[List[Comment]]

  def delete(c: Comment): Future[Comment]

  def getByPostId(postId: PostId): Future[List[Comment]]

  def getByPostIds(postIds: List[PostId]): Future[List[Comment]]

  def count: Future[Long]

  def getAll(): Future[List[Comment]]
}
