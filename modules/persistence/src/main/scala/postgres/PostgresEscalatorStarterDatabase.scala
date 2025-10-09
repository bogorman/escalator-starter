package com.escalatorstarter.persistence.postgres

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:619

import io.getquill._
import escalator.util.logging.Logger

import escalator.util.monitoring.Monitoring
import escalator.util.events.EventBus

import monix.execution.Scheduler
import com.escalatorstarter.persistence.EscalatorStarterDatabase

import escalator.util.postgres.PostgresDatabase
import escalator.util.postgres.PostgresDatabase.PostgresDatabaseConfiguration
import escalator.util.postgres.CustomNamingStrategy

import com.escalatorstarter.persistence.postgres.PostgresCustomEncoder
import com.escalatorstarter.common.persistence.postgres.PostgresMappedEncoder

import com.escalatorstarter.persistence.postgres.tables._
import com.escalatorstarter.persistence._

class PostgresEscalatorStarterDatabase(
    config: PostgresDatabaseConfiguration
)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends PostgresDatabase(
      new PostgresMonixJdbcContext(CustomNamingStrategy, "postgres")
    )
    with EscalatorStarterDatabase {

  ///
  object candles extends PostgresCandlesTable(this) with PostgresCustomEncoder
  object comments extends PostgresCommentsTable(this) with PostgresCustomEncoder
  object entities extends PostgresEntitiesTable(this) with PostgresCustomEncoder
  object posts extends PostgresPostsTable(this) with PostgresCustomEncoder
  object tokens extends PostgresTokensTable(this) with PostgresCustomEncoder
  object users extends PostgresUsersTable(this) with PostgresCustomEncoder
  object workQueues extends PostgresWorkQueuesTable(this) with PostgresCustomEncoder

  /// include these in your repository
  //	object candles extends CandlesRepository(this)
  //	object comments extends CommentsRepository(this)
  //	object entities extends EntitiesRepository(this)
  //	object posts extends PostsRepository(this)
  //	object tokens extends TokensRepository(this)
  //	object users extends UsersRepository(this)
  //	object workQueues extends WorkQueuesRepository(this)
}
