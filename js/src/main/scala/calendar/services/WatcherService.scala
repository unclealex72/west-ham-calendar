package calendar.services

import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{Factory, Service, injectable}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.Array
import scala.scalajs.js.JSConverters._
import scalaz._
import Scalaz._

/**
  * Created by alex on 09/04/16.
  */
@injectable("watcher")
class WatcherService extends Service {

  private def createListener[A](watcher: A => A => Unit): js.Function = {
    new js.Function2[A, A, js.Any] {
      override def apply(newValue: A, oldValue: A): js.Any = {
        if (newValue != oldValue) watcher(newValue)(oldValue)
        false
      }
    }
  }

  private def createExpr[S <: Scope, A](expr: S => A): js.Function = {
    (s: S) => expr(s)
  }

  private def createExprs[S <: Scope](exprs: Seq[(S) => Any]): js.Array[js.Function] = {
    exprs.map { expr =>
      val watchExpression: js.Function = (s: S) => expr(s)
      watchExpression
    }.toJSArray
  }

  def on[S <: Scope, A]($scope: S)(expr: S => A)(onChange: A => A => Unit): Unit = {
    $scope.$watch(createExpr(expr), createListener(onChange))
  }

  import ScopeWithScopeWatchGroup._

  /**
    * Watch for a list of expressions and call a function when any of them changes.
    * @param $scope
    * @param exprs
    * @param onChange
    * @tparam S
    */
  def onAll[S <: Scope]($scope: S)(exprs: Seq[S => Any])(onChange: S => Unit): Unit = {
    val jsExprs: js.Array[js.Function] = createExprs(exprs)
    val listener = createListener((a: Any) => (b: Any) => onChange($scope))
    $scope.$watchGroup(jsExprs, listener)
  }

  /**
    * Create a builder that supplies a function to list for an expression change.
    * @param $scope
    * @tparam S
    * @return
    */
  def onEach[S <: Scope]($scope: S) = new WatcherBuilderListen[S]($scope, Seq.empty, Seq.empty)

  class WatcherBuilderListen[S <: Scope]($scope: S, exprs: Seq[S => Any], onChanges: Seq[Any => Any => Unit]) {
    def listen[A](expr: S => A): WatcherBuilderFire[S, A] = {
      val newExprs = exprs :+ expr
      new WatcherBuilderFire[S, A]($scope, newExprs, onChanges)
    }
    def go(): Unit = {
      val watcher: js.Array[Any] => js.Array[Any] => Unit = newValues => oldValues => {
        val newAndOldValues: Array[(Any, Any)] = newValues.zip(oldValues)
        newAndOldValues.zip(onChanges).foreach {
          case ((newValue, oldValue), onChange) if newValue != oldValue =>
            onChange(newValue)(oldValue)
            true
          case _ => true
        }
      }
      $scope.$watchGroup(createExprs(exprs), createListener(watcher))
    }
  }

  class WatcherBuilderFire[S <: Scope, A]($scope: S, exprs: Seq[S => Any], onChanges: Seq[Any => Any => Unit]) {
    def fire(onChange: S => Unit): WatcherBuilderListen[S] = {
      val anyOnChange: Any => Any => Unit = newValue => oldValue => {
        onChange($scope)
      }
      val newOnChanges = onChanges :+ anyOnChange
      new WatcherBuilderListen[S]($scope, exprs, newOnChanges)
    }
  }
}


@injectable("watcher")
class WatcherServiceFactory extends Factory[WatcherService] {

  override def apply(): WatcherService = new WatcherService
}

//noinspection NotImplementedCode
@js.native
trait ScopeWatchGroup extends Scope {
  def $watchGroup(watchExpression: js.Array[js.Function], listener: js.Any): js.Function = js.native

}

object ScopeWithScopeWatchGroup {
  implicit def scope2watchGroup(scope: Scope): ScopeWatchGroup = scope.asInstanceOf[ScopeWatchGroup]
}