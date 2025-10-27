package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:31:322

case class WorkQueueId(id: Long) extends AnyVal

case class WorkQueue(
    id: WorkQueueId,
    workType: WorkType,
    work: Option[String],
    workStatus: WorkStatusType,
    workResult: Option[String],
    createdAt: escalator.util.Timestamp,
    updatedAt: escalator.util.Timestamp
) extends Persisted

object WorkQueue {

  def apply(
      workType: WorkType,
      work: Option[String],
      workStatus: WorkStatusType,
      workResult: Option[String]
  ): WorkQueue = {
    WorkQueue(
      WorkQueueId(0L),
      workType,
      work,
      workStatus,
      workResult,
      escalator.util.Timestamp(0L),
      escalator.util.Timestamp(0L)
    )
  }

}
