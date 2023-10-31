package com.escalatorstarter.http.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes.{ `application/json`, `text/plain(UTF-8)` }

// import starter.data.Error
// import starter.data.PostRepr
// import starter.http.RoutesBase

import scala.util.Random
import scala.util.control.NonFatal
import unindent._

import java.time.Instant

// import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK, Unauthorized}

import models.users._
import com.escalatorstarter.models._

import io.circe.generic.auto._

import com.escalatorstarter.core.repositories.EscalatorStarterRepository

import escalator.util.auth.PasswordUtil
import escalator.util.TextUtil

import escalator.errors._
import com.escalatorstarter.errors._

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration

import monix.eval.Task

import escalator.errors.BackendError._

import cats.implicits._

import com.escalatorstarter.http.server.controllers._
import com.escalatorstarter.http.server.auth._

import com.escalatorstarter.shared.api._
import com.escalatorstarter.http.akka._

import escalator.common.util.debug.StackTrace

// import concurrent.Future
// import scala.util.Success
// import scala.util.Failure


class EscalatorStarterRoutes()(implicit repository: EscalatorStarterRepository) extends RoutesBase with CorsHandler with SecureHandler {
  
  import monix.execution.Scheduler.Implicits.global
  import escalator.util.monix.TaskSyntax._

  import EscalatorStarterBackendError._

  def route: Route = {
    // corsHandler( allRoutes )
    TraceDirectives.log(allRoutes)
  }

  def allRoutes: Route = {
    (handleRejections(apiRejectionHandler) & handleExceptions(apiExceptionHandler)) {
      apiPublicRoutes ~
      rpcPublicRoutes ~
      userPublicRoutes ~
      secureHandler( apiSecureRoutes )      
    }
  }

  val rpcController: SharedApiController = SharedApiController()

  def rpcPublicRoutes(): Route = {
    AutowireAkkaHttpRoute("rpc", _.route[SharedApi](rpcController))
  }

  def userPublicRoutes(): Route = {
    pathPrefix("api") {
      path("login") {
          post {
            entity(as[LoginUser]) { loginUser =>
              // onSuccess(repository.users.store(newUserToUser(newUser))) { user =>
                  // complete(StatusCodes.OK -> user)
              // }
              println("LOGIN")
              println("email: '" + loginUser.email + "'")
              println("password: '" + loginUser.password + "'")

              val encrypedPass = PasswordUtil.encrypt(loginUser.password,PEPPER)
              
              println("encryptedPass:" + encrypedPass)
              // PasswordUtil.encrypt(newUser.password,pepper)

              // PasswordUtils.matches(password, PEPPER, users(0).getEncryptedPassword)

              val t: Task[Either[BackendError,User]] = for {
                userOpt <- Task.fromFuture(repository.users.getByEmail(UserEmail(loginUser.email.toLowerCase)))
                r <- if (userOpt.isEmpty) {
                  Task.pure(EmailNotFound(loginUser.email.toLowerCase).asLeft[User])
                // } else if (userOpt.get.encryptedPassword != encrypedPass) {
                } else if (PasswordUtil.matches(loginUser.password, PEPPER, userOpt.get.encryptedPassword)) {
                  Task.pure(userOpt.get.asRight[BackendError])
                } else {
                  Task.pure(PasswordIncorrect(loginUser.email.toLowerCase).asLeft[User])                  
                }
              } yield (r)

              // complete(StatusCodes.OK -> Instant.now.toString)
              
              onSuccess(t.runToFuture) { result =>
                result match {
                  case Right(u) => {
                    //add headers with session!!!
                    println("RETURNING USER")

                    // val sessionCache = WebAppSessionCache

                    val session = SessionData(sessionCache.getRandomSessionId, u.username.username, u.role)

                    sessionCache.setSession(session.id, session)

                    import SessionCookie._

                    respondWithHeader(getSessionCookieHeader(content = session.id)) {
                      // _.complete(redirectToRoute("/index"))
                      _.complete(StatusCodes.OK -> u)
                    }
                  }
                  case Left(error) => {
                    println("RETURNING BAD REQUEST")
                    complete(StatusCodes.BadRequest -> Map(error.errorKey -> List(error)))    
                  }
                }
              }      

            }
          }
        } ~ 
        path("register") {
          post {
            entity(as[NewUser]) { newUser =>
              val dbUser = newUserToUser(newUser)

              val t: Task[Either[BackendError,User]] = for {
                usernameExists <- Task.fromFuture(repository.users.existsOnUsername(dbUser))
                emailExists <- Task.fromFuture(repository.users.existsOnEmail(dbUser))
                invalidForInsert <- Task.pure(usernameExists || emailExists)
                result <- if (invalidForInsert){
                  if (usernameExists){
                    Task.pure(UserNameAlreadyExists(dbUser.fullName).asLeft[User])
                  } else {
                    Task.pure(EmailAlreadyExists(dbUser.email.email).asLeft[User])
                  }
                } else {
                  UserController.storeUser(dbUser)
                }
              } yield (result)

              onSuccess(t.runToFuture) { result =>
                result match {
                  case Right(u) => {
                    complete(StatusCodes.OK -> u)
                  }
                  case Left(error) => {
                    complete(StatusCodes.BadRequest -> Map(error.errorKey -> List(error)))    
                  }
                }
              }
              
            }
          }
        }
    }    
  }


