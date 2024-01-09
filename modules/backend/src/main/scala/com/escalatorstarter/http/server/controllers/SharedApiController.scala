package com.escalatorstarter.http.server.controllers

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.directives.Credentials

import escalator.util.logging.Logger

import monix.eval.Task
import com.escalatorstarter.errors._

import escalator.errors._
import escalator.errors.BackendError._


import cats.implicits._

import com.escalatorstarter.models._
import com.escalatorstarter.core.repositories._

import escalator.util.monix._
import com.escalatorstarter.shared.api._

import escalator.util.TimeUtil

 // 
class SharedApiController()(implicit repository: EscalatorStarterRepository) extends SharedApi {
  import BackendExceptionHandler._

  import monix.execution.Scheduler.Implicits.global

  def getTimeAsString(): Future[String] = {
    Task {
      s"${TimeUtil.nowString}"
    }.runToFuture
  }

}

object SharedApiController {

  def apply()(implicit repository: EscalatorStarterRepository): SharedApiController = {
    new SharedApiController()
  }
}