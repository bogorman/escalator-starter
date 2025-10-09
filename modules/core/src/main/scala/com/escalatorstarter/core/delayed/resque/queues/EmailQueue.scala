package com.escalatorstarter.core.delayed.resque

import escalator.util.resque.ResqueController

class EmailQueue extends ResqueController {
  val controllerName = ResqueWorkProtocol.EMAIL_QUEUE
  val packageName: String = ResqueWorkProtocol.PACKAGE_NAME

  val workerNames = List(ResqueWorkProtocol.EMAIL_WORKER)

  log.debug("EmailQueue init starting")
  init()
  log.debug("EmailQueue init complete")
}
