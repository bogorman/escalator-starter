package com.escalatorstarter.http.server

import com.github.pjfanning.pekkohttpcirce.BaseCirceSupport
import com.github.pjfanning.pekkohttpcirce.ErrorAccumulatingUnmarshaller
import io.circe.Printer

object DropNullKeysPrinter {

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

}

trait DropNullKeysPrinter { this: BaseCirceSupport =>

  implicit val printer: Printer = DropNullKeysPrinter.printer

}

trait AkkaHttpCirceSupport extends BaseCirceSupport with DropNullKeysPrinter with ErrorAccumulatingUnmarshaller

trait CirceSupportWithCodecs extends AkkaHttpCirceSupport
