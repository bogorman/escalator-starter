package com.escalatorstarter.http.server.auth

import org.apache.pekko.http.scaladsl.server.RejectionHandler
// import org.apache.pekko.http.scaladsl.model.HttpResponse

import org.apache.pekko.http.scaladsl.model.StatusCodes.Found
// import org.apache.pekko.http.scaladsl.model.HttpHeaders.Location
import org.apache.pekko.http.scaladsl.model.HttpResponse
import org.apache.pekko.http.scaladsl.model.headers.Location
import org.apache.pekko.http.scaladsl.model.HttpHeader

import scala.concurrent.ExecutionContext

// import spray.http.StatusCodes.Found
// import spray.http.HttpHeaders.Location

// import spray.http.HttpResponse
// import spray.routing.RejectionHandler
// import spray.routing.HttpServiceActor
// extends HttpServiceActor

import org.apache.pekko.http.scaladsl.server.Directive1
import org.apache.pekko.http.scaladsl.server.directives.{ BasicDirectives, HeaderDirectives, RouteDirectives }


// import org.apache.pekko.http.scaladsl.server.RejectionHandler
// import org.apache.pekko.http.scaladsl.server.Rejection
import org.apache.pekko.http.scaladsl.server._
import com.escalatorstarter.http.server.auth._

// import concurrent.Future
// import scala.util.Success
// import scala.util.Failure
// import scala.concurrent.{ ExecutionContext, Future }

import org.apache.pekko.http.scaladsl.marshalling._

import org.apache.pekko.http.scaladsl.server.RequestContext
import org.apache.pekko.http.scaladsl.server.{ Directives, Route }
import org.apache.pekko.http.scaladsl.model.{ ContentTypes, HttpEntity }
import org.apache.pekko.http.scaladsl.model.headers._
import org.apache.pekko.http.scaladsl.model._
import MediaTypes._

object SecureProtocol {
  case class MissingSessionCookieRejection() extends Rejection
  case class WebAppAuthenticationRejection() extends Rejection
}

trait SecureService extends Secure {
  import BasicDirectives._
  import HeaderDirectives._
  import RouteDirectives._
  import SecureProtocol._

  // implicit def rejectionHandler: RejectionHandler

  // implicit def ec: ExecutionContext = actorRefFactory.dispatcher

  // override implicit val rejectionHandler: RejectionHandler = RejectionHandler {
  //   case MissingSessionCookieRejection() :: _ ⇒ complete(redirectToRoute("/logout"))
  //   case WebAppAuthenticationRejection() :: _ ⇒ complete(redirectToRoute("/logout"))
  // }

  // implicit val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder()
  implicit def rejectionHandler = RejectionHandler.newBuilder()
    // .handleNotFound {
    //   println("Rejection NOT FOUND to logout")
    //   complete(redirectToRoute("/logout"))
    // }
    .handleAll[MethodRejection] { _ ⇒
      {
        println("MethodRejection redirectToRoute to logout")
        complete(redirectToRoute("/logout"))
      }
    }.handleAll[Rejection] { _ ⇒
      {
        println("Rejection redirectToRoute to logout")
        complete(redirectToRoute("/logout"))
      }
    }.result()

  protected def authenticator() = {
    // new UserAuthenticator()
    true
  }

  // private def bmProps = BMConfig.getProperties

  //settings wrapper!
  private def getSetting(key: String,defaultValue: String): String = {
    defaultValue
  } 

  private def getBooleanSetting(key: String,defaultValue: Boolean): Boolean = {
    defaultValue
  } 


  protected def redirectToRoute(route: String): HttpResponse = {
    println("redirectToRoute:" + route)
    HttpResponse(status = Found, headers = List(org.apache.pekko.http.scaladsl.model.headers.Location(route)))
  }

  private def authBypassCheck: Option[SessionData] = {
    // Option[SessionData]
    Some(SessionData("-1", "Auth disabled!", None))
  }

