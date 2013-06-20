import sbt._
import Keys._
import sbt.Keys._
import play.Project._
import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import ReleaseStateTransformations._

object ApplicationBuild extends Build {

  val appName = "west-ham-calendar"

  val appDependencies = Seq(
    //web dependencies
    "org.webjars" % "webjars-play" % "2.1.0-1",
    "org.webjars" % "angular-ui-bootstrap" % "0.3.0-1",
    "org.webjars" % "bootstrap" % "2.3.2",
    "org.webjars" % "jquery" % "1.9.1",
    "org.webjars" % "lodash" % "1.2.0",
    "org.webjars" % "angularjs" % "[1.0.7]" force(),
    // JSON
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.2",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.2.2",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.2.2",
    //backend libraries
    "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
    "org.mozilla" % "rhino" % "1.7R3",
    "javax.transaction" % "jta" % "1.1",
    "joda-time" % "joda-time" % "2.2",
    "org.joda" % "joda-convert" % "1.3.1",
    "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.2",
    "javax.inject" % "javax.inject" % "1",
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    jdbc,
    "org.mnode.ical4j" % "ical4j" % "1.0.4",
    "org.squeryl" %% "squeryl" % "0.9.6-RC1",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "com.rockymadden.stringmetric" % "stringmetric-core" % "0.25.3",
    // test
    "com.h2database" % "h2" % "1.3.171" % "test",
    "org.specs2" %% "specs2" % "1.14" % "test",
    "org.scalamock" %% "scalamock-specs2-support" % "3.0.1" % "test")

  // Do not publish during part of the release process.

  val main = play.Project(appName).settings(releaseSettings: _*).settings(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges),
    scalaVersion := "2.10.1",
    organization := "uk.co.unclealex.calendar",
    version <<= version in ThisBuild,
    libraryDependencies ++= appDependencies,
    resolvers ++= Seq(
      "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools/",
      "cloudbees-private-release-repository" at "https://repository-unclealex.forge.cloudbees.com/release",
      "cloudbees-private-snapshot-repository" at "https://repository-unclealex.forge.cloudbees.com/snapshot"))

}
