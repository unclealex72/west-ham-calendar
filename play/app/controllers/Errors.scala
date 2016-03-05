package controllers

import dao.FatalErrorDao
import logging.{Fatal, RemoteStream}
import play.api.mvc._
import scaldi.{Injectable, Injector}

import scala.concurrent.ExecutionContext

class Errors(implicit injector: Injector) extends Secret with JsonResults with Injectable {

  val secret: SecretToken = inject[SecretToken]
  implicit val fatal: Fatal = inject[Fatal]
  implicit val fatalErrorDao: FatalErrorDao = inject[FatalErrorDao]
  implicit val ec: ExecutionContext = inject[ExecutionContext]

  def quickFail = {
    Action { implicit request =>
      implicit val remoteStream: RemoteStream = new RemoteStream {
        override def logToRemote(message: String): Unit = println(message)
      }
      fatal.fail("Poo")
      Ok
    }
  }
}

