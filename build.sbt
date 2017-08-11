lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "learning.airt",
      scalaVersion := "2.12.3",
      version := "0.0.1"
    )),
    name := "learning-concurrent-programming",
    fork := false,
    scalacOptions ++= Seq("-deprecation"),
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.5",
      "com.jsuereth" %% "scala-arm" % "2.0",
      "io.reactivex" %% "rxscala" % "0.26.5",
      "org.scala-stm" %% "scala-stm" % "0.8",
      "org.scala-lang.modules" %% "scala-async" % "0.9.7",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test
    )
  )
