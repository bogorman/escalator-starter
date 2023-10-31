package com.escalatorstarter.core.delayed.resque

import escalator.util.resque.SlaveActor

import escalator.util.email._

import org.apache.commons.lang3.StringEscapeUtils

class EmailWorker extends SlaveActor {
  import ResqueWorkProtocol._
  import upickle.default._

  val classname: String = ResqueWorkProtocol.EMAIL_WORKER
  val queue: String = ResqueWorkProtocol.EMAIL_QUEUE

  // val mailSender = new SmtpMailer(EmailConf.smtpConfig)
  val mailSender = new SendGridMailer()

  def doOperation(msg: List[String]) = {
    println("EmailWorker doOperation")
    println(msg.toString)

    // import EmailSender._

    val payload = StringEscapeUtils.unescapeJson(msg(0))
    // val email = payload.unpickle[EmailDetails]
    val email = read[EmailDetails](payload)

    mailSender.sendEmail(email.toField, email.subject, email.message, email.isHtml)
  }

}
