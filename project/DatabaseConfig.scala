import io.github.davidmweber.FlywayPlugin.autoImport._
import sbt._

object DatabaseConfig {

    def dbHost = System.getenv("DB_HOST")
    def dbPort = System.getenv("DB_PORT").toInt
    def dbUser = System.getenv("DB_USER")
    def dbPass = System.getenv("DB_PASSWORD")
    def dbName = System.getenv("DB_NAME")

  lazy val flywaySettings = Seq(
    flywayDriver := "org.postgresql.Driver",
    flywayUrl := s"jdbc:postgresql://${dbHost}:${dbPort}/${dbName}",
    flywayUser := dbUser,
    flywayPassword := dbPass,
    flywayLocations ++= Seq(
      "filesystem:modules/db/migration"//,
      // "filesystem:modules/db/seed"
    ),
    flywayOutOfOrder := true,
    flywayIgnoreMissingMigrations := true,
    flywayIgnoreFutureMigrations := true,
    flywayValidateOnMigrate := false
  )

  // var seedMode: Boolean = false 
  // def customFlywayLocations() = {
  //   if (seedMode){
  //     println("DatabaseConfig SEED MODE: location flyway locations")
  //     Seq("filesystem:modules/db/seed")
  //   } else {
  //     println("DatabaseConfig: location flyway locations")
  //     Seq("filesystem:modules/db/migration")
  //   }
  // }

}
