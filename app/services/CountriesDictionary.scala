/**
  * @author Francisco Miguel Aramburo Torres - atfm05@gmail.com
  */

package services

import javax.inject._

case class Country (code: String, name: String, keywords: Option[List[String]])

/** Trait for the component that does fuzzy search over a list of [[services.Country]] */
trait CountriesDictionary {

  /** List of countries to be computed fuzzy searches on. */
  var dictionary: List[Country]

  /** [[services.MinEditDistance]] algorithm component. */
  val minEditAlgo: MinEditDistance

  /** Appends a country to the dictionary. */
  def add (country: Country): Unit = dictionary = country :: dictionary

  /** Iterates the dictionary retrieving the best fuzzy match.
    * Fuzzy match priorities:
    * 1) query is contained in the name of the country.
    * 2) query is contained in a keyword of the country.
    * 3) query represents the minimum edit distance for each word in the name and keywords.
    *
    * @param query string to search.
    */
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

/** Component which saves the list of countries in memory and does fuzzy search
  * to retrieve country codes from misspelled or incomplete country names.
  *
  * @param minEditAlgo to be used for the fuzzy search.
  */
@Singleton
class MemCountriesDictionary @Inject() (val minEditAlgo: MinEditDistance) extends CountriesDictionary {

  var dictionary = List.empty[Country]
}
