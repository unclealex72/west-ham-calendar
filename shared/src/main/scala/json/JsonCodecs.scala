package json

import java.text.{ParseException, SimpleDateFormat, DateFormat}
import java.util.{Date, Locale}

import upickle.Js

import scala.language.implicitConversions

/**
  * Helper methods for deserialising JSON and returning Eithers and Options
  * Created by alex on 14/02/16.
  */
trait JsonCodecs {

  private def df(): DateFormat = {
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH)
  }

  def dateToJson(date: Date): Js.Value = {
    Js.Str(df().format(date))
  }

  case class Fields(fields: Map[String, Js.Value]) {

    def mandatory[E](name: String, errorOnMissing: String)(f: Js.Value => Either[String, E]): Either[String, E] = {
      for {
        field <- fields.get(name).toRight(errorOnMissing).right
        value <- f(field).right
      }
      yield value
    }

    def optional[E](name: String)(f: Js.Value => Either[String, E]): Either[String, Option[E]] = {
      fields.get(name).map(f).map { e =>
        e match {
          case Right(value) => Right(Some(value))
          case Left(msg) => Left(msg)
        }
      }.getOrElse(Right(None))
    }

    def optionalDefault[E](name: String)(f: Js.Value => Either[String, E])(default: => E): Either[String, E] = {
      optional(name)(f) match {
        case Right(ov) => Right(ov.getOrElse(default))
        case Left(msg) => Left(msg)
      }
    }
  }

  implicit class ValueImplicits(value: Js.Value) {

    def jsObj[T](f: Fields => Either[String, T]): Either[String, T] = {
      value match {
        case obj: Js.Obj => f(Fields(obj.value.toMap))
        case _ => Left("Did not find a JSON object when one was required.")
      }
    }

    def jsStr[T](f: String => Either[String, T]): Either[String, T] = {
      value match {
        case str: Js.Str => f(str.value)
        case _ => Left("Did not find a JSON string when one was required.")
      }
    }

    def jsStr: Either[String, String] = jsStr(str => Right[String, String](str))

    def jsDate: Either[String, Date] = jsStr { str =>
      try {
        Right(df().parse(str))
      }
      catch {
        case _: ParseException => Left(s"Cannot parse date $str")
      }
    }

    def jsBool: Either[String, Boolean] = {
      value match {
        case Js.False => Right(false)
        case Js.True => Right(true)
        case _ => Left("Did not find a JSON boolean when one was required.")
      }
    }

    def jsArr[E](f: Js.Value => Either[String, E]): Either[String, Seq[E]] = value match {
      case arr: Js.Arr  =>
        val empty: Either[String, Seq[E]] = Right(Seq.empty)
        arr.value.foldLeft(empty) { (result, jsValue) =>
          for {
            values <- result.right
            value <- f(jsValue).right
          } yield values :+ value
        }
      case _ => Left("Did not find a JSON array when one was required.")
    }

    def jsNum: Either[String, Long] = {
      value match {
        case num: Js.Num => Right(num.value.toLong)
        case _ => Left(s"Did not find a JSON number when one was required.")
      }
    }
  }
}
