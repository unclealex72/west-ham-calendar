package calendar.circuit

import calendar.ajax.Ajax
import diode._
import diode.react.ReactConnector
import models.{GameRow, GameRowRel, Root}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by alex on 04/01/17
  **/
// Application circuit
class CalendarCircuit(val initialRoot: Root) extends Circuit[Root] with ReactConnector[Root] {
  // initial application model
  override protected def initialModel = initialRoot
  // combine all handlers into one

  override protected val actionHandler: HandlerFunction = composeHandlers({
    def get(root: Root): Root = root
    def set(root: Root, newRoot: Root): Root = newRoot
    new GameHandler(zoomRW(get)(set))
  })
}

case class AlterAttendance(game: GameRow, newAttendence: Boolean) extends Action

class GameHandler[M](modelRW: ModelRW[M, Root]) extends ActionHandler(modelRW) {

  override def handle = {
    case AlterAttendance(game, newAttendance) =>
      updated(value.alterAttendance(game, newAttendance), Effect {
        val calls = Future.sequence {
          val rel = if (newAttendance) GameRowRel.ATTEND else GameRowRel.UNATTEND
          game.links.get(rel).toSeq.map {
            url => Ajax.put[GameRow](url)
          }
        }
        calls.map(_ => NoAction)
      })

      /*


    case UpdateAllTodos(todos) =>
      // got new todos, update model
      updated(Ready(Todos(todos)))
    case UpdateTodo(item) =>
      // make a local update and inform server
      updated(value.map(_.updated(item)), Effect(AjaxClient[Api].updateTodo(item).call().map(UpdateAllTodos)))
    case DeleteTodo(item) =>
      // make a local update and inform server
      updated(value.map(_.remove(item)), Effect(AjaxClient[Api].deleteTodo(item.id).call().map(UpdateAllTodos)))
      */
  }
}