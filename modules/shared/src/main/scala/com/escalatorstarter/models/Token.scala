package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:10:391

case class TokenId(id: Long) extends AnyVal
case class TokenAddress(address: String) extends AnyVal

case class Token(
    id: TokenId,
    chainIdent: Option[String],
    address: TokenAddress,
    symbol: String,
    name: String,
    totalSupply: Option[Double],
    maxSupply: Option[Double],
    icon: Option[String],
    color: Option[String],
    active: Boolean,
    disabledAt: Option[escalator.util.Timestamp],
    tags: Option[List[String]],
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object Token {

  def apply(
      chainIdent: Option[String],
      address: TokenAddress,
      symbol: String,
      name: String,
      totalSupply: Option[Double],
      maxSupply: Option[Double],
      icon: Option[String],
      color: Option[String],
      active: Boolean,
      disabledAt: Option[escalator.util.Timestamp],
      tags: Option[List[String]]
  ): Token = {
    Token(
      TokenId(0L),
      chainIdent,
      address,
      symbol,
      name,
      totalSupply,
      maxSupply,
      icon,
      color,
      active,
      disabledAt,
      tags,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
