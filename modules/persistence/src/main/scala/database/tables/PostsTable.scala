package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:11:539

import scala.concurrent.Future

import com.escalatorstarter.models._

trait PostsTable {
  def tableName = "posts"

  //def store(p: Post): Future[Post]

  //def store(pl: List[Post]): Future[List[Post]]

  //def insert(p: Post): Future[_]

  def updateUserIdById(id: PostId, userId: UserId): Future[Post]

  def updateActiveById(id: PostId, active: Option[Boolean]): Future[Post]

  def updateContentById(id: PostId, content: Option[String]): Future[Post]

  def getById(p: PostId): Future[Option[Post]]

  def update(p: Post): Future[Post]

  def upsert(p: Post): Future[Post]

  def upsert(pl: List[Post]): Future[List[Post]]

  def delete(p: Post): Future[Post]

  def getByUserId(userId: UserId): Future[List[Post]]

  def getByUserIds(userIds: List[UserId]): Future[List[Post]]

  def count: Future[Long]

  def getAll(): Future[List[Post]]
}
