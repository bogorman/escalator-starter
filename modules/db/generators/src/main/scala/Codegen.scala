package db.generators

import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

import io.getquill.NamingStrategy
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

    val appFolder = System.getenv().get("PWD")

    val modelsBaseFolder = s"${appFolder}/modules/shared/src/main/scala/${packageDomain}/${packageAppName}/models"
    val persistenceBaseFolder = s"${appFolder}/modules/persistence/src/main/scala/"

    val databaseFolder = s"${persistenceBaseFolder}/database/"
    val postgresFolder = s"${persistenceBaseFolder}/postgres/"

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
      dbUrl)    

    val customGen = new AppCustomGenerator

    if (args.size > 0){
      CodeGenerator.reset(opts,customGen)
    } else {
      CodeGenerator.run(opts,customGen)  
    }
    
  }

}
