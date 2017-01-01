import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.sbt.PlayImport.{cache, filters, ws}

/**
  * Application settings. Configure the build for your application here.
  * You normally don't have to touch the actual build definition after this.
  */
object Settings {
  /** The name of your application */
  val name = "west-ham-calendar"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.8"
    val scalaDom = "0.9.1"
    val scalajsReact = "0.11.3"
    val scalaCSS = "0.5.0"
    val diode = "1.1.0"

    val react = "15.3.1"
    val jQuery = "1.11.1"

    val scalajsScripts = "1.0.0"

    val silhouette = "4.0.0-RC1"

    object slick {
      val play = "1.1.1"
      val codegen = "3.1.1"
    }

    val scalaz = "7.2.0"
    val logging = "2.1.2"

    object joda {
      val time = "2.2"
      val convert = "1.3.1"
    }

    val htmlcleaner = "2.2"
    val ficus = "1.2.2"

    val guice = "4.0.1"
    val ical4j = "1.0.4"
    val postgresql = "9.4.1207"
    val stringmetric = "0.25.3"
    val imgscalr = "4.2"
    val hsqldb = "2.3.3"
    val specs2 = "3.7"
    val jetty = "9.2.10.v20150310"

    val upickle = "0.3.8"
    val enumeratum = "1.3.6"
  }

  /**
    * These dependencies are shared between JS and JVM projects
    * the special %%% function selects the correct version for each project
    */
  val sharedDependencies = Def.setting(Seq(
    "com.beachape" %% "enumeratum" % versions.enumeratum,
    "com.lihaoyi" %% "upickle" % versions.upickle,
    "org.scalaz" %% "scalaz-core" % versions.scalaz,
    "org.specs2" %% "specs2-core" % versions.specs2 % Test,
    "org.specs2" %% "specs2-mock" % versions.specs2 % Test,
    "org.specs2" %% "specs2-junit" % versions.specs2 % Test
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    //security
    "com.mohiva" %% "play-silhouette" % versions.silhouette,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % versions.silhouette,
    //backend libraries
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % versions.logging,
    "org.mozilla" % "rhino" % "1.7R3",
    "joda-time" % "joda-time" % versions.joda.time,
    "org.joda" % "joda-convert" % versions.joda.convert,
    "net.sourceforge.htmlcleaner" % "htmlcleaner" % versions.htmlcleaner,
    "com.iheart" %% "ficus" % versions.ficus,
    "org.scalaz" %% "scalaz-core" % versions.scalaz,
    // Dependency Injection
    "net.codingwell" %% "scala-guice" % versions.guice,
    // Database
    "com.typesafe.play" %% "play-slick" % versions.slick.play,
    "com.typesafe.play" %% "play-slick-evolutions" % versions.slick.play,
    "com.typesafe.slick" %% "slick-codegen" % versions.slick.codegen,
    cache,
    filters,
    ws,
    "org.mnode.ical4j" % "ical4j" % versions.ical4j,
    "org.postgresql" % "postgresql" % versions.postgresql,
    "com.rockymadden.stringmetric" % "stringmetric-core" % versions.stringmetric,
    "org.imgscalr" % "imgscalr-lib" % versions.imgscalr,

    // webjars & frontend
    "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,

    // test
    "org.hsqldb" % "hsqldb" % versions.hsqldb % Test,
    "org.specs2" %% "specs2-core" % versions.specs2 % Test,
    "org.specs2" %% "specs2-mock" % versions.specs2 % Test,
    "org.specs2" %% "specs2-junit" % versions.specs2 % Test,
    "org.eclipse.jetty" % "jetty-server" % versions.jetty % Test
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "me.chrons" %%% "diode" % versions.diode,
    "me.chrons" %%% "diode-react" % versions.diode,
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "com.lihaoyi" %%% "upickle" % versions.upickle,
    "com.github.japgolly.fork.scalaz" %%% "scalaz-core" % versions.scalaz,
    "com.github.japgolly.fork.scalaz" %%% "scalaz-scalacheck-binding" % versions.scalaz

  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars.bower" % "react" % versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars" % "jquery" % versions.jQuery / "jquery.js" minified "jquery.min.js"
  ))
}
