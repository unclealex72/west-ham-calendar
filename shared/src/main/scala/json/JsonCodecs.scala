package json

import java.text.{DateFormat, ParseException, SimpleDateFormat}
import java.util.{Date, Locale}

import upickle.Js
import scalaz._
import Scalaz._

/**
  * Created by alex on 17/02/16.
  */
trait JsonCodecs {

  private def df(): DateFormat = {
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH)
  }

  def dateToJson(date: Date): Js.Value = {
    Js.Str(df().format(date))
  }

  case class Fields(fields: Map[String, Js.Value]) {

    def mandatory[E](name: String, errorOnMissing: String)(f: Js.Value => ValidationNel[String, E]): ValidationNel[String, E] = {
      val disjunction = for {
        field <- fields.get(name).toRightDisjunction(NonEmptyList(errorOnMissing))
        value <- f(field).disjunction
      } yield value
      disjunction.validation
    }

    def optional[E](name: String)(f: Js.Value => ValidationNel[String, E]): ValidationNel[String, Option[E]] = {
      val onSome = fields.get(name).map(f).map { e =>
        e match {
          case Success(value) => {
            val success: Option[E] = Some(value)
            success.successNel[String]
          }
          case Failure(msg) => msg.failure[Option[E]]
        }
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

    def jsObj[T](f: Fields => ValidationNel[String, T]): ValidationNel[String, T] = {
      value match {
        case obj: Js.Obj => f(Fields(obj.value.toMap))
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

    def jsDate: ValidationNel[String, Date] = jsStr { str =>
      try {
        df().parse(str).successNel[String]
      }
      catch {
        case _: ParseException => s"Cannot parse date $str".failureNel[Date]
      }
    }

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

    def jsNum: ValidationNel[String, Long] = {
      value match {
        case num: Js.Num => num.value.toLong.successNel
        case _ => "Did not find a JSON number when one was required.".failureNel
      }
    }
  }

}
