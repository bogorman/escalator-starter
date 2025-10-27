package com.escalatorstarter.persistence

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:485

import com.escalatorstarter.persistence.database.tables._

trait EscalatorStarterDatabase {
  def candles: CandlesTable
  def comments: CommentsTable
  def entities: EntitiesTable
  def posts: PostsTable
  def tokens: TokensTable
  def userSessions: UserSessionsTable
  def users: UsersTable
  def workQueues: WorkQueuesTable
}
