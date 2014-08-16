package monocleintro

import monocle.{SimpleLens, SimpleIso}
import org.specs2.scalaz.Spec


class IsoExample extends Spec {

  case class Person(name: String, age: Int)

  "String to list of Char" in {
    val stringToList = SimpleIso[String, List[Char]](_.toList, _.mkString)

    stringToList.get("Beamly")               shouldEqual List('B', 'e', 'a', 'm', 'l', 'y')
    stringToList.modify("Beamly", _.reverse) shouldEqual "ylmaeB"
  }

  "Single value wrapper and conversions" in {
    case class Meter(value: BigDecimal)
    val meter = SimpleIso[Meter, BigDecimal](_.value, Meter.apply)

    meter.modify(Meter(20), _ * 2) shouldEqual Meter(40)

    case class Yard(value: BigDecimal)

    val meterToYard = SimpleIso[Meter, Yard](
      m => Yard(m.value * BigDecimal(1.0936133)),
      y => Meter(y.value * BigDecimal(0.9144))
    )

    meterToYard.get(Meter(200)) shouldEqual Yard(218.7226600)
  }

  "meta programming" in {
    import shapeless.{HList, ::, HNil}
    def hlistHead[A, T <: HList] = SimpleLens[A :: T, A](_.head, (s, a) => s.copy(head = a))

    hlistHead.get("plop" :: 3 :: true :: HNil) shouldEqual "plop"

    import monocle.syntax._
    import monocle.generic._

    (Person("Robert", 25) applyIso toHList composeLens hlistHead get) shouldEqual "Robert"

  }



}
