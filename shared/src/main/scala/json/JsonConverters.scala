package json

import dates.SharedDate
import upickle.Invalid.Data
import upickle.Js

import scala.collection.SortedSet
import scala.language.implicitConversions
import scala.math.{Ordering => SOrdering}
import scalaz.Scalaz._
import scalaz._

trait JsonSerialiser {

  def dateToJson(date: SharedDate): Js.Value = {
    Js.Str(date.toString)
  }

  def jsArr[A](f: A => Js.Value)(as: Seq[A]): Js.Value = Js.Arr(as.map(f): _*)

  def jsBool(value: Boolean): Js.Value = if (value) Js.True else Js.False
}

trait JsonDeserialiser {
  case class Fields(className: String, fields: Map[String, Js.Value]) {

    def mandatory[E](name: String)(f: Js.Value => ValidationNel[String, E]): ValidationNel[String, E] = {
      val a = if (!name.isEmpty && "aeiouAEIOU".contains(name(0))) "an" else "a"
      val disjunction = for {
        field <- fields.get(name).toRightDisjunction(NonEmptyList(s"Cannot find $a $name property for class $className"))
        value <- f(field).disjunction
      } yield value
      disjunction.validation
    }

    def optional[E](name: String)(f: Js.Value => ValidationNel[String, E]): ValidationNel[String, Option[E]] = {
      val onSome = fields.get(name).map(f).map {
        case Success(value) => {
          val success: Option[E] = Some(value)
          success.successNel[String]
        }
        case Failure(msg) => msg.failure[Option[E]]
      }
      onSome.getOrElse {
        val none: Option[E] = None
        none.successNel[String]
      }
    }

    def optionalDefault[E](name: String)(f: Js.Value => ValidationNel[String, E])(default: => E): ValidationNel[String, E] = {
      optional(name)(f) match {
        case Success(ov) => ov.getOrElse(default).successNel[String]
        case Failure(msg) => msg.failure[E]
      }
    }
  }

  implicit class ValueImplicits(value: Js.Value) {

    def jsObj[T](className: String)(f: Fields => ValidationNel[String, T]): ValidationNel[String, T] = {
      value match {
        case obj: Js.Obj => f(Fields(className, obj.value.toMap))
        case _ => "Did not find a JSON object when one was required.".failureNel[T]
      }
    }

    def jsStr[T](f: String => ValidationNel[String, T]): ValidationNel[String, T] = {
      value match {
        case str: Js.Str => f(str.value)
        case _ => "Did not find a JSON string when one was required.".failureNel[T]
      }
    }

    def jsStr: ValidationNel[String, String] = jsStr(str => str.successNel[String])

    def jsDate: ValidationNel[String, SharedDate] = jsStr(SharedDate.apply)

    def jsBool: ValidationNel[String, Boolean] = {
      value match {
        case Js.False => false.successNel
        case Js.True => true.successNel
        case _ => "Did not find a JSON boolean when one was required.".failureNel
      }
    }

    def jsArr[E](f: Js.Value => ValidationNel[String, E]): ValidationNel[String, Seq[E]] = value match {
      case arr: Js.Arr  =>
        val empty: ValidationNel[String, Seq[E]] = Seq.empty.successNel[String]
        arr.value.foldLeft(empty) { (result, jsValue) =>
          val newResult = for {
            values <- result.disjunction
            value <- f(jsValue).disjunction
          } yield values :+ value
          newResult.validation
        }
      case _ => "Did not find a JSON array when one was required.".failureNel
    }

    def jsLong: ValidationNel[String, Long] = {
      value match {
        case num: Js.Num => num.value.toLong.successNel
        case _ => "Did not find a JSON number when one was required.".failureNel
      }
    }

    def jsInt: ValidationNel[String, Int] = jsLong.map(_.toInt)
  }

}

trait JsonSerialisers[A] extends JsonSerialiser {

  def serialise(a: A): Js.Value

  implicit val writer: upickle.default.Writer[A] = upickle.default.Writer[A](serialise)

}

trait JsonDeserialisers[A] extends JsonDeserialiser {

  def deserialise(value: Js.Value): ValidationNel[String, A]

  implicit val validationReader: upickle.default.Reader[ValidationNel[String, A]] =
    upickle.default.Reader[ValidationNel[String, A]] {
      case value => deserialise(value)
    }

  implicit val eitherReader: upickle.default.Reader[\/[NonEmptyList[String], A]] =
    upickle.default.Reader[\/[NonEmptyList[String], A]] {
      case value => deserialise(value).disjunction
    }

  implicit val reader: upickle.default.Reader[A] = upickle.default.Reader[A] { case value =>
    deserialise(value).valueOr { msgs =>
      throw new Data(value, msgs.toList.mkString("\n"))
    }
  }

}
/**
  * Helper methods for deserialising JSON and returning Eithers and Options
  * Created by alex on 14/02/16.
  */
trait JsonConverters[A] extends JsonSerialisers[A] with JsonDeserialisers[A]