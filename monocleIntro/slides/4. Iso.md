## Iso

Focus from a type `S` into an equivalent type `A`

```scala
trait Iso[S, A]{
  def get(s: S): A
  def set(s: S, a: A): S
  def modify(s: S, f: A => A): S
  
  def reverse: Iso[A, S]
  
  def compose[B](other: Iso[A, B]): Iso[S, B]
  def compose[S](other: Lens[A, B]): Lens[S, B]
}
```

#### Laws

```scala
iso.reverse.reverse == iso

// Same than Lens
∀ s: S        => set(s, get(s)) == s // What you set is what you get
∀ s: S, a: A  => set(s, set(s, a)) == set(s, a) 
∀ s: S        => modify(s, id)  == s  
```

#### Example

```scala

  //String to list of Char
  val stringToList = SimpleIso[String, List[Char]](_.toList, _.mkString)

  stringToList.get("Beamly")               shouldEqual List('B', 'e', 'a', 'm', 'l', 'y')
  stringToList.modify("Beamly", _.reverse) shouldEqual "ylmaeB"
  

  // Single value wrapper and conversions
  case class Meter(value: BigDecimal)
  val meter = SimpleIso[Meter, BigDecimal](_.value, Meter.apply)

  meter.modify(Meter(20), _ * 2) shouldEqual Meter(40)

  case class Yard(value: BigDecimal)

  val meterToYard = SimpleIso[Meter, Yard](
    m => Yard(m.value * BigDecimal(1.0936133)),
    y => Meter(y.value * BigDecimal(0.9144))
  )

  meterToYard.get(Meter(200)) shouldEqual Yard(218.7226600)
  

  // meta programming
  import shapeless.{HList, ::, HNil}
  def hlistHead[A, T <: HList] = SimpleLens[A :: T, A](_.head, (s, a) => s.copy(head = a))

  hlistHead.get("plop" :: 3 :: true :: HNil) shouldEqual "plop"

  case class Person(name: String, age: Int)

  import monocle.generic._
  (toHList composeLens hlistHead).get(Person("Robert", 25)) shouldEqual "Robert"

```