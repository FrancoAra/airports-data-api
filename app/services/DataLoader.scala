/**
  * @author Francisco Miguel Aramburo Torres - atfm05@gmail.com
  */

package services

import javax.inject._
import scala.io.Source

import play.api.Environment
import play.api.libs.json._

/** Object to hold helper regular expresions. */
object DataLoader {

  /** Regular expresion for parsing the countries.csv lines. */
  val CountryRegex =
    /* id */
    ( """(\d+),"""
    /* code */
    + """"(\w+)","""
    /* name */
    + """"(.+?)","""
    /* continent */
    + """"(\w+)","""
    /* wikipediaLink */
    + """"(.+?)","""
    /* keywords */
    + """(?:"(.+?)")?""").r

  /** Regular expresion for parsing the airports.csv lines. */
  val AirportRegex =
    /* id */
    ( """(\d+)(?=,),"""
    /* ident */
    + """"(.+?)"(?=,),"""
    /* type */
    + """"(\w+)"(?=,),"""
    /* name */
    + """"(.+?)"(?=,),"""
    /* latitudeDeg */
    + """(-?\d+(?:\.\d+)?),"""
    /* longitudeDeg */
    + """(-?\d+(?:\.\d+)?),"""
    /* elevationFt */
    + """(?:(-?\d+)(?=,))?,"""
    /* continent */
    + """"(\w+)"(?=,),"""
    /* isoCountry */
    + """"(\w+)"(?=,),"""
    /* isoRegion */
    + """(?:"(.+?)"(?=,))?,"""
    /* municipality */
    + """(?:"(.+?)"(?=,))?,"""
    /* scheduledService */
    + """"(\w+)"(?=,),"""
    /* gpsCode */
    + """(?:"(.+?)"(?=,))?,"""
    /* iataCode */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* localCode */
    + """(?:"(.+?)"(?=,))?,"""
    /* homeLink */
    + """(?:"(.+?)"(?=,))?,"""
    /* wikipediaLink */
    + """(?:"(.+?)"(?=,))?,"""
    /* keywords */
    + """(?:"(.+?)")?""").r

  /** Regular expresion for parsing the runways.csv lines. */
  val RunwaysRegex =
    /* id */
    ( """(\d+)(?=,),"""
    /* airport_ref */
    + """(\d+)(?=,),"""
    /* airport_ident */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* length_ft */
    + """(?:(.+?)(?=,))?,"""
    /* width_ft */
    + """(?:(.+?)(?=,))?,"""
    /* surface */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* lighted */
    + """(?:(\d)(?=,))?,"""
    /* closed */
    + """(?:(\d)(?=,))?,"""
    /* le_ident */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* le_latitude_deg */
    + """(?:(.+?)(?=,))?,"""
    /* le_longitude_deg */
    + """(?:(.+?)(?=,))?,"""
    /* le_elevation_ft */
    + """(?:(.+?)(?=,))?,"""
    /* le_heading_degT */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* le_displaced_threshold_ft */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* he_ident */
    + """(?:"?(.+?)"?(?=,))?,"""
    /* he_latitude_deg */
    + """(?:(.+?)(?=,))?,"""
    /* he_longitude_deg */
    + """(?:(.+?)(?=,))?,"""
    /* he_elevation_ft */
    + """(?:(.+?)(?=,))?,"""
    /* he_heading_degT */
    + """(?:(.+?)(?=,))?,"""
    /* he_displaced_threshold_ft */
    + """(?:(.+?))?""").r
}

/** Component which methods read the data csv files in the conf directory, and returns
  * a stream of json objects with each line data.
  *
  * @param env given by play framework to access the conf directory.
  * @param dictionary component to save the countries in memory for fuzzy searches.
  */
@Singleton
class DataLoader @Inject() (env: Environment, dictionary: CountriesDictionary) {

  import DataLoader._

