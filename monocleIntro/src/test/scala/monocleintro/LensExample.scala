package monocleintro

import monocle.Lenser
import org.specs2.scalaz.Spec


class LensExample extends Spec {

  case class Person(name: String, age: Int)
  object Person {
    private val lenser = Lenser[Person]
    val (_name, _age) = (lenser(_.name), lenser(_.age))
  }

  "example" in {
    Person._age.set(Person("John", 25), 32) shouldEqual Person("John", 32)
  }



}
