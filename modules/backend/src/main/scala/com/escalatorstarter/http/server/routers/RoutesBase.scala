package com.escalatorstarter.http.server

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.PathMatcher1

trait RoutesBase extends Directives with CirceSupportWithCodecs
