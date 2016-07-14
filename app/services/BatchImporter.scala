/**
  * @author Francisco Miguel Aramburo Torres - atfm05@gmail.com
  */

package services

import javax.inject._
import scala.concurrent.Future
import com.google.inject.ImplementedBy

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.inject.ApplicationLifecycle
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

trait BatchImporter

@Singleton
class NoImport extends BatchImporter

/** Component eagerly loaded which uses [[services.DataLoader]] to import the data
  * at conf directory to MongoDB. Also cleans the database at shutdown.
  *
  * @param dataLoader component to read the streams of data.
  * @param reactiveMongoApi component to access mongodb.
  * @param lifecycle component provided by play framework to add the stop hook.
  */
@Singleton
class MongoBatchImporter @Inject() (
  dataLoader: DataLoader,
  reactiveMongoApi: ReactiveMongoApi,
  lifecycle: ApplicationLifecycle
) extends BatchImporter {

  // This lines of code are executed at the starting phase of play framework
  // because the component is eagerly loaded and its constructor is executed.

  futureCollections.map { case (countries, airports, runways) =>
    countries.bulkInsert(dataLoader.countries, ordered = false).
      foreach(_ => Logger.info("MongoDB: countries imported"))
    airports.bulkInsert(dataLoader.airports, ordered = false).
      foreach(_ => Logger.info("MongoDB: airports imported"))
    runways.bulkInsert(dataLoader.runways, ordered = false).
      foreach(_ => Logger.info("MongoDB: runways imported"))
  }

  lifecycle.addStopHook { () =>
    futureCollections.flatMap { case (countries, airports, runways) =>
      for {
        _ <- countries.drop(false)
        _ <- airports.drop(false)
        _ <- runways.drop(false)
      } yield Logger.info("MongoDB: collections dropped")
    }
  }

  private def futureCollections = reactiveMongoApi.database.map { case db =>
    val countries = db.collection[JSONCollection]("countries")
    val airports = db.collection[JSONCollection]("airports")
    val runways = db.collection[JSONCollection]("runways")
    (countries, airports, runways)
  }
}
