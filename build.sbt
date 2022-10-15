import com.amazonaws.regions.{Region, Regions}

ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := Versions.scala

lazy val root = (project in file("."))
  .settings(
    organization := "eu.throup",
    name         := "komoot"
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging"       % Versions.scalaLogging,
      "io.circe"                   %% "circe-generic"       % Versions.circe,
      "io.circe"                   %% "circe-parser"        % Versions.circe,
      "org.http4s"                 %% "http4s-ember-server" % Versions.http4s,
      "org.http4s"                 %% "http4s-ember-client" % Versions.http4s,
      "org.http4s"                 %% "http4s-circe"        % Versions.http4s,
      "org.http4s"                 %% "http4s-dsl"          % Versions.http4s,
      "org.typelevel"              %% "cats-core"           % Versions.cats,
      "org.typelevel"              %% "cats-effect"         % Versions.catsEffect,
      "software.amazon.awssdk"      % "sns"                 % Versions.awsSdk
    ),
    libraryDependencies ++= Seq(
      "io.circe"          %% "circe-testing"        % Versions.circe,
      "com.rallyhealth"   %% "scalacheck-ops_1-15"  % "2.11.0",
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
  .enablePlugins(EcrPlugin)
  .settings(
    Ecr / region           := Region.getRegion(Regions.EU_WEST_1),
    Ecr / repositoryName   := (Docker / packageName).value,
    Ecr / localDockerImage := (Docker / packageName).value + ":" + (Docker / version).value,

    // Create the repository before authentication takes place (optional)
    Ecr / login := ((Ecr / login) dependsOn (Ecr / createRepository)).value,

    // Authenticate and publish a local Docker image before pushing to ECR
    Ecr / push := ((Ecr / push) dependsOn (Docker / publishLocal, Ecr / login)).value
  )
