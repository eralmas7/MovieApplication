package edu.assignment.service

import edu.assignment.config.MovieActorContext
import edu.assignment.model.{MovieIdentification, MovieInformation}
import edu.assignment.repository.MoviesRepository
import edu.assignment.model._

import scala.concurrent.Future

/**
  * An abstraction of services provided by this movie application.
  */
trait MovieService {

  /**
    * Save movie information onto data source abstracted within repository.
    * @param movieInformation
    * @return
    */
  def save(movieInformation: MovieInformation): Future[RegistrationResult.Value]

  /**
    * Reserve a seat for a movie.
    * @param movieIdentification
    * @return
    */
  def reserve(movieIdentification: MovieIdentification): Future[ReservationResult.Value]

  /**
    * Read the movie information from a repository.
    * @param movieIdentification
    * @return
    */
  def read(movieIdentification: MovieIdentification): Future[Option[MovieInformation]]

}

object MovieService {
  def apply(): MovieService = new MovieServiceImpl(MoviesRepository())
}

class MovieServiceImpl(moviesRepository: MoviesRepository) extends MovieService with MovieActorContext {

  override def save(movieInformation: MovieInformation): Future[RegistrationResult.Value] = {
    val existingMovie = read(movieInformation.movieIdentification)

    def save =
      for {
        _ <- moviesRepository.create(movieInformation)
      } yield RegistrationResult.RegistrationSuccessful

    existingMovie flatMap {
      case Some(x) => Future.successful(RegistrationResult.RegistrationAlreadyExists)
      case None => save

    }
  }

  override def reserve(movieIdentification: MovieIdentification): Future[ReservationResult.Value] = {
    val existingMovie = read(movieIdentification)
    existingMovie flatMap {
      case Some(x) =>
        if (x.reservedSeats < x.availableSeats)
          moviesRepository.update(x.reserveOneSeat()).map(_ => ReservationResult.ReservationSuccessful)
        else
          Future.successful(ReservationResult.NoSeatsLeftToReserve)
      case None =>
        Future.successful(ReservationResult.NoSuchMovieFound)
    }
  }

  override def read(movieIdentification: MovieIdentification): Future[Option[MovieInformation]] = {
    moviesRepository.read(movieIdentification)
  }

}
