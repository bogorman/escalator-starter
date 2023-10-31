
lazy val escalatorDb = Projects.escalatorDb
lazy val escalatorSharedCommon = Projects.escalatorSharedCommon
lazy val escalatorCommon = Projects.escalatorCommon
lazy val escalatorFrontend = Projects.escalatorFrontend


lazy val common = Projects.common
lazy val core = Projects.core
lazy val backend = Projects.backend
lazy val cli = Projects.cli
lazy val sharedCommon = Projects.sharedCommon
lazy val shared = Projects.shared
lazy val frontend = Projects.frontend
lazy val cliJs = Projects.cliJs
lazy val persistence = Projects.persistence
lazy val externals = Projects.externals

lazy val dbgen = Projects.dbgen

commands ++= Commands.commands