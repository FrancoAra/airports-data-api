import org.specs2.mutable._

import play.api.test._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._

import services._

class QueriesSpec extends PlaySpecification {

  val injector = new GuiceApplicationBuilder()
    .overrides(bind[BatchImporter].to[NoImport])
    .injector

  val query = injector.instanceOf[Queries]

  "Queries" should {

    "retrieve a country" in  {
      val countries = await(query.countriesPage(2))
      val first = (countries(0).as[JsObject] \ "name").as[String]
      first must beEqualTo("American Samoa")
    }

    "retrieve airports" in {
      val airports = await(query.airportsFrom("NE"))
      val first = (airports(0).as[JsObject] \ "name").as[String]
      first must beEqualTo("Tessaoua Airport")
    }

    "retrieve runways" in {
      val runways = await(query.runwaysFrom(6543))
      val first = (runways(0).as[JsObject] \ "_id").as[Int]
      first must beEqualTo(251984)
    }

    "calculate countries with most airports" in {
      val countries = await(query.countriesWithMostAirports)
      val first = (countries(0).as[JsObject] \ "_id").as[String]
      first must beEqualTo("US")
    }

    "calculate countries with least airports" in {
      val countries = await(query.countriesWithLeastAirports)
      val amount = countries.as[List[JsObject]].size
      amount must beEqualTo(24)
    }

    "calculate most common latitudes" in {
      val latitudes = await(query.mostCommonRunwayLatitudes)
      val first = (latitudes(0).as[JsObject] \ "_id").as[String]
      first must beEqualTo("H1")
    }
  }
}
