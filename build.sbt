ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := Versions.scala

lazy val root = (project in file("."))
  .settings(
    organization := "eu.throup",
    name         := "komoot"
  )
  .settings(
    libraryDependencies ++= Seq(
      "io.circe"              %% "circe-generic"       % Versions.circe,
      "org.http4s"            %% "http4s-ember-server" % Versions.http4s,
      "org.http4s"            %% "http4s-ember-client" % Versions.http4s,
      "org.http4s"            %% "http4s-circe"        % Versions.http4s,
      "org.http4s"            %% "http4s-dsl"          % Versions.http4s,
      "org.typelevel"         %% "cats-core"           % Versions.cats,
      "org.typelevel"         %% "cats-effect"         % Versions.catsEffect,
      "software.amazon.awssdk" % "sns"                 % Versions.awsSdk
    ),
    libraryDependencies ++= Seq(
      "io.circe"          %% "circe-parser"         % Versions.circe,
      "io.circe"          %% "circe-testing"        % Versions.circe,
      "com.rallyhealth"   %% "scalacheck-ops_1-15"  % "2.8.2",
      "org.scalatest"     %% "scalatest"            % Versions.scalaTest,
      "org.scalatestplus" %% "scalacheck-1-16"      % Versions.scalaTestPlus,
      "org.typelevel"     %% "discipline-scalatest" % "2.1.5"
    ).map(_ % Test),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % Versions.logback
    ).map(_ % Runtime)
  )
  .enablePlugins(UniversalPlugin)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerExposedPorts ++= Seq(8080)
  )
