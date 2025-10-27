package com.escalatorstarter.api

import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._
import escalator.errors.BackendError
import org.scalajs.dom
import com.escalatorstarter.users.{NewUser, LoginUser}
import com.escalatorstarter.models.User

import sttp.client3._
import sttp.model.{MediaType, Uri}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * HTTP API client for making requests to the backend
  * Uses STTP library with FetchBackend for browser compatibility
  */
object ApiClient {

  implicit val backend: SttpBackend[Future, sttp.capabilities.WebSockets] = FetchBackend()

  /**
    * Get the host from the current page
    */
  def host: Uri = Uri.parse(dom.document.location.origin.toString).getOrElse(uri"http://localhost:30199")

  /**
    * Build API path with /api prefix
    */
  def path(s: String, ss: String*): Uri = host.path("api" +: s +: ss.toList)

  /**
    * Base request
    */
  def boilerplate: RequestT[Empty, Either[String, String], Any] = basicRequest

  /**
    * Response handler for responses with data
    */
  def responseAs[A](
      implicit aDecoder: Decoder[A]
  ): ResponseAs[Either[Either[Error, Map[String, List[BackendError]]], Either[Error, A]], Any] = asEither(
    asStringAlways.map(decode[Map[String, List[BackendError]]](_)),
    asStringAlways.map(decode[A](_))
  )

  /**
    * Body serializer
    */
  implicit def bodySerializer[A](implicit aEncoder: Encoder[A]): A => BasicRequestBody =
    (a: A) =>
      StringBody(
        a.asJson.noSpaces,
        "utf-8",
        MediaType.ApplicationJson
      )

  /**
    * Register a new user
    */
  def register(newUser: NewUser): Future[Either[Map[String, List[BackendError]], User]] = {
    boilerplate
      .post(path("register"))
      .body(newUser)
      .response(responseAs[User])
      .send()
      .map { response =>
        response.body match {
          case Right(Right(user)) => Right(user)
          case Right(Left(error)) => Left(Map("general" -> List(BackendError("error.parse", error.getMessage))))
          case Left(Right(backendErrors)) => Left(backendErrors)
          case Left(Left(error)) => Left(Map("general" -> List(BackendError("error.parse", error.getMessage))))
        }
      }
      .recover {
        case ex: Throwable =>
          dom.console.error(s"Registration error: ${ex.getMessage}")
          Left(Map("general" -> List(BackendError("error.network", "Network error occurred"))))
      }
  }

  /**
    * Login a user
    */
  def login(loginUser: LoginUser): Future[Either[Map[String, List[BackendError]], User]] = {
    boilerplate
      .post(path("login"))
      .body(loginUser)
      .response(responseAs[User])
      .send()
      .map { response =>
        response.body match {
          case Right(Right(user)) => Right(user)
          case Right(Left(error)) => Left(Map("general" -> List(BackendError("error.parse", error.getMessage))))
          case Left(Right(backendErrors)) => Left(backendErrors)
          case Left(Left(error)) => Left(Map("general" -> List(BackendError("error.parse", error.getMessage))))
        }
      }
      .recover {
        case ex: Throwable =>
          dom.console.error(s"Login error: ${ex.getMessage}")
          Left(Map("general" -> List(BackendError("error.network", "Network error occurred"))))
      }
  }

  /**
    * Logout the current user
    */
  def logout(): Future[Either[String, Unit]] = {
    boilerplate
      .post(path("logout"))
      .send()
      .map { response =>
        if (response.code.isSuccess) Right(())
        else Left("Logout failed")
      }
      .recover {
        case ex: Throwable =>
          dom.console.error(s"Logout error: ${ex.getMessage}")
          Left(s"Network error: ${ex.getMessage}")
      }
  }

  /**
    * Generic GET request
    */
  def get[A](endpoint: String)(implicit aDecoder: Decoder[A]): Future[Either[String, A]] = {
    boilerplate
      .get(path(endpoint))
      .response(asStringAlways.map(decode[A](_)))
      .send()
      .map { response =>
        response.body match {
          case Right(data) => Right(data)
          case Left(error) => Left(error.getMessage)
        }
      }
      .recover {
        case ex: Throwable =>
          dom.console.error(s"GET error: ${ex.getMessage}")
          Left(s"Network error: ${ex.getMessage}")
      }
  }

  /**
    * Generic POST request
    */
  def post[A, B](endpoint: String, body: A)(implicit aEncoder: Encoder[A], bDecoder: Decoder[B]): Future[Either[String, B]] = {
    boilerplate
      .post(path(endpoint))
      .body(body)
      .response(asStringAlways.map(decode[B](_)))
      .send()
      .map { response =>
        response.body match {
          case Right(data) => Right(data)
          case Left(error) => Left(error.getMessage)
        }
      }
      .recover {
        case ex: Throwable =>
          dom.console.error(s"POST error: ${ex.getMessage}")
          Left(s"Network error: ${ex.getMessage}")
      }
  }
}
