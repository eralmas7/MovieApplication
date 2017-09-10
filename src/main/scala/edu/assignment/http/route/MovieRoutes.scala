package edu.assignment.http.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import edu.assignment.model.{MovieIdentification, MovieInformation}
import edu.assignment.model.RegistrationResult._
import edu.assignment.model.ReservationResult._
import edu.assignment.service.MovieService
import edu.assignment.config.MovieJsonProtocol

/**
  * Route definition to various services exposed by this assignment.
  * @param movieService
  */
class MovieRoutes(movieService: MovieService) extends MovieJsonProtocol {

  val movieRoutes = pathPrefix(Segment / Segment) { (imdbId, screentId) =>
    val urlIdentifiers = MovieIdentification(imdbId, screentId)
    pathEndOrSingleSlash {
      post {
        entity(as[MovieInformation]) { movieInformation =>
          registerMovie(movieInformation, urlIdentifiers)
        }
      } ~
        post {
          entity(as[MovieIdentification]) { movieIdentification =>
            reserveSeat(movieIdentification, urlIdentifiers)
          }
        } ~
        get {
          retrieveMovieInfo(urlIdentifiers)
        }
    }
  }

  private def registerMovie(movieInformation: MovieInformation, urlIdentifiers: MovieIdentification): Route = {
    validateEquals(urlIdentifiers, movieInformation.movieIdentification) {
      val saveResult = movieService.save(movieInformation)
      onSuccess(saveResult) {
        case RegistrationSuccessful => complete("movie registered")
        case RegistrationAlreadyExists => complete(BadRequest, "movie already exists")
      }
    }
  }

  private def reserveSeat(movieIdentification: MovieIdentification, urlIdentifiers: MovieIdentification): Route = {
    validateEquals(urlIdentifiers, movieIdentification) {
      val reservationResult = movieService.reserve(movieIdentification)
      onSuccess(reservationResult) {
        case ReservationSuccessful => complete("seat reserved")
        case NoSeatsLeftToReserve => complete(BadRequest, "no seats left")
        case NoSuchMovieFound => notFound(movieIdentification)
      }
    }
  }

  private def retrieveMovieInfo(urlIdentifiers: MovieIdentification): Route = {
    val movieInfo = movieService.read(urlIdentifiers)
    onSuccess(movieInfo) {
      case Some(x) => complete(x)
      case None => notFound(urlIdentifiers)
    }
  }

  private def notFound(movieIdentification: MovieIdentification): Route = {
    complete(NotFound, s"Could not find th movie identified by: $movieIdentification")
  }

  private def validateEquals(urlIdentifiers: MovieIdentification, bodyIdentifiers: MovieIdentification): Directive0 = {
    validate(urlIdentifiers == bodyIdentifiers, s"resource identifiers from the path [$urlIdentifiers] and the body: [$bodyIdentifiers] do not match")
  }

}

object MovieRoutes {
  def apply(): MovieRoutes = new MovieRoutes(MovieService())
}
