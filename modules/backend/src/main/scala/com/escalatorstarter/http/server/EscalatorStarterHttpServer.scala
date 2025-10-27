package com.escalatorstarter.http.server

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.apache.pekko.http.scaladsl.model.Uri.Path
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.stream.Materializer

import escalator.websocket.Http

import com.escalatorstarter.http.server.controllers.AdminController.AdminAuthentication
import com.escalatorstarter.core.repositories.EscalatorStarterRepository
import com.escalatorstarter.http.server.auth.SessionCache

// import com.escalatorstarter.backend.http.server.controllers._

import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

import escalator.util._
import com.escalatorstarter.util._

import com.escalatorstarter.http.server.controllers._
import com.escalatorstarter.shared.api._

import pureconfig._
import pureconfig.generic.auto._
import escalator.util.Configuration
import com.typesafe.config.Config

object EscalatorStarterHttpServer {
  def start(
    auth: AdminAuthentication,
    sessionCache: SessionCache
  )(implicit
    repository: EscalatorStarterRepository,
    executionContext: ExecutionContext,
    http: Http,
    logger: Logger,
    system: ActorSystem,
    materializer: Materializer,
    monitoring: Monitoring,

      timestampProvider: TimestampProvider,
      config: Config
  ): Future[ServerBinding] = {
   val bindingFuture =
      Http()
        .newServerAt(
          "0.0.0.0", //take from config
          30099 //take from config
        )
        .bind(route(auth,sessionCache))

    bindingFuture.failed.foreach { ex =>
      // logger.error(s"bind failed: ", ex)
    }
    bindingFuture.foreach { binding =>
      // logger.info(s"bound: $binding")
    }

    bindingFuture
  }

  def route(
    auth: AdminAuthentication,
    sessionCache: SessionCache
  )(implicit
    repository: EscalatorStarterRepository,
    executionContext: ExecutionContext,
    http: Http,
    logger: Logger,
    materializer: Materializer,
    monitoring: Monitoring,

      timestampProvider: TimestampProvider,
      config: Config
  ): Route = {

      val routes = new EscalatorStarterRoutes(sessionCache)
      routes.route

  }
}
