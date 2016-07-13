
package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

import services.Queries

class AirportsAPI @Inject() (query: Queries) extends Controller {

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
