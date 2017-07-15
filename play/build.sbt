import com.typesafe.config.ConfigFactory

name := """play-catch-up"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies += filters

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.24",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

EclipseKeys.preTasks := Seq(compile in Compile)

wartremoverWarnings ++= Warts.unsafe
wartremoverWarnings --= Seq(Wart.NonUnitStatements,Wart.Throw,Wart.DefaultArguments)

wartremoverExcluded ++= routes.in(Compile).value
wartremoverExcluded ++= Seq(
	baseDirectory.value / "app" / "models" / "Tables.scala",
	baseDirectory.value / "app" / "slickgenerator" / "Customsbt Generator.scala"
)
	