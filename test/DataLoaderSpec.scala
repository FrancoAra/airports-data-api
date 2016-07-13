import org.specs2.mutable._

import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import services._

class DataLoaderSpec extends Specification {

  val injector = new GuiceApplicationBuilder()
    .overrides(bind[BatchImporter].to[NoImport])
    .injector

  val loader = injector.instanceOf[DataLoader]

  "DataLoader" should {

    "load countries" in  {
      loader.countries.size must beEqualTo(247)
    }

    "load airports" in {
      loader.airports.size must beEqualTo(46505)
    }

    "load runways" in {
      loader.runways.size must beEqualTo(39536)
    }
  }
}
