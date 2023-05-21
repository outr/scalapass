ThisBuild / organization := "com.outr"
ThisBuild / version := "1.2.5"

ThisBuild / scalaVersion := "2.13.10"
ThisBuild / crossScalaVersions := List("2.13.10", "3.2.2")

ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
publishTo := sonatypePublishTo.value
sonatypeProfileName := "com.outr"
publishMavenStyle := true
licenses := Seq("MIT" -> url("https://github.com/outr/scalapass/blob/master/LICENSE"))
sonatypeProjectHosting := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "scalapass", "matt@outr.com"))
homepage := Some(url("https://github.com/outr/scalapass"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/outr/scalapass"),
    "scm:git@github.com:outr/scalapass.git"
  )
)
developers := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
)

ThisBuild / Test / fork := true
ThisBuild / Test / testOptions += Tests.Argument("-oD")

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalapass",
    libraryDependencies ++= Seq(
      "com.outr" %% "profig" % "3.4.9",
      "de.mkammerer" % "argon2-jvm" % "2.11",
      "commons-codec" % "commons-codec" % "1.15",
      "org.scalatest" %% "scalatest" % "3.2.16" % "test"
    )
  )

lazy val docs = project
  .in(file("documentation"))
  .dependsOn(root)
  .enablePlugins(MdocPlugin)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    mdocOut := file(".")
  )