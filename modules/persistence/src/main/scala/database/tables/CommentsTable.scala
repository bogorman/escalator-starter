package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:752

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

  def getByIds(c: List[CommentId]): Future[List[Comment]]

  def update(c: Comment): Future[Comment]

  def upsert(c: Comment): Future[Comment]

  def upsert(cl: List[Comment]): Future[List[Comment]]

  def delete(c: Comment): Future[Comment]

  def getListByPostId(postId: PostId): Future[List[Comment]]

  def getListByPostIds(postIds: List[PostId]): Future[List[Comment]]

  def count: Future[Long]

  def getAll(): Future[List[Comment]]
}
