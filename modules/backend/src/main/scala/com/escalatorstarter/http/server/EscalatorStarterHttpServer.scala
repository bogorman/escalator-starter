package com.escalatorstarter.http.server

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer

import escalator.websocket.Http

import com.escalatorstarter.http.server.controllers.AdminController.AdminAuthentication
import com.escalatorstarter.core.repositories.EscalatorStarterRepository

// import com.escalatorstarter.backend.http.server.controllers._

import escalator.util.logging.Logger
import escalator.util.monitoring.Monitoring

// import com.escalatorstarter.core.exchanges._
// import com.escalatorstarter.core.sessions._

import escalator.util._
import com.escalatorstarter.util._

import com.escalatorstarter.http.server.controllers._
import com.escalatorstarter.shared.api._



// import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
// import io.circe.Json
// import io.circe.Decoder
// import io.circe.Encoder
// import io.circe.generic.auto._
// import io.circe.parser._
// import io.circe.syntax._

object EscalatorStarterHttpServer {
  def start(
    auth: AdminAuthentication
  )(implicit
    repository: EscalatorStarterRepository,
    executionContext: ExecutionContext,
    http: Http,
    logger: Logger,
    system: ActorSystem,
    materializer: Materializer,
    monitoring: Monitoring,
    // exchangeCatalogue: ExchangeCatalogue,
    // sessionHub: SessionHub,
    timestampProvider: TimestampProvider    
  ): Future[ServerBinding] = {
   val bindingFuture =
      Http()
        .newServerAt(
          "0.0.0.0", //take from config
          30099 //take from config
        )
        .bind(route(auth))

    bindingFuture.failed.foreach { ex =>
      // logger.error(s"bind failed: ", ex)
    }
    bindingFuture.foreach { binding =>
      // logger.info(s"bound: $binding")
    }

    bindingFuture
  }

  // val cryptoConsoleHtml: String = Source.fromResource("web/escalatorstarter.html").mkString
  //   .replace("${JS_VERSION}", com.escalatorstarter.Conventions.version.toString)  

  // val chartsResponse = HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, cryptoConsoleHtml))

  // val rpcController: SharedApiController = SharedApiController()

  // def rpcPublicRoutes(rpcController: SharedApiController): Route = {
  //   // import monix.execution.Scheduler.Implicits.global
  //   implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  //   AutowireAkkaHttpRoute("rpc", _.route[SharedApi](rpcController))
  // }


  def route(
    auth: AdminAuthentication
  )(implicit
    repository: EscalatorStarterRepository,
    executionContext: ExecutionContext,
    http: Http,
    logger: Logger,
    materializer: Materializer,
    monitoring: Monitoring,
    // exchangeCatalogue: ExchangeCatalogue,
    // sessionHub: SessionHub,
    timestampProvider: TimestampProvider       
  ): Route = {

    // pathSingleSlash(get(redirect(Uri./.withPath(Path("charts/")), StatusCodes.TemporaryRedirect))) ~
      // path("charts"./)(get(complete(chartsResponse))) ~
      // path("frontend.js")(get(getFromResource("frontend-opt.js"))) ~
      // pathPrefix("img")(get(getFromResourceDirectory("web/img"))) ~
      val routes = new EscalatorStarterRoutes()
      routes.route
      // val rpcController: SharedApiController = SharedApiController()

      // FeedWebsocketController.controller ~
      // routes.route
       // ~ 
       // rpcPublicRoutes(rpcController)


      
       // ~
      // AdminController.controller(auth)

      // path("test") {
    	// 	get {
    	// 		complete("ok")	
    	// 	}
      // }

      // 
  }
}
