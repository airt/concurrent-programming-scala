lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "learning.airt",
      scalaVersion := "2.12.2",
      version := "0.0.1"
    )),
    name := "learning-concurrent-programming",
    fork := false,
    scalacOptions ++= Seq("-deprecation"),
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.5",
      "com.jsuereth" %% "scala-arm" % "2.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test
    )
  )
