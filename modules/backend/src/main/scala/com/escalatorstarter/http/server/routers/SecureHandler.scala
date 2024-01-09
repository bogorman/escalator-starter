package com.escalatorstarter.http.server

import org.apache.pekko.http.scaladsl.model.HttpMethods._
import org.apache.pekko.http.scaladsl.model.HttpResponse
import org.apache.pekko.http.scaladsl.model.headers._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.{Directive0, Route}

import com.escalatorstarter.http.server.auth._
// import com.escalatorstarter.http.server.services._

import org.apache.pekko.http.scaladsl.server.Directive1
import org.apache.pekko.http.scaladsl.server.directives.{ BasicDirectives, HeaderDirectives, RouteDirectives }
import org.apache.pekko.http.scaladsl.server._

trait SecureHandler extends SecureService {
  import SecureProtocol._

  // val sessionCache = WebAppSessionCache

  // val corsResponseHeaders = List(
  //   `Access-Control-Allow-Origin`.*,
  //   `Access-Control-Allow-Credentials`(true),
  //   `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
  // )

  // // , Account.licenseHeader

  // def corsHandler(route: Route): Route = addAccessControlHeaders { preflightRequestHandler ~ route }

  // def preflightRequestHandler: Route = options {
  //   complete(
  //     HttpResponse().withHeaders(
  //       `Access-Control-Allow-Methods`(POST, GET, PUT, DELETE, PATCH, OPTIONS)
  //     )
  //   )
  // }

  // def addCORSHeaders(response: HttpResponse): HttpResponse = response.withHeaders( corsResponseHeaders )

  // def addSecureControlHeaders: Directive0 = respondWithHeaders( corsResponseHeaders )

  def secureHandler(route: Route): Route = {
    // addAccessControlHeaders { preflightRequestHandler ~ route }

    route
  }


  def authenticate(): Directive1[SessionData] = {
    extractRequestContext.flatMap { ctx =>
      val sessionDataOpt = validateAuth(ctx)
      if (sessionDataOpt.isDefined) {
        provide(sessionDataOpt.get)
      } else {
        reject(WebAppAuthenticationRejection())
      }
    }
  }

}