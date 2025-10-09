package com.escalatorstarter.core.repositories.postgres

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:12:298

import scala.concurrent.Future

import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

import escalator.util.postgres.PostgresDatabase
import escalator.util.events.EventBus
import com.escalatorstarter.common.persistence.postgres.PostgresMappedEncoder

import com.escalatorstarter.models._
import com.escalatorstarter.persistence.database.tables.TokensTable
import com.escalatorstarter.persistence.postgres.tables.PostgresTokensTable

class TokensRepository(database: PostgresDatabase)(implicit logger: Logger, monitoring: Monitoring, eventBus: EventBus)
    extends PostgresTokensTable(database) {

  // Override methods here to add custom repository logic
  // This class inherits all standard CRUD operations from PostgresTokensTable

  import PostgresMappedEncoder._
  import monix.execution.Scheduler.Implicits.global
  import ctx._

}
