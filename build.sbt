import Dependencies._

ThisBuild / scalaVersion := "2.13.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "akka-http-websockets-example",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.typesafe.akka" %% "akka-http" % "10.1.9",
      "com.typesafe.akka" %% "akka-stream" % "2.6.0-M5",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.0-M5" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.9" % Test
    )
  )
