package monocleintro

import monocle.{SimpleOptional, SimpleLens, SimplePrism}
import org.specs2.scalaz.Spec
import monocle.std._

import scala.util.Try


class PrismExample extends Spec {

  def some[A] = SimplePrism[Option[A], A](Some.apply[A], identity)

  sealed trait Json
  case class JsNumber(value: Double) extends Json
  case class JsString(value: String) extends Json
  case class JsArray(value: List[Json]) extends Json
  case class JsObject(value: Map[String, Json]) extends Json

  val jsNumber = SimplePrism[Json, Double](JsNumber.apply, { case JsNumber(n) => Some(n); case _ => None })
  val jsString = SimplePrism[Json, String](JsString.apply, { case JsString(s) => Some(s); case _ => None })
  val jsArray  = SimplePrism[Json, List[Json]](JsArray.apply, { case JsArray(a)  => Some(a); case _ => None })
  val jsObject = SimplePrism[Json, Map[String, Json]](JsObject.apply, { case JsObject(m)  => Some(m); case _ => None })


  "stringToInt" in {
    val stringToInt = SimplePrism[String, Int](_.toString, s => Try(s.toInt).toOption)

    stringToInt.getOption("12345")  shouldEqual Some(12345)
    stringToInt.getOption("-12345") shouldEqual Some(-12345)
    stringToInt.getOption("hello")  shouldEqual None
    stringToInt.getOption("999999999999999999") shouldEqual None

    stringToInt.modify("1234", _ * 2) shouldEqual "2468"

    stringToInt.getOption("꩙") shouldEqual Some(9)
  }

  "basic example" in {

    val json: Json = JsObject(Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(26),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Zoe"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(21)
        )),
        JsObject(Map(
          "first_name" -> JsString("Bill"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(23)
        ))
      ))
    ))

    jsNumber.getOption(json) shouldEqual None

    def at(fieldName: String): SimpleLens[Map[String, Json], Option[Json]] =
      SimpleLens[Map[String, Json]](_.get(fieldName))((map, optField) => optField match {
        case None        => map - fieldName
        case Some(field) => map + (fieldName -> field)
      })

    def index(fieldName: String): SimpleOptional[Map[String, Json], Json] =
      at(fieldName) composeOptional some

    (jsObject composeOptional at("first_name")
              composeOptional some
              composeOptional jsString).getOption(json) shouldEqual Some("John")

    (jsObject composeOptional at("city")).set(json, Some(JsString("London"))) shouldEqual JsObject(Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(26),
      "city"       -> JsString("London"),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Zoe"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(21)
        )),
        JsObject(Map(
          "first_name" -> JsString("Bill"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(23)
        ))
      ))
    ))

    (jsObject composeOptional monocle.function.Index.index("siblings")
              composeOptional jsArray
              composeOptional monocle.function.Index.index(1)
              composeOptional jsObject
              composeOptional monocle.function.Index.index("age")
              composeOptional jsNumber).modify(json, _ + 1) shouldEqual JsObject(Map(
      "first_name" -> JsString("John"),
      "last_name"  -> JsString("Doe"),
      "age"        -> JsNumber(26),
      "siblings"   -> JsArray(List(
        JsObject(Map(
          "first_name" -> JsString("Zoe"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(21)
        )),
        JsObject(Map(
          "first_name" -> JsString("Bill"),
          "last_name"  -> JsString("Doe"),
          "age"        -> JsNumber(24)
        ))
      ))
    ))

  }

}
