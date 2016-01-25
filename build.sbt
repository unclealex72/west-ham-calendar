name := "west-ham-calendar"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
    //pdf
    "org.apache.pdfbox" % "pdfbox" % "1.8.8",
    //security
    "com.mohiva" %% "play-silhouette" % "3.0.4",
    "com.mohiva" %% "play-silhouette-testkit" % "3.0.4" % "test",
    // JSON
    "io.argonaut" %% "argonaut" % "6.1",
    "org.apache.httpcomponents" % "httpclient" % "4.4",
    "org.apache.httpcomponents" % "httpcore" % "4.4",
    //backend libraries
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "org.mozilla" % "rhino" % "1.7R3",
    "javax.transaction" % "jta" % "1.1",
    "joda-time" % "joda-time" % "2.2",
    "org.joda" % "joda-convert" % "1.3.1",
    "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.2",
    "net.ceedubs" %% "ficus" % "1.1.2",
    "javax.inject" % "javax.inject" % "1",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    "org.scalaz" %% "scalaz-core" % "7.1.6",
    "com.typesafe.play" %% "play-slick" % "1.1.1",
    "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
    "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
    cache,
    filters,
    "org.mnode.ical4j" % "ical4j" % "1.0.4",
    "org.squeryl" %% "squeryl" % "0.9.5-7",
    "org.postgresql" % "postgresql" % "9.4.1207",
    "com.rockymadden.stringmetric" % "stringmetric-core" % "0.25.3",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    // test
    "com.h2database" % "h2" % "1.3.171" % "test",
    "org.specs2" %% "specs2-core" % "3.7-scalaz-7.1.6" % "test",
    "org.specs2" %% "specs2-mock" % "3.7-scalaz-7.1.6" % "test",
    "org.specs2" %% "specs2-junit" % "3.7-scalaz-7.1.6" % "test",
    "org.eclipse.jetty" % "jetty-server" % "9.2.10.v20150310" % "test"
)

routesGenerator := InjectedRoutesGenerator

scalacOptions in Test ++= Seq("-Yrangepos")

// Needed for Silhouette
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

resolvers += "releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

//templatesTypes += ("js" -> "play.api.templates.JavascriptFormat")

Keys.fork in Test := false
