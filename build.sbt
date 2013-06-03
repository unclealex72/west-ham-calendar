name := "West Ham Calendar"

version := "6.0.0-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
	"com.typesafe"  %% "scalalogging-slf4j" % "1.0.1", 
	"org.mozilla"  % "rhino"  % "1.7R3", 
    "ch.qos.logback" % "logback-classic" % "1.0.6",
	"javax.transaction"  % "jta"  % "1.1", 
	"joda-time"  % "joda-time"  % "2.2", 
	"org.joda"  % "joda-convert"  % "1.3.1", 
	"net.sourceforge.htmlcleaner"  % "htmlcleaner"  % "2.2", 
	"javax.inject"  % "javax.inject"  % "1",
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    "org.squeryl" % "squeryl_2.10.0-RC5" % "0.9.5-5",
    "org.hsqldb"  % "hsqldb"  % "2.2.8" % "test",
	"org.specs2"  %% "specs2"  % "1.14"  % "test"
)
