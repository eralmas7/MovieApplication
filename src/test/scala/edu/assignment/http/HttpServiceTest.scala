package edu.assignment.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import edu.assignment.ApiSpec
import edu.assignment.http.route.MovieRoutes
import edu.assignment.model.{MovieIdentification, MovieInformation}
import edu.assignment.repository.MoviesRepository
import edu.assignment.service.MovieServiceImpl
import edu.assignment.config.MovieJsonProtocol
import spray.json._

class HttpServiceTest extends ApiSpec with MovieJsonProtocol {
  val httpService = new HttpService(
    new MovieRoutes(
      new MovieServiceImpl(
        MoviesRepository()
      )
    )
  )

  import MovieTestData._

  "service" should {

    s"respond with HTTP-$NotFound for a non existing path" in {
      Get("/non/existing/") ~> httpService.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe "The path requested [/non/existing/] does not exist."
      }
    }

    s"respond with HTTP-$MethodNotAllowed for a non supported HTTP method" in {
      Head(generalUrl()) ~> httpService.routes ~> check {
        status shouldBe MethodNotAllowed
        responseAs[String] shouldBe "Supported methods are: POST, GET!"
      }
    }

  }

  "registration" should {
    s"respond with HTTP-$OK when registering a new movie" in {
      val movieRegistration = movieInfo()
      val requestEntity = HttpEntity(MediaTypes.`application/json`, retrieveJsonString(movieRegistration).toString)
      Post(generalUrl(movieRegistration.movieIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[String] shouldBe "movie registered"
      }
    }

    s"respond with HTTP-$BadRequest when trying to register an existing movie" in {
      val existingMovie = movieInfo()
      dao.create(existingMovie)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, retrieveJsonString(existingMovie))
      Post(generalUrl(existingMovie.movieIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe BadRequest
        responseAs[String] shouldBe "movie already exists"
      }
    }

    s"respond with HTTP-$BadRequest failing validation if path and body resource identifiers are different" in {
      val movieinfo = movieInfo()
      val requestEntity = HttpEntity(MediaTypes.`application/json`, retrieveJsonString(movieinfo))
      Post(generalUrl(movieInfo().movieIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe BadRequest
      }
    }
  }

  "reservation" should {

    s"respond with HTTP-$OK when reserving an existing movie" in {
      val existingMovie = movieInfo()
      dao.create(existingMovie)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, reservationJson(existingMovie.movieIdentification))
      Post(generalUrl(existingMovie.movieIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[String] shouldBe "seat reserved"
      }
    }

    s"respond with HTTP-$BadRequest if no seats left" in {
      val existingMovie = movieInfo(availableSeats = 50, reserverdSeats = 50)
      dao.create(existingMovie)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, reservationJson(existingMovie.movieIdentification))
      Post(generalUrl(existingMovie.movieIdentification), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe BadRequest
        responseAs[String] shouldBe "no seats left"
      }
    }

    s"respond with HTTP-$NotFound for a non existing movie/screen combination" in {
      val notExistingId = movieInfo().movieIdentification
      val requestEntity = HttpEntity(MediaTypes.`application/json`, reservationJson(notExistingId))
      Post(generalUrl(notExistingId), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe NotFound
        responseAs[String] shouldBe s"Could not find th movie identified by: $notExistingId"
      }
    }

    s"respond with HTTP-$BadRequest failing validation if path and body resource identifiers are different" in {
      val pathIdentifiers = movieInfo().movieIdentification
      val bodyIdentifiers = movieInfo().movieIdentification
      val requestEntity = HttpEntity(MediaTypes.`application/json`, reservationJson(bodyIdentifiers))
      Post(generalUrl(pathIdentifiers), requestEntity) ~> httpService.routes ~> check {
        response.status shouldBe BadRequest
        responseAs[String] shouldBe
          s"validation failed: resource identifiers from the path [$pathIdentifiers] " +
          s"and the body: [$bodyIdentifiers] do not match"
      }
    }

  }

  "retrieval" should {

    s"respond with HTTP-$OK for an existing movie" in {
      val existingMovie = movieInfo()
      dao.create(existingMovie)
      Get(generalUrl(existingMovie.movieIdentification)) ~> httpService.routes ~> check {
        response.status shouldBe OK
        responseAs[JsValue] shouldBe retrieveJson(existingMovie)
      }
    }

    s"respond with HTTP-$NotFound for a non existing movie/screen combination" in {
      val notExistingId = movieInfo().movieIdentification
      Get(generalUrl(notExistingId)) ~> httpService.routes ~> check {
        status shouldBe NotFound
        responseAs[String] shouldBe s"Could not find th movie identified by: $notExistingId"
      }
    }

  }

  object MovieTestData {

    def retrieveJson(movieInformation: MovieInformation = movieInfo()): JsValue =
      s"""
         |{
         |"imdbId": "${movieInformation.imdbId}",
         |"screenId": "${movieInformation.screenId}",
         |"movieTitle": "${movieInformation.movieTitle}",
         |"availableSeats": ${movieInformation.availableSeats},
         |"reservedSeats": ${movieInformation.reservedSeats}
         |}
         |""".stripMargin.parseJson

    def retrieveJsonString(movieInformation: MovieInformation = movieInfo()): String =
      s"""
         |{
         |"imdbId": "${movieInformation.imdbId}",
         |"screenId": "${movieInformation.screenId}",
         |"movieTitle": "${movieInformation.movieTitle}",
         |"availableSeats": ${movieInformation.availableSeats},
         |"reservedSeats": ${movieInformation.reservedSeats}
         |}
         |""".stripMargin.parseJson.toString

    def reservationJson(movieIdentification: MovieIdentification = movieInfo().movieIdentification): String =
      s"""
         |{
         |"imdbId": "${movieIdentification.imdbId}",
         |"screenId": "${movieIdentification.screenId}"
         |}
         |""".stripMargin.parseJson.toString

    def generalUrl(movieIdentification: MovieIdentification = movieInfo().movieIdentification): String =
      s"/movies/${movieIdentification.imdbId}/${movieIdentification.screenId}/"

  }

}
