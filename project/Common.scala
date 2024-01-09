import sbt._
import sbt.Keys._
import sbt.Tests

object Common {
  val mainScalacOptions = Seq(
    "-Xlint",
    // "-Xfatal-warnings",
    // "-Ywarn-unused-import",
    "-Ywarn-unused:imports",
    "-Ywarn-unused",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    // "-Ywarn-adapted-args",
    "-Ywarn-value-discard",
    // "-Ypartial-unification",
    "-unchecked",
    "-deprecation",
    "-feature",
    // "-Xmax-classfile-name", "100"
    "-Ymacro-annotations"
  )

  // val mainScalacOptions = Seq(
  //    "-Ymacro-annotations"
  // )

  val excludeConsoleScalacOptions = Seq(
    "-Xlint",
    // "-Ywarn-unused-import",
    "-Ywarn-unused:imports",
    "-Ywarn-unused"
  )

  val sVersion = "2.13.6"

  val baseSettings = List(
    organization := "com.escalatorstarter",
    scalacOptions ++= mainScalacOptions,
    scalacOptions in (Compile, console) := mainScalacOptions.filterNot(excludeConsoleScalacOptions.contains),

    // javaOptions ++= Seq("--add-opens", "java.base/java.util=ALL-UNNAMED", "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED", "--add-opens", "java.base/java.math=ALL-UNNAMED"),
    // javaOptions ++= Seq("--add-opens", "java.base/java.nio=ALL-UNNAMED", "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED"),
    javaOptions ++= Seq("--add-opens", "java.base/java.util=ALL-UNNAMED", "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED", "--add-opens", "java.base/java.math=ALL-UNNAMED"),


    scalaVersion := sVersion,
    // scalaVersion := "2.12.12",
    testOptions in Test += Tests.Argument("-oFDI"),
    testForkedParallel := false,
    parallelExecution in Test := false,
    // parallelExecution in IntegrationTest := false,
    resolvers ++= Dependencies.resolvers,
    // resolvers ++= Dependencies.releaseResolvers,
    // resolvers ++= Dependencies.snapsotResolvers
  )

  val jvmSettings = (fork := System.getProperty("sbt.fork.test", "true") == "true") :: baseSettings
  val jsSettings = baseSettings

  val compilerPlugins = Seq(
    // "org.spire-math" %% "kind-projector" % "0.9.5",
    // "com.github.ghik" %% "silencer-plugin" % Dependencies.silencerVersion
  )
}
