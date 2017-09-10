package edu.assignment.service

import edu.assignment.UnitSpec
import edu.assignment.model.ReservationResult
import edu.assignment.model.ReservationResult._
import edu.assignment.model.RegistrationResult._
import edu.assignment.repository.MoviesRepository
import scala.concurrent.Future

class MovieServiceTest extends UnitSpec {
  private val existingMovie = movieInfo("existingMovie")
  private val nonExistingMovie = movieInfo("nonExistingMovie")
  private val noSeatsLeftMovie = movieInfo("noSeatsLeftMovie", reserverdSeats = 100)

  "read" should {

    "read an existing entry" in new TestFixture {
      service.read(existingMovie.movieIdentification).futureValue.value shouldBe existingMovie
    }

    "return NONE for a non existing entry" in new TestFixture {
      service.read(nonExistingMovie.movieIdentification).futureValue shouldBe None
    }

  }

  "save" should {
    s"return $RegistrationSuccessful for a new entry" in new TestFixture {
      service.save(nonExistingMovie).futureValue shouldBe RegistrationSuccessful
    }

    s"return $RegistrationAlreadyExists for an existing entry" in new TestFixture {
      service.save(existingMovie).futureValue shouldBe RegistrationAlreadyExists
    }
  }

  "reserve" should {
    s"return $ReservationSuccessful for an existing entry with available seats" in new TestFixture {
      service.reserve(existingMovie.movieIdentification).futureValue shouldBe ReservationSuccessful
    }

    s"return $NoSeatsLeftToReserve for an existing entry with no available seats" in new TestFixture {
      service.reserve(noSeatsLeftMovie.movieIdentification).futureValue shouldBe NoSeatsLeftToReserve
    }

    s"return ${ReservationResult.NoSuchMovieFound} for a non existing entry" in new TestFixture {
      service.reserve(nonExistingMovie.movieIdentification).futureValue shouldBe NoSuchMovieFound
    }

  }

  trait TestFixture {
    val daoStub = stub[MoviesRepository]
    val service = new MovieServiceImpl(daoStub)

    List(existingMovie, noSeatsLeftMovie) foreach (movie => daoStub.read _ when movie.movieIdentification returns Future.successful(Some(movie)))
    daoStub.read _ when nonExistingMovie.movieIdentification returns Future.successful(None)
    daoStub.create _ when nonExistingMovie returns Future.successful(())
    daoStub.update _ when * returns Future.successful(())
  }
}