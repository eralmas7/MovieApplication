package edu.assignment

import edu.assignment.repository.MoviesRepository
import edu.assignment.config.MovieDataSourceInitializer
import org.scalatest.BeforeAndAfterAll

trait IntegrationSpec extends BaseSpec with MovieDataSourceInitializer with BeforeAndAfterAll {
  val dao = MoviesRepository()

  override protected def beforeAll() = {
    reloadSchema()
  }
}
