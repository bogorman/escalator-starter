package com.escalatorstarter.common.persistence.postgres

import escalator.util.Timestamp
import com.escalatorstarter.models.AttributeType

import io.getquill._
import escalator.util.DateTimeConverter

// import org.postgresql.util.PGobject
// import java.sql.Types

import io.getquill.MappedEncoding
// import io.getquill.MappedEncoding._

// import io.getquill.PostgresAsyncContext
// import io.getquill.context.async._
// import io.getquill.context.sql.encoding._

// import io.getquill.context.async._

object PostgresMappedEncoder {

	def dateToTimestamp(dt: java.time.LocalDateTime): Timestamp = {
		Timestamp( DateTimeConverter.localDateTimeToEpochNano(dt) )
	}

	def timestampToDate(t: Timestamp): java.time.LocalDateTime = {
		DateTimeConverter.epochNanoToLocalDateTime( t.nanos )
	}

	implicit val encodeTimestamp = MappedEncoding[java.time.LocalDateTime, Timestamp]( dateToTimestamp )
	implicit val decodeTimestamp = MappedEncoding[Timestamp, java.time.LocalDateTime]( timestampToDate )

}