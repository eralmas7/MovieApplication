package edu.assignment.http

import akka.http.scaladsl.server.Directives._
import edu.assignment.http.route.MovieRoutes

/**
  * A service responsible for taking a http request and then routing to appropriate handler.
  * @param movieRoutes
  */
class HttpService(movieRoutes: MovieRoutes) extends RejectionHandling {
  val routes =
    handleRejections(customRejectionHandler) {
      pathPrefix("movies") {
        movieRoutes.movieRoutes
      }
    }

}

object HttpService {
  def apply(): HttpService =
    new HttpService(MovieRoutes())
}
