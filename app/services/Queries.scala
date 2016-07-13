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

@ImplementedBy(classOf[MongoBatchImporter])
trait Queries {

  def countriesPage (page: Int): Future[JsArray]
}

@Singleton
class MockQueries extends Queries {

  def countriesPage (page: Int): Future[JsArray] = Future(Json.arr())
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
}
