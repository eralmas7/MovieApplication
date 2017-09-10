package edu.assignment.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

/**
  * Handling Rejection of a rest request so we can get meaningful status and message back from service.
  */
trait RejectionHandling {

  def customRejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case ValidationRejection(msg, _) =>
          complete((BadRequest, s"validation failed: $msg"))
      }
      .handleNotFound {
        extractUnmatchedPath { p =>
          complete((NotFound, s"The path requested [$p] does not exist."))
        }
      }
      .handleAll[MethodRejection] { methodRejections =>
        complete((MethodNotAllowed, s"Supported methods are: ${methodRejections.map(_.supported.name) mkString ", "}!"))
      }
      .result()
}