lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "learning.airt",
      scalaVersion := "2.12.2",
      version := "0.0.1"
    )),
    name := "learning-concurrent-programming",
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.5",
      "ch.qos.logback" % "logback-classic" % "1.2.+" % Test,
      "org.scalatest" %% "scalatest" % "3.0.+" % Test
    )
  )
