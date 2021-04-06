ThisBuild / scalaVersion     := "2.13.5"
ThisBuild / version          := "0.0.1"
ThisBuild / organization     := "fp-in-scala"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  // "-Wvalue-discard",
  // "-Xfatal-warnings",
  "-Ymacro-annotations",
  // "-Ywarn-unused",
  "-Yrangepos"
)

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

lazy val commonSettings = Seq(
  resolvers += Resolver.mavenLocal,
  libraryDependencies ++= Dependencies.cats,
  libraryDependencies ++= Dependencies.fs2,
  libraryDependencies ++= Dependencies.decline,
  libraryDependencies ++= Dependencies.slf4j,
  libraryDependencies ++= Dependencies.http4s,
  libraryDependencies ++= Dependencies.munit map (_ % Test),
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  libraryDependencies += "org.typelevel" %% "simulacrum" % "1.0.0"
)

lazy val `session-01` = (project in file("sessions/01"))
  .settings(commonSettings: _*)
  .settings(name := "session-01")

lazy val root = (project in file("."))
  .aggregate(`session-01`)