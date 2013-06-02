package com.vasilrem.sbt.pomtopm

import scala.xml.Node

object PomToPm extends Application {
    
  val placeholderTemplate = """\$\{([^}]+)}"""

  def sbtProjectType(packageType: String) = packageType match {
    case "war" => "DefaultWebProject"
    case _ => "DefaultProject"
  }

  def escapeInlineResourceName(name: String) = name.replaceAll(placeholderTemplate, "").replaceAll("[\\.\\-]", "_")

  def retrievePomProperties(pom: Node) = Map[String, String]() ++
  ((pom \ "properties") flatMap { properties =>
      properties.child map {
        property =>
        ("${" + property.label + "}", property.text)
      }
    })

  def replacePlaceholders(text: String, pomProperties:Map[String, String]): String =
    (text /: (placeholderTemplate.r findAllIn text).toSeq){
      (textWithPlaceholders: String, placeholder: String)=>
      textWithPlaceholders.replaceAll(placeholderTemplate,
                                      pomProperties
                                      .get(placeholder)
                                      .getOrElse("\\" + placeholder))
    }

  def repositories(pom: Node) = ("" /: pom \\ "repository") {
    (text: String, repository: Node) =>
    (text + "\n\t" +  """val %s = "%s" at "%s" """)
    .format(escapeInlineResourceName(repository \ "id" text),
            repository \ "id" text,
            repository \ "url" text)
  } 
 
  def dependencies(pom: Node) = {

    def quotedText(text: String): String =  "\"" + text + "\""

    def dependencyAttribute(attributeValue: String): String =
      if(attributeValue.nonEmpty) " % " + quotedText(attributeValue) else ""

    ("" /: pom \\ "dependency") {
      (text: String, dependency: Node) =>
      text + ("\n\tval %s = %s %s %s %s").format(
        escapeInlineResourceName(dependency \ "artifactId" text),
        quotedText(dependency \ "groupId" text),
        dependencyAttribute(dependency \ "artifactId" text),
        dependencyAttribute(dependency \ "version" text),
        dependencyAttribute(dependency \ "scope" text)
      )
    }
  }

  def pmTemplate(pmFileName: String, sbtProjectType: String, repos: String, deps: String) = """import sbt._
  
  class %s(info: ProjectInfo) extends %s(info){
  
    //Repositories---
    %s

    //---Repositories

    //Dependencies---
    %s
  
    //---Dependencies
  
  }""" format (pmFileName, sbtProjectType, repos, deps)
  
  override def main(args : Array[java.lang.String]) : Unit = {
    val pom = scala.xml.XML.loadFile(if(args.length > 0) args(0) + "/pom.xml" else "pom.xml")

    println(replacePlaceholders(pmTemplate(escapeInlineResourceName(pom \ "artifactId" text),
                                           sbtProjectType(pom \ "packaging" text),
                                           repositories(pom),
                                           dependencies(pom)),
                                retrievePomProperties(pom)))
  }

}



