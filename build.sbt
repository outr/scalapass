name := "scalapass"
organization := "com.outr"
version := "1.0.2"

scalaVersion := "2.12.7"
crossScalaVersions in ThisBuild := List("2.12.7", "2.11.12")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

publishTo := sonatypePublishTo.value
sonatypeProfileName := "com.outr"
publishMavenStyle := true
licenses in ThisBuild := Seq("MIT" -> url("https://github.com/outr/scalapass/blob/master/LICENSE"))
sonatypeProjectHosting in ThisBuild := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "scalapass", "matt@outr.com"))
homepage in ThisBuild := Some(url("https://github.com/outr/scalapass"))
scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/outr/scalapass"),
    "scm:git@github.com:outr/scalapass.git"
  )
)
developers in ThisBuild := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
)


fork := true

libraryDependencies ++= Seq(
  "com.outr" %% "profig" % "2.3.2",
  "de.mkammerer" % "argon2-jvm" % "2.5"
)