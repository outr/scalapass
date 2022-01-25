name := "scalapass"
organization := "com.outr"
version := "1.0.5"

scalaVersion := "2.13.8"
crossScalaVersions := List("2.13.8", "2.12.15", "3.1.0")
scalacOptions ++= Seq("-unchecked", "-deprecation")

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


fork := true

libraryDependencies ++= Seq(
  "com.outr" %% "profig" % "3.2.8",
  "de.mkammerer" % "argon2-jvm" % "2.11"
)