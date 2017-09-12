logLevel := Level.Warn

// Resolvers
resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Madoushi sbt-plugins" at "https://dl.bintray.com/madoushi/sbt-plugins/")

resolvers += Resolver.sonatypeRepo("releases")

//resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

// Play framework

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.3")

// ScalaJS

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.18")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.5")

// Front end plugins

//addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.8")

// Git flow

addSbtPlugin("com.servicerocket" % "sbt-git-flow" % "0.1.2")