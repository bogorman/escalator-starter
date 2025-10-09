package com.escalatorstarter.http.server.controllers

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.directives.Credentials

import escalator.util.logging.Logger

import monix.eval.Task

import escalator.errors._
import com.escalatorstarter.errors._

import escalator.errors.BackendError._
import cats.implicits._

import com.escalatorstarter.models._
import com.escalatorstarter.core.repositories._

import escalator.util.monix._

object BackendExceptionHandler {
  
    implicit final class RecoverFromBackendException[T](task: Task[Either[BackendError, T]]) {

    def recoverFromBackendException: Task[Either[BackendError, T]] = task.onErrorRecover {
      case e: BackendException => Left[BackendError, T](e.backendError)
    }
    
  }  

}