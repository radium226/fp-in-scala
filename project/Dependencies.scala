import sbt._

object Dependencies {

  lazy val kindProjector = "org.typelevel" % "kind-projector_2.13.5" % "0.11.3"

  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.2.3"
  )

  lazy val munit = Seq(
    "org.scalameta" %% "munit" % "0.7.22",
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.2"
  )

  lazy val cats = Seq(
    "org.typelevel" %% "cats-core" % "2.6.0",
    "org.typelevel" %% "cats-effect" % "3.1.0",
    "org.typelevel" %% "kittens" % "2.2.2",
    "org.typelevel" %% "mouse" % "1.0.2"
  )

  lazy val catsParse = Seq(
    "org.typelevel" %% "cats-parse" % "0.3.1"
  )

  lazy val fs2 = Seq(
    "co.fs2" %% "fs2-core" % "3.0.2",
    "co.fs2" %% "fs2-io" % "3.0.2"
  )

  lazy val selenium = Seq(
    "org.seleniumhq.selenium" % "selenium-java" % "3.141.59"
  )

  lazy val slf4j = Seq(
    "org.slf4j" % "slf4j-simple" % "1.7.30"
  )

  lazy val http4s = Seq(
    "org.http4s"      %% "http4s-blaze-server",
    "org.http4s"      %% "http4s-blaze-client",
    "org.http4s"      %% "http4s-circe",
    "org.http4s"      %% "http4s-dsl"
  ) map(_ % Versions.http4s)

  lazy val decline = Seq(
    "com.monovore" %% "decline" % "2.0.0"
  )

  lazy val azure = Seq(
    "com.microsoft.cognitiveservices.speech" % "client-sdk" % "1.15.0"
  )

}