  def apiPublicRoutes(): Route = {
    // (handleRejections(apiRejectionHandler) & handleExceptions(apiExceptionHandler)) {
      pathPrefix("api") {
        path("now") {
          get {
            complete(StatusCodes.OK -> Instant.now.toString)
          }
        } 
      }
    // }
  }

  def apiSecureRoutes(): Route = {
    // (handleRejections(apiRejectionHandler) & handleExceptions(apiExceptionHandler)) {
      pathPrefix("api") {
      	path("now2") {
            authenticate() { session ⇒
      		    get {
      		      complete(StatusCodes.OK -> Instant.now.toString)
      		    }
            }
    		} ~ path("me") {
            authenticate() { session ⇒
              get {
                // complete(StatusCodes.OK -> Instant.now.toString)
                onSuccess(repository.users.getByUsername(Username(session.username))) { userOpt =>
                  userOpt match {
                    case Some(user) => {
                      val appUser = AppUser(""+user.id,user.fullName)
                      complete(StatusCodes.OK -> appUser)
                    }
                    case None => {
                      complete(StatusCodes.BadRequest -> Error("not Authorization"))
                    }
                  }
                }
              }
            }
        } ~ path("am-i-admin") {
            authenticate() { session ⇒
              get {
                // complete(StatusCodes.OK -> Instant.now.toString)
                // onSuccess(repository.users.getByUsername(Username(session.username))) { user =>
                //   complete(StatusCodes.OK -> user)
                // }                
                if (session.isAdmin){
                  complete(StatusCodes.OK -> Instant.now.toString)
                } else {
                  complete(StatusCodes.BadRequest -> Error("not Authorization"))
                }
              }
            }
          } ~ path("logoff") {
            authenticate() { session ⇒
              get {
                // complete(StatusCodes.OK -> Instant.now.toString)
                // onSuccess(repository.users.getByUsername(Username(session.username))) { user =>
                //   complete(StatusCodes.OK -> user)
                // }                
                // if (session.isAdmin){
                //   complete(StatusCodes.OK -> Instant.now.toString)
                // } else {
                //   complete(StatusCodes.BadRequest -> Error("not Authorization"))
                // }

                sessionCache.removeSession(session.id)

                complete(redirectToRoute("/home"))
              }
            }
          }
        }
      // }
    // }
  }

  private def apiRejectionHandler = {
    // println("RejectionHandler....")
    RejectionHandler
      .newBuilder()
      .handle {
        case AuthorizationFailedRejection =>
          extractUri { uri =>
            scribe.error(s"${uri.path} | unauthorized API call")
            complete(StatusCodes.Unauthorized -> Error("Authorization Required"))
          }
        case MalformedRequestContentRejection(reason, error) =>
          extractUri { uri =>
            scribe.error(s"${uri.path} | malformed content - ${reason}", error)

            println("reason:" + reason)
            println("stack:" + StackTrace.stackTraceToString(error))

            complete(StatusCodes.BadRequest -> Error(reason))
          }
      }
      .handleNotFound {
        complete(StatusCodes.NotFound -> Error("API: Not Found"))
      }
      .result()
  }

  private def apiExceptionHandler(): ExceptionHandler = {
    // println("ExceptionHandler....")
    ExceptionHandler { 
      case NonFatal(ex) => {
        extractUri { uri =>
          scribe.error(s"$uri | unhandled exception", ex)
          complete(StatusCodes.InternalServerError, Error(ex.getMessage))
        }
      }
      case ex => {
        println("unhandled exception:" + ex)
        println("stack:" + StackTrace.stackTraceToString(ex))
        complete(StatusCodes.InternalServerError, Error(ex.getMessage))
      }      
    }
  }

  //TODO: take this from config.
  val PEPPER = "8f492a1986b28d42a42da845757a3a84ddf6f22c1900093fa0323dd9eccc26fe64bf891635eab04c4e698611f91a09e8ac9ac4c5b3fd9c97f9f47ee88e860f34"

  def newUserToUser(newUser: NewUser): User = {
    val encryptedPass = PasswordUtil.encrypt(newUser.password,PEPPER)
    println("newUser.password: '" + newUser.password + "'")
    println("encryptedPass:" + encryptedPass)

    User(
      Username(newUser.name.toLowerCase),
      UserEmail(newUser.email.toLowerCase),
      encryptedPass,//: String,
      None,//resetPasswordToken: Option[UserResetPasswordToken],
      None,//rememberToken: Option[String],
      None,//rememberCreatedAt: Option[escalator.models.Timestamp],
      0,//signInCount: Int,
      None,//currentSignInAt: Option[escalator.models.Timestamp],
      None,//lastSignInAt: Option[escalator.models.Timestamp],
      None,//currentSignInIp: Option[String],
      None,//lastSignInIp: Option[String],
      None,//confirmationToken: Option[UserConfirmationToken],
      None,//confirmationAt: Option[escalator.models.Timestamp],
      None,//confirmationSentAt: Option[escalator.models.Timestamp],
      None,//passwordSalt: Option[String],
      newUser.name,  //name: Option[String],
      TextUtil.extractInitials(newUser.name.toLowerCase),//initials: Option[String],
      None,//twoFactorAuthActive: Option[Boolean],
      None,//twoFactorAuthSecret: Option[String],
      None,//role: Option[String],
      "ACTIVE"//status: String
    )
  }

}