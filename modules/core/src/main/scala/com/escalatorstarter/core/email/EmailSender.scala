package com.escalatorstarter.util.email

import org.apache.commons.lang3.StringEscapeUtils
import scala.collection.mutable.{ ArrayBuffer => MList  }

import escalator.util.resque._
import com.escalatorstarter.core.delayed.resque._

object EmailSender {
  // import ResqueWorkProtocol._
  import upickle.default._

  // var liveEmailSending = Constants.isProduction

  private def fillToAddress(toField: String) = {
    val recipients = MList.empty[String]

    // if (liveEmailSending) {
    //   recipients += toField
    // } else if (Uniquifier.isPersonalEmail(toField)) {
    //   recipients += toField
    // } else if (toField.contains("@minus42.com")) { //testing email
      recipients += toField
    // }

    // if (!Constants.isProduction) {
    //   if (Constants.globalCcEmailAddress != "") {
    //     recipients += Constants.globalCcEmailAddress
    //   }
    // }

    recipients.mkString(",")
  }

  // def sendAdminEmail(toField: String, subject: String, message: String) = {
  //   println("sendUserEmail:" + toField)
  //   println("sendUserEmail:" + subject)
  //   println("sendUserEmail:" + message)

  //   val work = EmailDetails(fillToAddress(toField), subject, message.trim, false)

  //   Resque.enqueue("EmailQueue", "EmailWorker", List(StringEscapeUtils.escapeJson(write(work))))
  // }

  def sendEmail(toField: String, subject: String, message: String) = {
  //   println("sendUserEmail:" + toField)
  //   println("sendUserEmail:" + subject)
  //   println("sendUserEmail:" + message)

    val work = ResqueWorkProtocol.EmailDetails(fillToAddress(toField), subject, message.trim, true)

    val classname: String = ResqueWorkProtocol.EMAIL_WORKER
    val queue: String = ResqueWorkProtocol.EMAIL_QUEUE

    Resque.enqueue(queue, classname, List(StringEscapeUtils.escapeJson(write(work))))
  }

}