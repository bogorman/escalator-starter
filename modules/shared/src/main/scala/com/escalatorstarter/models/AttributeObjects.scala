package com.escalatorstarter.models

// THIS FILE IS AUTO-GENERATED. REMOVE THIS LINE TO STOP THIS FILE BEING RE-GENERATED
// GENERATED AT: 24-10-25 12:59:30:916

// Type-safe constants for AttributeType values

object EntityTypes {
  val person = EntityType("person")
  val company = EntityType("company")
  val non_profit = EntityType("non_profit")
  val fund = EntityType("fund")
  val cause = EntityType("cause")
  val country = EntityType("country")
  val government_branch = EntityType("government_branch")
  val political_party = EntityType("political_party")
  val intergov_org = EntityType("intergov_org")
  val internal = EntityType("internal")
  val university = EntityType("university")
  val page = EntityType("page")
  val group = EntityType("group")
  val city = EntityType("city")
  val state = EntityType("state")
  val territory = EntityType("territory")
  val investor = EntityType("investor")
  val media = EntityType("media")
}

object UserRoleTypes {
  val ADMIN = UserRoleType("ADMIN")
  val USER = UserRoleType("USER")
  val TRADER = UserRoleType("TRADER")
  val VIEWER = UserRoleType("VIEWER")
}

object UserStatusTypes {
  val ACTIVE = UserStatusType("ACTIVE")
  val SUSPENDED = UserStatusType("SUSPENDED")
  val DELETED = UserStatusType("DELETED")
  val PENDING = UserStatusType("PENDING")
}

object UserSessionTypes {
  val UI = UserSessionType("UI")
}

object WorkTypes {
  val email = WorkType("email")
}

object WorkStatusTypes {
  val failed = WorkStatusType("failed")
  val complete = WorkStatusType("complete")
  val working = WorkStatusType("working")
  val pending = WorkStatusType("pending")
}
