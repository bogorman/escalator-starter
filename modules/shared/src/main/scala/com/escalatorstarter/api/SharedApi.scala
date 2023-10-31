package com.escalatorstarter.shared.api

// import scala.concurrent.Future
import scala.concurrent.Future
// import monix.eval.Task

import escalator.errors._
// import com.escalatorstarter.errors._
import com.escalatorstarter.models._

trait SharedApi {
 def getTimeAsString(): Future[String]

}