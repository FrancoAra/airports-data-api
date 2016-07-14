
package controllers

import javax.inject.Inject

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

import services.Queries
import services.MinEditDistance
import services.CountriesDictionary

class AirportsAPI @Inject() (
  query: Queries,
  dictionary: CountriesDictionary
) extends Controller {

  val RunwaysRegex = """^\$((?:\w|-)+)""".r
  val CountryCodeRegex = """^([a-zA-Z]{2})$""".r

  def search (queryString: String) = Action.async {
    queryString match {
      case CountryCodeRegex(countryCode) =>
        query.airportsPage(countryCode.toUpperCase, 1).map {
          json => Ok(Json.obj(
            "type" -> "airports",
            "code" -> countryCode.toUpperCase,
            "data" -> json
          ))
        }
      case RunwaysRegex(airportIdent) =>
        query.runwaysFromIdent(airportIdent).map {
          json => Ok(Json.obj(
            "type" -> "runways",
            "data" -> json
          ))
        }
      case _ =>
        val countryCode = dictionary.fuzzySearch(queryString)
        query.airportsPage(countryCode, 1).map {
          json => Ok(Json.obj(
            "type" -> "airports",
            "code" -> countryCode,
            "data" -> json
          ))
        }
    }
  }

  def countriesPage (page: Int) = Action.async {
    query.countriesPage(page).map {
      json => Ok(json)
    }
  }

  def airportsFrom (country: String) = Action.async {
    query.airportsFrom(country).map {
      json => Ok(json)
    }
  }

  def airportsPage (country: String, page: Int) = Action.async {
    query.airportsPage(country, page).map {
      json => Ok(json)
    }
  }

  def runwaysFrom (airport: Int) = Action.async {
    query.runwaysFrom(airport).map {
      json => Ok(json)
    }
  }

  def countriesWithMostAirports = Action.async {
    query.countriesWithMostAirports.map {
      json => Ok(json)
    }
  }

  def countriesWithLeastAirports = Action.async {
    query.countriesWithLeastAirports.map {
      json => Ok(json)
    }
  }

  def mostCommonRunwayLatitudes = Action.async {
    query.mostCommonRunwayLatitudes.map {
      json => Ok(json)
    }
  }
}
