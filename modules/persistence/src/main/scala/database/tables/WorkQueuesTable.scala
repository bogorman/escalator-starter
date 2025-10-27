package com.escalatorstarter.persistence.database.tables

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:33:129

import scala.concurrent.Future

import com.escalatorstarter.models._

trait WorkQueuesTable {
  def tableName = "work_queues"

  //def store(w: WorkQueue): Future[WorkQueue]

  //def store(wl: List[WorkQueue]): Future[List[WorkQueue]]

  //def insert(w: WorkQueue): Future[_]

  def updateWorkTypeById(id: WorkQueueId, workType: WorkType): Future[WorkQueue]

  def updateWorkById(id: WorkQueueId, work: Option[String]): Future[WorkQueue]

  def updateWorkStatuById(id: WorkQueueId, workStatus: WorkStatusType): Future[WorkQueue]

  def updateWorkResultById(id: WorkQueueId, workResult: Option[String]): Future[WorkQueue]

  def getById(w: WorkQueueId): Future[Option[WorkQueue]]

  def getByIds(w: List[WorkQueueId]): Future[List[WorkQueue]]

  def update(w: WorkQueue): Future[WorkQueue]

  def upsert(w: WorkQueue): Future[WorkQueue]

  def upsert(wl: List[WorkQueue]): Future[List[WorkQueue]]

  def delete(w: WorkQueue): Future[WorkQueue]

  def getListByWorkType(workType: WorkType): Future[List[WorkQueue]]

  def getListByWorkTypes(workTypes: List[WorkType]): Future[List[WorkQueue]]
  def getListByWorkStatu(workStatus: WorkStatusType): Future[List[WorkQueue]]

  def getListByWorkStatus(workStatuss: List[WorkStatusType]): Future[List[WorkQueue]]

  def count: Future[Long]

  def getAll(): Future[List[WorkQueue]]
}
