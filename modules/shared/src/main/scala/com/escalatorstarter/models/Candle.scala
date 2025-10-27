package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:137

case class CandleId(id: Long) extends AnyVal

case class Candle(
    id: CandleId,
    tokenAddress: TokenAddress,
    openTime: Long,
    closeTime: Long,
    openTimestamp: escalator.util.Timestamp,
    closeTimestamp: escalator.util.Timestamp,
    duration: Long,
    open: Double,
    high: Double,
    low: Double,
    close: Double,
    volume: Double,
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object Candle {

  def apply(
      tokenAddress: TokenAddress,
      openTime: Long,
      closeTime: Long,
      openTimestamp: escalator.util.Timestamp,
      closeTimestamp: escalator.util.Timestamp,
      duration: Long,
      open: Double,
      high: Double,
      low: Double,
      close: Double,
      volume: Double
  ): Candle = {
    Candle(
      CandleId(0L),
      tokenAddress,
      openTime,
      closeTime,
      openTimestamp,
      closeTimestamp,
      duration,
      open,
      high,
      low,
      close,
      volume,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
