package com.github.rotstrom.spray.demo

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.io.IO
import com.github.rotstrom.spray.demo.api.PetServiceActor
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("app")

  val petService: ActorRef = system.actorOf(Props(classOf[PetServiceActor]), "pet-service")

  IO(Http) ! Http.Bind(petService, interface = "localhost", port = 8080)
}
