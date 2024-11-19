import sbt._
import sbt.Keys._

// import sbt._
import sbt.Def.setting
// import sbt.Keys.scalaVersion

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  // val snapsotResolvers = Seq(
  //   // Resolver.bintrayRepo("kamon-io", "releases"),
  //   Resolver.sonatypeOssRepos("snapshots")
  //   // "custom-repo" at "https://s3-eu-west-1.amazonaws.com/bm-jars/",
  //   // "Kolich repo" at "https://markkolich.github.io/repo"
  //   // Resolver.JCenterRepository,
  // )

  // val releaseResolvers = Seq(
  //     Resolver.sonatypeRepo("releases")
  // )

   // val resolvers: Seq[Resolver] = Seq(
   //    // Resolver.mavenLocal,
   //    // Resolver.mavenCentral,
   //    Resolver.sonatypeRepo("releases")
   //  ) ++ Resolver.sonatypeOssRepos("snapshots")

   val resolvers: Seq[Resolver] = Resolver.sonatypeOssRepos("snapshots")


  // val akkaActor: ModuleID = "com.typesafe.akka" %% "akka-actor" % Versions.akkaVersion
  // val akkaSlf4j: ModuleID = "com.typesafe.akka" %% "akka-slf4j" % Versions.akkaVersion
  // val akkaStream: ModuleID = "com.typesafe.akka" %% "akka-stream" % Versions.akkaVersion
  // val akkaRemote: ModuleID = "com.typesafe.akka" %% "akka-remote" % Versions.akkaVersion

  // // val akkaCluster: ModuleID = "com.typesafe.akka" %% "akka-cluster" % Versions.akkaVersion
  // // val akkaPersistence: ModuleID = "com.typesafe.akka" %% "akka-persistence" % Versions.akkaVersion

  // val akkaHttp: ModuleID = "com.typesafe.akka" %% "akka-http" % Versions.akkaHttpVersion
  // // val akkaHttpCaching: ModuleID = "com.typesafe.akka" %% "akka-http-caching" % Versions.akkaHttpVersion
  // val akkaSprayJson: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttpVersion

  // val akkaHttpCirce: ModuleID = "de.heikoseeberger" %% "akka-http-circe" % Versions.akkaHttpJson
  // // val akkaHttpUpickle: ModuleID = "de.heikoseeberger" %% "akka-http-upickle" % Versions.akkaHttpJson
  // // val akkaHttpPlay: ModuleID = "de.heikoseeberger" %% "akka-http-play-json" % Versions.akkaHttpJson


  val pekkoActor: ModuleID = "org.apache.pekko" %% "pekko-actor" % Versions.pekkoVersion
  val pekkoSlf4j: ModuleID = "org.apache.pekko" %% "pekko-slf4j" % Versions.pekkoVersion
  val pekkoStream: ModuleID = "org.apache.pekko" %% "pekko-stream" % Versions.pekkoVersion
  val pekkoRemote: ModuleID = "org.apache.pekko" %% "pekko-remote" % Versions.pekkoVersion

  val pekkoHttp: ModuleID = "org.apache.pekko" %% "pekko-http" % Versions.pekkoHttpVersion
  val pekkoSprayJson: ModuleID = "org.apache.pekko" %% "pekko-http-spray-json" % Versions.pekkoHttpVersion
  // val pekkoHttpCirce: ModuleID = "org.mdedetrich" %% "pekko-http-circe" % Versions.pekkoHttpVersion
  val pekkoHttpCirce: ModuleID = "com.github.pjfanning" %% "pekko-http-circe" % "0.0.0_1-2d3e5833-SNAPSHOT"
  //Versions.pekkoHttpVersion

  val commonsLang3: ModuleID = "org.apache.commons" % "commons-lang3" % "3.4"

  val sttpCore: ModuleID = "com.softwaremill.sttp.client3" %% "core" % Versions.sttpVersion
  val sttpCirce: ModuleID = "com.softwaremill.sttp.client3" %% "circe" % Versions.sttpVersion

  val sttpOkHttpBackend: ModuleID = "com.softwaremill.sttp.client3" %% "okhttp-backend" % Versions.sttpVersion

  val monixEval: ModuleID = "io.monix" %% "monix-eval" % Versions.monixVersion

  val postgres: ModuleID = "org.postgresql" % "postgresql" % "42.2.8"

  val quillMonix: ModuleID = "io.getquill" %% "quill-jdbc-monix" % Versions.quillVersion
  val quillAsync: ModuleID = "io.getquill" %% "quill-async-postgres" % Versions.quillVersion

  val flyWay: ModuleID = "org.flywaydb" % "flyway-core" % Versions.flywayVersion

  // val cediConfig: ModuleID = "com.ccadllc.cedi" %% "config" % "1.2.0"

  val pureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % "0.17.4"
  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % "1.2.3"

  // val catsCore: ModuleID = "org.typelevel" %% "cats-core" % "2.12.0"

  val kamonCore: ModuleID = "io.kamon" %% "kamon-core" % Versions.kamonVersion
  val kamonSystemMetrics: ModuleID = "io.kamon" %% "kamon-system-metrics" % Versions.kamonVersion
  val kamonInfluxDB: ModuleID = "io.kamon" %% "kamon-influxdb" % Versions.kamonVersion
  val kamonLogReporter: ModuleID = "io.kamon" %% "kamon-log-reporter" % Versions.kamonVersion

  val playJson: ModuleID = "com.typesafe.play" %% "play-json" % Versions.playVersion
  val twirlApi: ModuleID = "com.typesafe.play" %% "twirl-api" % "1.6.5" //"1.5.1"
  val lucene4s: ModuleID = "com.outr" %% "lucene4s" % "1.11.1"

  // "2.4.1"/
  val ammoniteSshd: ModuleID = "com.lihaoyi" % "ammonite-sshd" % "2.5.11" cross CrossVersion.full

  val circeGeneric: ModuleID = "io.circe" %% "circe-generic" % Versions.circeVersion
  val circeParser: ModuleID = "io.circe"  %% "circe-parser" % Versions.circeVersion
  // val circeRefined: ModuleID = "io.circe" %% "circe-refined" % Versions.circeVersion

  val enumeratum: ModuleID = "com.beachape" %% "enumeratum" % Versions.enumeratumVersion

  val upickle: ModuleID = "com.lihaoyi" %% "upickle" % Versions.upickleVersion

  val colorThief: ModuleID = "de.androidpit" % "color-thief" % "1.1.2"
  val betterFiles: ModuleID = "com.github.pathikrit" %% "better-files" % "3.9.2" //3.9.1
  val scalaFmt: ModuleID = "org.scalameta" %% "scalafmt-dynamic" % "3.7.9"  //"2.6.1"
  val scalaMeta: ModuleID = "org.scalameta" %% "scalameta" % "4.8.4"  //"4.3.13"
  // val evoInflector: ModuleID =  "org.atteo" % "evo-inflector" % "1.3"

  val scribe: ModuleID =  "com.outr" %% "scribe" % Versions.scribe
  val unindent: ModuleID =  "com.davegurnell" %% "unindent" % "1.6.0" 

  val scalaJsScripts: ModuleID = "com.vmunier" %% "scalajs-scripts" % "1.2.0"

  // val jodd: ModuleID = "org.jodd" % "jodd-mail" % "6.0.5"
  val jakartaMail = "com.sun.mail" % "jakarta.mail" % "1.6.5"
  val jakartaMailApi = "jakarta.mail" % "jakarta.mail-api" % "1.6.5"

  val caffeine: ModuleID = "com.github.cb372" %% "scalacache-caffeine" % "0.28.0"

  // val t3hnar: ModuleID = "com.github.t3hnar" %% "scala-bcrypt" % "4.1"
  val bcrypt: ModuleID = "org.mindrot" % "jbcrypt" % "0.4"

  val urlDslShared: ModuleID = "be.doeraene" %% "url-dsl" % Versions.urlDsl

  // val circeGenericExtras: ModuleID = "io.circe" %% "circe-generic-extras" % Versions.circeVersion
  // val circeOptics: ModuleID = "io.circe" %% "circe-optics" % Versions.circeVersion
  // val circeLiteral: ModuleID = "io.circe" %% "circe-literal" % Versions.circeVersion
 
  val javaWebSocket: ModuleID = "org.java-websocket" % "Java-WebSocket" % "1.4.0"
  // val quantego: ModuleID = "com.quantego" % "clp-java" % "1.16.10"

  // val circeConfig: ModuleID = "io.circe" %% "circe-config" % "0.8.0"
  val circeConfig: ModuleID = "com.hunorkovacs" %% "circe-config" % "0.9.0"

  // val jacksonCore: ModuleID = "com.fasterxml.jackson.core" % "jackson-core" % "2.9.8"
  val jacksonSchema: ModuleID = "com.kjetland" %% "mbknor-jackson-jsonschema" % "1.0.35"

  val jacksonCore: ModuleID = "com.fasterxml.jackson.core" % "jackson-core" % "2.14.1"
  val jacksonDatabind: ModuleID = "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.1"
  val jacksonScala: ModuleID = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1"
  val jacksonJoda: ModuleID = "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.14.1"

  val sendgrid: ModuleID = "com.sendgrid" % "sendgrid-java" % "4.7.2"

  val jedis: ModuleID = "redis.clients" % "jedis" % "2.9.0"

  val kolichCommon: ModuleID = "com.kolich" % "kolich-common" % "0.5.0" % "compile" from "https://markkolich.github.io/repo/com/kolich/kolich-common/0.5/kolich-common-0.5.jar"
 
  val commonsCodec: ModuleID = "commons-codec" % "commons-codec" % "1.10"  
  val commonsIO: ModuleID = "commons-IO" % "commons-io" % "2.6"  

  val logstash: ModuleID = "net.logstash.logback" % "logstash-logback-encoder" % "6.3"
  
  val sttpCoreLegacy: ModuleID = "com.softwaremill.sttp.client" %% "core" % "2.0.7"
  val sttpOkHttpBackendLegacy: ModuleID = "com.softwaremill.sttp.client" %% "okhttp-backend" % "2.0.7"

  val lombok: ModuleID =  "org.projectlombok"   %  "lombok"          % "1.18.22"

  val autowire: ModuleID = "com.lihaoyi" %% "autowire" % Versions.autowire

  val scalaCompiler: ModuleID = "org.scala-lang" % "scala-compiler" % Common.sVersion

  // "2.2.0" 
  // val scalaParserCombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

  val commonDependencies = Seq(
    pekkoSlf4j,
    pekkoHttp,
    // akkaHttpCaching,
    pekkoSprayJson,
    pekkoStream,
    //
    sttpCore,
    sttpCirce,
    //
    monixEval,
    postgres,
    quillMonix,
    quillAsync,
    //    
    kamonCore,
    kamonSystemMetrics,
    // catsCore,
    pureConfig,
    logback,
    playJson,

    circeGeneric,
    circeParser,
    // circeRefined,

    enumeratum,
    colorThief,
    betterFiles,
    scalaFmt,
    scalaMeta,
    // evoInflector,
    //
    // jodd,
    jakartaMail,
    jakartaMailApi,
    caffeine,
    // t3hnar,
    bcrypt,
    jedis,
    // dataDiff

    jacksonCore,
    jacksonDatabind,
    jacksonScala,
    jacksonJoda,
    //
    sendgrid,
    commonsLang3,
    // scalaParserCombinators
  )

  val backendDependencies = Seq(
    pekkoActor,
    pekkoSlf4j,
    pekkoHttp,
    // akkaHttpCaching,
    pekkoStream,
    // akkaCluster,
    pekkoHttpCirce,
    // akkaHttpUpickle,
    // akkaHttpPlay,
    // 
    sttpCore,
    sttpCirce,
    // sttpHttpClientBackendMonix,
    //
    monixEval,
    postgres,
    quillMonix,
    quillAsync,
    
    logback,
    playJson,
    twirlApi,
    lucene4s,
    
    ammoniteSshd,
    
    circeGeneric,
    circeParser,
    // circeRefined,
  
    enumeratum,
    betterFiles,
    scalaFmt,
    scalaMeta,
    // evoInflector,

    scribe,
    unindent,

    scalaJsScripts,


    scalaCompiler,
    //
    // jodd,
    caffeine,

    kolichCommon,
    commonsCodec,
    commonsIO,

    javaWebSocket,
    lombok,

    autowire,
    // wireo

    // scalaParserCombinators
  )

  val sharedDependencies = Seq(
    circeGeneric,
    enumeratum,
    upickle,
    sttpCore,
    urlDslShared
  )

  def externalDependencies = Seq(

  )


}
