package services

import javax.inject._
import scala.concurrent.Future
import com.google.inject.ImplementedBy

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.inject.ApplicationLifecycle
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

@ImplementedBy(classOf[MongoBatchImporter])
trait BatchImporter

@Singleton
class NoImport extends BatchImporter

@Singleton
class MongoBatchImporter @Inject() (
  dataLoader: DataLoader,
  reactiveMongoApi: ReactiveMongoApi,
  lifecycle: ApplicationLifecycle
) extends BatchImporter {

  //futureCollections.map { case (countries, airports, runways) =>
  //  countries.bulkInsert(dataLoader.countries, ordered = false).
  //    foreach(_ => Logger.info("MongoDB: countries imported"))
  //  airports.bulkInsert(dataLoader.airports, ordered = false).
  //    foreach(_ => Logger.info("MongoDB: airports imported"))
  //  runways.bulkInsert(dataLoader.runways, ordered = false).
  //    foreach(_ => Logger.info("MongoDB: runways imported"))
  //}

  //lifecycle.addStopHook { () =>
  //  futureCollections.flatMap { case (countries, airports, runways) =>
  //    for {
  //      _ <- countries.drop(false)
  //      _ <- airports.drop(false)
  //      _ <- runways.drop(false)
  //    } yield Logger.info("MongoDB: collections dropped")
  //  }
  //}

  def futureCollections = reactiveMongoApi.database.map { case db =>
    val countries = db.collection[JSONCollection]("countries")
    val airports = db.collection[JSONCollection]("airports")
    val runways = db.collection[JSONCollection]("runways")
    (countries, airports, runways)
  }
}
