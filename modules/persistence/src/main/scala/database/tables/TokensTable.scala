package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:32:427

import scala.concurrent.Future

import com.escalatorstarter.models._

trait TokensTable {
  def tableName = "tokens"

  //def store(t: Token): Future[Token]

  //def store(tl: List[Token]): Future[List[Token]]

  //def insert(t: Token): Future[_]

  def upsertOnAddres(t: Token): Future[Token]

  def existsOnAddres(t: Token): Future[Boolean]

  def updateChainIdentById(id: TokenId, chainIdent: Option[String]): Future[Token]

  def updateSymbolById(id: TokenId, symbol: String): Future[Token]

  def updateNameById(id: TokenId, name: String): Future[Token]

  def updateTotalSupplyById(id: TokenId, totalSupply: Option[Double]): Future[Token]

  def updateMaxSupplyById(id: TokenId, maxSupply: Option[Double]): Future[Token]

  def updateIconById(id: TokenId, icon: Option[String]): Future[Token]

  def updateColorById(id: TokenId, color: Option[String]): Future[Token]

  def updateActiveById(id: TokenId, active: Boolean): Future[Token]

  def updateDisabledAtById(id: TokenId, disabledAt: Option[escalator.util.Timestamp]): Future[Token]

  def updateTagById(id: TokenId, tags: Option[List[String]]): Future[Token]

  def updateChainIdentByAddres(address: TokenAddress, chainIdent: Option[String]): Future[_]

  def updateSymbolByAddres(address: TokenAddress, symbol: String): Future[_]

  def updateNameByAddres(address: TokenAddress, name: String): Future[_]

  def updateTotalSupplyByAddres(address: TokenAddress, totalSupply: Option[Double]): Future[_]

  def updateMaxSupplyByAddres(address: TokenAddress, maxSupply: Option[Double]): Future[_]

  def updateIconByAddres(address: TokenAddress, icon: Option[String]): Future[_]

  def updateColorByAddres(address: TokenAddress, color: Option[String]): Future[_]

  def updateActiveByAddres(address: TokenAddress, active: Boolean): Future[_]

  def updateDisabledAtByAddres(address: TokenAddress, disabledAt: Option[escalator.util.Timestamp]): Future[_]

  def updateTagByAddres(address: TokenAddress, tags: Option[List[String]]): Future[_]

  def getById(t: TokenId): Future[Option[Token]]

  def getByIds(t: List[TokenId]): Future[List[Token]]

  def update(t: Token): Future[Token]

  def upsert(t: Token): Future[Token]

  def upsert(tl: List[Token]): Future[List[Token]]

  def delete(t: Token): Future[Token]

  def getByAddres(address: TokenAddress): Future[Option[Token]]

  def count: Future[Long]

  def getAll(): Future[List[Token]]
}