  private def authTokenCheck(authTokenValue: String): Option[SessionData] = {
    val apiFullAccessToken = getSetting("apiFullAccessToken", "9CB7F2FEF8EE4D1CBE955F0A24E365E7")
    val apiPriceAccessToken = getSetting("apiPriceAccessToken", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")

    if (authTokenValue == apiFullAccessToken) {
      Some(SessionData("0", "API Token Full Access User", None))
    } else if (authTokenValue == apiPriceAccessToken) {
      Some(SessionData("1", "API Token Price Access User", None))
    } else {
      None
    }
  }

  private def sessionCheck(cookieOpt: Option[SessionCookie]): Option[SessionData] = {
    val sessionCache = WebAppSessionCache
    cookieOpt match {
      case Some(cookie) ⇒ sessionCache.getSession(cookie.value)
      case _            ⇒ None
    }
  }

  private def findExistingSession(ctx: RequestContext): Option[SessionCookie] = {
    // None

    // println("validateAuth start")
    // val headers = ctx.request.headers
    // println(headers)

    val cookieHeaders = ctx.request.headers.filter(_.is("cookie")).toList
    // val cookieHeaders = ctx.request.headers.cookies //filter(_.is("cookie"))
    // println(cookieHeaders)
    // cookieHeaders.foreach { ch =>
    //   println("ch.name:" + ch.name)
    //   println("ch.value:" + ch.value)
    // }

    val pairs = cookiePairs(cookieHeaders)
    // println(pairs)
    // println("validateAuth end")

    pairs.find(_.name == SessionCookie.cookieName).map(SessionCookie(_))
  }

  protected def cookiePairs(headers: List[HttpHeader]) = {
    for (`Cookie`(cookies) ← headers; cookie ← cookies) yield cookie
  }

  protected def validateAuth(ctx: RequestContext): Option[SessionData] = {
    lazy val bypassAuth = getBooleanSetting("bypassWebAuthentication", false)
    lazy val authTokenValue = ctx.request.uri.query().get("access_token")

    if (bypassAuth) {
      authBypassCheck
    } else if (authTokenValue.isDefined) {
      authTokenCheck(authTokenValue.get)
    } else { //session/
      val sessionOpt = findExistingSession(ctx)
      sessionCheck(sessionOpt)
    }
  }

  // authenticator: UserAuthenticator

  def authenticate(xxxxxx: Boolean): Directive1[SessionData] = {
    // println("authenticate rejecting")
    //TODO: FIX THIS

    // headerValueByName("Token").flatMap { token =>
    //   onSuccess(AuthService.authenticate(token)).flatMap {
    //     case Some(user) => provide(user)
    //     case None       => reject
    //   }
    // }
    // if (authenticator.valid){
    //   provide(user)
    // } else {
    // reject(WebAppAuthenticationRejection())
    // }

    extractRequestContext.flatMap { ctx =>
      val sessionDataOpt = validateAuth(ctx)
      if (sessionDataOpt.isDefined) {
        provide(sessionDataOpt.get)
      } else {
        reject(WebAppAuthenticationRejection())
      }
    }

    // extractRequestContext.flatMap { ctx =>

    // authenticator.valid(ctx).onComplete {
    //   case Success(auth) => auth match {
    //     case Right(x) => {
    //       println("RIGHT")
    //       println("Future Rejecting")
    //       reject(WebAppAuthenticationRejection())
    //     }
    //     case Left(x) => {
    //       println("LEFT")
    //       println("Future Rejecting")
    //       reject(WebAppAuthenticationRejection())
    //     }
    //   }
    //   case Failure(exception) => {
    //     println("Future Failure Rejecting")
    //     reject(WebAppAuthenticationRejection())
    //   }
    // }

    // authenticator.valid(ctx).map[ToResponseMarshallable] {
    //   case Right(sessionData) => {
    //     provide(sessionData)
    //   }
    //   case Left(errorMessage) => {
    //     reject(WebAppAuthenticationRejection())
    //   }
    // }

    // val resf: Future[Either[Rejection, SessionData]] = authenticator.valid(ctx)
    // resf.onComplete {
    //   case Success(value) => {
    //     // reject(WebAppAuthenticationRejection())
    //     value match {
    //       case Right(x) => {
    //         println("RIGHT")
    //         println("Future Rejecting")
    //         reject(WebAppAuthenticationRejection())
    //       }
    //       case Left(x) => {
    //         println("LEFT")
    //         println("Future Rejecting")
    //         reject(WebAppAuthenticationRejection())
    //       }
    //     }
    //   }
    //   case Failure(e) => {
    //     reject(WebAppAuthenticationRejection())
    //   }
    // }

    // reject(WebAppAuthenticationRejection())

    // case Right(sessionData) => {
    //   provide(sessionData)
    // }
    // case Left(error) => {
    //   reject(WebAppAuthenticationRejection())
    // }

    // reject(WebAppAuthenticationRejection())
    // }

  }

}