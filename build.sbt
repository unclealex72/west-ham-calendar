name := "west-ham-calendar"

version := "1.0-SNAPSHOT"

scalacOptions += "-target:jvm-1.7"

libraryDependencies ++= Seq(
    //security
    "securesocial" %% "securesocial" % "2.1.2",
    // JSON
    "io.argonaut" %% "argonaut" % "6.0.1",
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
    "org.scalamock" %% "scalamock-specs2-support" % "3.0.1" % "test"
)     

resolvers += "releases" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

play.Project.playScalaSettings

//templatesTypes += ("js" -> "play.api.templates.JavascriptFormat")

Keys.fork in Test := false
