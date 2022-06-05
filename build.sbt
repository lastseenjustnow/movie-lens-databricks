import sbt.Keys.fork

name := "movie-lens"
version := "1.0"
scalaVersion := "2.12.15"

lazy val commonDependencies = Seq(
  "org.apache.spark" %% "spark-sql" % "3.2.1" % "provided",
  "io.delta" %% "delta-core" % "1.2.1" % "provided",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

lazy val app = (project in file("."))
  .settings(
    assembly / mainClass := Some("movielens.SparkApp"),
    libraryDependencies ++= commonDependencies,
    Test / fork := true
  )

javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled")
