package db.generators

import escalator.db.generators._

object Codegen {

  def main(args: Array[String]): Unit = {
    val dbHost = System.getenv("DB_HOST")
    val dbPort = System.getenv("DB_PORT").toInt
    val dbUser = System.getenv("DB_USER")
    val dbPass = System.getenv("DB_PASSWORD")
    val dbName = System.getenv("DB_NAME")

    val dbUrl = s"jdbc:postgresql://${dbHost}:${dbPort}/${dbName}"
    println("dbUrl:" + dbUrl)

    if (dbHost == null || dbHost == ""){
      throw new Exception("Missing DB Enviroment variables")
    }

    //<CHANGE THIS> if needed
    val packageName = "com.escalatorstarter"
    val appName = "EscalatorStarter"

    val packageDomain = packageName.split("\\.")(0)
    val packageAppName = packageName.split("\\.")(1)

    // Use relative path within project structure instead of PWD to ensure files are generated in correct location  
    // val currentDir = System.getProperty("user.dir")
    // val appFolder = currentDir

    val appFolder = System.getenv().get("PWD")

    val modelsBaseFolder = s"${appFolder}/modules/shared/src/main/scala/${packageDomain}/${packageAppName}/models"
    val persistenceBaseFolder = s"${appFolder}/modules/persistence/src/main/scala/"

    val databaseFolder = s"${persistenceBaseFolder}/database/"
    val postgresFolder = s"${persistenceBaseFolder}/postgres/"

    val generateEvents: Boolean = true
    val generateAppRepositories: Boolean = true
    val repositoriesFolder = s"${appFolder}/modules/core/src/main/scala/${packageDomain}/${appName.toLowerCase}/core/repositories/postgres"

    val generateAggregates: Boolean = false
    // val aggregatesFolder: String = "" // e.g. "modules/core/src/main/scala/aggregates"
    val aggregatesFolder = s"${appFolder}/modules/core/src/main/scala/${packageDomain}/${appName.toLowerCase}/core/repositories/aggregates"
     // ++ List("users", "orders", "products", "accounts", "organizations")
    val aggregateRootTables: List[String] = List("users") // e.g. List("users", "orders", "products")
    val maxAggregateDepth: Int = 3
    
    val generatePekkoActors: Boolean = false
    val aggregateBoundaryHints: Map[String, Boolean] = Map.empty

    val opts = CodegenOptions(
      packageName,
      appName,
      appFolder,
      modelsBaseFolder, 
      persistenceBaseFolder, 
      databaseFolder, 
      postgresFolder,
      dbUser,
      dbPass,
      dbUrl,
      "public",
      "org.postgresql.Driver",
      """import io.getquill.WrappedValue""",
      "tables",
      List("schema_version","flyway_schema_history"),
      None,
      // // Event generation options
      generateEvents,
      generateAppRepositories,
      repositoriesFolder,
      //
      generateAggregates,
      aggregatesFolder,
      aggregateRootTables,
      maxAggregateDepth,
      generatePekkoActors,
      aggregateBoundaryHints
    )    

    val customGen = new AppCustomGenerator

    if (args.size > 0){
      CodeGenerator.reset(opts,customGen)
    } else {
      CodeGenerator.run(opts,customGen)  
    }
    
  }

}
