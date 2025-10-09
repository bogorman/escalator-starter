package com.escalatorstarter.persistence

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:618

import com.escalatorstarter.persistence.database.tables._

trait EscalatorStarterDatabase {
  def candles: CandlesTable
  def comments: CommentsTable
  def entities: EntitiesTable
  def posts: PostsTable
  def tokens: TokensTable
  def users: UsersTable
  def workQueues: WorkQueuesTable
}
