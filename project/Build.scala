import sbt._
import Keys._
import sbt.Keys._
import play.Project._
import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import ReleaseStateTransformations._

object ApplicationBuild extends Build {

  val appName         = "west-ham-calendar"
  val appVersion      = "6.0.0-SNAPSHOT"

  val appDependencies = Seq(
    //web dependencies
    "org.webjars" % "angular-ui-bootstrap" % "0.3.0-1",
    "org.webjars" % "bootstrap" % "2.3.2",
    "org.webjars" % "jquery" % "1.9.1",
    //backend libraries
    "com.typesafe"  %% "scalalogging-slf4j" % "1.0.1",
    "org.mozilla"  % "rhino"  % "1.7R3",
    "ch.qos.logback" % "logback-classic" % "1.0.7",
    "javax.transaction"  % "jta"  % "1.1",
    "joda-time"  % "joda-time"  % "2.2",
    "org.joda"  % "joda-convert"  % "1.3.1",
    "net.sourceforge.htmlcleaner"  % "htmlcleaner"  % "2.2",
    "org.quartz-scheduler" % "quartz" % "2.1.7",
    "javax.inject"  % "javax.inject"  % "1",
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    jdbc,
    "org.mnode.ical4j" % "ical4j" % "1.0.4",
    "org.squeryl" %% "squeryl" % "0.9.6-RC1",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "com.rockymadden.stringmetric" % "stringmetric-core" % "0.25.3",
    // test
    "com.h2database" % "h2" % "1.3.171" % "test",
    "org.specs2"  %% "specs2"  % "1.14"  % "test",
    "org.scalamock" %% "scalamock-specs2-support" % "3.0.1" % "test")

  // Do not publish during part of the release process.

  val main = play.Project(appName, appVersion, appDependencies).settings(releaseSettings: _*).settings(
    releaseProcess := Seq[ReleaseStep](
	  checkSnapshotDependencies,
	  inquireVersions,
	  runTest,
	  setReleaseVersion,
	  commitReleaseVersion,
	  tagRelease,
	  publishArtifacts,
	  setNextVersion,
	  commitNextVersion,
	  pushChanges
	),
    scalaVersion := "2.10.1",
    organization := "uk.co.unclealex.calendar",
    resolvers ++= Seq(
      "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools/",
      "cloudbees-private-release-repository" at "https://repository-unclealex.forge.cloudbees.com/release",
      "cloudbees-private-snapshot-repository" at "https://repository-unclealex.forge.cloudbees.com/snapshot")
  )

}
