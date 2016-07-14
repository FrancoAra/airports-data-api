import org.specs2.mutable._

import services.MinEditDistance
import services.CountriesDictionary
import services.Country

class FuzzySearchSpec extends Specification {

  val minEdit = new MinEditDistance

  object Dictionary extends CountriesDictionary {
    val minEditAlgo = minEdit
    var dictionary = List(
      Country("NE", "Nethwerlands", Some(List("Holland"))),
      Country("GB", "United Kingdom", Some(List("Great Britain"))),
      Country("MX", "Mexico", None)
    )
  }

  "MinEditDistance" should {

    "compute min edit distance correctly" in  {
      val a = minEdit.compute("azced", "abcdef")
      val b = minEdit.compute("neth", "netherlands")
      val c = minEdit.compute("eico", "mexico")
      val d = minEdit.compute("rc", "franco")
      a must beEqualTo(3)
      b must beEqualTo(7)
      c must beEqualTo(2)
      d must beEqualTo(4)
    }
  }

  "CountriesDictionary" should {

    "make correct fuzzy search" in {
      val a = Dictionary.fuzzySearch("meico")
      val b = Dictionary.fuzzySearch("holl")
      val c = Dictionary.fuzzySearch("brit")
      val d = Dictionary.fuzzySearch("neth")
      val e = Dictionary.fuzzySearch("Mexico")
      a must beEqualTo("MX")
      b must beEqualTo("NE")
      c must beEqualTo("GB")
      d must beEqualTo("NE")
      e must beEqualTo("MX")
    }
  }
}
