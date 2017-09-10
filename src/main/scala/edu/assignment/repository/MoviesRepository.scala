package edu.assignment.repository

import edu.assignment.config.MovieActorContext
import edu.assignment.model.{MovieIdentification, MovieInformation}
import edu.assignment.repository.MoviesRepositoryDefinitions.MoviesDBRepository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

/**
  * A layer of abstraction which would be responsible for talking to a data source.
  */
trait MoviesRepository {

  /**
    * Create a new record for a given Movie.
    * @param movieInformation
    * @return
    */
  def create(movieInformation: MovieInformation): Future[Unit]

  /**
    * Get the movie data from datasource.
    * @param movieIdentification
    * @return
    */
  def read(movieIdentification: MovieIdentification): Future[Option[MovieInformation]]

  /**
    * Update an existing movie data onto data source.
    * @param movieInformation
    * @return
    */
  def update(movieInformation: MovieInformation): Future[Unit]
}

object MoviesRepository {
  def apply(): MoviesRepository = new MoviesDBRepository()
}

trait SlickDAO[DbRow, DomainObject] {
  protected val dbProfile: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("db.movies")
  protected val db = dbProfile.db
  import dbProfile.profile.api._

  val query: TableQuery[_ <: Table[DbRow]]
}

/**
  * ORM - Where mapping between database table and scala object is defined. This is the class responsible for performing DB operations.
  */
object MoviesRepositoryDefinitions extends SlickDAO[MovieInformation, MovieInformation] {

  import dbProfile.profile.api._

  override lazy val query: TableQuery[MoviesTable] = TableQuery[MoviesTable]

  class MoviesTable(tag: Tag) extends Table[MovieInformation](tag, "movies") {
    override def * : ProvenShape[MovieInformation] =
      (imdbId, screenId, movieTitle, availableSeats, reservedSeats) <> (MovieInformation.tupled, MovieInformation.unapply)

    def imdbId = column[String]("imdb_id")

    def screenId = column[String]("screen_id")

    def movieTitle = column[String]("movie_title")

    def availableSeats = column[Int]("available_seats")

    def reservedSeats = column[Int]("reserved_seats")

    def pk = primaryKey("movies_pk", (imdbId, screenId))

  }

  class MoviesDBRepository extends MoviesRepository with MovieActorContext {
    override def create(movieInformation: MovieInformation): Future[Unit] = {
      db.run(query += movieInformation).map(_ => ())
    }

    override def read(movieIdentification: MovieIdentification): Future[Option[MovieInformation]] = {
      db.run(query.filter(e => e.imdbId === movieIdentification.imdbId && e.screenId === movieIdentification.screenId).result)
        .map(_.headOption)
    }

    override def update(movieInformation: MovieInformation): Future[Unit] = {
      db.run(
        query.filter(e => e.imdbId === movieInformation.imdbId && e.screenId === movieInformation.screenId)
          .update(movieInformation)
      ).map(_ => ())
    }
  }

}
