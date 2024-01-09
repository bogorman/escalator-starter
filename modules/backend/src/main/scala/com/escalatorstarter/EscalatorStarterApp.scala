package com.escalatorstarter

import java.time.Clock
import scala.concurrent.{Await, Future}
import scala.util.Try

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.ActorMaterializer

import com.typesafe.config.{Config, ConfigFactory}

// import cats.implicits._
import monix.execution.Scheduler

import escalator.util.Configuration
import escalator.util.akka.AkkaActorSystem
import escalator.util.akka.streams.AkkaStreamsMaterializer
import escalator.util.logging.Slf4jLogger
import escalator.util.monitoring.KamonMonitoring

import com.escalatorstarter.core.repositories.EscalatorStarterRepository

import com.escalatorstarter.http.server.controllers.AdminController.AdminAuthentication
import com.escalatorstarter.http.server.EscalatorStarterHttpServer

import escalator.websocket.PekkoHTTP
import escalator.util._

import com.escalatorstarter.util._
import scala.concurrent.duration._


object EscalatorStarterApp {
  implicit val monitoring: KamonMonitoring = new KamonMonitoring
  implicit val logger: Slf4jLogger = new Slf4jLogger("escalatorstarter-logger")

  implicit val actorSystem: ActorSystem = AkkaActorSystem.create("escalatorstarter")
  implicit val materializer: ActorMaterializer = AkkaStreamsMaterializer()
  implicit val scheduler: Scheduler = Scheduler(actorSystem.dispatcher)

  implicit val config: Config = ConfigFactory.load()
  implicit val http: PekkoHTTP = new PekkoHTTP

  implicit val timestampProvider: TimestampProvider = new TimestampProvider()(Clock.systemUTC())

  implicit val repository = new EscalatorStarterRepository(null)

  def main(args: Array[String]): Unit = {
    println("EscalatorStarter App Started")

    val auth = Configuration.fetch[AdminAuthentication]("escalatorstarter.admin").right.get

    EscalatorStarterHttpServer.start(auth)

    EscalatorStarterReplServer.start()

    ()
  }
}
