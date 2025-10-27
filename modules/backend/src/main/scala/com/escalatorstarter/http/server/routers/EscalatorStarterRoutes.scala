package com.escalatorstarter.http.server

import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server._
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.model.ContentTypes.{ `application/json`, `text/plain(UTF-8)` }

// import starter.data.Error
// import starter.data.PostRepr
// import starter.http.RoutesBase

import scala.util.Random
import scala.util.control.NonFatal
import unindent._

import java.time.Instant

// import org.apache.pekko.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK, Unauthorized}

import com.escalatorstarter.users._
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
import com.escalatorstarter.http.server.privatizers._

import com.escalatorstarter.shared.api._
import com.escalatorstarter.http.akka._

import escalator.common.util.debug.StackTrace

import pureconfig._
import pureconfig.generic.auto._
import escalator.util.Configuration
import com.typesafe.config.Config

// import concurrent.Future
// import scala.util.Success
// import scala.util.Failure


class EscalatorStarterRoutes(override protected val sessionCache: SessionCache)(implicit repository: EscalatorStarterRepository, val config: Config)
	extends RoutesBase 
	with CorsHandler 
	with SecureHandler {
  
  import monix.execution.Scheduler.Implicits.global
  import escalator.util.monix.TaskSyntax._

  import EscalatorStarterBackendError._

  // Implement SecureService trait requirement
  protected def getSessionDataByAccessToken(accessToken: String): Option[SessionData] = {
    // This is a basic implementation - you may need to customize this based on your auth strategy
    sessionCache.getSession(accessToken)
  }

  def route: Route = {
    // corsHandler( allRoutes )
    TraceDirectives.log(allRoutes)
  }

  def allRoutes: Route = {
    (handleRejections(apiRejectionHandler) & handleExceptions(
      apiExceptionHandler
    )) {
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

            val t: Task[Either[BackendError, User]] = for {
              userOpt <- Task.fromFuture(
                repository.users.getByEmail(
                  UserEmail(loginUser.email.toLowerCase)
                )
              )
              r <-
                if (userOpt.isEmpty) {
                  Task.pure(
                    EmailNotFound(loginUser.email.toLowerCase).asLeft[User]
                  )
                  // } else if (userOpt.get.encryptedPassword != encrypedPass) {
                } else if (
                  PasswordUtil.matches(
                    loginUser.password,
                    PEPPER,
                    userOpt.get.encryptedPassword
                  )
                ) {
                  Task.pure(userOpt.get.asRight[BackendError])
                } else {
                  Task.pure(
                    PasswordIncorrect(loginUser.email.toLowerCase).asLeft[User]
                  )
                }
              } yield (r)

              // complete(StatusCodes.OK -> Instant.now.toString)
              
              onSuccess(t.runToFuture) { result =>
                result match {
                  case Right(u) => {
                    //add headers with session!!!
                    println("RETURNING USER")

                    // val sessionCache = WebAppSessionCache

                  val session = SessionData(
                    sessionCache.getRandomSessionId,
                    u.email.email,
                    Some(u.role.ident)
                  )

                    sessionCache.setSession(session.id, session)

                    import SessionCookie._

                  respondWithHeader(
                    getSessionCookieHeader(content = session.id)
                  ) {
                    // _.complete(redirectToRoute("/index"))
                    _.complete(StatusCodes.OK -> UserPrivatizer.sanitize(u))
                  }
                }
                  case Left(error) => {
                    println("RETURNING BAD REQUEST")
                  complete(
                    StatusCodes.BadRequest -> Map(error.errorKey -> List(error))
                  )
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
                emailExists <- Task.fromFuture(repository.users.existsOnEmail(dbUser))
                invalidForInsert <- Task.pure(emailExists)
                result <- if (invalidForInsert){
                  Task.pure(EmailAlreadyExists(dbUser.email.email).asLeft[User])
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
            authenticate() { session =>
      		    get {
      		      complete(StatusCodes.OK -> Instant.now.toString)
      		    }
            }
    		} ~ path("me") {
            authenticate() { session =>
              get {
                // complete(StatusCodes.OK -> Instant.now.toString)
            onSuccess(
              repository.users.getByEmail(UserEmail(session.email))
            ) { userOpt =>
              userOpt match {
                case Some(user) => {
                  complete(StatusCodes.OK -> UserPrivatizer.sanitize(user))
                }
                    case None => {
                      complete(StatusCodes.BadRequest -> Error("not Authorization"))
                    }
                  }
                }
              }
            }
        } ~ path("am-i-admin") {
            authenticate() { session =>
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
            authenticate() { session =>
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
            complete(
              StatusCodes.Unauthorized -> Error("Authorization Required")
            )
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

  // Load PEPPER from config
  val PEPPER: String = Configuration.fetch[SecureProtocol.AuthConfig]("escalatorstarter.auth")
    .map(_.pepper)
    .getOrElse("<SET IN CONIG PROPERLY>")

  def newUserToUser(newUser: NewUser): User = {
    val encryptedPass = PasswordUtil.encrypt(newUser.password,PEPPER)
    println("newUser.password: '" + newUser.password + "'")
    println("encryptedPass:" + encryptedPass)

    User(
      UserEmail(newUser.email.toLowerCase),
      encryptedPass,//encryptedPassword: String,
      None,//resetPasswordToken: Option[UserResetPasswordToken],
      None,//rememberToken: Option[String],
      None,//rememberCreatedAt: Option[escalator.util.Timestamp],
      None,//confirmationToken: Option[UserConfirmationToken],
      None,//confirmedAt: Option[escalator.util.Timestamp],
      None,//confirmationSentAt: Option[escalator.util.Timestamp],
      None,//passwordSalt: Option[String],
      Some(newUser.name),  //fullName: Option[String],
      Some(TextUtil.extractInitials(newUser.name.toLowerCase)),//initials: Option[String],
      None,//twoFactorAuthActive: Option[Boolean],
      None,//twoFactorAuthSecret: Option[String],
      Some(0),//signInCount: Option[Int],
      None,//currentSignInAt: Option[escalator.util.Timestamp],
      None,//lastSignInAt: Option[escalator.util.Timestamp],
      None,//currentSignInIp: Option[String],
      None,//lastSignInIp: Option[String],
      UserRoleType("USER"),//role: UserRoleType,
      UserStatusType("ACTIVE"),//status: UserStatusType
      UserAccessToken(java.util.UUID.randomUUID())//accessToken: UserAccessToken
    )
  }

}