package edu.assignment

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import edu.assignment.http.HttpService
import edu.assignment.config.{MovieActorContext, MovieConfig, MovieDataSourceInitializer}

import scala.io.StdIn

/**
  * Entry point into the movie application system.
  */
object Bootstrap extends App with MovieDataSourceInitializer with MovieConfig with MovieActorContext {
  val log: LoggingAdapter = Logging(system, getClass)
  val httpService = HttpService()

  migrate()

  val bindingFuture = Http().bindAndHandle(httpService.routes, httpInterface, httpPort)

  log.info("Press enter key to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