  /** Reads countries.csv and parses each line to json objects.
    *
    * @return a stream of json objects containing the data.
    */
  def countries: Stream[JsObject] =
    readCSV("conf/countries.csv").map {
      case CountryRegex(id, code, name, continent, wikipediaLink, keywords) =>
        val kw = if (keywords == null) JsNull
                 else JsArray(keywords.split(", ").map(JsString(_)))
        val kw2 = if (keywords == null) None
                 else Some(keywords.split(",").toList)
        dictionary.add(Country(code, name, kw2))
        Json.obj(
          "_id" -> JsNumber(id.toInt),
          "code" -> code,
          "name" -> name,
          "continent" -> continent,
          "wikipedia_link" -> wikipediaLink,
          "keywords" -> kw
        )
    }

  /** Reads airports.csv and parses each line to json objects.
    *
    * @return a stream of json objects containing the data.
    */
  def airports: Stream[JsObject] =
    readCSV("conf/airports.csv").map {
      case AirportRegex(
        id, ident, typ, name, latitudeDeg, longitudeDeg,
        elevationFt, continent, isoCountry, isoRegion,
        municipality, scheduledService, gpsCode, iataCode,
        localCode, homeLink, wikipediaLink, keywords
      ) =>
        val kw = if (keywords == null) JsNull
                 else JsArray(keywords.split(",").map(JsString(_)))
        Json.obj(
          "_id" -> JsNumber(id.toInt),
          "ident" -> stringOrNull(ident),
          "type" -> stringOrNull(typ),
          "name" -> stringOrNull(name),
          "latitude_deg" -> stringOrNull(latitudeDeg),
          "longitude_deg" -> stringOrNull(longitudeDeg),
          "elevation_ft" -> stringOrNull(elevationFt),
          "continent" -> stringOrNull(continent),
          "iso_country" -> stringOrNull(isoCountry),
          "iso_region" -> stringOrNull(isoRegion),
          "municipality" -> stringOrNull(municipality),
          "scheduled_service" -> stringOrNull(scheduledService),
          "gps_code" -> stringOrNull(gpsCode),
          "iata_code" -> stringOrNull(iataCode),
          "local_code" -> stringOrNull(localCode),
          "home_link" -> stringOrNull(homeLink),
          "wikipedia_link" -> stringOrNull(wikipediaLink),
          "keywords" -> kw
        )
    }

  /** Reads runways.csv and parses each line to json objects.
    *
    * @return a stream of json objects containing the data.
    */
  def runways: Stream[JsObject] =
    readCSV("conf/runways.csv").map {
      case RunwaysRegex(
        id, airportRef, airportIdent, lengthFt, widthFt, surface, lighted,
        closed, leIdent, leLatitudeDeg, leLongitudeDeg, leElevationFt,
        leHeadingDegT, leDisplacedThresholdFt, heIdent, heLatitudeDeg,
        heLongitudeDeg, heElevationFt, heHeadingDegT, heDisplacedThresholdFt
      ) =>
        Json.obj(
          "_id" -> JsNumber(id.toInt),
          "airport_ref" -> JsNumber(airportRef.toInt),
          "airport_ident" -> stringOrNull(airportIdent),
          "length_ft" -> stringOrNull(lengthFt),
          "width_ft" -> stringOrNull(widthFt),
          "surface" -> stringOrNull(surface),
          "lighted" -> stringOrNull(lighted),
          "closed" -> stringOrNull(closed),
          "le_ident" -> stringOrNull(leIdent),
          "le_latitude_deg" -> stringOrNull(leLatitudeDeg),
          "le_longitude_deg" -> stringOrNull(leLongitudeDeg),
          "le_elevation_ft" -> stringOrNull(leElevationFt),
          "le_heading_degT" -> stringOrNull(leHeadingDegT),
          "le_displaced_threshold_ft" -> stringOrNull(leDisplacedThresholdFt),
          "he_ident" -> stringOrNull(heIdent),
          "he_latitude_deg" -> stringOrNull(heLatitudeDeg),
          "he_longitude_deg" -> stringOrNull(heLongitudeDeg),
          "he_elevation_ft" -> stringOrNull(heElevationFt),
          "he_heading_degT" -> stringOrNull(heHeadingDegT),
          "he_displaced_threshold_ft" -> stringOrNull(heDisplacedThresholdFt)
        )
    }

  private def readCSV (path: String): Stream[String] =
    Source.fromFile(env.getFile(path)).getLines.drop(1).toStream

  private def stringOrNull (v: String): JsValue =
    if (v == null) JsNull
    else JsString(v)
}
