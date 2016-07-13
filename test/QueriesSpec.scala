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
  }
}
