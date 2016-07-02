import sbt.Project.projectToRef
import com.servicerocket.sbt.release.git.flow.Steps._
import com.servicerocket.sbt.release.git.flow.Util._
import sbt.Keys._
import sbt._
import sbtrelease._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleasePlugin.autoImport.{ReleaseStep, releaseProcess}

name := "west-ham-calendar"

lazy val clients = Seq(js)
lazy val scalaV = "2.11.7"

//resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"

lazy val play = (project in file(".")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, digest),
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
      //security
      "com.mohiva" %% "play-silhouette" % "4.0.0-RC1",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-RC1",
      //backend libraries
      "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
      "org.mozilla" % "rhino" % "1.7R3",
      "joda-time" % "joda-time" % "2.2",
      "org.joda" % "joda-convert" % "1.3.1",
      "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.2",
      "com.iheart" %% "ficus" % "1.2.2",
      "org.scalaz" %% "scalaz-core" % "7.2.0",
      // Dependency Injection
      "net.codingwell" %% "scala-guice" % "4.0.1",
      // Database
      "com.typesafe.play" %% "play-slick" % "1.1.1",
      "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
      "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
      cache,
      filters,
      ws,
      "org.mnode.ical4j" % "ical4j" % "1.0.4",
      "org.postgresql" % "postgresql" % "9.4.1207",
      "com.rockymadden.stringmetric" % "stringmetric-core" % "0.25.3",
      "org.imgscalr" % "imgscalr-lib" % "4.2",
      // webjars & frontend
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "org.webjars.bower" % "roboto-fontface" % "0.4.5",
    "org.webjars.bower" % "angular-materialize" % "0.1.5",
    "org.webjars.bower" % "materialize" % "0.97.6",
    "org.webjars.bower" % "angular" % "1.5.7",
      // test
      "org.hsqldb" % "hsqldb" % "2.3.3" % "test",
      "org.specs2" %% "specs2-core" % "3.7" % "test",
      "org.specs2" %% "specs2-mock" % "3.7" % "test",
      "org.specs2" %% "specs2-junit" % "3.7" % "test",
      "org.eclipse.jetty" % "jetty-server" % "9.2.10.v20150310" % "test"),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    fork in Test := false
).enablePlugins(PlayScala, SbtWeb).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm, js)

lazy val js = (project in file("js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  emitSourceMaps := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.greencatsoft" %%% "scalajs-angular" % "0.6",
    "org.querki" %%% "jquery-facade" % "1.0-RC3",
    "com.lihaoyi" %%% "upickle" % "0.3.8",
    "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % "7.2.0",
    "com.github.japgolly.fork.scalaz" %%% "scalaz-scalacheck-binding" % "7.2.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    emitSourceMaps := true,
    libraryDependencies ++= Seq(
      "com.beachape" %% "enumeratum" % "1.3.6",
      "com.lihaoyi" %% "upickle" % "0.3.8",
      "org.scalaz" %% "scalaz-core" % "7.2.0",
      "org.specs2" %% "specs2-core" % "3.7" % "test",
      "org.specs2" %% "specs2-mock" % "3.7" % "test",
      "org.specs2" %% "specs2-junit" % "3.7" % "test"
    )).
  jsConfigure(_ enablePlugins ScalaJSPlay)

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
