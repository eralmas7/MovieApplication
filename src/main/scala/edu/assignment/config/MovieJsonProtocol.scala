package edu.assignment.config

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import edu.assignment.model.{MovieIdentification, MovieInformation}
import spray.json.DefaultJsonProtocol

/**
  * Implicits for marshalling and unmarshalling movie data.
  */
trait MovieJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val movieReservationFormat = jsonFormat2(MovieIdentification)
  implicit val movieInformationFormat = jsonFormat5(MovieInformation)

}
