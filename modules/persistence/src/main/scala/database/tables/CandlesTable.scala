package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:534

import scala.concurrent.Future

import com.escalatorstarter.models._

trait CandlesTable {
  def tableName = "candles"

  //def store(c: Candle): Future[Candle]

  //def store(cl: List[Candle]): Future[List[Candle]]

  //def insert(c: Candle): Future[_]

  def updateTokenAddresById(id: CandleId, tokenAddress: TokenAddress): Future[Candle]

  def updateOpenTimeById(id: CandleId, openTime: Long): Future[Candle]

  def updateCloseTimeById(id: CandleId, closeTime: Long): Future[Candle]

  def updateOpenTimestampById(id: CandleId, openTimestamp: escalator.util.Timestamp): Future[Candle]

  def updateCloseTimestampById(id: CandleId, closeTimestamp: escalator.util.Timestamp): Future[Candle]

  def updateDurationById(id: CandleId, duration: Long): Future[Candle]

  def updateOpenById(id: CandleId, open: Double): Future[Candle]

  def updateHighById(id: CandleId, high: Double): Future[Candle]

  def updateLowById(id: CandleId, low: Double): Future[Candle]

  def updateCloseById(id: CandleId, close: Double): Future[Candle]

  def updateVolumeById(id: CandleId, volume: Double): Future[Candle]

  def getById(c: CandleId): Future[Option[Candle]]

  def getByIds(c: List[CandleId]): Future[List[Candle]]

  def update(c: Candle): Future[Candle]

  def upsert(c: Candle): Future[Candle]

  def upsert(cl: List[Candle]): Future[List[Candle]]

  def delete(c: Candle): Future[Candle]

  def getListByTokenAddres(tokenAddress: TokenAddress): Future[List[Candle]]

  def getListByTokenAddress(tokenAddresss: List[TokenAddress]): Future[List[Candle]]

  def count: Future[Long]

  def getAll(): Future[List[Candle]]
}
