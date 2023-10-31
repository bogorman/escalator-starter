package com.escalatorstarter.http.server.controllers

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials

import escalator.util.logging.Logger

import monix.eval.Task
import com.escalatorstarter.errors._

import escalator.errors._
import escalator.errors.BackendError._

import cats.implicits._

import com.escalatorstarter.models._
import com.escalatorstarter.core.repositories._

import escalator.util.monix._

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes.{ `application/json`, `text/plain(UTF-8)` }
import java.time.Instant

object UserController {
  import BackendExceptionHandler._

  def storeUser(user: User)(implicit repository: EscalatorStarterRepository): Task[Either[BackendError,User]] = {
    (
      for {
        maybeDbUser <- Task.fromFuture(repository.users.store(user))
        result <- Task.pure(maybeDbUser.asRight[BackendError])        
    } yield (result)
    ).recoverFromBackendException
  }

}