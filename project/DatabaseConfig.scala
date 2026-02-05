import io.github.davidmweber.FlywayPlugin.autoImport._
import sbt._

object DatabaseConfig {

    // Use Option to handle missing env vars during Docker build
    def dbHost = Option(System.getenv("DB_HOST")).getOrElse("localhost")
    def dbPort = Option(System.getenv("DB_PORT")).map(_.toInt).getOrElse(5432)
    def dbUser = Option(System.getenv("DB_USER")).getOrElse("postgres")
    def dbPass = Option(System.getenv("DB_PASSWORD")).getOrElse("")
    def dbName = Option(System.getenv("DB_NAME")).getOrElse("app")

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
