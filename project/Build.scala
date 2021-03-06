import sbt._
import Keys._

import xerial.sbt.Sonatype._
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact

import org.typelevel.sbt.TypelevelPlugin._

object BuildSettings {
  val buildScalaVersion = "2.11.2"

  val buildSettings = typelevelDefaultSettings ++ Seq(
    organization       := "com.github.julien-truffaut",
    scalaVersion       := buildScalaVersion,
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature",
      "-language:higherKinds", "-language:implicitConversions", "-language:postfixOps"),
    incOptions         := incOptions.value.withNameHashing(true),
    resolvers          += Resolver.sonatypeRepo("releases"),
    resolvers          += Resolver.sonatypeRepo("snapshots")
  )
}

object Dependencies {
  val monocleVersion    = "0.5.1-SNAPSHOT"
  val monocleCore       = "com.github.julien-truffaut"  %% "monocle-core"              % monocleVersion changing()
  val monocleMacro      = "com.github.julien-truffaut"  %% "monocle-macro"             % monocleVersion changing()
  val monocleGeneric    = "com.github.julien-truffaut"  %% "monocle-generic"           % monocleVersion changing()
  val scalaz            = "org.scalaz"                  %% "scalaz-core"               % "7.1.0"
  val shapeless         = "com.chuusai"                 %% "shapeless"                 % "2.0.0"
  val scalaCheckBinding = "org.scalaz"                  %% "scalaz-scalacheck-binding" % "7.1.0" % "test"
  val specs2Scalacheck  = "org.specs2"                  %% "specs2-scalacheck"         % "2.4"
  val scalazSpec2       = "org.typelevel"               %% "scalaz-specs2"             % "0.2"   % "test"
  val paradisePlugin    = compilerPlugin("org.scalamacros" % "paradise" %  "2.0.1" cross CrossVersion.full)
}

object TalkBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root: Project = Project(
    "talk",
    file("."),
    settings = buildSettings ++ Seq(
      publishArtifact := false
  )) aggregate(monocleIntro)

  lazy val monocleIntro: Project = Project(
    "optics-introduction",
    file("opticsIntroduction"),
    settings = buildSettings ++ Seq(
      addCompilerPlugin(paradisePlugin),
      libraryDependencies ++= Seq(monocleCore, monocleMacro, monocleGeneric, scalaz, shapeless, specs2Scalacheck, scalazSpec2)
    )
  )
}

