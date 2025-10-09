package com.escalatorstarter.core.delayed.resque

import upickle.default.{ ReadWriter => RW, macroRW }

object ResqueWorkProtocol {
  val PACKAGE_NAME = "com.escalatorstarter.core.delayed.resque."

  //queues
  val EMAIL_QUEUE = "EmailQueue"

  //workers
  val EMAIL_WORKER = "EmailWorker"

  ////////// WORK CASE CLASSES

  case class EmailDetails(toField: String, subject: String, message: String, isHtml: Boolean)
  object EmailDetails {
    implicit def rw: RW[EmailDetails] = macroRW
  }

}