import io.github.davidmweber.FlywayPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbt.Tests

object Commands {

	lazy val dbGenerate = Command.command("dbGenerate") { state =>
		// println(state)
	  	"dbgen/run" :: state
	}

	lazy val rmGenerated = Command.command("rmGenerated") { state =>
		// "dbgen/run" :: state
		"dbgen/run reset" :: state
	}	

	lazy val dbMigrate = Command.command("dbMigrate") { state =>
		val flywayLocation = "filesystem:modules/db/migration"
		s"""eval System.setProperty("flyway.locations", "${flywayLocation}")""" :: "flywayMigrate" :: state	 
	}

	lazy val dbReset = Command.command("dbReset") { state =>
		val flywayLocation = "filesystem:modules/db/migration"
		s"""eval System.setProperty("flyway.locations", "${flywayLocation}")""" :: "flywayClean" :: state
	}

	lazy val dbClean = Command.command("dbClean") { state =>
		val flywayLocation = "filesystem:modules/db/migration"
		s"""eval System.setProperty("flyway.locations", "${flywayLocation}")""" :: "flywayClean" :: state
	}

	lazy val dbSeed = Command.command("dbSeed") { state =>
		val seedLocation = "filesystem:modules/db/seed"
		s"""eval System.setProperty("flyway.locations", "${seedLocation}")""" :: "flywayMigrate" :: state
	}

	//TODO: do this
	lazy val dbDumpSeed = Command.command("dbDumpSeed") { state =>
		state
	}

	//TODO: do this
	lazy val dbRestoreSeed = Command.command("dbRestoreSeed") { state =>
		state
	}	

	lazy val dbRebuild = Command.command("dbRebuild") { state =>
		//reset 
		//migrate
		//generate

		state
	}	

	// sbt flywayMigrate
	// sbt flywayClean
	// dbSeed

	def commands = Seq(dbGenerate,dbMigrate,dbClean,dbReset,dbSeed,rmGenerated)

	// def commands = Seq()
}