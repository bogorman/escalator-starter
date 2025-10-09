package com.escalatorstarter.protocols

import urldsl.language.dummyErrorImpl._

object UserProtocol {

  final val userName = param[String]("userName")
  final val randomKey = param[String]("randomKey")

}
