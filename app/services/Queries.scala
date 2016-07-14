package services

import javax.inject._
import scala.concurrent._
import com.google.inject.ImplementedBy

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.api.QueryOpts

// BSON-JSON conversions/collection
import reactivemongo.play.json._
import play.modules.reactivemongo.json.collection._

trait Queries {

  def countriesPage (page: Int): Future[JsArray]
  def airportsFrom (country: String): Future[JsArray]
  def airportsPage (country: String, page: Int): Future[JsArray]
  def runwaysFrom (airport: Int): Future[JsArray]
  def runwaysFromIdent (airport: String): Future[JsArray]
  def countriesWithMostAirports: Future[JsArray]
  def countriesWithLeastAirports: Future[JsArray]
  def mostCommonRunwayLatitudes: Future[JsArray]
}

@Singleton
class MongoQueries @Inject() (reactiveMongoApi: ReactiveMongoApi) extends Queries {

  val pageSize: Int = 10

  private def collection (name: String )= reactiveMongoApi.database.map {
    case db => db.collection[JSONCollection](name)
  }

  def countriesPage (page: Int): Future[JsArray] = for {
    countries <- collection("countries")
    result <- countries
                .find(Json.obj())
                .options(QueryOpts((page - 1) * pageSize, pageSize))
                .cursor[JsObject]()
                .collect[List](pageSize)
  } yield JsArray(result)

  def airportsFrom (country: String): Future[JsArray] = for {
    airports <- collection("airports")
    result <- airports
                .find(Json.obj("iso_country" -> country))
                .cursor[JsObject]()
                .collect[List]()
  } yield JsArray(result)

  def airportsPage (country: String, page: Int): Future[JsArray] = for {
    airports <- collection("airports")
    result <- airports
                .find(Json.obj("iso_country" -> country))
                .options(QueryOpts((page - 1) * pageSize, pageSize))
                .cursor[JsObject]()
                .collect[List](pageSize)
  } yield JsArray(result)

  def runwaysFrom (airport: Int): Future[JsArray] = for {
    runways <- collection("runways")
    result <- runways
                .find(Json.obj("airport_ref" -> airport))
                .cursor[JsObject]()
                .collect[List]()
  } yield JsArray(result)

  def runwaysFromIdent (airport: String): Future[JsArray] = for {
    runways <- collection("runways")
    result <- runways
                .find(Json.obj("airport_ident" -> airport))
                .cursor[JsObject]()
                .collect[List]()
  } yield JsArray(result)

  def countriesWithMostAirports: Future[JsArray] = for {
    airports <- collection("airports")
    result <- {
      import airports.BatchCommands.AggregationFramework.{
        Group, Descending, Sort, SumValue, Limit
      }
      airports.aggregate(
        Group(JsString("$iso_country"))("airports" -> SumValue(1)), List(
          Sort(Descending("airports")),
          Limit(10)
        )
      ).map(_.firstBatch)
    }
  } yield JsArray(result)

  def countriesWithLeastAirports: Future[JsArray] = for {
    airports <- collection("airports")
    result <- {
      import airports.BatchCommands.AggregationFramework.{
        Group, Match, SumValue
      }
      airports.aggregate(
        Group(JsString("$iso_country"))("airports" -> SumValue(1)), List(
          Match(Json.obj("airports" -> Json.obj("$eq" -> 1)))
        )
      ).map(_.firstBatch)
    }
  } yield JsArray(result)

  def mostCommonRunwayLatitudes: Future[JsArray] = for {
    runways <- collection("runways")
    result <- {
      import runways.BatchCommands.AggregationFramework.{
        Group, Descending, Sort, SumValue, Limit
      }
      runways.aggregate(
        Group(JsString("$le_ident"))("hits" -> SumValue(1)), List(
          Sort(Descending("hits")),
          Limit(10)
        )
      ).map(_.firstBatch)
    }
  } yield JsArray(result)
}
