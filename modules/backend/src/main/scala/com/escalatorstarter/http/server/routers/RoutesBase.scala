package com.escalatorstarter.http.server

import org.apache.pekko.http.scaladsl.server.Directives
import org.apache.pekko.http.scaladsl.server.PathMatcher1

trait RoutesBase extends Directives with CirceSupportWithCodecs
