import sbt.Project.projectToRef
import com.servicerocket.sbt.release.git.flow.Steps._
import com.servicerocket.sbt.release.git.flow.Util._
import sbt.Keys._
import sbt._
import sbtrelease._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleasePlugin.autoImport.{ReleaseStep, releaseProcess}

name := "west-ham-calendar"

lazy val scalaV = "2.12.2"
val circeVersion = "0.8.0"

//resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"

// Stringmetric project https://github.com/zenaptix-lab/stringmetric.git
lazy val stringmetric = ProjectRef(uri("git://github.com/zenaptix-lab/stringmetric.git"), "core")

lazy val play = (project in file(".")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(js),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  resolvers ++= Seq(
    "Atlassian Releases" at "https://maven.atlassian.com/public/",
    "releases" at "http://oss.sonatype.org/content/repositories/releases",
    Resolver.jcenterRepo
  ),
  routesGenerator := InjectedRoutesGenerator,
  scalacOptions in Test ++= Seq("-Yrangepos"),
  libraryDependencies ++= Seq(
      //pdf
      "org.apache.pdfbox" % "pdfbox" % "2.0.0-RC3",
      //backend libraries
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1",
      "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.2",
      "com.iheart" %% "ficus" % "1.4.1",
      // Dependency Injection
      "net.codingwell" %% "scala-guice" % "4.1.0",
      // Database
      "com.typesafe.play" %% "play-slick" % "3.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
      // security
      "com.mohiva" %% "play-silhouette" % "5.0.0-RC2",
      filters,
      ws,
      guice,
      ehcache,
      "play-circe" %% "play-circe" % "2.6-0.8.0",
      "org.mnode.ical4j" % "ical4j" % "1.0.4",
      "org.postgresql" % "postgresql" % "9.4.1207",
      "org.imgscalr" % "imgscalr-lib" % "4.2",
      // webjars & frontend
      "com.vmunier" %% "scalajs-scripts" % "1.1.1",
      // test
      "org.hsqldb" % "hsqldb" % "2.3.3" % "test",
      "org.specs2" %% "specs2-core" % "3.9.1" % "test",
      "org.specs2" %% "specs2-junit" % "3.9.1" % "test",
      "org.specs2" %% "specs2-mock" % "3.9.1" % "test",
      "org.eclipse.jetty" % "jetty-server" % "9.4.5.v20170502" % "test") ++
      // Security
      Seq("", "-password-bcrypt", "-persistence", "-crypto-jca").map { suffix =>
        "com.mohiva" %% s"play-silhouette$suffix" % "5.0.0"
      },
    scalacOptions in Test ++= Seq("-Yrangepos"),
    fork in Test := false
).enablePlugins(PlayScala, SbtWeb).
  aggregate(Seq(js).map(projectToRef): _*).
  dependsOn(sharedJvm, js, stringmetric)

lazy val js = (project in file("js")).settings(
  scalaVersion := scalaV,
  emitSourceMaps := true,
  libraryDependencies ++= Seq(
    "ru.pavkin" %%% "scala-js-momentjs" % "0.8.1"
  )
).enablePlugins(ScalaJSPlugin).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "com.beachape" %% "enumeratum" % "1.5.12",
      "org.typelevel" %% "cats" % "0.9.0",
      "org.specs2" %% "specs2-core" % "3.9.1" % "test",
      "org.specs2" %% "specs2-junit" % "3.9.1" % "test"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion))

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

scalaJSStage in Global := FullOptStage

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  //Does not work on Linux
  //checkGitFlowExists
  inquireVersions,
  runTest,
  gitFlowReleaseStart,
  setReleaseVersion,
  commitReleaseVersion,
  //No artifacts to publish
  //publishArtifacts
  gitFlowReleaseFinish,
  pushMaster,
  //Push to Heroku
  execStep { _ =>
    "git push heroku master"
  },
  setNextVersion,
  commitNextVersion,
  pushChanges
)

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false
// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in (play, Compile))
