package com.escalatorstarter.core.repositories

import io.getquill._

import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

import monix.execution.Scheduler

import com.escalatorstarter.persistence.EscalatorStarterDatabase
import com.escalatorstarter.common.persistence.postgres.PostgresDatabase
import com.escalatorstarter.common.persistence.postgres.CustomNamingStrategy
import com.escalatorstarter.common.persistence.postgres.PostgresDatabase.PostgresDatabaseConfiguration
import com.escalatorstarter.common.persistence.postgres.PostgresCustomEncoder

import com.escalatorstarter.persistence.postgres.tables._

//IF YOU WANT TO HAVE MULTIPLE DATA SOURCES

class EscalatorStarterRepository(
    config: PostgresDatabaseConfiguration
)(implicit
    logger: Logger,
    monitoring: Monitoring //,
) extends PostgresDatabase(
      new PostgresMonixJdbcContext(CustomNamingStrategy, "postgres")
    )
    with EscalatorStarterDatabase {

    // PostgresEscalatorStarterDatabase - take the references from here
  object attributes extends PostgresAttributesTable(this) with PostgresCustomEncoder
  object users extends PostgresUsersTable(this) with PostgresCustomEncoder
  object workQueues extends PostgresWorkQueuesTable(this) with PostgresCustomEncoder

  object candles extends PostgresCandlesTable(this) with PostgresCustomEncoder
  object events extends PostgresEventsTable(this) with PostgresCustomEncoder  
}
