package com.escalatorstarter.http.server.auth

import scala.concurrent.ExecutionContext

trait Secure {
  protected val sessionCache = WebAppSessionCache
}