logLevel := Level.Warn

// Resolvers
resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Madoushi sbt-plugins" at "https://dl.bintray.com/madoushi/sbt-plugins/")

resolvers += Resolver.sonatypeRepo("releases")

//resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

// Play framework

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.3")

// ScalaJS

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.8")

addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.3.0")

// Front end plugins

//addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.8")

addSbtPlugin("org.databrary" % "sbt-angular-templates" % "0.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("org.madoushi.sbt" % "sbt-sass" % "0.9.3")

// Git flow

addSbtPlugin("com.servicerocket" % "sbt-git-flow" % "0.1.2")