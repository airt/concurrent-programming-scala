lazy val root = (project in file(".")) settings (
  inThisBuild(
    Seq(
      organization := "learning",
      scalaVersion := "2.12.4",
      version := "0.0.1"
    )
  ),
  name := "learning-concurrent-programming",
  fork := false,
  scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked"),
  libraryDependencies ++= Seq(
    "commons-io" % "commons-io" % "2.6",
    "com.lihaoyi" %% "pprint" % "0.5.3",
    "com.jsuereth" %% "scala-arm" % "2.0",
    "io.reactivex" %% "rxscala" % "0.26.5",
    "org.scala-stm" %% "scala-stm" % "0.8",
    "com.typesafe.akka" %% "akka-actor" % "2.5.7",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scala-lang.modules" %% "scala-async" % "0.9.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "com.typesafe.akka" %% "akka-testkit" % "2.5.7" % Test
  )
)
