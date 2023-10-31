import com.typesafe.sbt.GitVersioning
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport._

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

import io.github.davidmweber.FlywayPlugin
import io.github.davidmweber.FlywayPlugin.autoImport._

import play.twirl.sbt.SbtTwirl


import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.SbtWeb.autoImport._

import webscalajs.ScalaJSWeb
import webscalajs.WebScalaJS.autoImport.{scalaJSPipeline, scalaJSProjects}

import sbt._
import sbt.Keys._
import spray.revolver.RevolverPlugin

object Projects {
    lazy val root = project.in(file("."))
      .aggregate(common,backend,dbgen)
      .settings(Common.baseSettings: _*)

    lazy val escalatorCommon: Project = project.in(file("modules/escalator/modules/escalator-common"))
      .dependsOn(
        escalatorSharedCommonJVM
      )
      .settings(Common.jvmSettings: _*)
      .settings(
        libraryDependencies ++= Dependencies.commonDependencies
      )      

    lazy val common: Project = project.in(file("modules/common"))
      .dependsOn(
        sharedCommonJVM,
        escalatorCommon
      )
      .settings(Common.jvmSettings: _*)
      .settings(
        libraryDependencies ++= Dependencies.commonDependencies
      )      

    lazy val externals: Project = project.in(file("modules/externals"))
      .settings(Common.jvmSettings: _*)
      .settings(
        libraryDependencies ++= Dependencies.externalDependencies
      )      

    lazy val escalatorSharedCommon = crossProject(JSPlatform,JVMPlatform)
      .crossType(CrossType.Pure)
      .in(file("modules/escalator/modules/escalator-shared-common"))
      .settings(Common.baseSettings: _*)
      .settings(
        libraryDependencies ++= Dependencies.sharedDependencies
      )
      .jsConfigure(_.enablePlugins(ScalaJSWeb))
      .jsConfigure(_.disablePlugins(RevolverPlugin))

    lazy val escalatorSharedCommonJVM: Project = escalatorSharedCommon.jvm
    lazy val escalatorSharedCommonJS: Project = escalatorSharedCommon.js


    lazy val sharedCommon = crossProject(JSPlatform,JVMPlatform)
      .crossType(CrossType.Pure)
      .dependsOn(
        escalatorSharedCommon
      )      
      .in(file("modules/shared-common"))
      .settings(Common.baseSettings: _*)
      .settings(
        libraryDependencies ++= Dependencies.sharedDependencies
      )
      .jsConfigure(_.enablePlugins(ScalaJSWeb))
      .jsConfigure(_.disablePlugins(RevolverPlugin))

    lazy val sharedCommonJVM: Project = sharedCommon.jvm
    lazy val sharedCommonJS: Project = sharedCommon.js

    lazy val shared = crossProject(JSPlatform,JVMPlatform)
      .crossType(CrossType.Pure)
      .dependsOn(
        sharedCommon
      )
      .in(file("modules/shared"))
      .settings(Common.baseSettings: _*)
      .settings(
        libraryDependencies ++= Dependencies.sharedDependencies
      )
      .jsConfigure(_.enablePlugins(ScalaJSWeb))
      .jsConfigure(_.disablePlugins(RevolverPlugin))

    lazy val sharedJVM: Project = shared.jvm
    lazy val sharedJS: Project = shared.js

    lazy val persistence: Project = project.in(file("modules/persistence"))
      .dependsOn(
        sharedJVM,
        common
      )
      .settings(Common.jvmSettings: _*)
      .settings(
        // Common.compilerPlugins.map(addCompilerPlugin),
        libraryDependencies ++= Dependencies.commonDependencies
      )      

    lazy val core: Project = project.in(file("modules/core"))
    .dependsOn(
      sharedJVM,
      common,
      externals,
      persistence
    )
    // .configs(Configurations.IntTest)
    .settings(Common.jvmSettings: _*)
    .settings(
        // Common.compilerPlugins.map(addCompilerPlugin),
        libraryDependencies ++= Dependencies.backendDependencies
      )      


