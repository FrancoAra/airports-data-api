
package services

import javax.inject._

case class Country (code: String, name: String, keywords: Option[List[String]])

trait CountriesDictionary {

  var dictionary: List[Country]

  val minEditAlgo: MinEditDistance

  def add (country: Country): Unit = dictionary = country :: dictionary

  def fuzzySearch (query: String): String = {
    val (result, distance) = dictionary.foldLeft(("ZZ", Int.MaxValue)) {
      case (acc, country) => country.keywords match {
        case Some(keywords) =>
          calculateBetween(acc, query, country, (country.name.split(" ").toList ++ keywords))
        case None =>
          calculateBetween(acc, query, country, country.name.split(" ").toList)
      }
    }
    result
  }

  private def calculateBetween (old: (String, Int), q: String, country: Country, words: List[String]): (String, Int) = {
    val query = q.toLowerCase
    val newDistance = if (country.name.toLowerCase contains query) {
      0
    } else {
      (words).map { case (word) =>
        if (word.toLowerCase contains query) 0
        else minEditAlgo.compute(query.toLowerCase, word.toLowerCase)
      }.min
    }
    if (old._2 <= newDistance) old
    else (country.code, newDistance)
  }
}

@Singleton
class MemCountriesDictionary @Inject() (val minEditAlgo: MinEditDistance) extends CountriesDictionary {

  var dictionary = List.empty[Country]
}
