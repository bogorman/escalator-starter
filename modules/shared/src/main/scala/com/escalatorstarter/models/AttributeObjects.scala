package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 18-09-25 17:13:09:993

// Type-safe constants for AttributeType values

object EntityTypes {
  val Person = EntityType("person")
  val Company = EntityType("company")
  val NonProfit = EntityType("non_profit")
  val Fund = EntityType("fund")
  val Cause = EntityType("cause")
  val Country = EntityType("country")
  val GovernmentBranch = EntityType("government_branch")
  val PoliticalParty = EntityType("political_party")
  val IntergovOrg = EntityType("intergov_org")
  val Internal = EntityType("internal")
  val University = EntityType("university")
  val Page = EntityType("page")
  val Group = EntityType("group")
  val City = EntityType("city")
  val State = EntityType("state")
  val Territory = EntityType("territory")
  val Investor = EntityType("investor")
  val Media = EntityType("media")
}

object WorkTypes {
  val Email = WorkType("email")
}

object WorkStatusTypes {
  val Failed = WorkStatusType("failed")
  val Complete = WorkStatusType("complete")
  val Working = WorkStatusType("working")
  val Pending = WorkStatusType("pending")
}