    lazy val backend: Project = project.in(file("modules/backend"))
    .dependsOn(
      core//,
    )
    .settings(Common.jvmSettings: _*)
    .settings(MergeAssembly.mergeSettings)
    .settings(
      scalaJSProjects := Seq(frontend),
      Assets / pipelineStages := Seq(scalaJSPipeline),      
      Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
      // inConfig(Configurations.IntTest)(Defaults.testSettings),
      resourceGenerators in Compile += Def.task {
        val f1 = (ScalaJSPlugin.autoImport.fullOptJS in Compile in frontend).value
        Seq(f1.data)
      }.taskValue,
      libraryDependencies ++= Dependencies.backendDependencies,
      libraryDependencies := libraryDependencies.value.map {
        case module if module.name == "twirl-api" =>
          module.cross(CrossVersion.for3Use2_13)
        case module => module
      },
      Assets / WebKeys.packagePrefix := "public/",
      Runtime / managedClasspath += (Assets / packageBin).value      
    )
    .enablePlugins(SbtWeb, SbtTwirl, JavaAppPackaging, GitVersioning)

   lazy val escalatorDb: Project = project.in(file("modules/escalator/modules/escalator-db/generators"))
    .dependsOn(
      common
    )   
    .settings(Common.jvmSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.backendDependencies
    )

   lazy val dbgen: Project = project.in(file("modules/db/generators"))
    .dependsOn(
      escalatorDb,
      common
    )   
    .settings(DatabaseConfig.flywaySettings: _*)    
    .settings(Common.jvmSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.backendDependencies
    ).enablePlugins(FlywayPlugin)

    lazy val cli: Project = project.in(file("modules/cli"))
    .dependsOn(
      sharedJVM,
      common,
      backend,
    )
    .settings(Common.jvmSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.backendDependencies,
    )
    .enablePlugins(GitVersioning, JavaAppPackaging)

  ////////

  lazy val escalatorFrontend: Project = project.in(file("modules/escalator/modules/escalator-frontend"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(escalatorSharedCommonJS)
    .settings(Common.jsSettings: _*)
    .settings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },

      Common.compilerPlugins.map(addCompilerPlugin),  

      libraryDependencies ++= Seq.concat(
        FrontendDependencies.scalaJsDom.value,
        FrontendDependencies.laminar.value,
        FrontendDependencies.airstream.value,
        FrontendDependencies.laminext.value,
        FrontendDependencies.waypoint.value,
        FrontendDependencies.circeJs.value,
        FrontendDependencies.urlDsl.value,
        FrontendDependencies.sttpClientJsVersion.value,
        FrontendDependencies.akkaJsVersion.value       
      ),
      scalaJSUseMainModuleInitializer := true,
    )
    .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
    .disablePlugins(RevolverPlugin)


  lazy val frontend: Project = project.in(file("modules/frontend"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(escalatorFrontend, sharedJS, sharedCommonJS)
    .settings(Common.jsSettings: _*)
    .settings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      Common.compilerPlugins.map(addCompilerPlugin),
      libraryDependencies ++= Seq.concat(

        FrontendDependencies.scalaJsDom.value,
        FrontendDependencies.laminar.value,
        FrontendDependencies.autowire.value,
        FrontendDependencies.catsEffect.value,
        FrontendDependencies.airstream.value,
        FrontendDependencies.laminext.value,
        FrontendDependencies.waypoint.value,
        FrontendDependencies.circeJs.value,
        FrontendDependencies.urlDsl.value,
        FrontendDependencies.scalablyTyped.value,
        FrontendDependencies.scalaJsReact.value, //pulls in cats
        FrontendDependencies.scalaCss.value,
        //
        FrontendDependencies.upickleJs.value,
        FrontendDependencies.akkaJsVersion.value,
        //
        FrontendDependencies.sttpClientJsVersion.value,
        //
        FrontendDependencies.scalaJsPlayJson.value
      ),

      scalaJSUseMainModuleInitializer := true,
    )
    .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
    .disablePlugins(RevolverPlugin)


  lazy val cliJs: Project = project.in(file("modules/cli-js"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(sharedJS, sharedCommonJS)
    .settings(Common.jsSettings: _*)
    .settings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },

      libraryDependencies ++= Seq.concat(
        FrontendDependencies.scalablyTyped.value
      ),

      scalaJSUseMainModuleInitializer := true,
    )
    .enablePlugins(ScalaJSPlugin)
      // Common.compilerPlugins.map(addCompilerPlugin),

}
