package com.escalatorstarter.common.persistence.postgres

import escalator.util.Timestamp
import com.escalatorstarter.models.AttributeType

import io.getquill._
import escalator.util.DateTimeConverter

import io.getquill.MappedEncoding

import java.sql.Types

trait PostgresCustomEncoder {

  def db: PostgresDatabase
  protected val ctx: PostgresMonixJdbcContext[CustomNamingStrategy] = db.context()

  protected lazy implicit val attributeTypeDecoder: ctx.Decoder[AttributeType] = 
    ctx.decoder((index, row, session) => {
      val i = row.getString(index)
      val cleanedInput = i.trim.stripPrefix("(").stripSuffix(")")
      val parts = cleanedInput.split(",")        

      if (parts.size == 2){
        AttributeType(parts(0).trim, parts(1).trim)
      } else {
        null
      }
    }
  )

  protected lazy implicit val attributeTypeEncoder: ctx.Encoder[AttributeType] = ctx.encoder(java.sql.Types.OTHER, (index, value, row) => {
      val pgObj = new org.postgresql.util.PGobject()
      pgObj.setType("attribute_type")
      pgObj.setValue(s"(${value.attr},${value.ident})")
      row.setObject(index, pgObj, java.sql.Types.OTHER)      
    }
  )
}