source source_local.sh && sbt dbReset && sbt dbMigrate && sbt dbSeed && sbt dbGenerate
