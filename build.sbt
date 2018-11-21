name := "scalapass"
organization := "com.outr"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.7"

fork := true

libraryDependencies ++= Seq(
  "com.outr" %% "profig" % "2.3.2",
  "de.mkammerer" % "argon2-jvm" % "2.5"
)