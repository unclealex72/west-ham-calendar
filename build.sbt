name := "West Ham Calendar"

version := "6.0.0-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
	"com.typesafe"  %% "scalalogging-slf4j" % "1.0.1", 
	"org.mozilla"  % "rhino"  % "1.7R3", 
	"org.slf4j"  % "slf4j-api"  % "1.0.7", 
	"org.slf4j"  % "slf4j-log4j12"  % "1.7.5", 
	"javax.transaction"  % "jta"  % "1.1", 
	"org.springframework"  % "spring-aop"  % "3.2.1.RELEASE",
	"org.springframework"  % "spring-webmvc"  % "3.2.1.RELEASE", 
	"org.springframework"  % "spring-orm"  % "3.2.1.RELEASE",
	"org.springframework.security"  % "spring-security-core"  % "3.1.3.RELEASE" ,
	"org.springframework.security"  % "spring-security-web"  % "3.1.3.RELEASE" ,
	"org.springframework.security"  % "spring-security-config"  % "3.1.3.RELEASE" ,
	"org.springframework.security"  % "spring-security-taglibs"  % "3.1.3.RELEASE" ,
	"junit"  % "junit"  % "4.10"  % "test",
	"org.springframework"  % "spring-test"  % "3.2.1.RELEASE"  % "test",
	"org.hamcrest"  % "hamcrest-all"  % "1.3",
	"javax.annotation"  % "jsr250-api"  % "1.0",
	"org.quartz-scheduler"  % "quartz"  % "2.1.3",
	"commons-lang"  % "commons-lang"  % "2.5", 
	"org.hibernate"  % "hibernate-core"  % "4.0.1.Final", 
	"org.hsqldb"  % "hsqldb"  % "2.2.8",
	"joda-time"  % "joda-time"  % "2.2", 
	"org.joda"  % "joda-convert"  % "1.3.1", 
	"com.google.guava"  % "guava"  % "11.0.1", 
	"net.sourceforge.htmlcleaner"  % "htmlcleaner"  % "2.2", 
	"javax.inject"  % "javax.inject"  % "1",
	"org.specs2"  %% "specs2"  % "1.14"  % "test"
)
