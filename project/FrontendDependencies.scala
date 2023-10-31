import sbt._
import sbt.Keys._

// import sbt._
import sbt.Def.setting
// import sbt.Keys.scalaVersion

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object FrontendDependencies {

    val laminar: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.raquo" %%% "laminar" % Versions.laminar
      )
    }

    val autowire: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.lihaoyi" %%% "autowire" % Versions.autowire
      )
    }

    val catsEffect: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "org.typelevel" %%% "cats-effect" % "3.4.2"
      )
    }

    val laminext: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "io.laminext" %%% "core"            % Versions.laminext,
        "io.laminext" %%% "validation-core" % Versions.laminext,
        "io.laminext" %%% "validation-cats" % Versions.laminext,
        "io.laminext" %%% "fetch"           % Versions.laminext,
        "io.laminext" %%% "fetch-circe"     % Versions.laminext,
        "io.laminext" %%% "markdown"        % Versions.laminext,
        "io.laminext" %%% "websocket"       % Versions.laminext,
        "io.laminext" %%% "websocket-circe" % Versions.laminext,
        "io.laminext" %%% "util"            % Versions.laminext
      )
    }

    val airstream: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.raquo" %%% "airstream" % Versions.airstream
      )
    }  

    val circeJs: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "io.circe" %%% "circe-core"                   % Versions.circeVersion,
        "io.circe" %%% "circe-generic"                % Versions.circeVersion,
        "io.circe" %%% "circe-parser"                 % Versions.circeVersion
      )
    }

    val waypoint: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.raquo" %%% "waypoint" % Versions.waypoint
      )
    }

    val urlDsl: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "be.doeraene" %%% "url-dsl" % Versions.urlDsl
      )
    }  

    val upickleJs: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.lihaoyi" %%% "upickle" % Versions.upickleVersion
      )
    }

    val akkaJsVersion: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "org.akka-js" %%% "akkajsactor" % Versions.akkaJsVersion,
        "org.akka-js" %%% "akkajsactorstream" % Versions.akkaJsVersion
      )
    }  

    val sttpClientJsVersion: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
         // "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC1"
         "com.softwaremill.sttp.client3" %%% "core" % Versions.sttpVersion
      )
    }  

    val playVersion = "2.9.3"

    val scalaJsDom: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
         "org.scala-js" %%% "scalajs-dom" % "2.0.0",
         "org.scala-js" %%% "scalajs-fake-insecure-java-securerandom" % "1.0.0"
      )
    }

    val scalaJsPlayJson: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
         "com.typesafe.play" %%% "play-json" % playVersion
      )
    }

    val scalaJsReactVersion = "2.1.1"
    val scalaJsReact: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "core-ext-cats" % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "core-ext-cats_effect" % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra" % scalaJsReactVersion
      )
    }

    // val scalaCssVersion = "0.7.0"
    val scalaCssVersion = "1.0.0"
    val scalaCss: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(
        "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
        "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion
      )
    }

    /// 
    val scalablyTyped: Def.Initialize[Seq[ModuleID]] = Def.setting {
      Seq(

      )
    }
}

