package monocleintro

import org.specs2.scalaz.Spec

import scala.collection.JavaConversions._


class IntroExample extends Spec {

  "for loop" in {
    val list = seqAsJavaList( (-5 to 5).map( i => i:java.lang.Integer ) )
    IntroJavaExample.example(list).toList shouldEqual (1 to 6).toList
  }



}
