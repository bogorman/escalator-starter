package com.escalatorstarter.http.server.controllers

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials

import escalator.util.logging.Logger

object AdminController {

  case class AdminAuthentication(password: String)

  def authenticator(auth: AdminAuthentication)(credentials: Credentials): Option[Unit] = credentials match {
    case Credentials.Provided(auth.password) =>
      Some(())

    case _ =>
      None
  }

}
