import sbt.Project.projectToRef
import com.servicerocket.sbt.release.git.flow.Steps._
import com.servicerocket.sbt.release.git.flow.Util._
import sbt.Keys._
import sbt._
import sbtrelease._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleasePlugin.autoImport.{ReleaseStep, releaseProcess}

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    version := (version in ThisBuild).value,
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  // set up settings specific to the JS project
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// instantiate the JS project for SBT with some additional settings
lazy val js: Project = (project in file("js"))
  .settings(
    name := "js",
    version := (version in ThisBuild).value,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.scalajsDependencies.value,
    jsDependencies ++= Settings.jsDependencies.value,
    // RuntimeDOM is needed for tests
    jsDependencies += RuntimeDOM % "test",
    // yes, we want to package JS dependencies
    skip in packageJSDependencies := false,
    // use Scala.js provided launcher code to start the client app
    persistLauncher := true,
    persistLauncher in Test := false,
    // use uTest framework for tests
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJS)

// Client projects (just one in this case)
lazy val clients = Seq(js)

lazy val play = (project in file(".")).settings(
  name := Settings.name,
  resolvers ++= Seq(
    "Atlassian Releases" at "https://maven.atlassian.com/public/",
    "releases" at "http://oss.sonatype.org/content/repositories/releases",
    Resolver.jcenterRepo
  ),
  version := (version in ThisBuild).value,
  scalaVersion := Settings.versions.scala,
  scalacOptions ++= Settings.scalacOptions,
  libraryDependencies ++= Settings.jvmDependencies.value,
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
  // connect to the client project
  scalaJSProjects := clients,
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // compress CSS
  LessKeys.compress in Assets := true,

  routesGenerator := InjectedRoutesGenerator,
  scalacOptions in Test ++= Seq("-Yrangepos"),
  fork in Test := false
).enablePlugins(PlayScala, SbtWeb).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJVM)

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