package com.escalatorstarter.core.repositories

import io.getquill._

import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

import monix.execution.Scheduler

import escalator.util.events.EventBus

import com.escalatorstarter.persistence.EscalatorStarterDatabase
import escalator.util.postgres.PostgresDatabase
import escalator.util.postgres.CustomNamingStrategy

import escalator.util.postgres.PostgresDatabase.PostgresDatabaseConfiguration
import com.escalatorstarter.persistence.postgres.PostgresCustomEncoder

import com.escalatorstarter.persistence.postgres.tables._
import com.escalatorstarter.core.repositories.postgres._

//IF YOU WANT TO HAVE MULTIPLE DATA SOURCES

class EscalatorStarterRepository(
    config: PostgresDatabaseConfiguration
)(implicit
    logger: Logger,
    monitoring: Monitoring,
    eventBus: EventBus
) extends PostgresDatabase(
      new PostgresMonixJdbcContext(CustomNamingStrategy, "postgres")
    )
    with EscalatorStarterDatabase {

    // PostgresEscalatorStarterDatabase - take the references from here
  // object attributes extends PostgresAttributesTable(this) with PostgresCustomEncoder
  // object users extends PostgresUsersTable(this) with PostgresCustomEncoder
  // object workQueues extends PostgresWorkQueuesTable(this) with PostgresCustomEncoder

  // object candles extends PostgresCandlesTable(this) with PostgresCustomEncoder
  // object events extends PostgresEventsTable(this) with PostgresCustomEncoder  

     object candles extends CandlesRepository(this)
     object comments extends CommentsRepository(this)
     object entities extends EntitiesRepository(this)
     object posts extends PostsRepository(this)
     object tokens extends TokensRepository(this)
     object users extends UsersRepository(this)
     object userSessions extends UserSessionsRepository(this)
     object workQueues extends WorkQueuesRepository(this)
}