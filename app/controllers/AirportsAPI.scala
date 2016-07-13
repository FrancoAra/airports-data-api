
package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

import services.Queries

class AirportsAPI @Inject() (query: Queries) extends Controller {

  //def list = Action.async {implicit request =>
  //  postRepo.find()
  //    .map(posts => Ok(Json.toJson(posts.reverse)))
  //    .recover {case PrimaryUnavailableException => InternalServerError("Please install MongoDB")}
  //}
}
