package com.escalatorstarter.common.persistence.postgres

import escalator.util.Timestamp

import com.escalatorstarter.models._

import io.getquill._
import escalator.util.DateTimeConverter

import io.getquill.MappedEncoding

import java.sql.Types

import escalator.util.postgres.PostgresDatabase
import escalator.util.postgres.CustomNamingStrategy

trait PostgresCommonEncoder {

  def db: PostgresDatabase
  protected val ctx: PostgresMonixJdbcContext[CustomNamingStrategy] = db.context()

  protected lazy implicit val jsonbDecoder: ctx.Decoder[io.circe.Json] =
    ctx.decoder((index, row, session) => {
      val i = row.getString(index)

      io.circe.parser.parse(i) match {
        case Right(v) => {
          v
        }
        case Left(err) => {
          throw new java.sql.SQLException(
            s"Could not parse string to error",
            err
          )
          // null
        }
      }
    })

  protected lazy implicit val jsonbEncoder: ctx.Encoder[io.circe.Json] =
    ctx.encoder(
      java.sql.Types.OTHER,
      (index, value, row) => {
        val pgObj = new org.postgresql.util.PGobject()
        pgObj.setType("jsonb")
        val json: io.circe.Json = value
        pgObj.setValue(json.noSpaces)
        row.setObject(index, pgObj, java.sql.Types.OTHER)
      }
    )

  // Generic helper methods for attribute types
  protected def parseAttributeType(str: String): (String, String) = {
    val cleanedInput = str.trim.stripPrefix("(").stripSuffix(")")
    val parts = cleanedInput.split(",")
    if (parts.size == 2) (parts(0).trim, parts(1).trim) else ("", "")
  }

  protected def createAttributeTypeDecoder[T](constructor: String => T): ctx.Decoder[T] = {
    println("createAttributeTypeDecoder " + constructor)
    ctx.decoder((index, row, session) => {
      val str = row.getString(index)
      val (_, ident) = parseAttributeType(str)
      if (ident.nonEmpty) constructor(ident) else null.asInstanceOf[T]
    })
  }

  protected def createAttributeTypeEncoder[T <: BaseAttributeType]: ctx.Encoder[T] = {
    println("createAttributeTypeEncoder ")
    ctx.encoder(java.sql.Types.OTHER, (index, value, row) => {
      val pgObj = new org.postgresql.util.PGobject()
      pgObj.setType("attribute_type")
      pgObj.setValue(s"(${value.attr},${value.ident})")
      println(s"(${value.attr},${value.ident})")
      row.setObject(index, pgObj, java.sql.Types.OTHER)
    })
  }

  protected def createMappedEncoding[T <: BaseAttributeType]: MappedEncoding[T, String] =
    MappedEncoding[T, String](t => s"(${t.attr},${t.ident})")

  protected def createMappedDecoding[T <: BaseAttributeType](constructor: String => T): MappedEncoding[String, T] =
    MappedEncoding[String, T] { str =>
      val (_, ident) = parseAttributeType(str)
      if (ident.nonEmpty) constructor(ident) else constructor("")
    }

  protected def createAttributeTypeMapping[T <: BaseAttributeType]: MappedEncoding[T, AttributeType] =
    MappedEncoding[T, AttributeType](t => AttributeType(t.attr, t.ident))

  // Base AttributeType (generic)
  protected lazy implicit val attributeTypeDecoder: ctx.Decoder[AttributeType] = 
    ctx.decoder((index, row, session) => {
      val str = row.getString(index)
      val (attr, ident) = parseAttributeType(str)
      if (attr.nonEmpty && ident.nonEmpty) AttributeType(attr, ident) else null
    })

  protected lazy implicit val attributeTypeEncoder: ctx.Encoder[AttributeType] = 
    createAttributeTypeEncoder[AttributeType]


}