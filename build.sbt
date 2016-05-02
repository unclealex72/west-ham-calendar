import com.typesafe.sbt.web.PathMapping
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Project.projectToRef

import scala.io.Source

name := "west-ham-calendar"

version := "1.0-SNAPSHOT"

lazy val clients = Seq(js)
lazy val scalaV = "2.11.7"

//resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"

lazy val jsDepsTask = taskKey[Pipeline.Stage]("Concatenate javascript libraries")

lazy val play = (project in file("play")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(jsDepsTask, scalaJSProd, gzip),
  pipelineStages in Assets := Seq(jsDepsTask),
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
      "com.mohiva" %% "play-silhouette" % "4.0.0-BETA1",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-BETA1",
      "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA1" % "test",
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
      "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
      "org.mnode.ical4j" % "ical4j" % "1.0.4",
      "org.postgresql" % "postgresql" % "9.4.1207",
      "com.rockymadden.stringmetric" % "stringmetric-core" % "0.25.3",
      "org.imgscalr" % "imgscalr-lib" % "4.2",
      // webjars
      "org.webjars.bower" % "roboto-fontface" % "0.4.5",
      // test
      "org.hsqldb" % "hsqldb" % "2.3.3" % "test",
      "org.specs2" %% "specs2-core" % "3.7" % "test",
      "org.specs2" %% "specs2-mock" % "3.7" % "test",
      "org.specs2" %% "specs2-junit" % "3.7" % "test",
      "org.eclipse.jetty" % "jetty-server" % "9.2.10.v20150310" % "test"),
  jsDepsTask := { mappings: Seq[PathMapping] =>
    // pretend to combine all .js files into one .min.js file
    val targetDir = WebKeys.webTarget.value / "myPipelineTask" / "target"
    val (jsdeps, other) = mappings partition (_._2.endsWith(".jsdeps"))
    val dependencies = jsdeps.map(_._1).flatMap(IO.readLines(_)).map(_.trim).filterNot(_.isEmpty)
    val js = dependencies.map { filename =>
      val file = new File(filename).getAbsoluteFile
      println(s"reading dependency $file")
      IO.read(file)
    }.mkString("\n")
    val libsFile = targetDir / "javascripts" / "libs.js"
    IO.write(libsFile, js)
    val newMappings = Seq(libsFile) pair relativeTo(targetDir)
    newMappings ++ other
  }
 ).enablePlugins(PlayScala, SbtWeb).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val js = (project in file("js")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  emitSourceMaps := true,
  libraryDependencies ++= Seq(
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

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project play", _: State)) compose (onLoad in Global).value

scalaJSStage in Global := FullOptStage

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false
// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in (play, Compile))
