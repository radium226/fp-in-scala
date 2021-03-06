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
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full),
  resolvers += Resolver.mavenLocal,
  libraryDependencies ++= Dependencies.munit map (_ % Test),
)

lazy val `session-01` = (project in file("sessions/01"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Dependencies.cats,
    libraryDependencies ++= Dependencies.fs2,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    libraryDependencies += "org.typelevel" %% "simulacrum" % "1.0.0"
  )
  .settings(name := "session-01")

lazy val `session-02` = (project in file("sessions/02"))
  .settings(commonSettings: _*)
  .settings(name := "session-02")

lazy val `session-03` = (project in file("sessions/03"))
  .settings(commonSettings: _*)
  .settings(name := "session-03")
  .settings(
    libraryDependencies ++= Dependencies.cats,
    libraryDependencies ++= Dependencies.fs2,
  )

lazy val root = (project in file("."))
  .aggregate(
    `session-01`,
    `session-02`,
    `session-03`
  )